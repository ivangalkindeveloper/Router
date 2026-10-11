package io.github.ivangalkindeveloper.router.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.ivangalkindeveloper.router.*
import io.github.ivangalkindeveloper.router.compose.*

enum class Screen { HOME, PROFILE }
enum class Sheet { FILTERS }
enum class Cover { LOGIN }
data class ProfileArguments(val name: String) : RouteArgumentsProtocol

class NavigationModel : ViewModel() {
    val router = Router<Screen, Sheet, Cover>(RouteScreen(Screen.HOME))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val model: NavigationModel = viewModel()
                RouterView(router = model.router, builder = SampleBuilder())
            }
        }
    }
}

class SampleBuilder : RouterBuilderProtocol<Screen, Sheet, Cover> {
    @Composable override fun build(route: RouteScreen<Screen>) {
        val router = currentRouter<Screen, Sheet, Cover>()
        var count by rememberSaveable { mutableIntStateOf(0) }
        Column(Modifier.fillMaxSize().systemBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (route.type == Screen.HOME) "Home" else "Profile: ${(route.arguments as? ProfileArguments)?.name}", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { count++ }) { Text("Local state: $count") }
            Button(onClick = { router.push(RouteScreen(Screen.PROFILE, ProfileArguments("User ${router.path.size + 1}"))) }) { Text("Push profile") }
            Button(onClick = { router.push(RouteSheet(Sheet.FILTERS)) }) { Text("Show sheet") }
            Button(onClick = { router.push(RouteFullScreenCover(Cover.LOGIN)) }) { Text("Show full screen") }
            Button(onClick = router::pop) { Text("Back") }
            Button(onClick = router::popRoot) { Text("Back to root") }
        }
    }

    @Composable override fun build(route: RouteSheet<Sheet>) {
        val router = currentRouter<Screen, Sheet, Cover>()
        Column(Modifier.padding(24.dp)) {
            Text("Filters", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = router::dismissSheet) { Text("Close sheet") }
        }
    }

    @Composable override fun build(route: RouteFullScreenCover<Cover>) {
        val router = currentRouter<Screen, Sheet, Cover>()
        Column(Modifier.fillMaxSize().systemBarsPadding().padding(24.dp)) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = router::dismissFullScreenCover) { Text("Close full screen") }
        }
    }
}
