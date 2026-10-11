package io.ivangalkindeveloper.router.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ivangalkindeveloper.router.*

interface RouterBuilderProtocol<Screen : Any, Sheet : Any, FullScreenCover : Any> {
    @Composable fun build(route: RouteScreen<Screen>)
    @Composable fun build(route: RouteSheet<Sheet>)
    @Composable fun build(route: RouteFullScreenCover<FullScreenCover>)
}

/** Equivalent to SwiftUI's environmentObject(router). */
val LocalRouter = staticCompositionLocalOf<RouterProtocol<*, *, *>?> { null }

/** The requested type parameters must match those supplied to the surrounding RouterView. */
@Composable
@Suppress("UNCHECKED_CAST")
fun <S : Any, H : Any, F : Any> currentRouter(): RouterProtocol<S, H, F> =
    checkNotNull(LocalRouter.current) { "currentRouter() must be called inside RouterView" } as RouterProtocol<S, H, F>

/** Remembered only for this composition. Keep a Router in a ViewModel to survive recreation. */
@Composable
fun <S : Any, H : Any, F : Any> rememberRouter(initialRouteScreen: RouteScreen<S>): Router<S, H, F> =
    remember { Router(initialRouteScreen) }

@Composable
fun <S : Any, H : Any, F : Any> RouterView(
    builder: RouterBuilderProtocol<S, H, F>,
    initialRouteScreen: RouteScreen<S>,
    modifier: Modifier = Modifier,
) {
    val router = rememberRouter<S, H, F>(initialRouteScreen)
    RouterView(router = router, builder = builder, modifier = modifier)
}

/** Android screen stack, bottom sheet and full-screen modal host. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <S : Any, H : Any, F : Any> RouterView(
    router: RouterProtocol<S, H, F>,
    builder: RouterBuilderProtocol<S, H, F>,
    modifier: Modifier = Modifier,
) {
    val state by router.state.collectAsStateWithLifecycle()
    val holder = rememberSaveableStateHolder()
    val activeKeys = buildSet {
        add(state.rootEntry.key)
        addAll(state.screenEntries.map { it.key })
        state.sheetEntry?.let { add(it.key) }
        state.fullScreenCoverEntry?.let { add(it.key) }
    }
    var previousKeys by remember { mutableStateOf(emptySet<String>()) }
    // Remove saved state only after a destination has left the navigation model.
    LaunchedEffect(activeKeys) {
        (previousKeys - activeKeys).forEach(holder::removeState)
        previousKeys = activeKeys
    }

    CompositionLocalProvider(LocalRouter provides router) {
        BackHandler(enabled = state.screenEntries.isNotEmpty() && state.sheet == null && state.fullScreenCover == null) {
            router.pop()
        }
        Box(modifier = modifier.fillMaxSize()) {
            val screen = state.currentScreenEntry
            holder.SaveableStateProvider(screen.key) { builder.build(screen.route) }
        }
        // Compose modal order also sets dismissal precedence: cover, sheet, then screen.
        state.sheetEntry?.let { entry ->
            key(entry.key) {
                ModalBottomSheet(onDismissRequest = router::dismissSheet) {
                    holder.SaveableStateProvider(entry.key) { builder.build(entry.route) }
                }
            }
        }
        state.fullScreenCoverEntry?.let { entry ->
            key(entry.key) {
                Dialog(
                    onDismissRequest = router::dismissFullScreenCover,
                    properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false, decorFitsSystemWindows = false),
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        holder.SaveableStateProvider(entry.key) { builder.build(entry.route) }
                    }
                }
            }
        }
    }
}
