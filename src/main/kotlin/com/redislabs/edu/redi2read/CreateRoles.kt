package com.redislabs.edu.redi2read

import com.redislabs.edu.redi2read.models.Role
import com.redislabs.edu.redi2read.repositories.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class CreateRoles(
    @Autowired val roleRepository: RoleRepository
    ): CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (roleRepository.count() == 0L) {
            val adminRole = Role(null, "admin")
            val customerRole = Role(null, "customer")
            roleRepository.save(adminRole)
            roleRepository.save(customerRole)
            logger.info(">>>> Created admin and customer roles...")
        }
    }
}