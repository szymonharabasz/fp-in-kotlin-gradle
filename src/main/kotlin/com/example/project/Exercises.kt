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

import java.lang.IllegalArgumentException
import java.math.BigInteger

typealias UnaryOp<T, U> = (T) -> U
typealias BinaryOp<T, U, R> = (T) -> (U) -> R
fun <T> kotlin.collections.List<T>.head(): T =
        if (this.isEmpty())
            throw IllegalArgumentException("head called on empty list")
        else
            this[0]

fun <T> kotlin.collections.List<T>.tail(): kotlin.collections.List<T> =
        if (this.isEmpty())
            throw IllegalArgumentException("tail called on empty list")
        else
            this.drop(1)



// Exercise 3.1
fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { x -> f(g(x))}

// Exercise 3.2
fun <T, U, V>composeAny(f: (U) -> V, g: (T) -> U): (T) -> V = { x -> f(g(x))}

// Exercise 3.3
val add: (Int) -> (Int) -> Int = { a -> { b -> a + b}}

// Exercise 3.4
typealias UnaryIntOp = (Int) -> Int
val composeLambda: (UnaryIntOp) -> (UnaryIntOp) -> UnaryIntOp = { f -> { g -> { x -> f(g(x))}}}

// Exercise 3.5
fun <T, U, V>lambdaGenericCompose() = { f:UnaryOp<U, V> -> { g:UnaryOp<T, U> -> { x:T -> f(g(x)) } } }

// Exercise 3.6
fun <T, U, V>lambdaGenericAndThen() = { f:UnaryOp<T, U> -> { g:UnaryOp<U, V> -> { x:T -> g(f(x)) } } }

// Exercise 3.7
fun <T, U, V>partialA(f: BinaryOp<T, U, V>, a: T): UnaryOp<U, V> = f(a)

// Exercise 3.8
fun <T, U, V>partialB(f: BinaryOp<T, U, V>, b: U): UnaryOp<T, V> = { x -> f(x)(b) }

// Exercise 3.10
fun <T, U, V>curry(f: (T, U) -> V): BinaryOp<T, U, V> = { x -> { y -> f(x,y) }}

// Exercise 3.11
fun <T, U, V>swapArgs(f: BinaryOp<T, U, V>): BinaryOp<U, T, V> = { x -> { y -> f(y)(x)}}

// Exercise 4.1
fun inc(n: UInt) = n + 1u
fun dec(n: UInt) = n - 1u
fun addCorecursive(a: UInt, b: UInt): UInt {
    tailrec fun corecursion(a: UInt, b: UInt): UInt = if (b == 0u) a else corecursion(inc(a), dec(b))

    return corecursion(a, b)
}

// Exercise 4.2
fun factorialProducer(): (UInt) -> UInt {
    lateinit var fact: (UInt) -> UInt
    fact =
    { n -> if(n > 1u) n * fact(dec(n)) else 1u }
    return fact
}
val factorial = factorialProducer()

// Exercise 4.3
fun fibonacci(n: UInt): BigInteger {
    tailrec fun go(m: UInt, prev: BigInteger, nextPrev: BigInteger): BigInteger =
        if (m <= 1u) prev else go(dec(m), prev.add(nextPrev), prev)

    return go(n, BigInteger.valueOf(1), BigInteger.valueOf(1))
}

// Exercise 4.4
fun <T>makeString(list: kotlin.collections.List<T>, delim: String): String {
    fun append(acc: String, elem: T) = if (acc.isEmpty()) "$elem" else "$acc$delim$elem"
    tailrec fun go(list: kotlin.collections.List<T>, acc: String): String = when {
        list.isEmpty() -> acc
        list.tail().isEmpty() -> append(acc, list.head())
        else -> go(list.tail(), append(acc, list.head()))
    }
    return go(list, "")
}

// Exercise 4.5
fun <T, U> kotlin.collections.List<T>.foldLeft(start: U, f: (T, U) -> U): U {
    tailrec fun go(list: kotlin.collections.List<T>, acc: U): U = when {
        list.isEmpty() -> acc
        list.tail().isEmpty() -> f(list.head(), acc)
        else -> go(list.tail(), f(list.head(), acc))
    }
    return go(this, start)
}
fun <T> makeStringFoldLeft(list: kotlin.collections.List<T>, delim: String): String = list.foldLeft("") {
    elem, acc -> if (acc.isEmpty()) "$elem" else "$acc$delim$elem" }
