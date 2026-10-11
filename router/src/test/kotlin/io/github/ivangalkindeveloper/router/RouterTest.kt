package io.github.ivangalkindeveloper.router

import kotlin.test.*
import kotlinx.coroutines.*
import org.junit.Test

class RouterTest {
    enum class Screen { HOME, PROFILE }
    enum class Sheet { FILTERS }
    enum class Cover { LOGIN }
    data class Arguments(val userId: Int) : RouteArgumentsProtocol
    private fun router() = Router<Screen, Sheet, Cover>(RouteScreen(Screen.HOME))

    @Test fun routesIgnoreArgumentsAndDistinguishPresentationKind() {
        assertEquals(RouteScreen(Screen.PROFILE, Arguments(1)), RouteScreen(Screen.PROFILE, Arguments(2)))
        assertEquals(RouteScreen(Screen.PROFILE).hashCode(), RouteScreen(Screen.PROFILE, Arguments(2)).hashCode())
        assertNotEquals<Any>(RouteScreen(Screen.PROFILE), RouteSheet(Screen.PROFILE))
        val first = object { override fun hashCode() = 1 }
        val second = object { override fun hashCode() = 1 }
        assertNotEquals<Any>(RouteScreen(first), RouteScreen(second))
    }

    @Test fun pathArgumentsAndDuplicateEntryIdentity() {
        val r = router()
        val route = RouteScreen(Screen.PROFILE, Arguments(1))
        r.push(route); r.push(route)
        assertEquals(2, r.path.size)
        assertSame(route.arguments, r.path.last().arguments)
        assertNotEquals(r.state.value.screenEntries[0].key, r.state.value.screenEntries[1].key)
        r.pop(); assertEquals(1, r.path.size)
        r.popRoot(); assertTrue(r.path.isEmpty())
        r.pop(); assertEquals(Screen.HOME, r.rootRoute.type)
    }

    @Test fun rootReplacementPreservesPathAndModals() {
        val r = router()
        r.push(RouteScreen(Screen.PROFILE)); r.push(RouteSheet(Sheet.FILTERS)); r.push(RouteFullScreenCover(Cover.LOGIN))
        r.root(RouteScreen(Screen.PROFILE))
        assertEquals(1, r.path.size)
        assertNotNull(r.sheet); assertNotNull(r.fullScreenCover)
        r.dismissSheet(); assertNull(r.sheet); assertNotNull(r.fullScreenCover)
        r.dismissFullScreenCover(); assertNull(r.fullScreenCover)
    }

    @Test fun pathSnapshotsCannotBeMutated() {
        val r = router(); r.push(RouteScreen(Screen.PROFILE))
        val snapshot = r.state.value
        assertFailsWith<UnsupportedOperationException> { (snapshot.path as MutableList).clear() }
        assertFailsWith<UnsupportedOperationException> { (snapshot.screenEntries as MutableList).clear() }
        r.pop()
        assertEquals(1, snapshot.path.size)
    }

    @Test fun simultaneousPushesAreNotLost() = runBlocking {
        val r = router()
        coroutineScope { repeat(100) { launch(Dispatchers.Default) { r.push(RouteScreen(Screen.PROFILE)) } } }
        assertEquals(100, r.path.size)
        assertEquals(100, r.state.value.screenEntries.map { it.key }.toSet().size)
    }
}
