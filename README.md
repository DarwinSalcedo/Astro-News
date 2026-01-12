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

<img width="308" height="663" alt="Screenshot 2026-01-12 at 2 55 38 AM" src="https://github.com/user-attachments/assets/691417d9-fe17-42e2-9a5d-580038620914" />
<img width="313" height="670" alt="Screenshot 2026-01-12 at 2 58 25 AM" src="https://github.com/user-attachments/assets/29edfea0-329e-42cf-bb11-f48c1a561a22" />
<img width="309" height="664" alt="Screenshot 2026-01-12 at 2 58 09 AM" src="https://github.com/user-attachments/assets/720cd279-31f1-4d71-89ad-249a748058b3" />
