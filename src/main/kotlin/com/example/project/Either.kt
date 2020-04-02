package com.example.project

sealed class Either<E, A> {

    abstract fun <B> map(f: (A) -> B): Either<E, B>

    abstract fun <B> flatMap(f: (A) -> Either<E, B>): Either<E, B>

    abstract fun getOrElse(default: () -> A): A

    fun orElse(default: () -> Either<E, A>) = map { this }.getOrElse(default)

    internal class Left<E, A>(private val value: E): Either<E, A>() {
        override fun toString(): String = "Left($value)"

        override fun <B> map(f: (A) -> B): Either<E, B> = Left(value)

        override fun <B> flatMap(f: (A) -> Either<E, B>): Either<E, B> = Left(value)

        override fun getOrElse(default: () -> A): A = default()
    }

    internal class Right<E, A>(private val value: A): Either<E, A>() {
        override fun toString(): String = "Right($value)"

        override fun <B> map(f: (A) -> B): Either<E, B> = Right(f(value))

        override fun <B> flatMap(f: (A) -> Either<E, B>): Either<E, B> = f(value)

        override fun getOrElse(default: () -> A): A = value
    }

    companion object {
        fun <E, A> left(value: E): Either<E, A> = Left(value)
        fun <E, A> right(value: A): Either<E, A> = Right(value)
    }
}