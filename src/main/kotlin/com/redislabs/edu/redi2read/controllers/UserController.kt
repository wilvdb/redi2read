package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/users")
class UserController(
    @Autowired val userRepository: UserRepository,
    ) {

    @GetMapping
    fun all(@RequestParam(defaultValue = "") email: String) = if (email.isEmpty()) {
        userRepository.findAll()
    } else {
        listOfNotNull(userRepository.findFirstByEmail(email))
    }
}