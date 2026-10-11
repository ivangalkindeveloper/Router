# Router

Router предоставляет типизированную навигацию: стек экранов, аргументы маршрутов, смену корневого экрана, sheet и полноэкранные модальные окна. `RouterView` связывает модель навигации с интерфейсом приложения.

В одном репозитории находятся самостоятельные реализации для SwiftUI и Android / Jetpack Compose:

| | Swift | Kotlin |
|---|---|---|
| Исходники и документация | [swift/](swift/) · [Swift README](swift/README.md) | [kotlin/](kotlin/) · [Kotlin README](kotlin/README.md) |
| Платформы | Модель: iOS 16+, macOS 13+; SwiftUI host: iOS | JVM-модель; Compose-модуль для Android API 23+ |
| Инструменты сборки | Swift 6.1+, Swift Package Manager / Xcode | JDK 17, Gradle Wrapper, Android SDK 36 |
| Модель | `NavigationPath`, `ObservableObject`, `@Published` | `StateFlow<RouterState<...>>` |
| Интерфейс | SwiftUI `NavigationStack` и `RouterView` | Jetpack Compose `RouterView` |
| Модальные окна | `.sheet`, `.fullScreenCover` | Material 3 `ModalBottomSheet`, полноэкранный `Dialog` |
| Доступ к router в UI | `environmentObject` | `LocalRouter`, `currentRouter` |
| Модуль / namespace | `Router` | `io.ivangalkindeveloper.router`, `.router.compose` |

Сохраняются `RouteScreen`, `RouteSheet`, `RouteFullScreenCover`, аргументы и операции `push`, `pop`, `popRoot`, `root`, `dismissSheet`, `dismissFullScreenCover`. В обеих реализациях `root(route)` меняет корневой экран, сохраняя стек и открытые модальные окна. Для полного сброса дополнительно вызовите `popRoot()` и методы закрытия модальных окон.

Android-реализация обрабатывает Back, хранит отдельный ключ каждой записи стека и сохраняет `rememberSaveable` экранов. Стек рекомендуется хранить в `ViewModel`; восстановление после смерти процесса требует сериализации со стороны приложения. Анимации переходов между экранами и predictive back в Kotlin-порте пока не реализованы. SwiftUI сам предоставляет нативные переходы; текущий Swift `RouterView` явно недоступен на macOS, поэтому для проверки UI используется iOS Simulator.

## Swift

Подключение через Swift Package Manager:

```swift
.package(
    url: "https://github.com/ivangalkindeveloper/Router.git",
    branch: "master"
)
```

Корневой `Package.swift` указывает на `swift/Sources` и `swift/Tests` и сохраняет подключение по прежнему URL. В `swift/` находится самостоятельный Swift-пакет. Откройте корень или `swift/` в Xcode и выберите iOS Simulator. Сборка из корня:

```sh
xcodebuild -scheme Router \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath .build/ios \
  CODE_SIGNING_ALLOWED=NO build
```

Описание маршрутов, builder и примеры SwiftUI — в [swift/README.md](swift/README.md).

## Kotlin / Android

Откройте `kotlin/` в Android Studio. Модули: `router` — JVM-модель, `router-compose` — Android UI, `sample` — запускаемый пример.

```sh
cd kotlin
./gradlew test assembleDebug lintDebug publishToMavenLocal
```

Android SDK задаётся через `ANDROID_HOME` или `kotlin/local.properties`. После локальной публикации добавьте `mavenLocal()` в repositories приложения:

```kotlin
implementation("io.ivangalkindeveloper:router-compose:1.0.0")
// Только модель навигации без Compose:
// implementation("io.ivangalkindeveloper:router:1.0.0")
```

Альтернатива — `includeBuild("/absolute/path/to/Router/kotlin")` в `settings.gradle.kts` приложения. Настройка Compose, builder, ограничения и пример — в [kotlin/README.md](kotlin/README.md). Публикация в удалённый Maven-репозиторий настраивается отдельно.

## Структура и проверки

- `swift/` — Swift Package, маршруты, SwiftUI host, тесты и документация.
- `kotlin/` — Gradle-проект, модель навигации, Compose host, Android-пример и тесты.
- `Package.swift` — вход для Swift Package Manager из корня репозитория.
- `.github/` — общие GitHub Actions; Kotlin-команды выполняются в `kotlin/`.

Реализации собираются независимо. Истории коммитов объединены без переписывания существующей Swift-истории. Kotlin-тесты проверяют модель и поведение Compose; Swift UI собирается для iOS Simulator.
