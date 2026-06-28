# Navigation Module

## Overview

The navigation module orchestrates screen transitions, back stack management, and UI state coordination using **Jetpack Navigation 3**. It follows the Repository pattern to decouple business logic from implementation details, with a clean separation between core infrastructure (`core-navigation`) and feature-specific implementations (`feat-navigation`).

---

## Module Structure

```
navigation/
├── core-navigation/          # Core infrastructure (shared across features)
│   ├── domain/               # Pure Kotlin interfaces & data classes
│   │   ├── repos/            # Repository interface & data types
│   │   └── NavigationState.kt  # State management for the navigation graph
│   ├── routes/               # Destination & DestinationType
│   └── utils/                # NavigationType sealed class
│
└── feat-navigation/          # Feature-specific implementation
    ├── data/                 # AppNavigator (internal data source)
    │   └── repos/            # NavigationRepositoryImpl
    └── di/                   # Koin dependency injection module
```

---

## Architecture Flow

```
┌─────────────────────────────────────────────────────────────┐
│  UI Composables (Collecting StateFlow)                      │
│  - BottomBar, TopBar, SideNav indicators                     │
│  - Screen-specific logic based on current destination        │
└───────────────────┬─────────────────────────────────────────┘
                    ↓ Collects navigationData StateFlow
┌─────────────────────────────────────────────────────────────┐
│  NavigationRepository (feat-navigation)                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Public API for all navigation actions                │   │
│  │ - navigate(NavigationType)                            │   │
│  │ - Exposes navigationData StateFlow                   │   │
│  └──────────────────────────────────────────────────────┘   │
│  ↓ Dispatches to AppNavigator based on NavigationType        │
└───────────────────┬─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────────────┐
│  AppNavigator (feat-navigation)                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Core operations: navigateTo(), navigateBack()        │   │
│  │ navigateAndClearBackStack()                           │   │
│  │ getCurrentDestination(), getPreviousDestination()     │   │
│  └──────────────────────────────────────────────────────┘   │
│  ↓ Mutates NavigationState (core-navigation)                 │
└───────────────────┬─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────────────┐
│  NavigationState (core-navigation)                           │
│  - mainRoute: Route to exit the app                          │
│  - topLevelRoute: Currently selected top-level route        │
│  - backStacks: Map of routes → NavBackStack<NavKey>         │
│  ↓ Provides reactive state via StateFlow                     │
└───────────────────┬─────────────────────────────────────────┘
                    ↓ Exposes via toEntries()
┌─────────────────────────────────────────────────────────────┐
│  Navigation 3 Runtime (androidx.navigation3.runtime)         │
│  - NavEntry, NavKey, rememberNavBackStack                   │
│  - rememberSaveableStateHolderNavEntryDecorator             │
│  - rememberViewModelStoreNavEntryDecorator                   │
│  - rememberDecoratedNavEntries                               │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Components

| Component | Purpose |
|-----------|---------|
| **NavigationRepository** | Public API for all navigation actions. Handles state updates, bar visibility logic, and destination tracking |
| **AppNavigator** | Internal data source: `navigateTo()`, `navigateBack()`, `navigateAndClearBackStack()`. Not exposed outside `feat-navigation` |
| **NavigationState** | Mutable state object managed by AppNavigator implementation |

## Navigation Types

- **Standard**: Push new screen onto back stack (default behavior)
- **Back**: Pop from current back stack level
- **ClearBackStack**: Navigate to destination and clear all previous history

---

## Component Details

### 1. NavigationRepository (The Entry Point)

**Use this for all navigation logic.** The repository abstracts away the complexity of back stack management and bar visibility rules.

```kotlin
// Injected via Koin in your NavHost or composable:
val navigationRepository = koinInject<NavigationRepository>()

