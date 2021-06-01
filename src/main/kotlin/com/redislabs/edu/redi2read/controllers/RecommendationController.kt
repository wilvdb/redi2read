package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.services.RecommendationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recommendations")
class RecommendationController(
    @Autowired val recommendationService: RecommendationService,
) {

    @GetMapping("/user/{userId}")
    fun userRecommendations(@PathVariable("userId") userId: String) =
        recommendationService.getBookRecommendationsFromCommonPurchasesForUser(userId);

    @GetMapping("/isbn/{isbn}/pairings")
    fun frequentPairings(@PathVariable("isbn") isbn: String) = recommendationService.getFrequentlyBoughtTogether(isbn)

}