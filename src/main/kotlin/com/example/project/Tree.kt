package com.example.project

import com.example.project.result.Result

sealed class Tree<A: Comparable<A>> {

    abstract val size: Int
    abstract val height: Int

    abstract fun isEmpty(): Boolean

    operator fun plus(a: A): Tree<A> = when (this) {
        Empty -> T(Empty as Tree<A>, a, Empty as Tree<A>)
        is T -> when {
            a < value -> T(left.plus(a), value, right)
            a > value -> T(left, value, right.plus(a))
            else -> T(left, a, right)
        }
    }

    fun max(): Result<A> = when (this) {
        Empty -> Result<A>()
        is T -> right.max().orElse { Result(value) }
    }

    fun min(): Result<A> = when (this) {
        Empty -> Result<A>()
        is T -> left.min().orElse { Result(value) }
    }

    fun contains(a: A): Boolean = when(this) {
        Empty -> false
        is T -> when {
            a < value -> left.contains(a)
            a > value -> right.contains(a)
            else -> true
        }
    }

    fun merge(tree: Tree<A>): Tree<A> = when (this) {
        Empty -> tree
        is T -> when (tree) {
            Empty -> this
            is T -> when {
                tree.value > value -> T(left, value, right
                        .merge(T(Empty as Tree<A>, tree.value, tree.right)))
                        .merge(tree.left)
                tree.value < value -> T(left
                        .merge(T(tree.left, tree.value, Empty as Tree<A>)), value, right)
                        .merge(tree.right)
                else -> T(tree.left.merge(left), value, tree.right.merge(right))
            }
        }
    }

    fun remove(a: A): Tree<A> = when (this) {
        Empty -> this
        is T -> when {
            a < value -> T(left.remove(a), value, right)
            a > value -> T(left, value, right.remove(a))
            else -> mergeOrdered(left, right)
        }
    }

    fun <B> foldLeft(
            identity: B,
            f: (B) -> (A) -> B,
            g: (B) -> (B) -> B): B = when (this) {
        Empty -> identity
        is T -> f ( g( left.foldLeft(identity,f,g) )( right.foldLeft(identity,f,g)) ) ( value )
    }

    internal object Empty: Tree<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "()"

        override val size: Int = 0
        override val height: Int = -1

    }

    internal class T<A: Comparable<A>>(
            internal val left: Tree<A>,
            internal val value: A,
            internal val right: Tree<A>): Tree<A>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "($left $value $right)"
        override val size: Int = 1 + left.size + right.size
        override val height:Int = 1 + Math.max( left.height, right.height )

    }

    companion object {

        operator fun <A: Comparable<A>> invoke(): Tree<A> = Empty as Tree<A>

        operator fun <A: Comparable<A>> invoke(list: LinkedList<A>): Tree<A> = list.coFoldRight(invoke()) {
            a, tree -> tree + a }

        operator fun <A: Comparable<A>> invoke(vararg az: A): Tree<A> = az.foldRight(invoke()) {
            a, tree -> tree + a
        }

        private fun <A: Comparable<A>> mergeOrdered(t1: Tree<A>, t2: Tree<A>): Tree<A> = when (t1) {
            Empty -> t2
            is T -> T(t1.left, t1.value, mergeOrdered(t1.right, t2))
        }

    }
}