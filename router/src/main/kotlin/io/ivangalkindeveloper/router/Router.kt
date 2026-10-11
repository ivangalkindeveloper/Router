package io.ivangalkindeveloper.router

import java.util.Collections
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Every push has its own UI identity, even when the same route is pushed twice. */
data class RouteEntry<out R : Route<*>>(val route: R, val key: String = UUID.randomUUID().toString())

@ConsistentCopyVisibility
data class RouterState<S : Any, H : Any, F : Any> internal constructor(
    val rootEntry: RouteEntry<RouteScreen<S>>,
    val screenEntries: List<RouteEntry<RouteScreen<S>>> = emptyList(),
    val sheetEntry: RouteEntry<RouteSheet<H>>? = null,
    val fullScreenCoverEntry: RouteEntry<RouteFullScreenCover<F>>? = null,
) {
    val rootRoute: RouteScreen<S> get() = rootEntry.route
    val path: List<RouteScreen<S>> get() = Collections.unmodifiableList(screenEntries.map { it.route })
    val sheet: RouteSheet<H>? get() = sheetEntry?.route
    val fullScreenCover: RouteFullScreenCover<F>? get() = fullScreenCoverEntry?.route
    val currentScreenEntry: RouteEntry<RouteScreen<S>> get() = screenEntries.lastOrNull() ?: rootEntry
}

interface RouterProtocol<Screen : Any, Sheet : Any, FullScreenCover : Any> {
    val state: StateFlow<RouterState<Screen, Sheet, FullScreenCover>>
    val path: List<RouteScreen<Screen>>
    val rootRoute: RouteScreen<Screen>
    val sheet: RouteSheet<Sheet>?
    val fullScreenCover: RouteFullScreenCover<FullScreenCover>?
    fun push(route: RouteScreen<Screen>)
    fun root(route: RouteScreen<Screen>)
    fun push(route: RouteSheet<Sheet>)
    fun push(route: RouteFullScreenCover<FullScreenCover>)
    fun pop()
    fun popRoot()
    fun dismissSheet()
    fun dismissFullScreenCover()
}

/** Thread-safe navigation model, usable without Compose and without an Android runtime. */
class Router<Screen : Any, Sheet : Any, FullScreenCover : Any>(initialRoute: RouteScreen<Screen>) :
    RouterProtocol<Screen, Sheet, FullScreenCover> {
    private val mutableState = MutableStateFlow(RouterState<Screen, Sheet, FullScreenCover>(RouteEntry(initialRoute)))
    override val state: StateFlow<RouterState<Screen, Sheet, FullScreenCover>> = mutableState.asStateFlow()
    override val path get() = state.value.path
    override val rootRoute get() = state.value.rootRoute
    override val sheet get() = state.value.sheet
    override val fullScreenCover get() = state.value.fullScreenCover

    override fun push(route: RouteScreen<Screen>) {
        val entry = RouteEntry(route)
        mutableState.update { it.copy(screenEntries = Collections.unmodifiableList(it.screenEntries + entry)) }
    }

    /** Matches Swift: replacing the root does not clear the path or dismiss modals. */
    override fun root(route: RouteScreen<Screen>) {
        val entry = RouteEntry(route)
        mutableState.update { it.copy(rootEntry = entry) }
    }

    override fun push(route: RouteSheet<Sheet>) {
        val entry = RouteEntry(route)
        mutableState.update { it.copy(sheetEntry = entry) }
    }

    override fun push(route: RouteFullScreenCover<FullScreenCover>) {
        val entry = RouteEntry(route)
        mutableState.update { it.copy(fullScreenCoverEntry = entry) }
    }

    override fun pop() {
        mutableState.update {
            if (it.screenEntries.isEmpty()) it
            else it.copy(screenEntries = Collections.unmodifiableList(it.screenEntries.dropLast(1)))
        }
    }

    override fun popRoot() { mutableState.update { it.copy(screenEntries = emptyList()) } }
    override fun dismissSheet() { mutableState.update { it.copy(sheetEntry = null) } }
    override fun dismissFullScreenCover() { mutableState.update { it.copy(fullScreenCoverEntry = null) } }
}
