package com.sample.puzzle.model

import kotlinx.serialization.Serializable

@Serializable
data class PuzzleResult(
    val houses: List<House> = listOf()
)

@Serializable
data class House(
    val position: Int,
    val color: String? = null,
    val nationality: String? = null,
    val drink: String? = null,
    val smoke: String? = null,
    val pet: String? = null,
)
