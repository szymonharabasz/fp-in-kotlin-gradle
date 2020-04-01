package com.example.project

sealed class Option<A> {

    abstract fun isEmpty(): Boolean

    fun getOrElse(default: A): A = when(this) {
        is None -> default
        is Some -> value
    }

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