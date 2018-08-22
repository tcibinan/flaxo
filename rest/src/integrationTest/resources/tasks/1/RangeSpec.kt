package org.flaxo.examples

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author Denis Verkhoturov, mod.satyr@gmail.com
 */
object RangeSpec : Spek({
    val smallestRangeLowerBound = 42
    val smallestRangeUpperBound = 47
    val greatestRangeLowerBound = 48
    val greatestRangeUpperBound = 53

    describe("range") {
        on("initialization") {
            it("should throw IllegalArgumentException if lower bound is greater than upper one") {
                assertFailsWith<IllegalArgumentException> { Range.bounds(smallestRangeUpperBound, smallestRangeLowerBound) }
            }

            it("should be able to instantiate range of one element if lower bound equals upper one") {
                assertTrue { Range.bounds(smallestRangeLowerBound, smallestRangeLowerBound).contains(smallestRangeLowerBound) }
            }

            it("should be able to have Integer.MIN_VALUE as lower bound and Integer.MAX_VALUE as upper bound") {
                val range = Range.bounds(Integer.MIN_VALUE, Integer.MAX_VALUE)
                assertEquals(range.lowerBound(), Integer.MIN_VALUE)
                assertEquals(range.upperBound(), Integer.MAX_VALUE)
            }

        }

        given("initialized ranges") {
            val smallest = Range.bounds(smallestRangeLowerBound, smallestRangeUpperBound)
            val greatest = Range.bounds(greatestRangeLowerBound, greatestRangeUpperBound)
            val overlapping = Range.bounds(smallestRangeUpperBound, greatestRangeLowerBound)

            it("should contain lower and upper bounds as an element") {
                assertTrue { smallest.contains(smallestRangeLowerBound) }
                assertTrue { smallest.contains(smallestRangeUpperBound) }
            }

            it("should not contain element if it is out of bounds") {
                assertFalse { overlapping.contains(smallestRangeLowerBound) }
                assertFalse { overlapping.contains(greatestRangeUpperBound) }
            }

            on("ordering") {
                it("should be before another if its upper bound is less than lower bound of another range") {
                    assertTrue { smallest.isBefore(greatest) }
                }

                it("should not be before another if its upper bound is equal to or greater than lower bound of another range") {
                    assertFalse { greatest.isBefore(smallest) }
                }

                it("should not be before another if ranges have overlapping elements") {
                    assertFalse { overlapping.isBefore(greatest) }
                }

                it("should be after another if its lower bound is greater than upper bound of another range") {
                    assertTrue { greatest.isAfter(smallest) }
                }

                it("should not be after another if its lower bound is equal to or less than upper bound of another range") {
                    assertFalse { smallest.isAfter(greatest) }
                }

                it("should not be after another if ranges have overlapping elements") {
                    assertFalse { greatest.isAfter(overlapping) }
                }

                it("should be concurrent if ranges have common elements") {
                    assertTrue { smallest.isConcurrent(overlapping) }
                    assertTrue { greatest.isConcurrent(overlapping) }
                }

                it("should not be concurrent if ranges have no common elements") {
                    assertFalse { smallest.isConcurrent(greatest) }
                    assertFalse { greatest.isConcurrent(smallest) }
                }

                it("should be concurrent to itself") {
                    assertTrue { smallest.isConcurrent(smallest) }
                    assertTrue { greatest.isConcurrent(greatest) }
                    assertTrue { overlapping.isConcurrent(overlapping) }
                }
            }

            on("viewing as a list") {
                it("should contain every single value") {
                    assertEquals(smallest.asList(), (smallestRangeLowerBound..smallestRangeUpperBound).toList())
                }
            }

            on("taking an iterator") {
                val iterator = smallest.asIterator()

                it("should have next") {
                    assertTrue { smallest.asIterator().hasNext() }
                }

                it("should throw NoSuchElementException if has no next value") {
                    val elements = (smallestRangeLowerBound..smallestRangeUpperBound).toList()
                    while (iterator.hasNext()) {
                        assertTrue { elements.contains(iterator.next()) }
                    }
                    assertFailsWith<NoSuchElementException> { iterator.next() }
                }
            }
        }
    }
})