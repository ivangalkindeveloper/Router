# Router для Kotlin / Android

Порт Swift-пакета `Router`: типизированные `RouteScreen`, `RouteSheet`, `RouteFullScreenCover`, аргументы маршрута, стек экранов, смена корня и закрытие модальных окон. Android-аналог `RouterView` реализован на Jetpack Compose.

Модули:

- `router` — JVM JAR: маршруты, `RouterProtocol`, потокобезопасный `Router` и состояние в `StateFlow`.
- `router-compose` — Android AAR: `RouterView`, `RouterBuilderProtocol`, `LocalRouter`, `currentRouter` и `rememberRouter`.
- `sample` — запускаемое Android-приложение с профилями, sheet и полноэкранным login.

## Сборка и подключение

JDK 17, Android SDK 36, Build Tools 35.0.0, Android API 23+. Версии: Gradle 8.13, AGP 8.11.1, Kotlin 2.2.21, Compose BOM 2025.09.01. SDK задайте через `ANDROID_HOME` или исключённый из Git файл `local.properties` (`sdk.dir=/path/to/Android/sdk`).

```sh
./gradlew test assembleDebug lintDebug publishToMavenLocal
```

В settings Android-проекта добавьте `mavenLocal()` в `dependencyResolutionManagement.repositories`, сохранив `google()` и `mavenCentral()`:

```kotlin
dependencies {
    implementation("io.github.ivangalkindeveloper:router-compose:1.0.0")
    // router подключается транзитивно.
}
```

В приложении должны быть включены `buildFeatures { compose = true }` и Kotlin Compose compiler plugin той же версии, что и Kotlin. Для использования модели без Compose достаточно зависимости `io.github.ivangalkindeveloper:router:1.0.0`.

Исходники можно подключить через `includeBuild("/absolute/path/to/Kotlin/Router")` вместо локальной публикации, сохранив эти строки зависимостей.

## Маршруты

```kotlin
enum class Screen { HOME, PROFILE }
enum class Sheet { FILTERS }
enum class FullScreen { LOGIN }
data class ProfileArguments(val userId: String) : RouteArgumentsProtocol

val router = Router<Screen, Sheet, FullScreen>(RouteScreen(Screen.HOME))
router.push(RouteScreen(Screen.PROFILE, ProfileArguments("user1")))
router.push(RouteSheet(Sheet.FILTERS))
router.push(RouteFullScreenCover(FullScreen.LOGIN))
router.dismissFullScreenCover()
router.dismissSheet()
router.pop()
router.popRoot()
router.root(RouteScreen(Screen.HOME))
```

Типы `Screen`, `Sheet`, `FullScreenCover` должны иметь корректные, стабильные в течение жизни объекта `equals`/`hashCode`; обычно это `enum` или неизменяемые data classes. Аргументы реализуют маркер `RouteArgumentsProtocol`.

## Compose

```kotlin
class Builder : RouterBuilderProtocol<Screen, Sheet, FullScreen> {
    @Composable override fun build(route: RouteScreen<Screen>) {
        val router = currentRouter<Screen, Sheet, FullScreen>()
        when (route.type) {
            Screen.HOME -> Button(onClick = {
                router.push(RouteScreen(Screen.PROFILE, ProfileArguments("user1")))
            }) { Text("Open profile") }
            Screen.PROFILE -> Text((route.arguments as? ProfileArguments)?.userId.orEmpty())
        }
    }
    @Composable override fun build(route: RouteSheet<Sheet>) {
        Text("Filters")
    }
    @Composable override fun build(route: RouteFullScreenCover<FullScreen>) {
        Text("Login")
    }
}

@Composable fun App(router: Router<Screen, Sheet, FullScreen>) {
    MaterialTheme { RouterView(router = router, builder = Builder()) }
}
```

Храните `Router` в Android `ViewModel`, чтобы стек переживал пересоздание Activity; работающий пример есть в `sample`. Для простых сценариев существует `RouterView(builder = Builder(), initialRouteScreen = RouteScreen(Screen.HOME))`, который создаёт router через `remember`.

`currentRouter` — аналог SwiftUI `environmentObject`; параметры типов должны совпадать с ближайшим `RouterView`. Вложенные hosts получают собственный router. `LocalRouter.current` позволяет получить интерфейс без приведения к конкретным типам.

## Поведение и отличия от Swift

| Swift | Android |
|---|---|
| `NavigationPath` | `state.value.path` и уникальные `screenEntries` |
| `ObservableObject` / `@Published` | Один согласованный `StateFlow<RouterState<...>>` |
| `.sheet` | Material 3 `ModalBottomSheet` |
| `.fullScreenCover` | Полноэкранный Compose `Dialog` |
| `environmentObject(router)` | `LocalRouter` / `currentRouter` |

Как в исходнике, `root(route)` заменяет корневой экран, сохраняя path и модальные окна. Для полного сброса вызовите также `popRoot()` и нужные методы dismiss. `pop()` на пустом стеке безопасно ничего не делает.

Equality маршрута учитывает вид представления, `type` и `id`, но не аргументы. `id == type.hashCode()`, поэтому это не идентификатор для долговременного хранения. Каждый push имеет отдельный `RouteEntry.key`, даже для двух одинаковых маршрутов; это сохраняет независимость `rememberSaveable` на экранах. Повторное открытие модального окна также обновляет его аргументы и состояние.

Android Back закрывает полноэкранное окно, затем sheet, затем верхний экран. На корневом экране Back передаётся Activity. Стандартные анимации модальных окон предоставляет Compose; анимации переходов между экранами и predictive back в этом порте не реализованы. `RouterView` сохраняет `rememberSaveable` предыдущих экранов, а обычный `remember` заново создаётся при возврате.

Все изменения состояния атомарны, старые snapshots и коллекции не изменяются. Аргументы и значения типов маршрутов остаются объектами приложения, поэтому используйте неизменяемые модели.

ViewModel сохраняет модель при изменениях конфигурации. Восстановление стека после смерти процесса не автоматическое: для произвольных типов и аргументов приложение должно определить их сериализацию и восстановить модель через `SavedStateHandle` или собственное хранилище. Вариант с `remember` живёт только до удаления composition.

## Пример и проверки

Откройте этот каталог в Android Studio и запустите модуль `sample`. Сборка APK: `./gradlew :sample:assembleDebug`; файл находится в `sample/build/outputs/apk/debug/`.

Тесты JVM проверяют операции со стеком, аргументы, equality, дубликаты, неизменяемость snapshots и одновременные push. Compose-тесты на Robolectric проверяют сохранение состояния экрана, повторные маршруты и показ/закрытие обоих типов модальных окон. GitHub Actions выполняет сборку, тесты и Android Lint.

## Отправка на GitHub

Создайте пустой репозиторий, например `kotlin-router`:

```sh
git remote add origin git@github.com:YOUR_USERNAME/kotlin-router.git
git push -u origin main
```

Maven-публикации обоих модулей уже настроены для `publishToMavenLocal`. Адрес и credentials удалённого Maven-репозитория настраиваются отдельно.
