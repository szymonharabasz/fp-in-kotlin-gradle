package com.example.project

import com.example.project.result.Result
import com.example.project.result.lift2
import kotlin.IllegalStateException

sealed class Tree<A: Comparable<A>> {

    abstract val size: Int
    abstract val height: Int

    abstract fun isEmpty(): Boolean

    operator fun plus(a: A): Tree<A> {
        fun plusUnbalanced(tree: Tree<A>, a: A): Tree<A> = when (tree) {
            Empty -> T(Empty as Tree<A>, a, Empty as Tree<A>)
            is T -> when {
                a < tree.value -> T(tree.left.plus(a), tree.value, tree.right)
                a > tree.value -> T(tree.left, tree.value, tree.right.plus(a))
                else -> T(tree.left, a, tree.right)
            }
        }
        return balance(plusUnbalanced(this, a))
    }

    fun max(): Result<A> = when (this) {
        Empty -> Result<A>()
        is T -> right.max().orElse { Result(value) }
    }

    fun min(): Result<A> = when (this) {
        Empty -> Result<A>()
        is T -> left.min().orElse { Result(value) }
    }

    fun contains(a: A): Boolean = when (this) {
        Empty -> false
        is T -> when {
            a < value -> left.contains(a)
            a > value -> right.contains(a)
            else -> true
        }
    }

    fun merge(tree: Tree<A>): Tree<A> {
        fun mergeUnbalanced(t1: Tree<A>, t2: Tree<A>): Tree<A> = when (t1) {
            Empty -> t2
            is T -> when (t2) {
                Empty -> t1
                is T -> when {
                    t2.value > t1.value -> T(t1.left, t1.value, t1.right
                            .merge(T(Empty as Tree<A>, t2.value, t2.right)))
                            .merge(t2.left)
                    t2.value < t1.value -> T(t1.left
                            .merge(T(t2.left, t2.value, Empty as Tree<A>)), t1.value, t1.right)
                            .merge(t2.right)
                    else -> T(t2.left.merge(t1.left), t1.value, t2.right.merge(t1.right))
                }
            }
        }
        return balance(mergeUnbalanced(this, tree))
    }

    fun remove(a: A): Tree<A> {
        fun removeUnbalanced(tree: Tree<A>, a: A): Tree<A> = when (tree) {
            Empty -> tree
            is T -> when {
                a < tree.value -> T(tree.left.remove(a), tree.value, tree.right)
                a > tree.value -> T(tree.left, tree.value, tree.right.remove(a))
                else -> mergeOrdered(tree.left, tree.right)
            }
        }
        return removeUnbalanced(this, a)
    }

    fun <B> foldLeft(
            identity: B,
            f: (B) -> (A) -> B,
            g: (B) -> (B) -> B): B = when (this) {
        Empty -> identity
        is T -> f(g(left.foldLeft(identity, f, g))(right.foldLeft(identity, f, g)))(value)
    }

    fun <B : Comparable<B>> map(f: (A) -> B): Tree<B> =
            foldInOrder(Empty as Tree<B>) { t1: Tree<B> ->
                { i: A -> { t2: Tree<B> -> Companion<B>(t1, f(i), t2) } }
            }

    fun toListInOrderRight(): LinkedList<A> = foldInOrder(LinkedList<A>()) { list1 ->
        { a ->
            { list2 ->
                list1.concat(list2.cons(a))
            }
        }
    }


