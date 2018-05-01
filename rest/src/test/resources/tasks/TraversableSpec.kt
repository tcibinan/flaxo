package org.flaxo.examples

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Denis Verkhoturov, mod.satyr@gmail.com
 */
object TraversableSpec : Spek({
    describe("a traversable") {
        on ("initialization") {
            it("should throw NullPointerException") {
                assertFailsWith<NullPointerException> { Traversable.from<Nothing>(null) }
            }

            it("should work fine with empty list") {
                Traversable.from(emptyList<String>())
            }
        }

        given("initialized traversable") {
            val traversable = Traversable.from(listOf("cat", "close", "camp", "airplane"))

            on("viewing as a list") {
                it("should contains every single element") {
                    assertEquals(listOf("cat", "close", "camp", "airplane"), traversable.toList())
                }
            }

            on("filtering") {
                it("should fails with NullPointerException when predicate is null") {
                    assertFailsWith<NullPointerException> {
                        Traversable.from(emptyList<String>()).filter(null)
                    }
                }

                it("should pass only elements that satisfy predicate") {
                    val filtered = traversable.filter({ word -> word.startsWith("a") })
                    assertEquals(listOf("airplane"), filtered.toList())
                }

                it("should return empty traversable if no elements satisfy predicate") {
                    val filtered = traversable.filter({ false })
                    assertEquals(emptyList<String>(), filtered.toList())
                }
            }

            on("mapping") {
                it("should throw NullPointerException if mapper function is null") {
                    assertFailsWith<NullPointerException> {
                        Traversable.from(emptyList<String>()).map<Nothing>(null)
                    }
                }

                it("should return traversable containing all elements mapped with function") {
                    val mapped = traversable.map({ word -> word.length })
                    assertEquals(listOf(3, 5, 4, 8), mapped.toList())
                }

                it("should works fine with mapper function that returns null as result") {
                    val mapped = traversable.map({ null })
                    assertEquals(listOf(null, null, null, null), mapped.toList())
                }
            }

            on("flatten mapping") {
                it("should throw NullPointerException if mapper function is null") {
                    assertFailsWith<NullPointerException> {
                        Traversable.from(emptyList<String>()).flatMap<Nothing>(null)
                    }
                }

                it("should return traversable containing all elements mapped with function") {
                    val mapped = traversable.flatMap({ word -> (0..word.length - 1).map { word[it] } })
                    assertEquals(
                            listOf(
                                    'c', 'a', 't',
                                    'c', 'l', 'o', 's', 'e',
                                    'c', 'a', 'm', 'p',
                                    'a', 'i', 'r', 'p', 'l', 'a', 'n', 'e'
                            ),
                            mapped.toList()
                    )
                }

                it("should work fine with mapper function that returns less elements than was initially") {
                    val mapped = traversable.flatMap({
                        word -> (0..word.length - 1).filter { it > 5 }
                            .map { word[it] }
                    })
                    assertEquals(listOf('n', 'e'), mapped.toList())
                }
            }
        }
    }
})