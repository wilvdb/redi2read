package com.redislabs.edu.redi2read.services

import com.redislabs.redisgraph.graph_entities.Node
import com.redislabs.redisgraph.impl.api.RedisGraph
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    @Value("\${app.graphId}") val graphId: String,
) {
    private val graph = RedisGraph()

    fun getBookRecommendationsFromCommonPurchasesForUser(userId: String): Set<String> {
        val recommendations = HashSet<String>()

        val query = """
            MATCH (u:User { id: '$userId' })-[:PURCHASED]->(ob:Book) 
            MATCH (ob)<-[:PURCHASED]-(:User)-[:PURCHASED]->(b:Book) 
            WHERE NOT (u)-[:PURCHASED]->(b)
            RETURN distinct b, count(b) as frequency 
            ORDER BY frequency DESC
            """

        val resultSet = graph.query(graphId, query)

        while (resultSet.hasNext()) {
            val record = resultSet.next()
            val book = record.getValue<Node>("b")
            recommendations.add(book.getProperty("id").value.toString())
        }

        return recommendations
    }

    fun getFrequentlyBoughtTogether(isbn: String): Set<String> {
        val recommendations = HashSet<String>()

        val query ="""MATCH (u:User)-[:PURCHASED]->(b1:Book {id: '$isbn'}) 
        MATCH (b1)<-[:PURCHASED]-(u)-[:PURCHASED]->(b2:Book)
        MATCH rated = (User)-[:RATED]-(b2) 
        WITH b1, b2, count(b2) as freq, head(relationships(rated)) as r 
        WHERE b1 <> b2 
        RETURN b2, freq, avg(r.rating) 
        ORDER BY freq, avg(r.rating) DESC"""

        val resultSet = graph.query(graphId, query)

        while(resultSet.hasNext()) {
            val record = resultSet.next()
            val book = record.getValue<Node>("b2")
            recommendations.add(book.getProperty("id").value.toString())
        }

        return recommendations
    }


}