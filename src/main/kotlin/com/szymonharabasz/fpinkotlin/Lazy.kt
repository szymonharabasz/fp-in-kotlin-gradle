package com.szymonharabasz.fpinkotlin

import com.szymonharabasz.fpinkotlin.result.Result

class Lazy<A>(val f: () -> A) : () -> (A) {
    private val v: A by lazy { f() }

    override fun invoke() = v

    fun <B> map(f: (A) -> B): Lazy<B> = Lazy { f(v) }

    fun <B> flatMap(f: (A) -> Lazy<B>) = Lazy { f(v)() }

    fun forEach(condition: Boolean, ifTrue: (A) -> Unit, ifFalse: (A) -> Unit) =
            if (condition) ifTrue(v) else ifFalse(v)

    fun forEach(condition: Boolean, ifTrue: () -> Unit, ifFalse: (A) -> Unit) =
            if (condition) ifTrue() else ifFalse(v)

    fun forEach(condition: Boolean, ifTrue: (A) -> Unit, ifFalse: () -> Unit) =
            if (condition) ifTrue(v) else ifFalse()

    fun forEach(condition: Boolean, ifTrue: () -> Unit, ifFalse: () -> Unit) =
            if (condition) ifTrue() else ifFalse()

}


fun <A, B> liftLazy(f: (A) -> B): (Lazy<A>) -> Lazy<B> = { la -> Lazy { f(la()) } }

fun <A, B, C> liftLazy2(f: (A) -> (B) -> C): (Lazy<A>) -> (Lazy<B>) -> Lazy<C> = { la ->
    { lb ->
        Lazy { f(la())(lb()) }
    }
}

fun <A> sequence(list: LinkedList<Lazy<A>>): Lazy<LinkedList<A>> = Lazy { list.map { it() } }

fun <A> sequenceResult(list: LinkedList<Lazy<A>>): Lazy<Result<LinkedList<A>>> = Lazy {
    traverse(list.reverse()) {
        try {
            Result(it())
        } catch (e: Exception) {
            Result.failure<A>(RuntimeException(e))
        }
    }
}