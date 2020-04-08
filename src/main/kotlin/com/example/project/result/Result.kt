package com.example.project.result

import java.io.Serializable

sealed class Result<A>: Serializable {

    internal data class Failure<A>(internal val exception: RuntimeException): Result<A>() {
        override fun toString(): String = "Failure(${exception.message})"

        override fun equals(other: Any?): Boolean {
            println("this = $this, other = $other, ${other is Failure<*>}")
            val result = when (other) {
                null -> false
                // is Failure<*> -> other.exception::class == exception::class && other.exception.message == exception.message
                is Failure<*> -> true
                else -> false
            }
            println("result $result")
            return result
        }

        override fun hashCode(): Int = exception::class.hashCode() + 41 * exception.message.hashCode()

        override fun isEmpty(): Boolean = false
    }

    internal data class Success<A>(internal val value: A): Result<A>() {
        override fun toString(): String = "Success($value)"
        override fun isEmpty(): Boolean = false
    }

    internal object Empty: Result<Nothing>() {
        override fun toString(): String = "Empty"
        override fun isEmpty(): Boolean = true
    }

    abstract fun isEmpty(): Boolean

    fun <B> map(f: (A) -> B): Result<B> = when (this) {
        is Success -> try {
            Success(f(value))
        } catch (e: Exception) {
            failure<B>(e)
        }
        is Failure -> this as Result<B>
        is Empty -> Empty as Result<B>

    }

    fun <B> flatMap(f: (A) -> Result<B>): Result<B> = when (this) {
        is Success -> try {
            f(value)
        } catch (e: Exception) {
            failure<B>(e)
        }
        is Failure -> this as Result<B>
        is Empty -> Empty as Result<B>

    }

    fun getOrElse(default: A): A = when (this) {
        is Success -> value
        else -> default
    }

    fun getOrElse(default: () -> A): A = when (this) {
        is Success -> value
        else -> default()
    }

    fun orElse(default: () -> Result<A>): Result<A> = map { this }.getOrElse( try {
        default()
    } catch (e: Exception) {
        failure<A>(e) as Result<A>
    })

    fun filter(p: (A) -> Boolean, message: String = "condition not fulfilled"): Result<A> = flatMap { a ->
        if (p(a)) this else failure(message) }

    fun exists(p: (A) -> Boolean): Boolean = map(p).getOrElse(false)

    fun mapFailure(message: String): Result<A> = when (this) {
        is Failure -> failure(RuntimeException(message, exception))
        else -> this
    }

    fun forEach(
            onSuccess: (A) -> Unit = {},
            onFailure: (RuntimeException) -> Unit = {},
            onEmpty: () -> Unit = {}) {
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(exception)
            is Empty -> onEmpty()
        }
    }

    companion object {
        operator fun  <A> invoke(a: A? = null): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> Success(a)
        }

        operator fun  <A> invoke(a: A? = null, message: String): Result<A> = when (a) {
            null -> Failure(NullPointerException(message))
            else -> Success(a)
        }

        operator fun <A> invoke(a: A? = null, p: (A) -> Boolean): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> if (p(a)) Success(a) else Empty as Result<A>
        }

        operator fun <A> invoke(a: A? = null, message: String, p: (A) -> Boolean): Result<A> = when (a) {
            null -> Failure(NullPointerException(message))
            else -> if (p(a)) Success(a) else Empty as Result<A>
        }

        fun <A> failure(message: String): Result<A> = Failure(IllegalStateException(message))
        fun <A> failure(exception: RuntimeException): Result<A> = Failure(exception)
        fun <A> failure(exception: Exception): Result<A> = Failure(IllegalStateException(exception))
    }
}

fun <A, B> lift(f: (A) -> B): (Result<A>) -> Result<B> = { it.map(f) }

fun <A, B, C> lift2( f: (A) -> (B) -> C): (Result<A>) -> (Result<B>) -> Result<C> = {
    a -> { b -> a.map(f).flatMap { b.map(it) } }
}

fun <A, B, C, D> lift3( f: (A) -> (B) -> (C) -> D): (Result<A>) -> (Result<B>) -> (Result<C>) -> Result<D> =
        { a ->
            { b ->
                {
                    c-> a.map(f).flatMap { b.map(it) }.flatMap { c.map(it) }
                }
            }
        }

fun <A, B, C> map2(a: Result<A>, b: Result<B>, f: (A) -> (B) -> C): Result<C> = lift2<A, B, C>(f)(a)(b)