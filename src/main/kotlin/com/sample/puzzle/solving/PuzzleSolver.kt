package com.sample.puzzle.solving

import com.sample.puzzle.model.Attribute
import com.sample.puzzle.model.Constraint
import com.sample.puzzle.model.PuzzleInput
import com.sample.puzzle.model.PuzzleResult
import kotlin.math.abs

/**
 * PuzzleSolver is responsible for solving a specific puzzle based on a given set of constraints.
 * It processes and applies constraints to Solution
 */
class PuzzleSolver(private val input: PuzzleInput) {

    private val houseCount = input.houses

    fun solve(): PuzzleResult {
        val solution = solve(input.constraints, SolutionState(input))
        return PuzzleResult(houses = solution.solution())
    }

    private fun solve(constraints: List<Constraint>, solution: SolutionState): SolutionState {
        val remainingConstraints = applyConstraints(constraints, solution)

        if (solution.isSolved()) {
            return solution
        }

        if (remainingConstraints.isEmpty()) {
            throw IllegalStateException("No constraints left to check, but puzzle is not solved")
        }

        return findSolutionWithAssumption(remainingConstraints, solution)
    }

    private fun findSolutionWithAssumption(
        remainingConstraints: List<Constraint>,
        solution: SolutionState,
    ): SolutionState {
        return remainingConstraints.flatMap { remainingConstraint ->
            if (remainingConstraint !is Constraint.SameHouse) {
                return@flatMap emptyList()
            }

            solution.houses.filter { house ->
                solution.canHouseHaveAttributes(house, remainingConstraint.attribute1, remainingConstraint.attribute2)
            }.mapNotNull { houseWithUnsetAttribute ->
                val forkSolution = solution.copy()
                val house = forkSolution.getHouse(houseWithUnsetAttribute.position)
                forkSolution.setAttribute(remainingConstraint.attribute1, house)
                forkSolution.setAttribute(remainingConstraint.attribute2, house)
                runCatching {
                    solve(remainingConstraints - remainingConstraint, forkSolution)
                }.getOrNull()
            }
        }.firstOrNull() ?: run {
            throw IllegalStateException("Cannot solve the puzzle with the given constraints")
        }
    }

    /**
     * Apply constraints to the solution
     * @return not applied contains
     */
    private fun applyConstraints(
        constraints: List<Constraint>,
        solution: SolutionState,
    ): List<Constraint> {
        var remainingConstraints: List<Constraint> = constraints
        do {
            var changes = 0
            remainingConstraints = remainingConstraints.mapNotNull { constraint ->
                when (constraint) {
                    is Constraint.SameHouse -> {
                        if (applySameHouseConstraint(constraint, solution)) {
                            return@mapNotNull null
                        }
                        changes += pruneSameHouseConstraint(constraint, solution)
                    }
                    is Constraint.NextTo -> {
                        if (applyNextToConstraint(constraint, solution)) {
                            return@mapNotNull null
                        }
                        changes += pruneNextToConstraint(constraint, solution)
                    }
                    is Constraint.PositionIs -> {
                        applyPositionIsConstraint(constraint, solution)
                        return@mapNotNull null
                    }
                    is Constraint.RightOf -> {
                        if (applyRightOfConstraint(constraint, solution)) {
                            return@mapNotNull null
                        }
                        changes += pruneRightOfConstraint(constraint, solution)
                    }
                }
                return@mapNotNull constraint
            }
        } while (changes > 0)
        return remainingConstraints
    }

    private fun applySameHouseConstraint(constraint: Constraint.SameHouse, solution: SolutionState): Boolean {
        val house1 = solution.findHouse(constraint.attribute1)
        val house2 = solution.findHouse(constraint.attribute2)

        if (house1 == null && house2 == null) {
            return false
        }

        if (house1 != null && house2 != null) {
            if (house1.position != house2.position) {
                throw IllegalStateException(
                    "Attributes must be in the same house " +
                        "(position: ${house1.position} != ${house2.position}), constraint: $constraint"
                )
            }
        } else if (house1 != null) {
            solution.setAttribute(constraint.attribute2, house1)
        } else if (house2 != null) {
            solution.setAttribute(constraint.attribute1, house2)
        }
        return true
    }

    private fun pruneSameHouseConstraint(constraint: Constraint.SameHouse, solution: SolutionState): Int {
        return solution.removeEitherHouseAttribute(
            constraint.attribute1,
            constraint.attribute2
        ) + solution.removeEitherHouseAttribute(constraint.attribute2, constraint.attribute1)
    }

