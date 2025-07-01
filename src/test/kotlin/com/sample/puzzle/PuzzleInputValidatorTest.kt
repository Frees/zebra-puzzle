package com.sample.puzzle

import com.sample.puzzle.model.Attribute
import com.sample.puzzle.model.Constraint
import com.sample.puzzle.model.PuzzleInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PuzzleInputValidatorTest {

    @Test
    fun `test validate with valid input`() {
        // given
        val puzzleInput = PuzzleInput(
            colors = listOf("red", "green"),
            nationalities = listOf("Englishman", "Spaniard"),
            drinks = listOf("coffee", "tea"),
            smokes = listOf("Old Gold", "Kools"),
            pets = listOf("dog", "snails")
        )

        // when/then
        // No exception should be thrown when validating a valid input
        // This test passes because the PuzzleInput constructor already calls validate
        assertThat(puzzleInput.houses).isEqualTo(2)
    }

    @Test
    fun `test validate with invalid houses count`() {
        // given
        val colors = listOf("red")
        val nationalities = listOf("Englishman")
        val drinks = listOf("coffee")
        val smokes = listOf("Old Gold")
        val pets = listOf("dog")
        val constraints = listOf<Constraint>()

        // when/then
        val exception = assertThrows<IllegalArgumentException> {
            PuzzleInput(colors, nationalities, drinks, smokes, pets, constraints)
        }
        assertThat(exception.message).contains("Number of houses must be greater than 1")
    }

    @Test
    fun `test validate with mismatched attribute counts`() {
        // given
        val colors = listOf("red", "green", "blue") // 3 colors
        val nationalities = listOf("Englishman", "Spaniard") // 2 nationalities
        val drinks = listOf("coffee", "tea")
        val smokes = listOf("Old Gold", "Kools")
        val pets = listOf("dog", "snails")
        val constraints = listOf<Constraint>()

        // when/then
        val exception = assertThrows<IllegalArgumentException> {
            PuzzleInput(colors, nationalities, drinks, smokes, pets, constraints)
        }
        assertThat(exception.message).contains("Number of nationalities must match number of houses")
    }

    @Test
    fun `test validate with duplicate attributes`() {
        // given
        val colors = listOf("red", "red") // Duplicate color
        val nationalities = listOf("Englishman", "Spaniard")
        val drinks = listOf("coffee", "tea")
        val smokes = listOf("Old Gold", "Kools")
        val pets = listOf("dog", "snails")
        val constraints = listOf<Constraint>()

        // when/then
        val exception = assertThrows<IllegalArgumentException> {
            PuzzleInput(colors, nationalities, drinks, smokes, pets, constraints)
        }
        assertThat(exception.message).contains("Number of colors must match number of houses")
    }

    @Test
    fun `test validate with invalid constraint attribute`() {
        // given
        val colors = listOf("red", "green")
        val nationalities = listOf("Englishman", "Spaniard")
        val drinks = listOf("coffee", "tea")
        val smokes = listOf("Old Gold", "Kools")
        val pets = listOf("dog", "snails")
        val constraints = listOf(
            Constraint.SameHouse(
                attribute1 = Attribute.Nationality("German"), // Not in the list
                attribute2 = Attribute.Color("red")
            )
        )

        // when/then
        val exception = assertThrows<IllegalArgumentException> {
            PuzzleInput(colors, nationalities, drinks, smokes, pets, constraints)
        }
        assertThat(exception.message).contains("Nationality 'German' is not in the list of nationalities")
    }

    @Test
    fun `test validate with invalid position constraint`() {
        // given
        val colors = listOf("red", "green")
        val nationalities = listOf("Englishman", "Spaniard")
        val drinks = listOf("coffee", "tea")
        val smokes = listOf("Old Gold", "Kools")
        val pets = listOf("dog", "snails")
        val constraints = listOf(
            Constraint.PositionIs(
                attribute = Attribute.Nationality("Englishman"),
                position = 3 // Out of range (1..2)
            )
        )

        // when/then
        val exception = assertThrows<IllegalArgumentException> {
            PuzzleInput(colors, nationalities, drinks, smokes, pets, constraints)
        }
        assertThat(exception.message).contains("Position 3 is out of range (1..2)")
    }
}
