package com.sample.puzzle.solving

import com.sample.puzzle.model.Attribute
import com.sample.puzzle.model.House
import com.sample.puzzle.model.PuzzleInput

class SolutionState(
    val input: PuzzleInput,
    val houses: MutableList<HouseCandidate> = mutableListOf(),
) {
    init {
        if (houses.isEmpty()) {
            repeat(input.houses) {
                houses.add(
                    HouseCandidate(
                        position = it + 1,
                        colors = HashSet(input.colors),
                        nationalities = HashSet(input.nationalities),
                        drinks = HashSet(input.drinks),
                        smokes = HashSet(input.smokes),
                        pets = HashSet(input.pets)
                    )
                )
            }
        }
    }

    fun copy(): SolutionState {
        return SolutionState(
            houses = houses.map { house ->
                HouseCandidate(
                    position = house.position,
                    colors = HashSet(house.colors),
                    nationalities = HashSet(house.nationalities),
                    drinks = HashSet(house.drinks),
                    smokes = HashSet(house.smokes),
                    pets = HashSet(house.pets)
                )
            }.toMutableList(),
            input = input
        )
    }

    fun solution(): List<House> =
        houses.map { it.toHouse() }.toList()

    fun findHouse(attr: Attribute): HouseCandidate? =
        houses.firstOrNull { house -> house.valueOf(attr) == attr.attrValue() }

    fun getHouse(position: Int): HouseCandidate =
        houses[position - 1]

    fun setAttribute(attr: Attribute, house: HouseCandidate) {
        house.valueOf(attr)?.let { houseAttrValue ->
            if (houseAttrValue != attr.attrValue()) {
                throw IllegalStateException("House ${house.position} already has attr: $attr with value: $houseAttrValue")
            }
            return
        }
        house.setValue(attr)

        // remove the attribute from all other houses
        houses
            .filter { it.position != house.position }
            .forEach { otherHouse -> removeAttribute(attr, otherHouse) }
    }

    fun removeAttribute(attr: Attribute, house: HouseCandidate): Boolean {
        val removed = house.removeValue(attr)
        if (removed) {
            // chek that after removing there is only one value need to remove it from others
            val remainingCount = house.availableValuesOf(attr).size
            if (remainingCount == 0) {
                throw IllegalStateException("House ${house.position} cannot have empty attribute: $attr")
            }
            if (remainingCount == 1) {
                // attribute can be only on this house
                // remove the attribute from all other houses
                house.valueOf(attr)?.let { currentValue ->
                    attr.withValue(currentValue)
                }?.also { remainAttrValue ->
                    houses
                        .filter { it.position != house.position }
                        .forEach { otherHouse -> removeAttribute(remainAttrValue, otherHouse) }
                }
            }
        }
        return removed
    }

    // if there is a house with set attribute not equal to the given attribute,
    // remove the another from that house
    fun removeEitherHouseAttribute(attr: Attribute, removeAttr: Attribute): Int =
        houses.filter { house ->
            house.valueOf(attr)?.let { currentValue -> currentValue != attr.attrValue() } ?: false
        }.filter { house ->
            removeAttribute(removeAttr, house)
        }.size


    // if there is a house with a set attribute not equal to the given attribute,
    // remove the another from all neighbors of that house
    fun removeNeighborsHouseAttribute(attr1: Attribute, attr2: Attribute): Int {
        val firstHouse = getHouse(1)
        var removed = 0
        firstHouse.valueOf(attr1)
            ?.takeIf { it != attr1.attrValue() }
            ?.run {
                val nextToFirst = getHouse(2)
                if (removeAttribute(attr2, nextToFirst)) removed++
            }

        val lastHouse = getHouse(input.houses)
        lastHouse.valueOf(attr2)
            ?.takeIf { it != attr2.attrValue() }
            ?.run {
                val nextToLast = getHouse(input.houses - 1)
                if (removeAttribute(attr1, nextToLast)) removed++
            }
        return removed
    }


    fun removeNonNeighborsAttr(position: Int, attr: Attribute): Int =
        houses
            .filter { it.position != position }
            .filter { it.position != position + 1 }
            .filter { it.position != position - 1 }
            .filter { removeAttribute(attr, it) }
            .size

    // if there is a house with a set attribute not equal to the given attribute,
    // remove the another from all neighbors of that house
    fun removeAttributeFromRightNeighbors(leftAttr: Attribute, rightAttr: Attribute): Int {
        val firstHouse = getHouse(1)
        removeAttribute(rightAttr, firstHouse)

        val lastHouse = getHouse(input.houses)
        removeAttribute(leftAttr, lastHouse)

        return houses.filter { house ->
            house.valueOf(rightAttr)?.let { currentValue -> currentValue != rightAttr.attrValue() } ?: false
        }.filter { house ->
            house.position > 1
        }.filter { house ->
            val leftHouse = getHouse(house.position - 1)
            val removeLeft = removeAttribute(leftAttr, leftHouse)
            var removeRight = false
            if (house.position < input.houses) {
                val rightHouse = getHouse(house.position + 1)
                removeRight = removeAttribute(rightAttr, rightHouse)
            }
            removeLeft || removeRight
        }.size
    }

    fun isSolved(): Boolean {
        return houses.all { house ->
            house.colors.size == 1 &&
                house.nationalities.size == 1 &&
                house.drinks.size == 1 &&
                house.smokes.size == 1 &&
                house.pets.size == 1
        }
    }

    fun canHouseHaveAttributes(house: HouseCandidate, attr1: Attribute, attr2: Attribute): Boolean {
        return house.availableValuesOf(attr1).contains(attr1.attrValue()) &&
            house.availableValuesOf(attr2).contains(attr2.attrValue())
    }

    data class HouseCandidate(
        val position: Int,
        var colors: MutableSet<String>,
        var nationalities: MutableSet<String>,
        var drinks: MutableSet<String>,
        var smokes: MutableSet<String>,
        var pets: MutableSet<String>,
    ) {
        val color: String?
            get() = colors.takeIf { it.size == 1 }?.firstOrNull()

        val nationality: String?
            get() = nationalities.takeIf { it.size == 1 }?.firstOrNull()

        val drink: String?
            get() = drinks.takeIf { it.size == 1 }?.firstOrNull()

        val smoke: String?
            get() = smokes.takeIf { it.size == 1 }?.firstOrNull()

        val pet: String?
            get() = pets.takeIf { it.size == 1 }?.firstOrNull()


        fun toHouse(): House {
            return House(
                position = position,
                color = color ?: "*",
                nationality = nationality ?: "*",
                drink = drink ?: "*",
                smoke = smoke ?: "*",
                pet = pet ?: "*",
            )
        }

        fun availableValuesOf(attr: Attribute): MutableSet<String> = when (attr) {
            is Attribute.Color -> colors
            is Attribute.Nationality -> nationalities
            is Attribute.Drink -> drinks
            is Attribute.Smoke -> smokes
            is Attribute.Pet -> pets
        }

        fun valueOf(attr: Attribute): String? = when (attr) {
            is Attribute.Color -> color
            is Attribute.Nationality -> nationality
            is Attribute.Drink -> drink
            is Attribute.Smoke -> smoke
            is Attribute.Pet -> pet
        }

        fun hasValueOf(attr: Attribute): Boolean = valueOf(attr) != null

        fun setValue(attr: Attribute) {
            when (attr) {
                is Attribute.Color -> colors = mutableSetOf(attr.value)
                is Attribute.Nationality -> nationalities = mutableSetOf(attr.value)
                is Attribute.Drink -> drinks = mutableSetOf(attr.value)
                is Attribute.Smoke -> smokes = mutableSetOf(attr.value)
                is Attribute.Pet -> pets = mutableSetOf(attr.value)
            }
        }

        fun removeValue(attr: Attribute): Boolean {
            return when (attr) {
                is Attribute.Color -> colors.remove(attr.value)
                is Attribute.Nationality -> nationalities.remove(attr.value)
                is Attribute.Drink -> drinks.remove(attr.value)
                is Attribute.Smoke -> smokes.remove(attr.value)
                is Attribute.Pet -> pets.remove(attr.value)
            }
        }
    }
}