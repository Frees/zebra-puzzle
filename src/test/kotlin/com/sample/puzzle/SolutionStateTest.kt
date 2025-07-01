package com.sample.puzzle

import com.sample.puzzle.model.Attribute
import com.sample.puzzle.model.House
import com.sample.puzzle.model.PuzzleInput
import com.sample.puzzle.solving.SolutionState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SolutionStateTest {

    private val input = PuzzleInput(
        colors = listOf("red", "green"),
        nationalities = listOf("Englishman", "Spaniard"),
        drinks = listOf("coffee", "tea"),
        smokes = listOf("Gold", "Kools"),
        pets = listOf("dog", "snails")
    )

    @Test
    fun `test initialization with empty houses`() {
        // when
        val solutionState = SolutionState(input)

        // then
        assertThat(solutionState.houses).hasSize(2)

        // Check first house
        val house1 = solutionState.getHouse(1)
        assertThat(house1.position).isEqualTo(1)
        assertThat(house1.colors).containsExactlyInAnyOrder("red", "green")
        assertThat(house1.nationalities).containsExactlyInAnyOrder("Englishman", "Spaniard")
        assertThat(house1.drinks).containsExactlyInAnyOrder("coffee", "tea")
        assertThat(house1.smokes).containsExactlyInAnyOrder("Gold", "Kools")
        assertThat(house1.pets).containsExactlyInAnyOrder("dog", "snails")

        // Check second house
        val house2 = solutionState.getHouse(2)
        assertThat(house2.position).isEqualTo(2)
        assertThat(house2.colors).containsExactlyInAnyOrder("red", "green")
        assertThat(house2.nationalities).containsExactlyInAnyOrder("Englishman", "Spaniard")
        assertThat(house2.drinks).containsExactlyInAnyOrder("coffee", "tea")
        assertThat(house2.smokes).containsExactlyInAnyOrder("Gold", "Kools")
        assertThat(house2.pets).containsExactlyInAnyOrder("dog", "snails")
    }

    @Test
    fun `test copy creates a deep copy`() {
        // given
        val original = SolutionState(input)

        // when
        val copy = original.copy()

        // then
        assertThat(copy.houses).hasSize(2)
        assertThat(copy.houses).isNotSameAs(original.houses)

        // Modify the copy and verify original is unchanged
        val attribute = Attribute.Color("red")
        val house = copy.getHouse(1)
        copy.setAttribute(attribute, house)

        assertThat(copy.getHouse(1).color).isEqualTo("red")
        assertThat(original.getHouse(1).color).isNull()
    }

    @Test
    fun `test setAttribute sets attribute and removes from other houses`() {
        // given
        val solutionState = SolutionState(input)

        // when
        val house1 = solutionState.getHouse(1)
        solutionState.setAttribute(Attribute.Color("red"), house1)

        // then
        assertThat(house1.color).isEqualTo("red")
        assertThat(solutionState.getHouse(2).colors).containsExactly("green")
    }

    @Test
    fun `test setAttribute throws exception when attribute already set to different value`() {
        // given
        val solutionState = SolutionState(input)
        val house = solutionState.getHouse(1)
        solutionState.setAttribute(Attribute.Color("red"), house)

        // when/then
        assertThrows<IllegalStateException> {
            solutionState.setAttribute(Attribute.Color("green"), house)
        }
    }

    @Test
    fun `test findHouse returns house with given attribute`() {
        // given
        val solutionState = SolutionState(input)
        val house1 = solutionState.getHouse(1)
        solutionState.setAttribute(Attribute.Color("red"), house1)

        // when
        val foundHouse = solutionState.findHouse(Attribute.Color("red"))

        // then
        assertThat(foundHouse?.position).isEqualTo(1)
    }

    @Test
    fun `test solution returns list of houses`() {
        // given
        val solutionState = SolutionState(input)

        // Set attributes for house 1
        val house1 = solutionState.getHouse(1)
        solutionState.setAttribute(Attribute.Color("red"), house1)
        solutionState.setAttribute(Attribute.Nationality("Englishman"), house1)
        solutionState.setAttribute(Attribute.Drink("coffee"), house1)
        solutionState.setAttribute(Attribute.Smoke("Gold"), house1)
        solutionState.setAttribute(Attribute.Pet("dog"), house1)
        // House 2 attributes are automatically set by constraints

        // when
        val solution = solutionState.solution()

        // then
        assertThat(solution).containsExactly(
            House(1, color = "red", nationality = "Englishman", drink = "coffee", smoke = "Gold", pet = "dog"),
            House(2, color = "green", nationality = "Spaniard", drink = "tea", smoke = "Kools", pet = "snails"),
        )
    }

    @Test
    fun `test isSolved returns true when all houses have single attributes`() {
        // given
        val solutionState = SolutionState(input)

        // Set attributes for house 1
        val house1 = solutionState.getHouse(1)
        solutionState.setAttribute(Attribute.Color("red"), house1)
        solutionState.setAttribute(Attribute.Nationality("Englishman"), house1)
        solutionState.setAttribute(Attribute.Drink("coffee"), house1)
        solutionState.setAttribute(Attribute.Smoke("Gold"), house1)
        solutionState.setAttribute(Attribute.Pet("dog"), house1)

        // when
        val solved = solutionState.isSolved()

        // then
        assertThat(solved).isTrue()
    }

    @Test
    fun `test removeAttribute removes attribute from house`() {
        // given
        val solutionState = SolutionState(input)
        val house = solutionState.getHouse(1)

        // when
        val removed = solutionState.removeAttribute(Attribute.Color("red"), house)

        // then
        assertThat(removed).isTrue()
        assertThat(house.colors).containsExactly("green")
    }

    @Test
    fun `test removeAttribute throws exception when removing last attribute`() {
        // given
        val solutionState = SolutionState(input)
        val house = solutionState.getHouse(1)

        // Manually set the house to have only one color
        house.colors = mutableSetOf("red")

        // when/then
        assertThrows<IllegalStateException> {
            solutionState.removeAttribute(Attribute.Color("red"), house)
        }
    }
}
