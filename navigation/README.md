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
│   ├── routes/               # Destination markers & type enums
│   └── utils/                # AppNavigator interface & NavigationType sealed class
│
└── feat-navigation/          # Feature-specific implementation
    ├── data/repos/           # NavigationRepositoryImpl
    ├── di/                   # Koin dependency injection module
    └── utils/               # AppNavigatorImpl
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
| **AppNavigator** | Core operations: `navigateTo()`, `navigateBack()`, `navigateAndClearBackStack()` |
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

### 2. AppNavigator (The Core Controller)

**Bind this inside your NavHost composable using `LaunchedEffect`.** The AppNavigator is the low-level controller that manages the actual navigation stack. All destinations implement the `Destination` interface (which extends `NavKey`) so they can be used directly as back stack keys.

```kotlin
// Destination marker interface — all screens implement this
interface Destination : NavKey {
    val destinationType: DestinationType
}

// Top-level destinations for the bottom bar
enum class BottomDestination(val destination: Destination) {
    MyHubs(destination = HubDest.OwnedHubs()),
    SharedHubs(destination = HubDest.SharedHubs()),
    Notifications(destination = NotificationsDest.Notifications),
    Menu(destination = MenuDest.Menu)
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val appNavigator = koinInject<AppNavigator>()
    val navigationRepository = koinInject<NavigationRepository>()

    // Build the navigation state with all top-level routes
    val navigationState = rememberNavigationState(
        startRoute = AuthDest.Login,
        topLevelRoutes = buildSet {
            add(AuthDest.Login)
            addAll(BottomDestination.entries.map { it.destination })
        }
    )

    // CRITICAL: Bind AppNavigator to the state before any navigation occurs
    LaunchedEffect(Unit) {
        appNavigator.bind(navigationState = navigationState)
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

**Use `AppNavigator` directly only when:** You need low-level back stack operations outside the repository abstraction (e.g., a custom use case or service layer). For all normal navigation, go through `NavigationRepository`.

---

### 3. NavigationState (The Mutable State Container)

**Never instantiate directly — use `rememberNavigationState` composable.** The state object holds the mutable navigation graph data.

```kotlin
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    // Serializes the state so it survives configuration changes and process death
    val mainRoute = rememberSerializable(startRoute, topLevelRoutes) { mutableStateOf(startRoute) }
    val topLevelRoute = rememberSerializable(startRoute, topLevelRoutes) { mutableStateOf(startRoute) }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }
    
    return NavigationState(mainRoute, topLevelRoute, backStacks)
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
| **Other**        | ❌      | ❌          | ❌             |

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
    // Serializes the state so it survives process death
    val mainRoute = rememberSerializable(startRoute, topLevelRoutes) { mutableStateOf(startRoute) }
    // ... similar for topLevelRoute and backStacks
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
- **DestinationType**: Enum defining screen types (MAIN, SIDE, etc.) for bar visibility rules
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
