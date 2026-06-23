# TrackHub

A personal inventory tracker for everyday essentials — groceries, kitchen supplies, and more. Know what you have, what's running low, and what needs restocking at a glance.

Hubs can be shared with others: owners can invite users by searching their username (GitHub-style) and assigning them as **editors** or **viewers**.

---

## Screenshots

### Hubs

| My Hubs | Hub Details | Shared Hubs |
|---------|-------------|-------------|
| ![My Hubs](screenshots/my_hubs.png) | ![Hub Details](screenshots/hub_details.png) | ![Shared Hubs](screenshots/shared_hubs.png) |

### Dialogs

| Invite User | Item Details |
|-------------|-------------|
| ![Invite User](screenshots/invite_user_dialog.png) | ![Item Details](screenshots/item_details_dialog.png) |

### Notifications

| Notifications |
|--------------|
| ![Notifications](screenshots/notifications.png) |

---

## Stack

| Layer | Tech |
|-------|------|
| Language | Kotlin |
| UI | Jetpack Compose · Material 3 |
| Navigation | Jetpack Navigation 3 |
| DI | Koin |
| Architecture | MVI · feature modularization |
| Backend / Auth | Supabase (Auth · PostgREST · Realtime · Storage) |
| Networking | Ktor (CIO engine) |
| Local cache | Room · Paging 3 |
| Persistence | DataStore |
| Image loading | Coil 3 |
| CI | GitHub Actions |

---

## Module Structure

<details>
<summary><code>:app</code> — entry point, nav host, bottom nav</summary>

```
app/src/main/java/com/skewnexus/trackhub/
├── MainActivity.kt
├── navigation/
│   ├── AppNavHost.kt
│   ├── components/
│   │   ├── BottomDestination.kt
│   │   └── BottomNavigationBar.kt
│   ├── graphs/
│   │   ├── AuthGraph.kt
│   │   ├── OwnedHubsGraph.kt
│   │   ├── SharedHubsGraph.kt
│   │   ├── NotificationsGraph.kt
│   │   └── MenuGraph.kt
│   └── utils/
│       └── SessionDestinationHandler.kt
└── di/
    └── AppModule.kt
```
</details>

<details>
<summary><code>:core:core-ui</code> — shared Compose components, theme, base ViewModel</summary>

```
core/core-ui/src/main/java/com/greenvenom/core_ui/
├── components/
│   ├── bars/          (TopAppBar, AppBarSearchBar)
│   ├── buttons/       (CustomButton, FilterDropdown, FloatingButton, …)
│   ├── dialogs/       (ErrorDialog, LoadingDialog, SuccessDialog, …)
│   └── text/          (CustomTextField, CustomMultilineTextField)
├── presentation/
│   ├── BaseScreen.kt
│   ├── BaseViewModel.kt
│   └── ScaffoldViewModel.kt
└── theme/             (Color, Type, Theme)
```
</details>

<details>
<summary><code>:core:core-util</code> — input validation, locale, phone formatting</summary>

```
core/core-util/src/main/java/com/greenvenom/core_util/
├── input/             (InputValidator, ValidationResult)
├── locale/            (LocaleManager, NormalizeDigits)
├── phone/             (PhoneNumberManager)
└── theme/             (ThemeManager)
```
</details>

<details>
<summary><code>:network:core-network</code> — NetworkResult, error types, Supabase call wrappers</summary>

```
network/core-network/src/main/java/com/greenvenom/core_network/
├── data/              (NetworkResult, NetworkError, ErrorType)
├── domain/            (SessionRepository, SessionDestination)
└── supabase/utils/    (SupabaseCall, SupabaseRealtimeCall)
```
</details>

<details>
<summary><code>:network:feat-network</code> — Supabase client factory, session repository</summary>

```
network/feat-network/src/main/java/com/trackhub/feat_network/
├── data/
│   ├── ClientFactory.kt
│   └── repository/SupabaseSessionRepository.kt
└── di/NetworkFeatureModule.kt
```
</details>

<details>
<summary><code>:navigation:core-navigation</code> — navigation contracts and state</summary>

```
navigation/core-navigation/src/main/java/com/greenvenom/core_navigation/
├── domain/
│   ├── NavigationState.kt
│   └── repos/NavigationRepository.kt
└── routes/            (Destination, DestinationType)
```
</details>

<details>
<summary><code>:navigation:feat-navigation</code> — AppNavigator implementation</summary>

