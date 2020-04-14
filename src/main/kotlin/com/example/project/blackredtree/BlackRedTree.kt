package com.example.project.blackredtree

import com.example.project.blackredtree.Color.NB
import com.example.project.blackredtree.Color.BB
import com.example.project.blackredtree.Color.B
import com.example.project.blackredtree.Color.R
import com.example.project.result.Result
import com.example.project.result.lift2

sealed class BlackRedTree<A: Comparable<A>> {

    abstract val size: Int
    abstract val height: Int
    internal abstract val color: Color
    internal abstract val isB: Boolean
    internal abstract val isBB: Boolean
    internal abstract val isTB: Boolean
    internal abstract val isTR: Boolean
    internal abstract val isTNB: Boolean
    internal abstract val left: BlackRedTree<A>
    internal abstract val right: BlackRedTree<A>
    internal abstract val value: A
    internal abstract fun redder(): BlackRedTree<A>

    abstract val isEmpty: Boolean

    fun max(): A = when (this) {
        is Empty -> throw IllegalStateException("max called on Empty")
        is T -> when {
            right.isEmpty -> value
            else -> right.max()
        }
    }

    fun min(): A = when (this) {
        is Empty -> throw IllegalStateException("min called on Empty")
        is T -> when {
            left.isEmpty -> value
            else -> left.min()
        }
    }

    fun contains(a: A): Boolean = when (this) {
        is Empty -> false
        is T -> when {
            a < value -> left.contains(a)
            a > value -> right.contains(a)
            else -> true
        }
    }
/*
    fun merge(tree: BlackRedTree<A>): BlackRedTree<A> {
        fun mergeUnbalanced(t1: BlackRedTree<A>, t2: BlackRedTree<A>): BlackRedTree<A> = when (t1) {
            Empty -> t2
            is T -> when (t2) {
                Empty -> t1
                is T -> when {
                    t2.value > t1.value -> T(t1.left, t1.value, t1.right
                            .merge(T(Empty as BlackRedTree<A>, t2.value, t2.right)))
                            .merge(t2.left)
                    t2.value < t1.value -> T(t1.left
                            .merge(T(t2.left, t2.value, Empty as BlackRedTree<A>)), t1.value, t1.right)
                            .merge(t2.right)
                    else -> T(t2.left.merge(t1.left), t1.value, t2.right.merge(t1.right))
                }
            }
        }
        return balance(mergeUnbalanced(this, tree))
    }

    fun remove(a: A): BlackRedTree<A> {
        fun removeUnbalanced(tree: BlackRedTree<A>, a: A): BlackRedTree<A> = when (tree) {
            Empty -> tree
            is T -> when {
                a < tree.value -> T(tree.left.remove(a), tree.value, tree.right)
                a > tree.value -> T(tree.left, tree.value, tree.right.remove(a))
                else -> mergeOrdered(tree.left, tree.right)
            }
        }
        return removeUnbalanced(this, a)
    }
*/
    fun <B> foldLeft(
            identity: B,
            f: (B) -> (A) -> B,
            g: (B) -> (B) -> B): B = when (this) {
        is Empty -> identity
        is T -> g(left.foldLeft(identity, f, g))(f(right.foldLeft(identity, f, g))(value))
    }
/*
    fun <B : Comparable<B>> map(f: (A) -> B): BlackRedTree<B> =
            foldInOrder(Empty as BlackRedTree<B>) { t1: BlackRedTree<B> ->
                { i: A -> { t2: BlackRedTree<B> -> Companion<B>(t1, f(i), t2) } }
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

    abstract fun rotateLeft(): BlackRedTree<A>
    abstract fun rotateRight(): BlackRedTree<A>
*/
    operator fun plus(value: A): BlackRedTree<A> = add(value).blacken()
    operator fun minus(value: A): BlackRedTree<A> = delete(value).blacken()

    private fun balance(color: Color, left: BlackRedTree<A>, value: A, right: BlackRedTree<A>): BlackRedTree<A> =
            when {
                color == B && left.isTR && left.left.isTR ->
                    T(R, left.left.blacken(), left.value, T(B, left.right, value, right))
                color == B && left.isTR && left.right.isTR ->
                    T(R, T(B, left.left, left.value, left.right.left), left.right.value,
                            T(B, left.right.right, value, right))
                color == B && right.isTR && right.left.isTR ->
                    T(R, T(B, left, value, right.left.left), right.left.value,
                            T(B, right.left.right, right.value, right.right))
                color == B && right.isTR && right.right.isTR ->
                    T(R, T(B, left, value, right.left), right.value, right.right.blacken())
                else -> T(color, left, value, right)
            }

