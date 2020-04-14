package com.example.project.blackredtree

import com.example.project.LinkedList
import com.example.project.result.Result

class PriorityQueue<K: Comparable<K>, V>(
        private val delegate: BlackRedTree<PriorityQueueEntry<K, V>> = BlackRedTree.invoke()
) {

    operator fun plus(entry: Pair<K, V>): PriorityQueue<K, V> = PriorityQueue(delegate + PriorityQueueEntry(entry))

    operator fun minus(key: K): PriorityQueue<K, V> = PriorityQueue(delegate - PriorityQueueEntry(key))

    fun contains(key: K): Boolean = delegate.contains(PriorityQueueEntry.Companion.invoke(key))

    fun get(key: K): Result<PriorityQueueEntry<K, V>> = delegate.get(PriorityQueueEntry(key))

    fun isEmpty(): Boolean = delegate.isEmpty

    fun size(): Int = delegate.size

    private fun <B> foldLeft(
            identity: B,
            f: (B) -> (PriorityQueueEntry<K, V>) -> B,
            g: (B) -> (B) -> B): B = delegate.foldLeft(identity, f, g)

    fun values(): LinkedList<V> = foldLeft(
            LinkedList<V>(),
            { list ->
                { me ->
                    me.value.let {
                        when (it) {
                            is Result.Success<V> -> list.cons(it.value)
                            else -> list
                        }
                    }

                }
            },
            { list1 -> { list2 -> list1.concat(list2) } }
    )

    fun keys(): LinkedList<K> = foldLeft(
            LinkedList<K>(),
            { list ->
                { me -> list.cons(me.key) }
            },
            { list1 -> { list2 -> list1.concat(list2) } }
    )

    companion object {
        operator fun invoke(): PriorityQueue<Nothing, Nothing> = PriorityQueue()
    }

}

class PriorityQueueEntry<K: Comparable<K>, V>
private constructor(internal val key: K, val value: Result<V>): Comparable<PriorityQueueEntry<K, V>> {

    override fun compareTo(other: PriorityQueueEntry<K, V>): Int = key.compareTo(other.key)

    override fun toString(): String = "PriorityQueueEntry($key, $value)"

    override fun equals(other: Any?): Boolean = this === other || when (other) {
        is PriorityQueueEntry<*,*> -> key == other.key
        else -> false
    }

    override fun hashCode(): Int = key.hashCode()

    companion object {

        fun <K: Comparable<K>, V> of(key: K, value: V): PriorityQueueEntry<K, V> = PriorityQueueEntry(key, Result(value))

        operator fun <K: Comparable<K>, V> invoke(pair: Pair<K, V>): PriorityQueueEntry<K, V> =
                PriorityQueueEntry(pair.first, Result(pair.second))

        operator fun <K: Comparable<K>, V> invoke(key: K): PriorityQueueEntry<K, V> =
                PriorityQueueEntry(key, Result())

    }
}