    abstract fun <B> foldInOrder(identity: B, f: (B) -> (A) -> (B) -> B): B
    abstract fun <B> foldPreOrder(identity: B, f: (A) -> (B) -> (B) -> B): B
    abstract fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (A) -> B): B
    abstract fun rotateLeft(): Tree<A>
    abstract fun rotateRight(): Tree<A>

    internal object Empty : Tree<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "E"

        override val size: Int = 0
        override val height: Int = -1

        override fun <B> foldInOrder(identity: B, f: (B) -> (Nothing) -> (B) -> B) = identity
        override fun <B> foldPreOrder(identity: B, f: (Nothing) -> (B) -> (B) -> B) = identity
        override fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (Nothing) -> B) = identity

        override fun rotateLeft(): Tree<Nothing> = this

        override fun rotateRight(): Tree<Nothing> = this

    }

    internal data class T<A : Comparable<A>>(
            internal val left: Tree<A>,
            internal val value: A,
            internal val right: Tree<A>) : Tree<A>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "($left $value $right)"
        override val size: Int = 1 + left.size + right.size
        override val height: Int = 1 + left.height.coerceAtLeast(right.height)

        override fun <B> foldInOrder(identity: B, f: (B) -> (A) -> (B) -> B): B =
                f(left.foldInOrder(identity, f))(value)(right.foldInOrder(identity, f))

        override fun <B> foldPreOrder(identity: B, f: (A) -> (B) -> (B) -> B): B =
                f(value)(left.foldPreOrder(identity, f))(right.foldPreOrder(identity, f))

        override fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (A) -> B): B =
                f(left.foldPostOrder(identity, f))(right.foldPostOrder(identity, f))(value)

        override fun rotateLeft(): Tree<A> = when (right) {
            Empty -> this
            is T -> T(T(left, value, right.left), right.value, right.right)
        }

        override fun rotateRight(): Tree<A> = when (left) {
            Empty -> this
            is T -> T(left.left, left.value, T(left.right, value, right))
        }

    }

    companion object {

        operator fun <A : Comparable<A>> invoke(): Tree<A> = Empty as Tree<A>

        operator fun <A : Comparable<A>> invoke(list: LinkedList<A>): Tree<A> = list.coFoldRight(invoke()) { a, tree -> tree + a }

        operator fun <A : Comparable<A>> invoke(vararg az: A): Tree<A> = az.foldRight(invoke()) { a, tree ->
            tree + a
        }

        private fun <A : Comparable<A>> lt(first: A, second: A): Boolean = first < second
        private fun <A : Comparable<A>> lt(first: A, second: A, third: A): Boolean =
                lt(first, second) && lt(second, third)


        private fun <A : Comparable<A>> ordered(t1: Tree<A>, a: A, t2: Tree<A>) =
                lift2<Boolean, Boolean, Boolean> { b1 -> { b2 -> b1 && b2 } }(
                        t1.max().map { it < a })(t2.min().map { a < it })
                        .getOrElse(t1.isEmpty() && t2.isEmpty())



        operator fun <A : Comparable<A>> invoke(left: Tree<A>, a: A, right: Tree<A>) = when {
            ordered(left, a, right) -> T(left, a, right)
            ordered(right, a, left) -> T(right, a, left)
            else -> Tree(a).merge(left).merge(right)
        }

        private fun <A : Comparable<A>> mergeOrdered(t1: Tree<A>, t2: Tree<A>): Tree<A> = when (t1) {
            Empty -> t2
            is T -> T(t1.left, t1.value, mergeOrdered(t1.right, t2))
        }

        fun <A> unfold(a: A, f: (A) -> Result<A>): A {
            tailrec fun <A> unfold(
                    a: Pair<Result<A>, Result<A>>,
                    f: (A) -> Result<A>): Pair<Result<A>, Result<A>> {
                val x = a.second.flatMap(f)
                return when (x) {
                    is Result.Success -> unfold(Pair(a.second, x), f)
                    else -> a
                }
            }
            return Result(a).let { unfold(Pair(it, it), f).second.getOrElse(a) }
        }

        fun <A : Comparable<A>> isUnbalanced(tree: Tree<A>): Boolean = when (tree) {
            Empty -> false
            is T -> Math.abs(tree.left.height - tree.right.height) > (tree.size - 1) % 2
        }


        private fun log2nlz(n: Int) = when (n) {
            0 -> 0
            else -> 31 - Integer.numberOfLeadingZeros(n)
        }

        fun <A : Comparable<A>> balance(tree: Tree<A>): Tree<A> =
                balanceHelper(tree.toListInOrderRight().coFoldRight(Empty as Tree<A>) { a: A, t: Tree<A> ->
                    T(Empty as Tree<A>, a, t)
                })

        private fun <A : Comparable<A>> balanceHelper(tree: Tree<A>): Tree<A> = when {
            tree is T && tree.height > log2nlz(tree.size) -> when {
                Math.abs(tree.left.height - tree.right.height) > 1 ->
                    balanceHelper(balanceFirstLevel(tree))
                else ->
                    T(balanceHelper(tree.left), tree.value, balanceHelper(tree.right))
            }
            else -> tree
        }

        private fun <A : Comparable<A>> balanceFirstLevel(tree: Tree.T<A>): Tree<A> = unfold(tree) { t: Tree<A> ->
            when {
                isUnbalanced(t) -> when {
                    tree.right.height > tree.left.height ->
                        t.rotateLeft().let {
                            when (it) {
                                is T -> Result(it)
                                else -> throw IllegalStateException("should never happen")
                            }
                        }
                    else -> t.rotateRight().let {
                        when (it) {
                            is T -> Result(it)
                            else -> throw IllegalStateException("should never happen")
                        }
                    }
                }
                else -> Result()
            }
        }

    }
}