fun sumFoldLeft(list: kotlin.collections.List<Int>) = list.foldLeft(0) { a, b -> a + b }
fun toStringFoldLeft(list: kotlin.collections.List<Char>) = makeStringFoldLeft(list, "")

// Exercise 4.6
fun <T, U> kotlin.collections.List<T>.foldRight(start: U, f: (T, U) -> U): U = when {
    this.isEmpty() -> start
    this.tail().isEmpty() -> f(this.head(), start)
    else -> f(this.head(), this.tail().foldRight(start, f))
}
fun toStringFoldRight(list: kotlin.collections.List<Char>) = list.foldRight("") { char, acc -> "$char$acc"}

// Exercise 4.7
fun <T> prependListContat(list: kotlin.collections.List<T>, elem: T): kotlin.collections.List<T> = listOf(elem) + list
fun <T> reverseListConcat(list: kotlin.collections.List<T>): kotlin.collections.List<T> = list.foldLeft(listOf<T>()) { elem, list -> prependListContat(list, elem) }

// Exercise 4.8
fun <T> prepend(list: kotlin.collections.List<T>, elem: T): kotlin.collections.List<T> = list.foldLeft(listOf(elem)) { e, l -> l + e }
fun <T> reverse(list: kotlin.collections.List<T>): kotlin.collections.List<T> = list.foldLeft(listOf<T>()) { elem, list -> prepend(list, elem) }

// Exercise 4.9
fun rangeLoop(start: Int, end: Int): kotlin.collections.List<Int> {
    var x = start
    var list = listOf<Int>()
    while (x < end) {
        list = list + x++
    }
    return list
}

// Exercise 4.10
fun <T> unfoldLoop(seed: T, f: (T) -> T, p: (T) -> Boolean): kotlin.collections.List<T> {
    var x = seed
    var list = listOf<T>()
    while (p(x)) {
        list = list + x
        x = f(x)
    }
    return list
}
fun rangeUnfoldLoop(start: Int, end: Int): kotlin.collections.List<Int> = unfoldLoop(start, { it + 1 }, { it < end })

// Exercise 4.12
fun range(start: Int, end: Int): kotlin.collections.List<Int> {
    tailrec fun go(start: Int, acc: kotlin.collections.List<Int>): kotlin.collections.List<Int> = when {
        start >= end -> acc
        else -> go(start + 1, acc + start)
    }
    return go(start, listOf<Int>())
}

// Exercise 4.13
fun <T> unfold(seed: T, f: (T) -> T, p: (T) -> Boolean): kotlin.collections.List<T> {
    fun go(seed: T, acc: kotlin.collections.List<T>): kotlin.collections.List<T> = when {
        p(seed) -> go(f(seed), acc + seed)
        else -> acc
    }
    return go(seed, listOf<T>())
}
fun rangeUnfold(start: Int, end: Int): kotlin.collections.List<Int> = unfold(start, { it + 1 }, { it < end })

// Exercise 4.15
fun fibonacciString(n: UInt): String {
    tailrec fun go(m: UInt, prev: BigInteger, nextPrev: BigInteger, acc: String): String =
            if (m <= 1u) acc else go(dec(m), prev.add(nextPrev), prev, "$acc, $prev")

    return when (n) {
        0u -> ""
        else -> go(n, BigInteger.valueOf(1), BigInteger.valueOf(1), "1")
    }
}

// Exercise 4.16
fun <T> iterate(seed: T, f: (T) -> T, n: Int): kotlin.collections.List<T> {
    tailrec fun go(seed: T, acc: kotlin.collections.List<T>, n: Int): kotlin.collections.List<T> = when {
        n > 0 -> go(f(seed), acc + seed, n-1)
        else -> acc
    }
    return go(seed, listOf<T>(), n)
}
fun rangeIterate(start: Int, end: Int): kotlin.collections.List<Int> = iterate(start, { it + 1 }, end - start)

// Exercise 4.17
fun <T, U> map(list: kotlin.collections.List<T>, f: (T) -> U): kotlin.collections.List<U> = list.foldLeft(listOf()) { elem, list -> list + f(elem) }

// Exercise 4.18
fun fibonacciStringCorecursive(n: Int): String {
    val pairs = iterate(Pair(BigInteger.valueOf(1), BigInteger.valueOf(1)),
    { (x, y) -> Pair(y, x + y) }, n)
    val elements = map(pairs) { x: Pair<BigInteger, BigInteger> -> x.first }
    return makeStringFoldLeft(elements, ", ")
}

class Exercises {

}