    private fun applyNextToConstraint(constraint: Constraint.NextTo, solution: SolutionState): Boolean {
        val house1 = solution.findHouse(constraint.attribute1)
        val house2 = solution.findHouse(constraint.attribute2)

        if (house1 == null && house2 == null) {
            return false
        }

        if (house1 != null && house2 != null) {
            if (abs(house1.position - house2.position) != 1) {
                throw IllegalStateException(
                    "Attributes must be next to each other " +
                        "(positions: ${house1.position} and ${house2.position}), constraint: $constraint"
                )
            }
            return true
        }

        return if (house1 != null) {
            applyNextToConstraintForHouse(house1, constraint.attribute2, solution)
        } else if (house2 != null) {
            applyNextToConstraintForHouse(house2, constraint.attribute1, solution)
        } else {
            false
        }
    }

    fun applyNextToConstraintForHouse(
        house1: SolutionState.HouseCandidate,
        attribute2: Attribute,
        solution: SolutionState,
    ): Boolean {
        val leftHouse = if (house1.position > 1) solution.getHouse(house1.position - 1) else null
        val rightHouse = if (house1.position < houseCount) solution.getHouse(house1.position + 1) else null

        if (leftHouse != null && (rightHouse?.hasValueOf(attribute2) ?: true)) {
            solution.setAttribute(attribute2, leftHouse)
            return true
        } else {
            if (rightHouse != null && (leftHouse?.hasValueOf(attribute2) ?: true)) {
                solution.setAttribute(attribute2, rightHouse)
                return true
            }
        }
        return false
    }

    private fun pruneNextToConstraint(constraint: Constraint.NextTo, solution: SolutionState): Int {
        var changes = solution.removeNeighborsHouseAttribute(constraint.attribute1, constraint.attribute2)
        changes += solution.removeNeighborsHouseAttribute(constraint.attribute2, constraint.attribute1)

        val house1 = solution.findHouse(constraint.attribute1)
        val house2 = solution.findHouse(constraint.attribute2)
        if (house1 == null && house2 == null) {
            return changes
        }
        if (house1 != null && house2 != null) {
            return changes
        }
        if (house2 != null) {
            changes += solution.removeNonNeighborsAttr(house2.position, constraint.attribute1)
        } else if (house1 != null) {
            changes += solution.removeNonNeighborsAttr(house1.position, constraint.attribute2)
        }
        return changes
    }

    private fun applyPositionIsConstraint(constraint: Constraint.PositionIs, solution: SolutionState) {
        val houseByPos = solution.getHouse(constraint.position)
        val houseWithAttr = solution.findHouse(constraint.attribute)

        if (houseWithAttr != null) {
            if (constraint.position != houseWithAttr.position) {
                throw IllegalStateException(
                    "Attributes must be in the same house " +
                        "(position: ${houseByPos.position} != ${houseWithAttr.position}), constraint: $constraint"
                )
            }
        } else {
            solution.setAttribute(constraint.attribute, houseByPos)
        }
    }

    private fun applyRightOfConstraint(constraint: Constraint.RightOf, solution: SolutionState): Boolean {
        val leftHouse = solution.findHouse(constraint.attributeLeft)
        val rightHouse = solution.findHouse(constraint.attributeRight)

        if (leftHouse == null && rightHouse == null) {
            return false
        }

        if (leftHouse != null && rightHouse != null) {
            if (leftHouse.position + 1 != rightHouse.position) {
                throw IllegalStateException(
                    "Attributes must be next to each other " +
                        "(positions: ${leftHouse.position} and ${rightHouse.position}), constraint: $constraint"
                )
            }
        } else if (leftHouse != null) {
            val newRightHouse = solution.getHouse(leftHouse.position + 1)
            solution.setAttribute(constraint.attributeRight, newRightHouse)
        } else if (rightHouse != null) {
            val newLeftHouse = solution.getHouse(rightHouse.position - 1)
            solution.setAttribute(constraint.attributeLeft, newLeftHouse)
        }
        return true
    }

    private fun pruneRightOfConstraint(constraint: Constraint.RightOf, solution: SolutionState): Int {
        return solution.removeAttributeFromRightNeighbors(constraint.attributeLeft, constraint.attributeRight)
    }
}
