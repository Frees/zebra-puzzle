package com.sample.puzzle

import com.sample.puzzle.model.Attribute.*
import com.sample.puzzle.model.Constraint.*
import com.sample.puzzle.model.House
import com.sample.puzzle.model.PuzzleInput
import com.sample.puzzle.solving.PuzzleSolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PuzzleSolverTest {

    @Test
    fun `test solve with simple puzzle`() {
        // given
        val input = PuzzleInput(
            colors = listOf("red", "green"),
            nationalities = listOf("Englishman", "Spaniard"),
            drinks = listOf("coffee", "tea"),
            smokes = listOf("Gold", "Kools"),
            pets = listOf("dog", "snails"),
            constraints = listOf(
                SameHouse(attribute1 = Nationality("Englishman"), attribute2 = Color("red")),
                SameHouse(attribute1 = Nationality("Spaniard"), attribute2 = Pet("dog")),
                PositionIs(attribute = Nationality("Englishman"), position = 1),
                SameHouse(attribute1 = Smoke("Kools"), attribute2 = Pet("dog")),
                SameHouse(attribute1 = Drink("coffee"), attribute2 = Smoke("Gold")),
            )
        )

        // when
        val result = PuzzleSolver(input).solve()

        // then
        assertThat(result.houses).containsExactly(
            House(1, color = "red", nationality = "Englishman", drink = "coffee", smoke = "Gold", pet = "snails"),
            House(2, color = "green", nationality = "Spaniard", drink = "tea", smoke = "Kools", pet = "dog")
        )
    }

    @Test
    fun `test solve with zebra puzzle`() {
        // given
        val input = PuzzleInput(
            colors = listOf("red", "green", "ivory", "yellow", "blue"),
            nationalities = listOf("Englishman", "Spaniard", "Ukrainian", "Norwegian", "Japanese"),
            drinks = listOf("coffee", "tea", "milk", "juice", "water"),
            smokes = listOf("Old Gold", "Kools", "Chesterfield", "Lucky Strike", "Parliament"),
            pets = listOf("dog", "snails", "fox", "horse", "zebra"),

            constraints = listOf(
                // 1. The Englishman lives in the red house
                SameHouse(attribute1 = Nationality("Englishman"), attribute2 = Color("red")),
                // 2. The Spaniard owns the dog
                SameHouse(attribute1 = Nationality("Spaniard"), attribute2 = Pet("dog")),
                // 3. Coffee is drunk in the green house
                SameHouse(attribute1 = Drink("coffee"), attribute2 = Color("green")),
                // 4. The Ukrainian drinks tea
                SameHouse(attribute1 = Nationality("Ukrainian"), attribute2 = Drink("tea")),
                // 5. The green house is immediately to the right of the ivory house
                RightOf(attributeLeft = Color("ivory"), attributeRight = Color("green")),
                // 6. The Old Gold smoker owns snails
                SameHouse(attribute1 = Smoke("Old Gold"), attribute2 = Pet("snails")),
                // 7. Kools are smoked in the yellow house
                SameHouse(attribute1 = Smoke("Kools"), attribute2 = Color("yellow")),
                // 8. Milk is drunk in the middle house
                PositionIs(attribute = Drink("milk"), position = 3),
                // 9. The Norwegian lives in the first house
                PositionIs(attribute = Nationality("Norwegian"), position = 1),
                // 10. The man who smokes Chesterfields lives in the house next to the man with the fox
                NextTo(attribute1 = Smoke("Chesterfield"), attribute2 = Pet("fox")),
                // 11. Kools are smoked in the house next to the house where the horse is kept
                NextTo(attribute1 = Smoke("Kools"), attribute2 = Pet("horse")),
                // 12. The Lucky Strike smoker drinks juice
                SameHouse(attribute1 = Smoke("Lucky Strike"), attribute2 = Drink("juice")),
                // 13. The Japanese smokes Parliaments
                SameHouse(attribute1 = Nationality("Japanese"), attribute2 = Smoke("Parliament")),
                // 14. The Norwegian lives next to the blue house
                NextTo(attribute1 = Nationality("Norwegian"), attribute2 = Color("blue"))
            ).shuffled()
        )

        // when
        val result = PuzzleSolver(input).solve()

        // then
        assertThat(result.houses).containsExactly(
            House(1, color = "yellow", nationality = "Norwegian", drink = "water", smoke = "Kools", pet = "fox"),
            House(2, color = "blue", nationality = "Ukrainian", drink = "tea", smoke = "Chesterfield", pet = "horse"),
            House(3, color = "red", nationality = "Englishman", drink = "milk", smoke = "Old Gold", pet = "snails"),
            House(4, color = "ivory", nationality = "Spaniard", drink = "juice", smoke = "Lucky Strike", pet = "dog"),
            House(5, color = "green", nationality = "Japanese", drink = "coffee", smoke = "Parliament", pet = "zebra"),
        )
    }
}