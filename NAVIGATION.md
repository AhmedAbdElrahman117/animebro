# Navigation Implementation in AnimBro App

## Overview
This app now uses **Jetpack Compose Navigation** with a single-activity architecture, replacing the previous multi-activity approach.

## Architecture

### 1. Single Activity (`MainActivity`)
- **File**: `MainActivity.kt`
- All screens are now composable functions within a single `MainActivity`
- Uses `@AndroidEntryPoint` for Hilt dependency injection
- Simply calls `AppNavigation()` composable

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnimBroTheme {
                AppNavigation()
            }
        }
    }
}
```

### 2. Navigation Routes (`Screen.kt`)
- **File**: `navigation/Screen.kt`
- Defines all app routes using a sealed class
- Type-safe navigation with parameter support

```kotlin
sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")

    // Main App Screens
    object Home : Screen("home")
    object Search : Screen("search")
    object AnimeList : Screen("anime_list")

    // Detail Screen with parameter
    object AnimeDetails : Screen("anime_details/{animeId}") {
        fun createRoute(animeId: Int) = "anime_details/$animeId"
    }
}
```

### 3. Navigation Graph (`AppNavigation.kt`)
- **File**: `navigation/AppNavigation.kt`
- Contains the `NavHost` with all composable destinations
- Handles authentication state to determine start destination
- Manages bottom navigation bar for main screens

**Key Features:**
- Checks Firebase authentication status on startup
- Routes unauthenticated users to login screen
- Routes authenticated users to home screen
- Integrates `BottomNavigationBar` for main app screens
- Passes callbacks for navigation actions

### 4. Bottom Navigation Bar
- **File**: `anime/components/BottomNavigationBar.kt`
- Updated to use `NavController` instead of Activity intents
- Maintains state across navigation
- Supports smooth transitions between screens

```kotlin
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String
) {
    // Navigation items: Home, Search, AnimeList
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
```

## Screen Structure

### Auth Screens (Composable Wrappers)
Created new composable wrappers for authentication screens:

1. **LoginScreen** (`auth/screens/LoginScreenComposable.kt`)
   - Callbacks: `onNavigateToSignUp`, `onNavigateToForgotPassword`, `onLoginSuccess`

2. **SignUpScreen** (`auth/screens/SignUpScreenComposable.kt`)
   - Callbacks: `onNavigateToLogin`, `onSignUpSuccess`

3. **ForgotPasswordScreen** (`auth/screens/ForgotPasswordScreenComposable.kt`)
   - Callbacks: `onNavigateBack`

### Main App Screens
Existing screens now integrated with navigation:

1. **HomeScreen**
   - ViewModels injected via `hiltViewModel()`
   - Includes bottom navigation bar
   - Callbacks: `onAnimeClick`, `onMoreClick`

2. **SearchScreen**
   - Includes bottom navigation bar
   - Returns anime search results

3. **UserListScreen (AnimeList)**
   - Shows user's anime lists (Watching, Completed, etc.)
   - Includes bottom navigation bar
   - Callback: `onAnimeClick`

4. **DetailScreen**
   - Full-screen anime details
   - No bottom navigation (immersive)
   - Callback: `onAnimeClick` for recommendations

## Navigation Patterns

### 1. Simple Navigation
```kotlin
// Navigate to a screen
navController.navigate(Screen.Search.route)
```

### 2. Navigation with Arguments
```kotlin
// Navigate with anime ID
navController.navigate(Screen.AnimeDetails.createRoute(animeId))
```

### 3. Navigation with Pop Behavior
```kotlin
// Navigate and clear back stack
navController.navigate(Screen.Home.route) {
    popUpTo(Screen.Login.route) { inclusive = true }
}
```

### 4. Bottom Nav Navigation (State Preservation)
```kotlin
navController.navigate(item.route) {
    popUpTo(Screen.Home.route) { saveState = true }
    launchSingleTop = true      // Don't create multiple instances
    restoreState = true          // Restore previous state
}
```

## Benefits

### 1. **Single Activity Architecture**
   - Simpler app structure
   - Reduced memory footprint
   - Faster navigation

### 2. **State Management**
   - ViewModels scoped to navigation graph
   - State preserved during navigation
   - No data loss on screen rotation

### 3. **Type-Safe Navigation**
   - Compile-time route checking
   - No string-based route errors
   - IDE autocomplete support

### 4. **Better Testing**
   - Easier to test individual screens
   - No need to manage activity lifecycle
   - Can test navigation logic separately

### 5. **Consistent User Experience**
   - Smooth transitions
   - Predictable back stack behavior
   - Better animation support

## Migration Notes

### Old Approach (Multi-Activity)
```kotlin
// Old way - Activity navigation
val intent = Intent(context, DetailActivity::class.java)
intent.putExtra("animeId", animeId)
context.startActivity(intent)
```

### New Approach (Compose Navigation)
```kotlin
// New way - Compose navigation
navController.navigate(Screen.AnimeDetails.createRoute(animeId))
```

## Adding New Screens

### Step 1: Define Route
Add to `Screen.kt`:
```kotlin
object NewScreen : Screen("new_screen")
```

### Step 2: Create Composable
```kotlin
@Composable
fun NewScreen(navController: NavController) {
    // Screen content
}
```

### Step 3: Add to Navigation Graph
In `AppNavigation.kt`:
```kotlin
composable(Screen.NewScreen.route) {
    NewScreen(navController = navController)
}
```

## Common Issues & Solutions

### Issue: Back button doesn't work as expected
**Solution**: Use proper `popUpTo` configuration:
```kotlin
navController.navigate(route) {
    popUpTo(startDestination) { inclusive = true }
}
```

### Issue: ViewModel data lost on navigation
**Solution**: Ensure ViewModels are injected via `hiltViewModel()` and scoped properly

### Issue: Bottom nav doesn't highlight correct item
**Solution**: Pass correct `currentRoute` to `BottomNavigationBar`

## Future Enhancements

1. **Deep Linking**: Add support for deep links to specific anime
2. **Nested Navigation**: Create sub-graphs for complex sections
3. **Animation**: Add custom transitions between screens
4. **Back Stack Customization**: Fine-tune back stack behavior per feature

## Resources

- [Jetpack Compose Navigation Documentation](https://developer.android.com/jetpack/compose/navigation)
- [Navigation with Compose Codelab](https://developer.android.com/codelabs/jetpack-compose-navigation)
- [Single Activity Architecture](https://www.youtube.com/watch?v=2k8x8V77CrU)
