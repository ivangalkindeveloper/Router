package io.ivangalkindeveloper.router

/** Marker for typed payloads, equivalent to the Swift RouteArgumentsProtocol. */
interface RouteArgumentsProtocol

interface Route<out T : Any> {
    val id: Int
    val type: T
    val arguments: RouteArgumentsProtocol?
}

/** Equality follows Swift: presentation kind, type and id; arguments are not identity. */
sealed class BaseRoute<T : Any>(
    final override val type: T,
    final override val arguments: RouteArgumentsProtocol?,
) : Route<T> {
    final override val id: Int = type.hashCode()
    final override fun hashCode(): Int = type.hashCode()
    final override fun equals(other: Any?): Boolean =
        this === other || (other is BaseRoute<*> && javaClass == other.javaClass && id == other.id && type == other.type)

    override fun toString(): String = "${javaClass.simpleName}(type=$type, arguments=$arguments)"
}

class RouteScreen<T : Any>(type: T, arguments: RouteArgumentsProtocol? = null) : BaseRoute<T>(type, arguments)
class RouteSheet<T : Any>(type: T, arguments: RouteArgumentsProtocol? = null) : BaseRoute<T>(type, arguments)
class RouteFullScreenCover<T : Any>(type: T, arguments: RouteArgumentsProtocol? = null) : BaseRoute<T>(type, arguments)
