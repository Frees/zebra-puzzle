package com.sample.puzzle.model

import com.sample.puzzle.util.PuzzleInputValidator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuzzleInput(
    val colors: List<String> = listOf(),
    val nationalities: List<String> = listOf(),
    val drinks: List<String> = listOf(),
    val smokes: List<String> = listOf(),
    val pets: List<String> = listOf(),
    val constraints: List<Constraint> = listOf(),
) {
    val houses: Int
        get() = colors.size

    init {
        PuzzleInputValidator.validate(this)
    }
}

@Serializable
sealed class Constraint {
    // Person with attribute X lives in house with attribute Y
    @Serializable
    @SerialName("SameHouse")
    data class SameHouse(val attribute1: Attribute, val attribute2: Attribute) : Constraint()

    // Person with attribute X lives next to person with attribute Y
    @Serializable
    @SerialName("NextTo")
    data class NextTo(val attribute1: Attribute, val attribute2: Attribute) : Constraint()

    // Person with attribute X lives in house number N
    @Serializable
    @SerialName("PositionIs")
    data class PositionIs(val attribute: Attribute, val position: Int) : Constraint()

    // Person with attribute X lives immediately to the right of person with attribute Y
    @Serializable
    @SerialName("RightOf")
    data class RightOf(val attributeLeft: Attribute, val attributeRight: Attribute) : Constraint()
}

@Serializable
sealed class Attribute() {
    @Serializable
    @SerialName("Color")
    data class Color(val value: String) : Attribute()

    @Serializable
    @SerialName("Nationality")
    data class Nationality(val value: String) : Attribute()

    @Serializable
    @SerialName("Drink")
    data class Drink(val value: String) : Attribute()

    @Serializable
    @SerialName("Smoke")
    data class Smoke(val value: String) : Attribute()

    @Serializable
    @SerialName("Pet")
    data class Pet(val value: String) : Attribute()

    fun attrValue(): String = when (this) {
        is Color -> value
        is Nationality -> value
        is Drink -> value
        is Smoke -> value
        is Pet -> value
    }

    fun withValue(newValue: String): Attribute = when (this) {
        is Color -> Color(newValue)
        is Nationality -> Nationality(newValue)
        is Drink -> Drink(newValue)
        is Smoke -> Smoke(newValue)
        is Pet -> Pet(newValue)
    }
}
