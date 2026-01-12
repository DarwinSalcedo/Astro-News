# Astro News

A sample Android application for browsing spaceflight news, built with **Jetpack Compose** and following **Clean Architecture** principles.

##  Features

-   **Browse News**: View a paginated list of the latest space news articles.
-   **Search**: Filter articles by keywords.
-   **Detail View**: Read article summaries and open full content in the browser.
-   **Offline Support**: Caches data locally using Room for offline access.
-   **Responsive UI**: Built with Material 3 components and proper error handling (Snackbars).

##  Tech Stack

-   **Architecture**: Clean Architecture (Multi-module: `:app`, `:feature`, `:domain`, `:data`, `:core`)
-   **UI Pattern**: MVI (Model-View-Intent)
-   **UI Toolkit**: Jetpack Compose via Material 3
-   **Navigation**: Jetpack Navigation Compose & Hilt Navigation
-   **Asynchrony**: Kotlin Coroutines & Flow
-   **Dependency Injection**: Hilt
-   **Network**: Retrofit & OkHttp
-   **Local Storage**: Room Database
-   **Pagination**: Paging 3 (with `RemoteMediator` for caching)
-   **Image Loading**: Coil

