package com.redislabs.edu.redi2read

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.RoleRepository
import com.redislabs.edu.redi2read.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@Order(2)
class CreateUsers(
    @Autowired val roleRepository: RoleRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val passwordEncoder: BCryptPasswordEncoder,
): CommandLineRunner {

    companion object {
        private val logger = LoggerFactory.getLogger(CreateUsers::class.java)
    }

    override fun run(vararg args: String?) {
        if (userRepository.count() == 0L) {
            // load the roles
            val admin = roleRepository.findFirstByName("admin")
            val customer = roleRepository.findFirstByName("customer")
            try {
                // create a Jackson object mapper
                val mapper = ObjectMapper()
                // make the JSON data available as an input stream
                val inputStream = javaClass.getResourceAsStream("/data/users/users.json")
                // convert the JSON to objects
                val users: List<User> = mapper.readValue(inputStream)
                users.stream().forEach {
                    it.password = passwordEncoder.encode(it.password)
                    it.addRole(customer)
                    userRepository.save(it)
                }
                logger.info(">>>> ${users.size} Users Saved!")
            } catch (e: IOException) {
                logger.info(">>>> Unable to import users:  ${e.message}")
            }
            val adminUser = User(null,
                "Adminus Admistradore",
                "admin@example.com",
                passwordEncoder.encode("Reindeer Flotilla"),
            null)
            adminUser.addRole(admin)
            userRepository.save(adminUser)
            logger.info(">>>> Loaded User Data and Created users...")
        }
    }
}