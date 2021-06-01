package com.redislabs.edu.redi2read

import com.redislabs.edu.redi2read.repositories.BookRatingRepository
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CategoryRepository
import com.redislabs.edu.redi2read.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
@Order(8)
class CreateGraph(
    @Autowired val redisTemplate: RedisTemplate<String, String>,
    @Autowired val userRepository: UserRepository,
    @Autowired val bookRepository: BookRepository,
    @Autowired val bookRatingRepository: BookRatingRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Value("\${app.graphId}") val graphId: String
) : CommandLineRunner {

    companion object {
        private val logger = LoggerFactory.getLogger(CreateGraph::class.java)
    }

    override fun run(vararg args: String?) {
        if (!redisTemplate.hasKey(graphId)) {
            val graph = com.redislabs.redisgraph.impl.api.RedisGraph()
            // create an index for Books on id
            graph.query(graphId, "CREATE INDEX ON :Book(id)")
            graph.query(graphId, "CREATE INDEX ON :Category(id)")
            graph.query(graphId, "CREATE INDEX ON :Author(name)")
            graph.query(graphId, "CREATE INDEX ON :User(id)")
            val authors = HashSet<String>()
            // for each category create a graph node
            categoryRepository.findAll().forEach { c ->
                graph.query(graphId, "CREATE (:Category {id:\" ${c.id}\", name: \"${c.name}\"})")

                // for each book create a graph node
                bookRepository.findAll().forEach { b ->
                    run {
                        graph.query(graphId, "CREATE (:Book {id: \"${b.id}\", title: \"${b.title}\"})")
                        // for each author create an AUTHORED relationship to the book
                        b.authors?.forEach { a ->
                            run {
                                if (!authors.contains(a)) {
                                    graph.query(graphId, "CREATE (:Author {name: \"$a\"})")
                                    authors.add(a);
                                }
                                graph.query(
                                    graphId,
                                    "MATCH (a:Author {name: \"$a\"}), (b:Book {id: \"${b.id}\"}) CREATE (a)-[:AUTHORED]->(b)"
                                )
                            }
                        }
                        b.categories.forEach { c ->
                            run {
                                graph.query(
                                    graphId,
                                    "MATCH (b:Book {id: \"${b.id}\"}), (c:Category {id: \"${c.id}\"}) CREATE (b)-[:IN]->(c)"
                                )
                            }
                        }
                    }
                }
                // for each user create a graph node
                userRepository.findAll().forEach { u ->
                    run {
                        graph.query(graphId, "CREATE (:User {id: \"${u.id}\", name: \"${u.name}\"})")
                        // for each of the user's book create a purchased relationship
                        u.books.forEach { book ->
                            graph.query(
                                graphId,
                                "MATCH (u:User {id: \"${u.id}\"}), (b:Book {id: \"${book.id}\"}) CREATE (u)-[:PURCHASED]->(b)"
                            )
                        }
                    }
                }
                // for each book rating create a rated relationship
                bookRatingRepository.findAll().forEach { br ->
                    graph.query(
                        graphId,
                        "MATCH (u:User {id: \"${br.user?.id}\"}), (b:Book {id: \"${br.book?.id}\"}) CREATE (u)-[:RATED {rating: ${br.rating}}]->(b)"
                    )
                }
            }
            logger.info(">>>> Created graph...");
        }
    }
}