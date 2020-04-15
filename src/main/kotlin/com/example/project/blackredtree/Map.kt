package com.example.project.blackredtree

import com.example.project.LinkedList
import com.example.project.result.Result

typealias MyMapEntry<K, V> = MapEntry<Int, LinkedList<Pair<K, V>>>

class Map<K: Any, V>(
        internal val delegate: BlackRedTree<MyMapEntry<K, V>> = BlackRedTree.invoke()
) {
        operator fun minus(key: K): Map<K, V> = delegate[MapEntry(key.hashCode())].flatMap { x -> x.value }
            .getOrElse(LinkedList<Pair<K, V>>()).filter { it.first != key }.let {
                when {
                    it.isEmpty() -> Map(delegate - MapEntry(key.hashCode()))
                    else -> Map(delegate - MapEntry(key.hashCode()) + MapEntry.of(key.hashCode(), it))
                }
            }


    fun contains(key: K): Boolean = delegate[MapEntry(key.hashCode())].flatMap {
        x -> x.value.map { list -> list.exists { it.first == key } }
    }.getOrElse(false)

    fun get(key: K): Result<V> = delegate[MapEntry(key.hashCode())].flatMap {
        x -> x.value.map { list -> list.filter { it.first == key }.headSafe().map { it.second } }
    }.getOrElse(Result())

    fun isEmpty(): Boolean = delegate.isEmpty

    fun size(): Int = delegate.size

    private fun <B> foldLeft(
            identity: B,
            f: (B) -> (MyMapEntry<K, V>) -> B,
            g: (B) -> (B) -> B): B = delegate.foldLeft(identity, f, g)

    private fun getAll(key: K): Result<LinkedList<Pair<K, V>>> =
            delegate[MapEntry.Companion.invoke(key.hashCode())].flatMap { x ->
                x.value.map { list ->
                    list.map { it }
                }
            }


    fun values(): LinkedList<V> = foldLeft(
            LinkedList<V>(),
            { list ->
                { me ->
                    me.value.let { result ->
                        when (result) {
                            is Result.Success<LinkedList<Pair<K, V>>> ->
                                result.value.map { it.second }.concat(list)
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
                { me ->
                    me.value.let { result ->
                        when (result) {
                            is Result.Success<LinkedList<Pair<K, V>>> ->
                                result.value.map { it.first }.concat(list)
                            else -> list
                        }
                    }
                }
            },
            { list1 -> { list2 -> list1.concat(list2) } }
    )

    companion object {
        operator fun invoke(): Map<Nothing, Nothing> = Map()
    }

}

operator fun <K: Any, V> Map<K, V>.plus(entry: Pair<K, V>): Map<K, V> =
        Map(delegate + MapEntry.of(entry.first.hashCode(),
                delegate[MapEntry(entry.first.hashCode())].flatMap { x -> x.value }
                        .getOrElse(LinkedList<Pair<K, V>>()).filter { it.first != entry.first }
                        .cons(entry)
        ))

@JvmName("plusComparable")
operator fun <K, V> Map<K, V>.plus(entry: Pair<K, V>): Map<K, V>
        where K: Comparable<K> =
        Map(delegate + MapEntry.of(entry.first.hashCode(),
                delegate[MapEntry(entry.first.hashCode())].flatMap { x -> x.value }
                        .getOrElse(LinkedList<Pair<K, V>>()).filter { it.first != entry.first }
                        .splitWhen { current ->
                            entry.first.let { entry_key ->
                                current.first.let { current_key ->
                                    entry_key <= current_key
                                }
                            }
                        }.let {
                            it.first.concat(it.second.cons(entry))
                        }
        ))

class MapEntry<K: Any, V>
private constructor(internal val key: K, val value: Result<V>): Comparable<MapEntry<K, V>> {

    override fun compareTo(other: MapEntry<K, V>): Int = key.hashCode().compareTo(other.key.hashCode())

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