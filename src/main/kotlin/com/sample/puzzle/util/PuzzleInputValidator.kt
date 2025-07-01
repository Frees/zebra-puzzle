package com.sample.puzzle.util

import com.sample.puzzle.model.Attribute
import com.sample.puzzle.model.Constraint
import com.sample.puzzle.model.PuzzleInput

object PuzzleInputValidator {

    fun validate(input: PuzzleInput) {
        validateHousesCount(input)
        validateAttributeLists(input)
        validateConstraintAttributes(input)
    }

    private fun validateHousesCount(input: PuzzleInput) {
        require(input.houses > 1) { "Number of houses must be greater than 1" }
    }

    private fun validateAttributeLists(input: PuzzleInput) {
        require(input.colors.distinct().size == input.houses) {
            "Number of colors must match number of houses"
        }
        require(input.nationalities.distinct().size == input.houses) {
            "Number of nationalities must match number of houses"
        }
        require(input.drinks.distinct().size == input.houses) {
            "Number of drinks must match number of houses"
        }
        require(input.smokes.distinct().size == input.houses) {
            "Number of smokes must match number of houses"
        }
        require(input.pets.distinct().size == input.houses) {
            "Number of pets must match number of houses"
        }
    }

    private fun validateConstraintAttributes(input: PuzzleInput) {
        input.constraints.forEach { constraint ->
            when (constraint) {
                is Constraint.SameHouse -> {
                    validateAttribute(constraint.attribute1, input)
                    validateAttribute(constraint.attribute2, input)
                }

                is Constraint.NextTo -> {
                    validateAttribute(constraint.attribute1, input)
                    validateAttribute(constraint.attribute2, input)
                }

                is Constraint.PositionIs -> {
                    validateAttribute(constraint.attribute, input)
                    require(constraint.position in 1..input.houses) {
                        "Position ${constraint.position} is out of range (1..${input.houses})"
                    }
                }

                is Constraint.RightOf -> {
                    validateAttribute(constraint.attributeLeft, input)
                    validateAttribute(constraint.attributeRight, input)
                }
            }
        }
    }

    private fun validateAttribute(attribute: Attribute, input: PuzzleInput) {
        val value = attribute.attrValue()
        when (attribute) {
            is Attribute.Color -> require(value in input.colors) {
                "Color '$value' is not in the list of colors: ${input.colors}"
            }

            is Attribute.Nationality -> require(value in input.nationalities) {
                "Nationality '$value' is not in the list of nationalities: ${input.nationalities}"
            }

            is Attribute.Drink -> require(value in input.drinks) {
                "Drink '$value' is not in the list of drinks: ${input.drinks}"
            }

            is Attribute.Smoke -> require(value in input.smokes) {
                "Smoke '$value' is not in the list of smokes: ${input.smokes}"
            }

            is Attribute.Pet -> require(value in input.pets) {
                "Pet '$value' is not in the list of pets: ${input.pets}"
            }
        }
    }
}
