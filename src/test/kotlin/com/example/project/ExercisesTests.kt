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

import com.example.project.option.*
import com.example.project.result.Result
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.math.BigInteger
import kotlin.IllegalStateException
import com.example.project.option.sequence as option_sequence
import org.mockito.Mockito.*
import java.io.PrintStream

@DisplayName("ALl the tests")
class ExercisesTests {
    /*
    @Nested
   // @Disabled
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
   // @Disabled
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
   // @Disabled
    inner class Chapter5 {
        @Test
        fun returnsCorrectStringRepresentationOfList() {
            assertEquals("[2, 3, 4, 5, NIL]", "${LinkedList(2,3,4,5)}")
        }
        @Test
        fun droppingZeroElementsReturnsTheSameList() {
            assertEquals(LinkedList(2,3,4,5).drop(0), LinkedList(2,3,4,5))
        }
        @Test
        fun droppingSomeElementsReturnsTruncatedList() {
            assertEquals(LinkedList(2,3,4,5).drop(2), LinkedList(4,5))
        }
        @Test
        fun droppingMoreElementsThanListHasReturnsEmptyList() {
            assertEquals(LinkedList(2,3,4,5).drop(5), LinkedList<Int>())
        }
        @Test
        fun droppingWhileConditionIsMet() {
            assertEquals(LinkedList(2,3,4,5).dropWhile {it < 5}, LinkedList(5))
        }
        @Test
        fun initOfListIsListWithoutLastElement() {
            assertEquals("${LinkedList(2,3,4,5).init()}", "${LinkedList(2,3,4)}")
        }
        @Test
        fun returnsSumOfListElements() {
            assertEquals(2+3+4+5, LinkedList(2,3,4,5).sum())
        }
        @Test
        fun returnsSumOfListElementsWithFoldRight() {
            assertEquals(2+3+4+5, LinkedList(2,3,4,5).sumFoldRight())
        }
        @Test
        fun returnsSumOfListElementsWithCoFoldRight() {
            assertEquals(2+3+4+5, LinkedList(2,3,4,5).sumCoFoldRight())
        }
        @Test
        fun returnsProductOfListElements() {
            assertEquals(2*3*4*5, LinkedList(2,3,4,5).product())
        }
        @Test
        fun returnsListLength() {
            assertEquals(4, LinkedList(2,3,4,5).length())
        }
        @Test
        fun lengthIsStackSafe() {
            assertDoesNotThrow { LinkedList(0 to 100000).length() }
        }
        @Test
        fun concatReturnsConcatenatedList() {
            assertEquals(LinkedList(1,2,3,4,5,6,7), LinkedList(1,2,3,4).concat(LinkedList(5,6,7)))
        }
        @Test
        fun flattenedListIsConcatenationOfSublists() {
            assertEquals(LinkedList(1,2,3,4,5,6,7,8,9),
                    LinkedList.flatten(LinkedList(LinkedList(1,2,3), LinkedList(4,5,6), LinkedList(7,8,9))))
        }
        @Test
        fun time3returnsListOfElementsMultipliedBy3() {
            assertEquals(LinkedList(3,6,9,12), LinkedList(1,2,3,4).times3())
        }
        @Test
        fun doubleToStringReturnsListOfStringRepresentations() {
            assertEquals(LinkedList("1.0", "2.0", "3.0", "4.2"),
                    LinkedList(1.0, 2.0, 3.0, 4.2).doubleToString())
        }
        @Test
        fun filtersOutEvenElements() {
            assertEquals(LinkedList(1,3,5,7,9), LinkedList(1,2,3,4,5,6,7,9,10).filter { it % 2 != 0 })
        }
        @Test
        fun flatMapReturnsCorrectFlatList() {
            assertEquals(LinkedList(1,2,3,4,5,6,7,8), LinkedList(1,3,5,7).flatMap { LinkedList(it, it + 1)})
        }
    }

    @Nested
   // @Disabled
    inner class Chapter6 {
        @Test
        fun getOrElseOnNoneReturnsDefaultValue() {
            val default = 5
            assertEquals(default, (Option<Int>().getOrElse(default)))
        }
        @Test
        fun getOrElseOnSomeReturnsContainedValue() {
            val default = 5
            val stored = 4
            assertEquals(stored, (Option<Int>(stored).getOrElse(default)))
        }
        @Test
        fun mapReturnsNoneForNone() {
            assertEquals(Option<Int>(), Option<Int>().map { 2 * it })
        }
        @Test
        fun mapReturnsMappedSomeForSome() {
            assertEquals(Option<Int>(6), Option<Int>(3).map { 2 * it })
        }
        @Test
        fun returnsNoneWhenPredicateNotFulfilled() {
            assertEquals(Option<Int>(), Option(5).filter { it % 2 == 0 })
        }
        @Test
        fun returnsTheSameWhenPredicateIsFulfilled() {
            assertEquals(Option(4), Option(4).filter { it % 2 == 0 })
        }
        @Test
        fun returnsMeanOfListOfNumbers() {
            assertEquals(Option(3.5), mean(LinkedList(2.0,3.0,4.0,5.0)))
        }
        @Test
        fun returnsVarianceOfListOfNumbers() {
            assertEquals(Option(1.25), variance(LinkedList(2.0,3.0,4.0,5.0)))
        }
        @Test
        fun liftedFunctionReturnsSomeOfFunctionValue() {
            val myF: (Int) -> Int = { 3 * it }
            assertEquals(Option(myF(4)), lift(myF)(Option(4)))
        }
        @Test
        fun liftedFunctionReturnsNoneForArgumentNone() {
            val myF: (Int) -> Int = { 3 * it }
            assertEquals(Option<Int>(), lift(myF)(Option<Int>()))
        }
        @Test
        fun liftedFunctionReturnsNoneIfExceptionIsThrown() {
            val myF: (Int) -> Int = { throw IllegalThreadStateException(); 3 * it }
            assertEquals(Option<Int>(), lift(myF)(Option<Int>(4)))
        }
        @Test
        fun map3returnsSomeOfFunctionAppliedToArguments() {
            val f: (Int) -> (Int) -> (Int) -> Int = { x -> { y -> { z -> (2*x + y)*z}}}
            assertEquals(Option(f(1)(2)(3)), map3(Option(1), Option(2), Option(3), f))
        }
        @Test
        fun map3returnsNoneIfOneOfArgumentsIsNone() {
            val f: (Int) -> (Int) -> (Int) -> Int = { x -> { y -> { z -> (2*x + y)*z}}}
            assertEquals(Option<Int>(), map3(Option(1), Option(), Option(3), f))
        }
        @Test
        fun sequenceReturnsSomeOfListIfAllElementsAreSome() {
            assertEquals(Option(LinkedList(1,2,3)), option_sequence(LinkedList(Option(1), Option(2), Option(3))))
        }
        @Test
        fun sequenceReturnsNoneIfOneOfElementsIsNone() {
            assertEquals(Option<LinkedList<Int>>(), option_sequence(LinkedList(Option(1), Option<Int>(), Option(3))))
        }
        @Test
        fun traverseReturnsListOfMappedValues() {
            val parseWithRadix: (Int) -> (String) -> Int = { radix ->
                { string ->
                    Integer.parseInt(string, radix)
                }
            }
            val parse16 = hLift(parseWithRadix(16))
            assertEquals(Option(LinkedList(4,5,6,7,8,9,10,11)),
                    traverse(LinkedList("4","5","6","7","8","9","A","B"), parse16))
        }
    }

    @Nested
   // @Disabled
    inner class Chapter7 {
        @Test
        fun filterReturnsTheOriginalResultIfConditionIsFulfilled() {
            assertEquals(
                    com.example.project.result.Result(4),
                    com.example.project.result.Result(4).filter( {it % 2 == 0} ))
        }
        @Test
        fun filterReturnsFailureWithCorrectStringIfConditionNotFulfilled() {
            val message = "condition not met"
            assertEquals(
                    com.example.project.result.Result.failure<Int>(message),
                    com.example.project.result.Result(5).filter( {it % 2 == 0}, message ))
        }
        @Test
        fun existsReturnsTrueIfConditionIsFulfilled() {
            assertEquals(true, com.example.project.result.Result(4).exists {it % 2 == 0})
        }
        @Test
        fun existsReturnsFalseIfConditionNotFulfilled() {
            assertEquals(false, com.example.project.result.Result(5).exists {it % 2 == 0})
        }
        @Test
        fun failuresWithDifferentExceptionTypesAreNotEqual() {
            assertNotEquals(
                    com.example.project.result.Result.failure<Int>(Exception("exception")),
                    com.example.project.result.Result.failure<Int>(NullPointerException("exception")))
        }
    }

    @Nested
   // @Disabled
    inner class Chapter8 {
        @Test
        fun flattenListReturnsListOfValuesOfSuccess() {
            assertEquals(LinkedList(2,4,6,8),
                    flattenResult(
                            LinkedList(
                                    Result(2),
                                    Result(),
                                    Result(4),
                                    Result(6),
                                    Result.failure(NullPointerException("empyt pointer")),
                                    Result(8)
                            )))
        }

        @Test
        fun sequenceReturnsSuccessForListOfSuccesses() {
            val input = LinkedList(Result(2), Result(3), Result(4), Result(5))
            val expected = Result(LinkedList(2,3,4,5))
            assertEquals(expected, sequence(input))
        }

        @Test
        fun sequenceReturnsFailureForListContainingFailure() {
            val input = LinkedList(
                    Result(2),
                    Result.failure(NullPointerException()),
                    Result(4),
                    Result.failure(NullPointerException("no element 5")))
            val expected = Result.failure<Int>(NullPointerException())
            assertEquals(expected, sequence(input))
        }

        @Test
        fun sequenceReturnsFailureForListContainingFailureAndEmpty() {
            val input = LinkedList(
                    Result(2),
                    Result(),
                    Result(4),
                    Result.failure(NullPointerException("no element 5")))
            val expected = Result.failure<Int>(NullPointerException())
            assertEquals(expected, sequence(input))
        }

        @Test
        fun ssequenceReturnsEmptyForListContainingSuccessesAndEmpty() {
            val input = LinkedList(
                    Result(2),
                    Result(),
                    Result(4),
                    Result(5))
            val expected = Result<Int>()
            assertEquals(expected, sequence(input))
        }

        @Test
        fun lengthOfZippedListIsLengthOfShorterInput() {
            val list1 = LinkedList(2,3,4,5)
            val list2 = LinkedList(3,4,5,6,7,8)
            assertEquals(Math.min(list1.length(), list2.length()),
                    zipWith(list1, list2) { _ -> { b: Int -> b }}.length())
        }

        @Test
        fun zipWithSumFunctionReturnsListOfSums() {
            val list1 = LinkedList(2,3,4,5)
            val list2 = LinkedList(3,4,5,6)
            val expected = LinkedList(5,7,9,11)
            assertEquals(expected, zipWith(list1, list2) { a -> { b: Int -> a + b}})

        }

        @Test
        fun productReturnsListOfAllCombinations() {
            val list1 = LinkedList("a", "b", "c")
            val list2 = LinkedList("d", "e", "f")
            val expected = LinkedList("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf")
            assertEquals(expected, product(list1, list2) { a -> { b: String -> a + b}})
        }

        @Test
        fun unzipReturnsPairOfLists() {
            assertEquals(
                    Pair(LinkedList(2,3,4,5), LinkedList(5,6,7,8)),
                    unzip(LinkedList(Pair(2,5), Pair(3,6), Pair(4, 7), Pair(5, 8)))
            )
        }

        @Test
        fun getAtNegativeIndexReturnsFailure() {
            assertEquals(
                    Result.failure<Int>(IndexOutOfBoundsException()),
                    LinkedList(2, 3, 4, 5).getAt(-10))
        }

        @Test
        fun getAtTooLargeIndexReturnsFailure() {
            assertEquals(
                    Result.failure<Int>(IndexOutOfBoundsException()),
                    LinkedList(2, 3, 4, 5).getAt(10))
        }

        @Test
        fun getAtCorrectIndexReturnsSuccessWithTheValue() {
            assertEquals(
                    Result(4),
                    LinkedList(2, 3, 4, 5).getAt(2))
        }

        @Test
        fun getAtIndex0ReturnsSuccessWithTheValue() {
            assertEquals(
                    Result(2),
                    LinkedList(2, 3, 4, 5).getAt(0))
        }

        @Test
        fun getAtLastIndexReturnsSuccessWithTheValue() {
            assertEquals(
                    Result(5),
                    LinkedList(2, 3, 4, 5).getAt(3))
        }

        @Test
        fun getAtNextToLastIndexReturnsFailure() {
            assertTrue(LinkedList(2, 3, 4, 5).getAt(4) is Result.Failure<Int>)
        }

        @Test
        fun foldLeftWithZeroReturnsIdentityForEmptyList() {
            val zero = 0
            val identity = 1
            assertEquals(identity, LinkedList<Int>().foldLeft(identity, zero, { b, a -> a * b}).first)
        }

        @Test
        fun foldLeftWithZeroReturnsZeroElementForListContainingItOnly() {
            val zero = 0
            val identity = 1
            assertEquals(zero, LinkedList<Int>(zero).foldLeft(identity, zero, { b, a -> a * b}).first)
        }

        @Test
        fun foldLeftWithZeroReturnsFoldedValueForListContainingZeroElement() {
            val zero = 0
            val identity = 1
            val foldedValue = 6*0*9
            assertEquals(foldedValue, LinkedList<Int>(6,0,8).foldLeft(identity, zero, { b, a -> a * b}).first)
        }

        @Test
        fun foldLeftWithZeroReturnsFoldedValueForListNotContainingZeroElement() {
            val zero = 0
            val identity = 1
            val foldedValue = 6*7*8
            assertEquals(foldedValue, LinkedList<Int>(6,7,8).foldLeft(identity, zero, { b, a -> a * b}).first)
        }

        @Test
        fun foldLeftWithZeroIsShortCircuiting() {
            val zero = 0
            val identity = 1
            assertDoesNotThrow { LinkedList<Int>(6,7,0,8).foldLeft(identity, zero, { b, a ->
                if (a > 7) throw IllegalStateException()
                a * b
            }) }
        }

        @Test
        fun splitAtIndexInsideTheListSplitsIt() {
            assertEquals(Pair(LinkedList(2,3,4), LinkedList(5,6,7,8)), LinkedList(2,3,4,5,6,7,8).splitAt(3))
        }

        @Test
        fun splitAtZeroReturnsOriginalListAndEmptyOne() {
            val list = LinkedList(2,3,4,5,6,7,8)
            assertEquals(Pair(LinkedList<Int>(), list), list.splitAt(0))
        }

        @Test
        fun splitAtNegativeIndexReturnsOriginalListAndEmptyOne() {
            val list = LinkedList(2,3,4,5,6,7,8)
            assertEquals(Pair(LinkedList<Int>(), list), list.splitAt(0))
        }

        @Test
        fun splitIndexAfterLastReturnsEmptyAndOriginalOne() {
            val list = LinkedList(2,3,4,5,6,7,8)
            assertEquals(Pair(list, LinkedList<Int>()), list.splitAt(10))
        }

        @Test
        fun startsWithReturnsTrueIfReceiverStartsWithArgument() {
            assertTrue(LinkedList(2,3,4,5,6,7).startsWith(LinkedList(2,3,4)))
        }

        @Test
        fun startsWithReturnsFalseIfThereIsDifferentElement() {
            assertFalse(LinkedList(3,4,5,6).startsWith(LinkedList(3,4,6,5)))
        }

        @Test
        fun startsWithReturnsFailsIfReceiverIsSublistOfArgument() {
            assertFalse(LinkedList(2,3,4).startsWith(LinkedList(2,3,4,5,6)))
        }

        @Test
        fun hasSublistReturnsTrueIfArgumentIsSublistOfReceiver() {
            assertTrue(LinkedList(2,3,4,5,6,7).hasSublist(LinkedList(4,5,6)))
        }

        @Test
        fun hasSublistReturnsFalseIfThereIsNotMatchingElement() {
            assertFalse(LinkedList(2,3,4,5,6,7).hasSublist(LinkedList(3,4,6,5)))
        }

        @Test
        fun unfoldGeneratesList() {
            assertEquals(LinkedList(0,1,2,3,4,5,6,7,8,9), unfold(0) { i ->
                if (i < 10)
                    Option(Pair(i, i + 1))
                else
                    Option()

            })
        }

        @Test
        fun rangeGeneratesListOfNumbers() {
            assertEquals(LinkedList(2,3,4,5,6), myRange(2,7))
        }

        @Test
        fun existsDetectsElementPresent() {
            assertTrue(LinkedList(2,3,4,5,6).exists { it == 4 })
        }

        @Test
        fun existsDetectsElementAbsent() {
            assertFalse(LinkedList(2,3,4,5,6).exists { it == 42 })
        }

        @Test
        fun forAllDetectsThatAllElemetsAreEven() {
            assertTrue(LinkedList(2,4,6,8,10).forAll { it % 2 == 0 })
        }

        @Test
        fun forAllDetectsThatThereIsOddElement() {
            assertFalse(LinkedList(2,4,6,7,10).forAll { it % 2 == 0 })
        }

        @Test
        fun divideAtDepth1returnsListOfTwoLists() {
            assertEquals(2, LinkedList(2,3,4,5,6,7,8,9).divide(1).length)
        }

        @Test
        fun divideAtDepth1returnsSplitLists() {
            assertEquals(
                    LinkedList(LinkedList(2, 3, 4, 5), LinkedList(6, 7, 8, 9)),
                    LinkedList(2, 3, 4, 5, 6, 7, 8, 9).divide(1))
        }

        @Test
        fun divideAtDepth1returnsSplitListsWhenOriginalHasOddElemwnts() {
            assertEquals(
                    LinkedList(LinkedList(2, 3, 4), LinkedList(6, 7, 8, 9)),
                    LinkedList(2, 3, 4, 6, 7, 8, 9).divide(1))
        }

        @Test
        fun divideAtDepth2returnsListOfFourLists() {
            assertEquals(4, LinkedList(2,3,4,5,6,7,8,9).divide(2).length)
        }

        @Test
        fun divideAtDepth2returnsSplitLists() {
            assertEquals(
                    LinkedList(LinkedList(2, 3), LinkedList(4, 5), LinkedList(6, 7), LinkedList(8, 9)),
                    LinkedList(2, 3, 4, 5, 6, 7, 8, 9).divide(2))
        }

        @Test
        fun divideAtDepth2returnsSplitListsWhenOriginalHasOddElements() {
            assertEquals(
                    LinkedList(LinkedList(2), LinkedList(4, 5), LinkedList(6, 7), LinkedList(8, 9)),
                    LinkedList(2, 4, 5, 6, 7, 8, 9).divide(2))
        }

        @Test
        fun divideAtDepth1ofTwoElementListReturnsTwoOneElementLists() {
            assertEquals(
                    LinkedList(LinkedList(2), LinkedList(3)),
                    LinkedList(2,3).divide(1))
        }

        @Test
        fun divideAtDepth1ofOneElementListReturnsOneElementLists() {
            assertEquals(
                    LinkedList(LinkedList(2)),
                    LinkedList(2).divide(1))
        }

        @Test
        fun divideAtTooLargeDepthResutnsListOfOneElementLists() {
            assertEquals(
                    LinkedList(LinkedList(2), LinkedList(3), LinkedList(4), LinkedList(5), LinkedList(6)),
                    LinkedList(2,3,4,5,6).divide(4))
        }

        @Test
        fun keyPresentAfterAdding() {
            assertTrue(MyLinkedMap<Int, Int>().set(2, 3).set(3, 4).hasKey(2))
        }

        @Test
        fun newValueForKeyAfterReset() {
            assertEquals(Result(5), MyLinkedMap<Int, Int>().set(2, 3).set(3, 4).set(2 ,5).get(2))
        }

        @Test
        fun lengthOfKeysListIsCorrect() {
            assertEquals(3, LinkedList(2,3,4,5,6,7,8,9,10,11).groupBy { it % 3 }.keys().length)
        }

        @Test
        fun lengthOfValuesListIsCorrect() {
            assertEquals(3, LinkedList(2,3,4,5,6,7,8,9,10,11).groupBy { it % 3 }.values().length)
        }

        @Test
        fun groupByReturnsMapWithAllKeys() {
            assertTrue(LinkedList(2,3,4,5,6,7,8,9,10,11).groupBy { it % 3 }.keys().exists { it == 0 })
            assertTrue(LinkedList(2,3,4,5,6,7,8,9,10,11).groupBy { it % 3 }.keys().exists { it == 1 })
            assertTrue(LinkedList(2,3,4,5,6,7,8,9,10,11).groupBy { it % 3 }.keys().exists { it == 2 })
        }

        @Test
        fun groupByReturnsCorrectValueFOrGivenKey() {
            assertEquals(Result(LinkedList(2,5,8,11)), LinkedList(2,3,4,5,6,7,8,9,10,11).groupBy { it % 3 }.get(2))
        }
    }
*/
    @Nested
    inner class Chapter9 {

        @Test
        fun lazyReturnsCorrectValues() {

            val fooBar = mock(FooBar::class.java)

            val first = Lazy {
                fooBar.hello(1)
                true
            }
            val second = Lazy<Boolean> {
                fooBar.hello(2)
                throw IllegalStateException()
            }

            fun or(a: Lazy<Boolean>, b: Lazy<Boolean>): Boolean = if (a()) true else b()

            assertTrue(first() || second())
            assertTrue(first() || second())
            assertTrue(or(first, second))

            verify(fooBar, times(1)).hello(1)
            verify(fooBar, times(0)).hello(2)

        }

        @Test
        fun sequenceResultIsEscaping() {
            val fooBar = mock(FooBar::class.java)

            sequenceResult(LinkedList(
                    Lazy<Int> { val n = 1; fooBar.hello(n); n },
                    Lazy<Int> { val n = 2; fooBar.hello(n); n },
                    Lazy<Int> { val n = 3; fooBar.hello(n); n },
                    Lazy<Int> { val n = 4; fooBar.hello(n); throw NullPointerException(); n },
                    Lazy<Int> { val n = 5; fooBar.hello(n); n },
                    Lazy<Int> { val n = 6; fooBar.hello(n); n }
            ))()

            verify(fooBar, times(1)).hello(1)
            verify(fooBar, times(1)).hello(2)
            verify(fooBar, times(1)).hello(3)
            verify(fooBar, times(1)).hello(4)
            verify(fooBar, times(0)).hello(5)
            verify(fooBar, times(0)).hello(6)

        }

        @Test
        fun forEachDoesNotEvaluateIfValueNotNeeded() {
            val fooBar = mock(FooBar::class.java)

            val emptyFunFooBar: (FooBar) -> Unit = { }
            val emptyFunVoid: () -> Unit = { }

            Lazy<FooBar> {
                fooBar.hello(1);
                fooBar
            }
                    .forEach(true, emptyFunVoid, emptyFunFooBar)
            verify(fooBar, times(0)).hello(1)
        }

        @Test
        fun takeAtMostWorksForHugeStream() {
            assertDoesNotThrow { Stream.repeat { 4 }.takeAtMost(100000) }
        }

        @Test
        fun takeAtMostWorksForHugeStreamEvenWhenConvertedToList() {
            assertDoesNotThrow { Stream.repeat { 4 }.takeAtMost(100000).toList() }
        }

        @Test
        fun dropAtMostWorksForHugeStream() {
            assertDoesNotThrow { Stream.repeat { 4 }.dropAtMost(100000) }
        }

        @Test
        fun streamIsConvertedToList() {
            assertEquals(LinkedList(3, 4, 5, 6, 7, 8), Stream.from(3).takeAtMost(6).toList())
        }

        @Test
        fun iterateReturnsCorrectStream() {
            assertEquals(
                    LinkedList(2, 4, 8, 16, 32, 64, 128),
                    Stream.iterate(2) { 2 * it }.takeAtMost(7).toList())
        }

        @Test
        fun takeWhileReturnsCorrectStream() {
            assertEquals(
                    LinkedList(2,4,8,16,32),
                    Stream.iterate(2) { 2 * it }.takeWhile { it < 64 }.toList()
            )
        }

        @Test
        fun dropWhileReturnsCorrectStream() {
            assertEquals(
                    LinkedList(64,128,256,512,1024,2048),
                    Stream.iterate(2) { 2 * it }.dropWhile { it < 64 }.takeAtMost(6).toList()
            )
        }

    }
}

interface FooBar {
    fun hello(n: Int)
}