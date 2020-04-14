package com.example.project.blackredtree

import com.example.project.LinkedList
import com.example.project.result.Result

class Map<K: Comparable<K>, V>(
        private val delegate: BlackRedTree<MapEntry<K, V>> = BlackRedTree.invoke()
) {

    operator fun plus(entry: Pair<K, V>): Map<K, V> = Map(delegate + MapEntry(entry))

    operator fun minus(key: K): Map<K, V> = Map(delegate - MapEntry(key))

    fun contains(key: K): Boolean = delegate.contains(MapEntry.Companion.invoke(key))

    fun get(key: K): Result<MapEntry<K, V>> = delegate.get(MapEntry(key))

    fun isEmpty(): Boolean = delegate.isEmpty

    fun size(): Int = delegate.size

    fun <B> foldLeft(
            identity: B,
            f: (B) -> (MapEntry<K, V>) -> B,
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
        operator fun invoke(): Map<Nothing, Nothing> = Map()
    }

}

class MapEntry<K: Comparable<K>, V>
private constructor(internal val key: K, val value: Result<V>): Comparable<MapEntry<K, V>> {

    override fun compareTo(other: MapEntry<K, V>): Int = key.compareTo(other.key)

    override fun toString(): String = "MapEntry($key, $value)"

    override fun equals(other: Any?): Boolean = this === other || when (other) {
        is MapEntry<*,*> -> key == other.key
        else -> false
    }

    override fun hashCode(): Int = key.hashCode()

    companion object {

        fun <K: Comparable<K>, V> of(key: K, value: V): MapEntry<K, V> = MapEntry(key, Result(value))

        operator fun <K: Comparable<K>, V> invoke(pair: Pair<K, V>): MapEntry<K, V> =
                MapEntry(pair.first, Result(pair.second))

        operator fun <K: Comparable<K>, V> invoke(key: K): MapEntry<K, V> =
                MapEntry(key, Result())

    }
}