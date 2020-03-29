/*
 * Copyright 2015-2018 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package com.example.project

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

import java.math.BigInteger

@DisplayName("ALl the tests")
class ExercisesTests {

    @Nested
    @Disabled
    inner class Chapter3 {
        @Nested
        @DisplayName("Exercise 3.1")
        inner class Exercise301 {
            @Test
            fun returnedValueSameForComposedFunctionAndNestedApplication() {
                val f: (Int) -> Int = { x -> x + 1 }
                val g: (Int) -> Int = { x -> 2 * x }
                assertEquals(f(g(4)), compose(f, g)(4))
                assertEquals(g(f(4)), compose(g, f)(4))
            }
        }

        @Nested
        @DisplayName("Exercise 3.2")
        inner class Exercise302 {
            @Test
            fun returnedValueSameForComposedFunctionAndNestedApplicationForAnyType() {
                val f: (Int) -> Int = { x -> x + 1 }
                val g: (Int) -> String = { x -> x.toString() }
                val h: (String) -> Int = { x -> x.toInt() }
                assertEquals(g(f(4)), composeAny(g, f)(4))
                assertEquals(f(h("5")), composeAny(f, h)("5"))
            }
        }

        @Nested
        @DisplayName("Exercise 3.3")
        inner class Exercise303 {
            @Test
            fun resultOfAddingIsSum() {
                assertEquals(3 + 4, add(3)(4))
            }
        }

        @Nested
        @DisplayName("Exercise 3.4")
        inner class Exercise304 {
            @Test
            fun returnedValueSameForComposedFunctionAndNestedApplication() {
                val f: (Int) -> Int = { x -> x + 1 }
                val g: (Int) -> Int = { x -> 2 * x }
                assertEquals(f(g(4)), composeLambda(f)(g)(4))
                assertEquals(g(f(4)), composeLambda(g)(f)(4))
            }
        }

        @Nested
        @DisplayName("Exercise 3.5")
        inner class Exercise305 {
            @Test
            fun returnedValueSameForComposedFunctionAndNestedApplicationForAnyType() {
                val f: (Int) -> Int = { x -> x + 1 }
                val g: (Int) -> String = { x -> x.toString() }
                val h: (String) -> Int = { x -> x.toInt() }
                assertEquals(g(f(4)), lambdaGenericCompose<Int, Int, String>()(g)(f)(4))
                assertEquals(f(h("5")), lambdaGenericCompose<String, Int, Int>()(f)(h)("5"))
            }
        }

        @Nested
        @DisplayName("Exercise 3.6")
        inner class Exercise306 {
            @Test
            fun returnedValueSameForComposedFunctionAndNestedApplicationForAnyType() {
                val f: (Int) -> Int = { x -> x + 1 }
                val g: (Int) -> String = { x -> x.toString() }
                val h: (String) -> Int = { x -> x.toInt() }
                assertEquals(g(f(4)), lambdaGenericAndThen<Int, Int, String>()(f)(g)(4))
                assertEquals(f(h("5")), lambdaGenericAndThen<String, Int, Int>()(h)(f)("5"))
            }
        }

        @Nested
        @DisplayName("Exercise 3.7")
        inner class Exercise307 {
            @Test
            fun returnedValueSameForPartiallyAppliedAndFullyApplied() {
                val fnc: BinaryOp<Int, String, String> = { x -> { y -> x.toString() + y } }
                assertEquals(fnc(3)(" beers"), partialA(fnc, 3)(" beers"))
            }
        }

        @Nested
        @DisplayName("Exercise 3.8")
        inner class Exercise308 {
            @Test
            fun returnedValueSameForPartiallyAppliedAndFullyApplied() {
                val fnc: BinaryOp<Int, String, String> = { x -> { y -> x.toString() + y } }
                assertEquals(fnc(3)(" beers"), partialB(fnc, " beers")(3))
            }
        }

        @Nested
        @DisplayName("Exercise 3.10")
        inner class Exercise310 {
            @Test
            fun curriedAndUncurriedReturnTheSame() {
                val fnc: (Int, String) -> String = { x, y -> x.toString() + y }
                assertEquals(fnc(3, " beers"), curry(fnc)(3)(" beers"))
            }
        }

        @Nested
        @DisplayName("Exercise 3.11")
        inner class Exercise311 {
            @Test
            fun functionWithSwappedArgumentsReturnsTheSameAsOriginal() {
                val fnc: (Int) -> (String) -> String = { x -> { y -> x.toString() + y } }
                assertEquals(fnc(3)(" beers"), swapArgs(fnc)(" beers")(3))
            }
        }
    }

    @Nested
    @Disabled
    inner class Chapter4 {
        @Nested
        @DisplayName("Exercise 4.1")
        inner class Exercise401 {
            @Test
            fun isAddingPositiveIntegers() {
                assertEquals(234u + 758u, addCorecursive(234u, 758u))
            }
        }

        @Nested
        @DisplayName("Exercise 4.2")
        inner class Exercise402 {
            @Test
            fun factorialOfLessThan21Is1() {
                assertEquals(1u, factorial(0u))
                assertEquals(1u, factorial(1u))
            }

            @Test
            fun factorialOf2Is2() {
                assertEquals(2u, factorial(2u))
            }

            @Test
            fun calculatesFactorialForSmallNumber() {
                assertEquals(40320u, factorial(8u))
            }
        }

        @Nested
        @DisplayName("Exercise 4.3")
        inner class Exercise403 {
            @Test
            fun fibonacciOfLessThan21Is1() {
                assertEquals(BigInteger.valueOf(1), fibonacci(0u))
                assertEquals(BigInteger.valueOf(1), fibonacci(1u))
            }

            @Test
            fun calculatesNumberAtLowPositionInFibonacciSeries() {
                assertEquals(BigInteger.valueOf(55), fibonacci(9u))
            }

            @Test
            fun worksForBigNumber() {
                assertDoesNotThrow { fibonacci(100000u) }
            }
        }

        @Nested
        @DisplayName("Exercise 4.4")
        inner class Exercise404 {
            @Test
            fun returnsEmptyStringForEmptyListWhen() {
                assertEquals("", makeString(listOf<Int>(), ""))
            }

            @Test
            fun returnsStringOfSinleElement() {
                assertEquals("42", makeString(listOf<Int>(42), ""))
            }

            @Test
            fun joinsFewElementsWithEmptyDeliminer() {
                assertEquals("42314271", makeString(listOf(42, 314, 271), ""))
            }

            @Test
            fun joinsFewElementsWithNonEmptyDeliminer() {
                assertEquals("42:314:271", makeString(listOf(42, 314, 271), ":"))
            }

            @Test
            fun handlesBigList() {
                assertDoesNotThrow { makeString((100000 to 200000).toList(), ",") }
            }
        }

        @Nested
        @DisplayName("Exercise 4.5")
        inner class Exercise405 {
            @Test
            fun returnsEmptyStringForEmptyListWhen() {
                assertEquals("", makeStringFoldLeft(listOf<Int>(), ""))
            }

            @Test
            fun returnsStringOfSinleElement() {
                assertEquals("42", makeStringFoldLeft(listOf<Int>(42), ""))
            }

            @Test
            fun joinsFewElementsWithEmptyDeliminer() {
                assertEquals("42314271", makeStringFoldLeft(listOf(42, 314, 271), ""))
            }

            @Test
            fun joinsFewElementsWithNonEmptyDeliminer() {
                assertEquals("42:314:271", makeStringFoldLeft(listOf(42, 314, 271), ":"))
            }

            @Test
            fun handlesBigList() {
                assertDoesNotThrow { makeStringFoldLeft((100000 to 200000).toList(), ",") }
            }

            @Test
            fun sumAddsNumbers() {
                assertEquals(42 + 314 + 271, sumFoldLeft(listOf(42, 314, 271)))
            }

            @Test
            fun toStringAddsChars() {
                assertEquals("hello", toStringFoldLeft(listOf('h', 'e', 'l', 'l', 'o')))
            }
        }

        @Nested
        @DisplayName("Exercise 4.6")
        inner class Exercise406 {
            @Test
            fun toStringAddsChars() {
                assertEquals("hello", toStringFoldRight(listOf('h', 'e', 'l', 'l', 'o')))
            }
        }

        @Nested
        @DisplayName("Exercise 4.7")
        inner class Exercise407 {
            @Test
            fun reverseOfListIsReversedList() {
                assertEquals(listOf<Int>(42, 314, 271), reverseListConcat(listOf<Int>(271, 314, 42)))
            }
        }

        @Nested
        @DisplayName("Exercise 4.8")
        inner class Exercise408 {
            @Test
            fun reverseOfListIsReversedList() {
                assertEquals(listOf<Int>(42, 314, 271), reverse(listOf<Int>(271, 314, 42)))
            }
        }

        @Nested
        @DisplayName("Exercise 4.9")
        inner class Exercise409 {
            @Test
            fun rangeOfZeroLengthIsEmptyList() {
                assertEquals(listOf<Int>(), rangeLoop(4, 4))
            }

            @Test
            fun returnsCorrectRange() {
                assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), rangeLoop(0, 7))
            }
        }

        @Nested
        @DisplayName("Exercise 4.10")
        inner class Exercise410 {
            @Test
            fun rangeOfZeroLengthIsEmptyList() {
                assertEquals(listOf<Int>(), unfoldLoop(4, { it + 1 }, { it < 4 }))
            }

            @Test
            fun returnsCorrectRange() {
                assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), unfoldLoop(0, { it + 1 }, { it < 7 }))
            }

            @Test
            fun returnsCorrectRangeWithLargerStep() {
                assertEquals(listOf(0, 2, 4, 6), unfoldLoop(0, { it + 2 }, { it < 7 }))
            }
        }

        @Nested
        @DisplayName("Exercise 4.11")
        inner class Exercise411 {
            @Test
            fun rangeOfZeroLengthIsEmptyList() {
                assertEquals(listOf<Int>(), rangeUnfoldLoop(4, 4))
            }

            @Test
            fun returnsCorrectRange() {
                assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), rangeUnfoldLoop(0, 7))
            }

            @Test
            fun returnsEmptyListIfEndSmallerThanStart() {
                assertEquals(listOf<Int>(), rangeUnfoldLoop(9, 7))
            }

            @Test
            fun returnsListWithDifferentSignsOfStartAndEnd() {
                assertEquals(listOf(-3, -2, -1, 0, 1, 2, 3), rangeUnfoldLoop(-3, 4))
            }
        }

        @Nested
        @DisplayName("Exercise 4.12")
        inner class Exercise412 {
            @Test
            fun rangeOfZeroLengthIsEmptyList() {
                assertEquals(listOf<Int>(), range(4, 4))
            }

            @Test
            fun returnsCorrectRange() {
                assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), range(0, 7))
            }

            @Test
            fun returnsEmptyListIfEndSmallerThanStart() {
                assertEquals(listOf<Int>(), range(9, 7))
            }

            @Test
            fun returnsListWithDifferentSignsOfStartAndEnd() {
                assertEquals(listOf(-3, -2, -1, 0, 1, 2, 3), range(-3, 4))
            }
        }

        @Nested
        @DisplayName("Exercise 4.13")
        inner class Exercise413 {
            @Test
            fun rangeOfZeroLengthIsEmptyList() {
                assertEquals(listOf<Int>(), rangeUnfold(4, 4))
            }

            @Test
            fun returnsCorrectRange() {
                assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), rangeUnfold(0, 7))
            }

            @Test
            fun returnsEmptyListIfEndSmallerThanStart() {
                assertEquals(listOf<Int>(), rangeUnfold(9, 7))
            }

            @Test
            fun returnsListWithDifferentSignsOfStartAndEnd() {
                assertEquals(listOf(-3, -2, -1, 0, 1, 2, 3), rangeUnfold(-3, 4))
            }
        }

        @Nested
        @DisplayName("Exercise 4.15")
        inner class Exercise415 {
            @Test
            fun returnsStringOfFIbonacciNumbers() {
                val fibonacciNumbers = (0u until 10u).toList().map { fibonacci(it) }
                assertEquals(makeStringFoldLeft(fibonacciNumbers, ", "), fibonacciString(10u))
            }

            @Test
            fun returnsEmptyStringForArgumentZero() {
                assertEquals("", fibonacciString(0u))
            }

            @Test
            fun returnsStringWithOneNumberForArgumentOne() {
                assertEquals("1", fibonacciString(1u))
            }
        }

        @Nested
        @DisplayName("Exercise 4.16")
        inner class Exercise416 {
            @Test
            fun rangeOfZeroLengthIsEmptyList() {
                assertEquals(listOf<Int>(), rangeIterate(4, 4))
            }

            @Test
            fun returnsCorrectRange() {
                assertEquals(listOf(0, 1, 2, 3, 4, 5, 6), rangeIterate(0, 7))
            }

            @Test
            fun returnsEmptyListIfEndSmallerThanStart() {
                assertEquals(listOf<Int>(), rangeIterate(9, 7))
            }

            @Test
            fun returnsListWithDifferentSignsOfStartAndEnd() {
                assertEquals(listOf(-3, -2, -1, 0, 1, 2, 3), rangeIterate(-3, 4))
            }
        }

        @Nested
        @DisplayName("Exercise 4.17")
        inner class Exercise417 {
            @Test
            fun mapOfEmptyListIsEmptyList() {
                assertEquals(listOf<String>(), map(listOf<Int>()) { x: Int -> (2 * x + 1).toString() })
            }

            @Test
            fun returnsListOfMappedElements() {
                assertEquals(listOf("5", "7", "9"), map(listOf(2, 3, 4)) { x -> (2 * x + 1).toString() })
            }
        }


        @Nested
        @DisplayName("Exercise 4.18")
        inner class Exercise418 {
            @Test
            fun returnsStringOfFIbonacciNumbers() {
                val fibonacciNumbers = (0u until 10u).toList().map { fibonacci(it) }
                assertEquals(makeStringFoldLeft(fibonacciNumbers, ", "), fibonacciStringCorecursive(10))
            }

            @Test
            fun returnsEmptyStringForArgumentZero() {
                assertEquals("", fibonacciStringCorecursive(0))
            }

            @Test
            fun returnsStringWithOneNumberForArgumentOne() {
                assertEquals("1", fibonacciStringCorecursive(1))
            }
        }
    }

    @Nested
    inner class Chapter5 {
        @Test
        fun returnsCorrectStringRepresentationOfList() {
            assertEquals("[2, 3, 4, 5, NIL]", "${List(2,3,4,5)}")
        }
        @Test
        fun droppingZeroElementsReturnsTheSameList() {
            assertEquals("${List(2,3,4,5).drop(0)}", "${List(2,3,4,5)}")
        }
        @Test
        fun droppingSomeElementsReturnsTruncatedList() {
            assertEquals("${List(2,3,4,5).drop(2)}", "${List(4,5)}")
        }
        @Test
        fun droppingMoreElementsThanListHasReturnsEmptyList() {
            assertEquals(List(2,3,4,5).drop(5), List<Int>())
        }
        @Test
        fun droppingWhileConditionIsMet() {
            assertEquals("${List(2,3,4,5).dropWhile {it < 5} }", "${List(5)}")
        }
    }
}
