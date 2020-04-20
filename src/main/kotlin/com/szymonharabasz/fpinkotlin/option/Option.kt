package com.szymonharabasz.fpinkotlin.option

import com.szymonharabasz.fpinkotlin.LinkedList
import com.szymonharabasz.fpinkotlin.sum
import kotlin.math.pow

sealed class Option<A> {

    abstract fun isEmpty(): Boolean

    fun getOrElse(default: A): A = when(this) {
        is None -> default
        is Some -> value
    }

    fun getOrElse(default: () -> A): A = when(this) {
        is None -> default()
        is Some -> value
    }

    fun orElse(default: () -> Option<A>): Option<A> = map { this }.getOrElse(default)

    @Suppress("UNCHECKED_CAST")
    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None as Option<B>
        is Some -> Some(f(value))
    }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = map(f).getOrElse(invoke())

    fun filter(p: (A) -> Boolean): Option<A> = flatMap { x -> if (p(x)) this else invoke() }

    internal object None: Option<Nothing>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "None"

        override fun equals(other: Any?): Boolean = other === None

        override fun hashCode(): Int = 0

    }

    internal data class Some<A>(internal val value: A): Option<A>() {

        override fun isEmpty(): Boolean = false

    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        operator fun <A> invoke(a: A? = null): Option<A> = when(a) {
            null -> None as Option<A>
            else -> Some(a)
        }
    }
}


val mean: (LinkedList<Double>) -> Option<Double> = { list -> when {
        list.isEmpty() -> Option()
        else -> Option(list.sum() / list.length())
    } }

val variance: (LinkedList<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { m -> mean(list.map { x -> (x - m).pow(2) }) }
}

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = { a ->
    try{
        a.map(f)
    } catch(e: Exception) {
        Option()
    }}

fun <A, B> hLift(f: (A) -> B): (A) -> Option<B> = { a ->
    try{
        Option(a).map(f)
    } catch(e: Exception) {
        Option()
    }}

fun <A, B, C> map2(a: Option<A>, b: Option<B>, f: (A) -> (B) -> C): Option<C> = a.flatMap { x -> b.map { y -> f(x)(y)} }
fun <A, B, C, D> map3(a: Option<A>, b: Option<B>, c: Option<C>, f: (A) -> (B) -> (C) -> D): Option<D> =
        a.flatMap { x ->
            b.flatMap { y ->
                c.map { z -> f(x)(y)(z)} } }

fun <A, B> traverse(list: LinkedList<A>, f: (A) -> Option<B>): Option<LinkedList<B>> =
        list.coFoldRight(Option(LinkedList<B>())) { elem, lst ->
            map2(lst, f(elem)) { l: LinkedList<B> -> { e: B -> l.cons(e) } }
        }

fun <A> sequence(list: LinkedList<Option<A>>): Option<LinkedList<A>> = traverse(list) { it }