```
navigation/feat-navigation/src/main/java/com/trackhub/feat_navigation/
├── data/
│   ├── AppNavigator.kt
│   └── repos/NavigationRepositoryImpl.kt
└── di/NavigationModule.kt
```
</details>

<details>
<summary><code>:auth:core-auth</code> — auth DTOs, session state contracts</summary>

```
auth/core-auth/src/main/java/com/greenvenom/core_auth/
├── data/dto/request/  (LoginRequest, RegisterRequest, ResetPasswordRequest, …)
└── data/repository/   (EmailState, EmailStateRepository)
```
</details>

<details>
<summary><code>:auth:feat-auth</code> — login, register, OTP, reset password screens</summary>

```
auth/feat-auth/src/main/java/com/greenvenom/feat_auth/
├── data/              (AuthSupabaseDataSource, AuthRepositoryImpl)
├── domain/            (AuthRepository, AuthRemoteDataSource)
└── presentation/
    ├── login/         (LoginScreen, LoginViewModel)
    ├── register/      (RegisterScreen, RegisterViewModel)
    ├── otp/           (OtpScreen, OtpViewModel)
    ├── reset_password/(VerifyEmailScreen, NewPasswordScreen)
    └── splash/        (SplashScreen)
```
</details>

<details>
<summary><code>:hub:core-hub</code> — hub/item models, DAOs, remote DTOs, mappers</summary>

```
hub/core-hub/src/main/java/com/trackhub/core_hub/
├── data/
│   ├── cache/         (HubDao, ItemDao, HubEntity, ItemEntity, ItemFts)
│   ├── mappers/       (HubMapper, ItemMapper, HubMemberMapper, …)
│   └── remote/dto/    (request & response DTOs)
└── domain/
    ├── models/        (Hub, Item, HubMember, UserSearch)
    └── enums/         (HubRole, BaseUnit, InvitationStatus, MemberStatus)
```
</details>

<details>
<summary><code>:hub:feat-hub</code> — hub list, hub details, invite flow</summary>

```
hub/feat-hub/src/main/java/com/trackhub/feat_hub/
├── data/              (SupabaseHubDataSource, HubRoomDataSource, repos)
├── domain/            (HubRepository, HubInvitationsRepository)
└── presentation/
    ├── hub_list/      (HubListScreen, HubListViewModel)
    ├── hub_details/   (HubDetailsScreen, HubDetailsViewModel)
    └── components/    (HubListCard, ItemListCard, InviteUserDialog, ItemDetailsDialog, …)
```
</details>

<details>
<summary><code>:local:core-local</code> — Room type converters</summary>

```
local/core-local/src/main/java/com/trackhub/core_local/
└── utils/ListStringConverters.kt
```
</details>

<details>
<summary><code>:local:feat-local</code> — database definition, DI</summary>

```
local/feat-local/src/main/java/com/trackhub/feat_local/
├── data/db/TrackHubDatabase.kt
└── di/LocalModule.kt
```
</details>

<details>
<summary><code>:menu:core-menu</code> — profile model, DAO</summary>

```
menu/core-menu/src/main/java/com/trackhub/core_menu/
├── data/              (ProfileDao, ProfileEntity, ProfileDto)
└── domain/Profile.kt
```
</details>

<details>
<summary><code>:menu:feat-menu</code> — settings / menu screen</summary>

```
menu/feat-menu/src/main/java/com/trackhub/feat_menu/
├── data/              (MenuSupabaseDataSource, MenuRoomDataSource, MenuRepositoryImpl)
├── domain/            (MenuRepository)
└── presentation/
    ├── MenuScreen.kt
    ├── MenuViewModel.kt
    └── components/    (AccountHeader, SettingCard, AppVersionItem)
```
</details>

<details>
<summary><code>:notifications:core-notifications</code> — invitation models, mappers</summary>

```
notifications/core-notifications/src/main/java/com/trackhub/core_notifications/
├── data/              (mappers, remote DTOs)
└── domain/models/     (HubInvitation, InvitationAcceptance)
```
</details>

<details>
<summary><code>:notifications:feat-notifications</code> — notifications screen, invitation accept/decline</summary>

```
notifications/feat-notifications/src/main/java/com/trackhub/feat_notifications/
├── data/              (SupabaseHubInvitations, NotificationsRepositoryImpl)
├── domain/            (NotificationsRepository, HubInvitationsDataSource)
└── presentation/
    ├── screens/NotificationsScreen.kt
    ├── viewmodel/NotificationsViewModel.kt
    └── components/HubInvitationItem.kt
```
</details>

---

## License

[Apache License 2.0](LICENSE)
