package com.sample.puzzle

import com.sample.puzzle.model.Attribute
import com.sample.puzzle.model.Constraint
import com.sample.puzzle.model.House
import com.sample.puzzle.model.PuzzleResult
import com.sample.puzzle.util.PuzzleSerializer
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PuzzleSerializerTest {

    @Test
    fun `test readPuzzleInput with valid JSON`() {
        // given
        val jsonText = """
            {
              "colors": ["red", "green"],
              "nationalities": ["Englishman", "Spaniard"],
              "drinks": ["coffee", "tea"],
              "smokes": ["Old Gold", "Kools"],
              "pets": ["dog", "snails"],
              "constraints": [
                {
                  "type": "SameHouse",
                  "attribute1": {
                    "type": "Nationality",
                    "value": "Englishman"
                  },
                  "attribute2": {
                    "type": "Color",
                    "value": "red"
                  }
                }
              ]
            }
        """.trimIndent()

        // when
        val puzzleInput = PuzzleSerializer.readPuzzleInput(jsonText)

        // then
        assertThat(puzzleInput).isNotNull
        assertThat(puzzleInput.houses).isEqualTo(2)
        assertThat(puzzleInput.colors).containsExactly("red", "green")
        assertThat(puzzleInput.nationalities).containsExactly("Englishman", "Spaniard")
        assertThat(puzzleInput.drinks).containsExactly("coffee", "tea")
        assertThat(puzzleInput.smokes).containsExactly("Old Gold", "Kools")
        assertThat(puzzleInput.pets).containsExactly("dog", "snails")
        assertThat(puzzleInput.constraints).containsExactly(
            Constraint.SameHouse(
                attribute1 = Attribute.Nationality("Englishman"),
                attribute2 = Attribute.Color("red"))
        )
    }

    @Test
    fun `test writePuzzleResult with valid result`() {
        // given
        val result = PuzzleResult(
            houses = listOf(
                House(1, "red", "Englishman", "coffee", "Old Gold", "dog"),
                House(2, "green", "Spaniard", "tea", "Kools", "snails")
            )
        )

        // when
        val jsonText = PuzzleSerializer.writePuzzleResult(result)

        // then
        assertThat(jsonText).isNotNull
        val parsedResult = Json.decodeFromString<PuzzleResult>(jsonText)
        assertThat(parsedResult.houses).containsExactly(
            House(1, "red", "Englishman", "coffee", "Old Gold", "dog"),
            House(2, "green", "Spaniard", "tea", "Kools", "snails")
        )
    }
}
