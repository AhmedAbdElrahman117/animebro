# AnimBro

AnimBro is a Kotlin-based Android application for anime fans. It provides a complete experience to discover, track, and manage anime, with integrated profiles, watchlists, favorites, ratings, and notifications. The app connects to the [MyAnimeList v2 API](https://myanimelist.net/apiconfig/references/api/v2#operation/manga_manga_id_my_list_status_put) to keep your anime lists up to date.

---

## Table of Contents

- [Features](#features)
  - [Profile](#profile)
  - [WatchList](#watchlist)
  - [Comments & Ratings](#comments--ratings)
  - [Favorites](#favorites)
  - [My Anime List Integration](#my-anime-list-integration)
  - [Anime Details](#anime-details)
  - [Home Page](#home-page)
  - [Notifications](#notifications)
- [Screens](#screens)
  - [Login Screen](#login-screen)
  - [Sign Up Screen](#sign-up-screen)
  - [Forgot Password Screen](#forgot-password-screen)
  - [Home Screen](#home-screen)
  - [Anime Details Screen](#anime-details-screen)
  - [Wishlist Screen](#wishlist-screen)
  - [Favourite Screen](#favourite-screen)
  - [Profile Screen](#profile-screen)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Future Improvements](#future-improvements)
- [License](#license)

---

## Features

### Profile

- **Authentication**
  - Login with email and password
  - Sign Up with email and password
  - Email verification flow
  - Reset password (intent to Firebase)
- **Session Management**
  - Secure user sessions
  - **Logout** support to clear user state and return to the login screen

> Authentication is powered by Firebase (email/password + verification + password reset).

---

### WatchList

Organize and track your anime with multiple list states:

- **Pending** – Anime you plan to watch
- **Continue Watching** – Anime currently in progress
- **Wishlist** – Anime you’re interested in but haven’t started
- **Completed** – Finished series
- **Dropped** – Anime you stopped watching
- **Random** – Random suggestions from your lists or catalog
- **Custom List (optional)** – User-defined lists for personalized categorization

Each status can be tied to the MyAnimeList list status to keep everything in sync.

---

### Comments & Ratings

- **Global Rating**
  - Aggregated rating based on all users’ scores
  - Useful for discovering top-rated and trending anime

- **Local (User) Rating**
  - Personal rating per anime
  - Stored per user and can be synced with MyAnimeList where applicable

---

### Favorites

- Mark anime as **Favorite**
- Quickly access your favorite anime from the **Favourite Screen**
- Can be used to influence recommendations and notifications (e.g., related news and updates)

---

### My Anime List Integration

AnimBro integrates with **MyAnimeList (MAL) v2 API** to manage user anime lists.

- API reference:  
  [MyAnimeList v2 API – My List Status](https://myanimelist.net/apiconfig/references/api/v2#operation/manga_manga_id_my_list_status_put)

Core integration points:

- Sync anime list statuses (watching, completed, on-hold, dropped, plan to watch, etc.)
- Update list entries based on user actions in the app
- Keep local WatchList aligned with MyAnimeList data

> Note: Proper API authentication (OAuth2) and rate limiting must be configured according to MAL’s guidelines.

---

### Anime Details

The **Anime Details Screen** shows detailed information about each anime (depending on the API):

- Title, cover image, and synopsis
- Genres, studios, and other metadata
- Airing status and episodes
- Average/global rating and your local rating
- Buttons to:
  - Add/remove from WatchList
  - Add/remove from Favorites
  - Update list status (e.g., completed, dropped)
  - Open comments/ratings section

---

### Home Page

The **Home Screen** acts as the main discovery hub and may include sections like:

- **Top Airing** – Currently airing popular anime
- **Most Favorites** – Anime with the highest favorite counts
- **Upcoming** – Anime that will air soon
- **All Animes** – General catalog list with filters and sorting

These sections can be backed by the MyAnimeList API or a custom backend.

---

### Notifications

Stay up to date with background notifications:

- **Background Notifications**
  - Anime news
  - Updates (new episodes, status changes)
  - Personalized suggestions based on your favorites and watch history
- **Authentication Integration**
  - Notifications can be tied to your profile (Firebase + Google auth if configured)
  - Supports login/sign up through Google (if enabled)

> Notifications can be implemented using Firebase Cloud Messaging (FCM) or a similar push notification service.

---

## Screens

### Login Screen

- Email and password input
- Login button with validation
- Link to **Sign Up Screen**
- Link to **Forgot Password Screen**
- Optional Google Sign-In button (if Google auth is enabled)
- Error handling for invalid credentials or unverified email

---

### Sign Up Screen

- Email, password, and confirm password fields
- Create account button
- Triggers email verification via Firebase
- Link to **Login Screen**
- Optional Google Sign-Up (if Google auth is enabled)

---

### Forgot Password Screen

- Email input to request a password reset
- Sends password reset email via Firebase
- Confirmation or error messages depending on result
- Link back to **Login Screen**

---

### Home Screen

- Displays multiple anime sections:
  - Top Airing
  - Most Favorites
  - Upcoming
  - All Animes
- Search bar to find specific anime
- Navigation to:
  - Anime Details Screen
  - Wishlist / Favorites
  - Profile Screen
- Can be implemented using RecyclerViews for each category and horizontal/vertical lists.

---

### Anime Details Screen

- Large cover image and title
- Synopsis, genres, and basic metadata
- Global rating and local (user) rating component
- Buttons for:
  - Add to WatchList (status selector: pending, continue watching, completed, dropped, etc.)
  - Add to Favorites
  - Add to Wishlist
- Section for comments and user reviews (if implemented)
- Deep links to related anime or recommendations

---

### Wishlist Screen

- Shows all anime marked as **Wishlist**
- List of cards with cover, title, and status
- Quick actions:
  - Move to another list state (e.g., start watching -> Continue Watching)
  - Add to Favorites
- Pagination or infinite scroll if the list is large

---

### Favourite Screen

- Displays all anime marked as **Favorite**
- Grid or list layout for quick browsing
- Filters/sorting:
  - By rating
  - By recently updated
  - By airing/completed status
- Navigate to **Anime Details Screen** on item click

---

### Profile Screen

- Shows user account information:
  - Display name/email
  - Avatar (if supported)
- Links to:
  - Edit profile (if implemented)
  - WatchList overview (Pending, Continue Watching, Wishlist, Completed, Dropped, Random, Custom)
  - Favorites
  - Notifications settings
- Authentication actions:
  - **Logout**
  - Re-authenticate if required (for sensitive operations)

---

## Tech Stack

- **Language:** Kotlin (100%)
- **Platform:** Android
- **Authentication:**
  - Firebase Authentication (Email/Password, Email Verification, Password Reset)
  - Optional Google Sign-In
- **Backend / API:**
  - [MyAnimeList v2 API](https://myanimelist.net/apiconfig/references/api/v2)
- **Data & Storage:**
  - Local persistence (Room/Datastore/SharedPreferences – depending on implementation)
  - Remote data via Retrofit/OKHttp (or similar libraries)
- **UI:**
  - Android Views or Jetpack Compose (depending on implementation)
  - Material Design components

---

## Getting Started

> The exact steps may vary depending on how the project is configured. Below is a generic setup flow.

1. **Clone the repository**

   ```bash
   git clone https://github.com/AhmedAbdElrahman117/animebro.git
   cd animebro
   ```

2. **Open in Android Studio**

   - Use the latest stable version of Android Studio.
   - Open the project from the root directory.

3. **Configure Firebase**

   - Create a Firebase project in the Firebase console.
   - Add an Android app, download the `google-services.json` file.
   - Place `google-services.json` in the `app/` module.
   - Enable **Email/Password** and optionally **Google** sign-in in Firebase Authentication.

4. **Configure MyAnimeList API**

   - Register a MyAnimeList application and obtain your client credentials.
   - Set your MAL client ID/secret and redirect URI inside the project (e.g., in a `local.properties` entry or a secure config file).
   - Implement OAuth2 flow as per MAL documentation.

5. **Build & Run**

   - Sync Gradle.
   - Build the project.
   - Run the app on an emulator or a physical device (Android 5.0+ recommended).

---

## Project Structure

A possible high-level structure (the actual structure in the repo may differ):

- `app/src/main/java/.../ui/`
  - `auth/` – Login, Sign Up, Forgot Password screens
  - `home/` – Home screen, lists (Top Airing, Most Favorites, Upcoming, All)
  - `details/` – Anime Details screen
  - `profile/` – Profile screen and settings
  - `watchlist/` – Pending, Continue Watching, Completed, Dropped, Random, Custom List
  - `favorites/` – Favourite screen
  - `notifications/` – Notification handling UI
- `app/src/main/java/.../data/`
  - `remote/` – API clients (MyAnimeList, other services)
  - `local/` – Local database and storage
  - `model/` – Data models (Anime, User, Rating, etc.)
- `app/src/main/java/.../domain/`
  - Use cases and repositories
- `app/src/main/res/`
  - Layouts, drawables, strings, themes

---

## Future Improvements

- Offline mode with caching for anime lists and details
- More advanced recommendation engine
- Richer comments system with replies and reactions
- In-depth stats and charts for user watching habits
- Multi-language support
- Theming (light/dark mode, custom themes)

---

## License

This project is licensed under the terms specified in the repository (e.g., MIT, Apache 2.0, or proprietary).  
Please check the `LICENSE` file in this repo for full details.
