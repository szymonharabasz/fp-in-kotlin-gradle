package com.example.project.option

import java.lang.Exception
import kotlin.math.pow

import com.example.project.*

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

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None as Option<B>
        is Some -> Some(f(value))
    }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = map(f).getOrElse(Option.invoke())

    fun filter(p: (A) -> Boolean): Option<A> = flatMap { x -> if (p(x)) this else Option.invoke() }

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
        operator fun <A> invoke(a: A? = null): Option<A> = when(a) {
            null -> None as Option<A>
            else -> Some(a)
        }
    }
}


val mean: (com.example.project.LinkedList<Double>) -> Option<Double> = { list -> when {
        list.isEmpty() -> Option()
        else -> Option(list.sum() / list.length())
    } }

val variance: (com.example.project.LinkedList<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { m -> mean(list.map {x -> (x - m).pow(2) }) }
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

fun <A> sequence(list: com.example.project.LinkedList<Option<A>>): Option<com.example.project.LinkedList<A>> =
        list.coFoldRight(Option(com.example.project.LinkedList())) { elem, list ->
            map2(list, elem) { l: com.example.project.LinkedList<A> -> { e: A -> l.cons(e) } }
        }

fun <A, B> traverse(list: com.example.project.LinkedList<A>, f: (A) -> Option<B>): Option<com.example.project.LinkedList<B>> =
        list.coFoldRight(Option(com.example.project.LinkedList<B>())) { elem, list ->
            map2(list, f(elem)) { l: com.example.project.LinkedList<B> -> { e: B -> l.cons(e) } }
        }