// Pass navigate as a lambda into each feature graph
NavDisplay(
    entries = navigationState.toEntries(
        entryProvider = entryProvider {
            authGraph(navigationRepository::navigate)
            ownedHubsGraph(navigationRepository::navigate)
        }
    ),
    onBack = { navigationRepository.navigate(NavigationType.Back) }
)
```

**Behind the scenes:** The repository handles:
- Dispatching to `AppNavigator` based on navigation type (Standard, Back, ClearBackStack)
- Retrieving current/previous destinations via `getCurrentDestination()` and `getPreviousDestination()`
- Updating bar visibility states (top/bottom bars) based on destination type
- Maintaining the reactive state flow for UI components to observe

---

### 2. AppNavigator (Internal Data Source)

**Never inject or use this directly.** `AppNavigator` is a concrete class internal to `feat-navigation` that `NavigationRepositoryImpl` delegates to. It is not an interface and is not exposed outside the module. All navigation goes through `NavigationRepository`.

Binding happens via `NavigationRepository.bind()`, which delegates internally to `AppNavigator.bind()` and immediately syncs the initial state:

```kotlin
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navigationRepository = koinInject<NavigationRepository>()

    val navigationState = rememberNavigationState(
        startRoute = AuthDest.Login,
        topLevelRoutes = buildSet {
            add(AuthDest.Login)
            addAll(BottomDestination.entries.map { it.destination })
        }
    )

    // Bind through the repository — AppNavigator is an internal detail
    LaunchedEffect(Unit) {
        navigationRepository.bind(
            navigationState = navigationState
        )
    }

    NavDisplay(
        entries = navigationState.toEntries(
            entryProvider = entryProvider {
                authGraph(navigationRepository::navigate)
                ownedHubsGraph(navigationRepository::navigate)
                // ... other graphs
            }
        ),
        onBack = { navigationRepository.navigate(NavigationType.Back) },
        modifier = modifier
    )
}
```

The Koin module wires everything internally — `AppNavigator` is registered as a `single` but never exposed as a bound interface:

```kotlin
val navigationModule = module {
    single { AppNavigator() }
    single<NavigationRepository> {
        NavigationRepositoryImpl(appNavigator = get())
    }
}
```

---

### 3. NavigationState (The Mutable State Container)

**Never instantiate directly — use `rememberNavigationState` composable.** The state object holds the mutable navigation graph data.

```kotlin
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) { mutableStateOf(startRoute) }

    val mainRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) { mutableStateOf(startRoute) }

    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(mainRoute = mainRoute, topLevelRoute = topLevelRoute, backStacks = backStacks)
    }
}
```

**Key properties:**
- `mainRoute` — Route to exit the app (when user exits from main route)
- `topLevelRoute` — Currently selected top-level route
- `backStacks` — Map of routes → `NavBackStack<NavKey>` for each top-level route
- `currentRoute` — The route at the top of the current back stack
- `previousRoute` — The route before the current one (if any)
- `stacksInUse` — List of active top-level routes with back stacks

**Important:** The state is wrapped in `rememberSerializable` to survive configuration changes and process death.

---

## Bar Visibility Rules

The repository automatically manages bar visibility based on destination type:

| Destination Type | Top Bar | Bottom Bar | Side Navigation |
|------------------|---------|-------------|----------------|
| **MAIN**         | ✅      | ✅          | ❌             |
| **SIDE**         | ✅      | ❌          | ✅             |
| **AUTH / GRAPH / OTHER** | ❌ | ❌       | ❌             |

---

## Navigation 3 Integration Details

### State Management with `rememberNavigationState`

The module uses Kotlin's `rememberSerializable` to survive configuration changes and process death:

```kotlin
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) { mutableStateOf(startRoute) }

    val mainRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) { mutableStateOf(startRoute) }

    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(mainRoute = mainRoute, topLevelRoute = topLevelRoute, backStacks = backStacks)
    }
}
```

### Converting State to NavEntries

The `toEntries()` composable converts the internal state into `NavEntry` instances that Navigation 3 can consume:

```kotlin
@Composable
fun NavigationState.toEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider
        )
    }
    return stacksInUse.flatMap { decoratedEntries[it] ?: emptyList() }.toMutableStateList()
}
```

---

## Key Dependencies

- **AppNavigator**: The central navigation controller that manages the back stack and destination routing
- **DestinationType**: Enum defining screen types (`MAIN`, `SIDE`, `AUTH`, `GRAPH`, `OTHER`) for bar visibility rules
- **androidx.navigation3.runtime**: Navigation 3 runtime APIs: NavEntry, NavKey, rememberNavBackStack
- **Koin**: Dependency injection module for the feature navigation

---

## Developer Guidelines

### When to use which NavigationType:
- Use `Standard` for normal navigation flow (Home → Profile → Settings)
- Use `Back` when implementing back button handlers or returning from screens
- Use `ClearBackStack` when starting fresh sessions, new users, or resetting the app state

### State Management:
The repository automatically updates the state flow after every navigation action. UI components should not manually call update methods; they simply observe the `StateFlow` and react to changes.