    private fun blacken(): BlackRedTree<A> = when (this) {
        is Empty -> this
        is T -> T(B, left, value, right)
    }

    private fun redden(): BlackRedTree<A> = when (this) {
        is Empty -> throw IllegalStateException("Empty trees may not be reddened")
        is T -> T(R, left, value, right)
    }

    fun add(a: A): BlackRedTree<A> = when (this) {
        is Empty -> T(R, E as BlackRedTree<A>, a, E as BlackRedTree<A>)
        is T -> when {
            a < value -> balance(color, left.add(a), value, right)
            a > value -> balance(color, left, value, right.add(a))
            else -> T(color, left, a, right)
        }
    }

    operator fun get(a: A): Result<A> = when (this) {
        is Empty -> Result()
        is T -> when {
            a < value -> left.get(a)
            a > value -> right.get(a)
            else -> Result(value)
        }
    }

    fun delete(a: A): BlackRedTree<A> = when (this) {
        is Empty -> E as BlackRedTree<A>
        is T -> when {
            a < value -> balance(color, left.delete(a), value, right)
            a > value -> balance(color, left, value, right.delete(a))
            else -> remove()
        }
    }

    private fun remove(): BlackRedTree<A> = when {
        isTR && left.isEmpty && right.isEmpty -> E as BlackRedTree<A>
        isTB && left.isEmpty && right.isEmpty -> EE as BlackRedTree<A>
        isTB && left.isEmpty && right.isTR    -> T(B, right.left, right.value, right.right)
        isTB && left.isTR && right.isEmpty    -> T(B, left.left, left.value, left.right)
        left.isEmpty                          -> bubble(right.color, right.left, right.value, right.right)
        else                                  -> bubble(color, left.removeMax(), left.max(), right)
    }

    fun removeMax(): BlackRedTree<A> = when (this) {
        is Empty -> throw IllegalStateException("removedMax called on Empty")
        is T -> when {
            right.isEmpty -> remove()
            else -> bubble(color, left, value, right.removeMax())
        }
    }

    private fun bubble(color: Color, left: BlackRedTree<A>, value: A, right: BlackRedTree<A>): BlackRedTree<A> = when {
        left.isBB || right.isBB -> balance(color.blacker, left.redder(), value, right.redder())
        else                    -> balance(color, left, value, right)
    }

    internal abstract class Empty<A: Comparable<A>> : BlackRedTree<A>() {

        override val isEmpty: Boolean = true

        override fun toString(): String = "E"

        override val size: Int = 0
        override val height: Int = -1
        /*
                override fun <B> foldInOrder(identity: B, f: (B) -> (Nothing) -> (B) -> B) = identity
                override fun <B> foldPreOrder(identity: B, f: (Nothing) -> (B) -> (B) -> B) = identity
                override fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (Nothing) -> B) = identity

                override fun rotateLeft(): BlackRedTree<Nothing> = this

                override fun rotateRight(): BlackRedTree<Nothing> = this
        */
        override val color: Color = Color.R
        override val isB: Boolean = false
        override val isBB: Boolean = false
        override val isTB: Boolean = false
        override val isTR: Boolean = false
        override val isTNB: Boolean = false
        override val value: Nothing by lazy {
            throw IllegalStateException("value called on Empty tree")
        }
        override val left: BlackRedTree<A> by lazy {
            throw IllegalStateException("left called on Empty tree")
        }
        override val right: BlackRedTree<A> by lazy {
            throw IllegalStateException("right called on Empty tree")
        }
    }

    internal object E: Empty<Nothing>() {

        override val color: Color = Color.R

        override val isB: Boolean = true

        override val isBB: Boolean = false

        override fun redder(): BlackRedTree<Nothing> = this

        override fun toString(): String = "E"
    }

    internal object EE: Empty<Nothing>() {

        override val color: Color = Color.BB

        override val isB: Boolean = false

        override val isBB: Boolean = true

        override fun redder(): BlackRedTree<Nothing> = E

        override fun toString(): String = "EE"
    }

