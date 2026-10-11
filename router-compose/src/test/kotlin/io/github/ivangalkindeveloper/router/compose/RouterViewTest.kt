package io.github.ivangalkindeveloper.router.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import io.github.ivangalkindeveloper.router.*
import kotlin.test.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class RouterViewTest {
    @get:Rule val compose = createComposeRule()
    private val router = Router<String, String, String>(RouteScreen("home"))
    private val builder = object : RouterBuilderProtocol<String, String, String> {
        @Composable override fun build(route: RouteScreen<String>) {
            assertSame(router, currentRouter<String, String, String>())
            var count by rememberSaveable { mutableIntStateOf(0) }
            Column {
                Text("screen:${route.type}")
                Text("count:$count")
                Button(onClick = { count++ }) { Text("increment") }
            }
        }
        @Composable override fun build(route: RouteSheet<String>) {
            Column {
                Text("sheet:${route.type}")
                Button(onClick = router::dismissSheet) { Text("close sheet") }
            }
        }
        @Composable override fun build(route: RouteFullScreenCover<String>) {
            Column {
                Text("cover:${route.type}")
                Button(onClick = router::dismissFullScreenCover) { Text("close cover") }
            }
        }
    }

    @Test fun screenStateSurvivesPushAndPopAndDuplicateRoutesAreIndependent() {
        compose.setContent { MaterialTheme { RouterView(router, builder) } }
        compose.onNodeWithText("increment").performClick()
        compose.runOnIdle { router.push(RouteScreen("profile")) }
        compose.onNodeWithText("count:0").assertExists()
        compose.onNodeWithText("increment").performClick()
        compose.runOnIdle { router.push(RouteScreen("profile")) }
        compose.onNodeWithText("count:0").assertExists()
        compose.runOnIdle { router.pop() }
        compose.onNodeWithText("count:1").assertExists()
        compose.runOnIdle { router.popRoot() }
        compose.onNodeWithText("screen:home").assertExists()
        compose.onNodeWithText("count:1").assertExists()
    }

    @Test fun sheetAndFullScreenCoverRenderAndDismiss() {
        compose.setContent { MaterialTheme { RouterView(router, builder) } }
        compose.runOnIdle { router.push(RouteSheet("filters")) }
        compose.onNodeWithText("sheet:filters").assertExists()
        compose.onNodeWithText("close sheet").performClick()
        compose.onNodeWithText("sheet:filters").assertDoesNotExist()
        compose.runOnIdle { router.push(RouteFullScreenCover("login")) }
        compose.onNodeWithText("cover:login").assertExists()
        compose.onNodeWithText("close cover").performClick()
        compose.onNodeWithText("cover:login").assertDoesNotExist()
    }

    @Test fun androidBackDismissesModalsThenPopsScreen() {
        compose.setContent { MaterialTheme { RouterView(router, builder) } }
        compose.runOnIdle { router.push(RouteScreen("profile")) }
        compose.runOnIdle { router.push(RouteSheet("filters")) }
        compose.onNodeWithText("sheet:filters").assertExists()
        Espresso.pressBack()
        compose.onNodeWithText("sheet:filters").assertDoesNotExist()
        compose.onNodeWithText("screen:profile").assertExists()
        compose.runOnIdle { router.push(RouteFullScreenCover("login")) }
        compose.onNodeWithText("cover:login").assertExists()
        Espresso.pressBack()
        compose.onNodeWithText("cover:login").assertDoesNotExist()
        Espresso.pressBack()
        compose.onNodeWithText("screen:home").assertExists()
    }
}