    internal data class T<A : Comparable<A>>(
            internal override val color: Color,
            internal override val left: BlackRedTree<A>,
            internal override val value: A,
            internal override val right: BlackRedTree<A>) : BlackRedTree<A>() {

        override val isEmpty: Boolean = false

        override fun toString(): String = "{$color $left $value $right}"
        override val size: Int = 1 + left.size + right.size
        override val height: Int = 1 + left.height.coerceAtLeast(right.height)
        /*
            override fun <B> foldInOrder(identity: B, f: (B) -> (A) -> (B) -> B): B =
                    f(left.foldInOrder(identity, f))(value)(right.foldInOrder(identity, f))

            override fun <B> foldPreOrder(identity: B, f: (A) -> (B) -> (B) -> B): B =
                    f(value)(left.foldPreOrder(identity, f))(right.foldPreOrder(identity, f))

            override fun <B> foldPostOrder(identity: B, f: (B) -> (B) -> (A) -> B): B =
                    f(left.foldPostOrder(identity, f))(right.foldPostOrder(identity, f))(value)


            override fun rotateLeft(): BlackRedTree<A> = when (right) {
                Empty -> this
                is T -> T(T(left, value, right.left), right.value, right.right)
            }

            override fun rotateRight(): BlackRedTree<A> = when (left) {
                Empty -> this
                is T -> T(left.left, left.value, T(left.right, value, right))
            }
        */
        override val isB: Boolean = color == Color.B
        override val isBB: Boolean = color == Color.BB
        override val isTB: Boolean = color == Color.B || color == Color.BB
        override val isTR: Boolean = color == Color.R
        override val isTNB: Boolean = color == Color.NB
        override fun redder(): BlackRedTree<A> = T(color.redder, left, value, right)

    }

    companion object {

        operator fun <A : Comparable<A>> invoke(): BlackRedTree<A> = E as BlackRedTree<A>
/*
        operator fun <A : Comparable<A>> invoke(list: LinkedList<A>): BlackRedTree<A> = list.coFoldRight(invoke()) { a, tree -> tree + a }

        operator fun <A : Comparable<A>> invoke(vararg az: A): BlackRedTree<A> = az.foldRight(invoke()) { a, tree ->
            tree + a
        }
*/
        private fun <A : Comparable<A>> lt(first: A, second: A): Boolean = first < second
        private fun <A : Comparable<A>> lt(first: A, second: A, third: A): Boolean =
                lt(first, second) && lt(second, third)


        private fun <A : Comparable<A>> ordered(t1: BlackRedTree<A>, a: A, t2: BlackRedTree<A>) =
                (t1.max() < a && a < t2.min()) || (t1.isEmpty && t2.isEmpty)


/*
        internal operator fun <A : Comparable<A>> invoke(left: BlackRedTree<A>, a: A, right: BlackRedTree<A>) = when {
            ordered(left, a, right) -> T(R, left, a, right)
            ordered(right, a, left) -> T(R, right, a, left)
            else -> BlackRedTree(a).merge(left).merge(right)
        }

        private fun <A : Comparable<A>> mergeOrdered(t1: BlackRedTree<A>, t2: BlackRedTree<A>): BlackRedTree<A> = when (t1) {
            Empty -> t2
            is T -> T(t1.left, t1.value, mergeOrdered(t1.right, t2))
        }
*/
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

        fun <A : Comparable<A>> isUnbalanced(tree: BlackRedTree<A>): Boolean = when (tree) {
            is Empty -> false
            is T -> Math.abs(tree.left.height - tree.right.height) > (tree.size - 1) % 2
        }


        private fun log2nlz(n: Int) = when (n) {
            0 -> 0
            else -> 31 - Integer.numberOfLeadingZeros(n)
        }
/*
        fun <A : Comparable<A>> balance(tree: BlackRedTree<A>): BlackRedTree<A> =
                balanceHelper(tree.toListInOrderRight().coFoldRight(Empty as BlackRedTree<A>) { a: A, t: BlackRedTree<A> ->
                    T(Empty as BlackRedTree<A>, a, t)
                })

        private fun <A : Comparable<A>> balanceHelper(tree: BlackRedTree<A>): BlackRedTree<A> = when {
            tree is T && tree.height > log2nlz(tree.size) -> when {
                Math.abs(tree.left.height - tree.right.height) > 1 ->
                    balanceHelper(balanceFirstLevel(tree))
                else ->
                    T(balanceHelper(tree.left), tree.value, balanceHelper(tree.right))
            }
            else -> tree
        }

        private fun <A : Comparable<A>> balanceFirstLevel(tree: BlackRedTree.T<A>): BlackRedTree<A> = unfold(tree) { t: BlackRedTree<A> ->
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

 */

    }
}

