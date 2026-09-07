# PruebaIdealista

An Android app built for the [idealista Android challenge](https://github.com/idealista/android-challenge/blob/master/README.md):
browse a list of property ads, view an ad's details, and mark/unmark ads as favorites, with
the favorited date persisted and shown across the app.

## Challenge requirements checklist

**Required**
- [x] Two screens: a **listing screen** (`PropertyListFragment`, with Buy/Rent tabs) and a
      **detail screen** (`PropertyDetailFragment` / `PropertyDetailDrawer`).
- [x] Written in **Kotlin**, using **XML views** for the listing screen (RecyclerView +
      ViewPager2 + Fragments, View Binding).
- [x] **Favorite ads**, with the **favorited date** stored and displayed on both the list item
      and the detail screen.
- [x] **AI tools used throughout development** — GitHub Copilot (Copilot CLI/agent) was used for
      the majority of the implementation, refactors, and this documentation. The initial project
      scaffolding was also instanced with Android Studio's built-in **Gemini** AI tools from a
      prompt, before Copilot took over for the rest of the implementation. See
      [AI context files](#ai-context-files) below.

**Bonus, also implemented**
- [x] **Tests**: 56 unit tests across `:domain` and `:app` — use cases, ViewModels, repository,
      DTO/entity mappers, `DiffUtil` callback, and preference parsing. See [Testing](#testing).
- [x] **Jetpack Compose alongside XML**: the detail screen (`PropertyDetailScreen`) is built
      entirely in Compose, shared between a full-screen navigation destination and an in-place
      "drawer" panel overlaid on the XML list screen — the user can pick either presentation
      from the toolbar's settings menu.
- [x] **Persistent storage**: favorites are persisted locally with **Room**
      (`IdealistaDatabase`/`FavoriteDao`), surviving app restarts.
- [x] **Beyond the requirements**: Clean Architecture with a standalone `:domain` Gradle module,
      Hilt dependency injection, a use-case layer decoupling ViewModels from the repository, a
      third "Favorites" tab, empty-state UI, remove-favorite confirmation dialogs, accessibility
      content descriptions, debug-only network logging, a splash screen, and more — see
      [`.agent/plan.md`](.agent/plan.md) for the full, chronological changelog.
- [x] **AI context files included**: [`CLAUDE.md`](CLAUDE.md) (repo root) is committed as
      requested by the challenge's bonus section.

## Screens & features

- **Listing screen**: Buy / Rent / Favorites tabs (ViewPager2), each showing a `RecyclerView` of
  property cards (image, price, rooms/baths/size, favorite toggle, favorited-date or a save
  prompt). A settings menu lets the user choose whether tapping a property opens the detail as a
  full screen or as an in-place drawer.
- **Detail screen**: image pager, price, characteristics, description, and a favorite
  toggle — shown either full-screen or as a drawer over the list, using the exact same Compose UI
  in both cases.
- **Favorites**: tapping the favorite icon adds a property instantly; removing one asks for
  confirmation first. The favorited date is recorded and shown wherever the property appears.

## Tech stack

| Concern       | Choice |
|---------------|--------|
| Language      | Kotlin |
| UI            | XML Views + View Binding (list screen), Jetpack Compose (detail screen) |
| Architecture  | MVVM + Clean Architecture (`:app` + standalone `:domain` module) |
| DI            | Hilt (KSP) |
| Networking    | Retrofit + OkHttp + Moshi, against `https://idealista.github.io/android-challenge/` |
| Persistence   | Room (favorites) + SharedPreferences (detail display mode) |
| Images        | Coil |
| Async         | Kotlin Coroutines + Flow |
| Testing       | JUnit4 + kotlinx-coroutines-test, hand-written fakes (no mocking framework) |

Min/target/compile SDK: 24 / 37 / 37. Java 11 source/target compatibility.

## Architecture

```
UI (Fragments/Compose) → ViewModel → UseCase → Repository (interface) → RepositoryImpl → API/DAO
```

- **`:domain`** is a pure Kotlin/JVM module (no Android/Room/Retrofit/Moshi dependencies) holding
  domain models, the `PropertyRepository` interface, and the use cases
  (`GetPropertyListUseCase`, `GetPropertyDetailUseCase`, `GetAllFavoritesUseCase`,
  `IsFavoriteUseCase`, `ToggleFavoriteUseCase`).
- **`:app`** depends on `:domain` and contains all Android-specific code: the Retrofit API/DTOs,
  the Room database, `PropertyRepositoryImpl` (mapping DTOs/entities to domain models), Hilt
  modules, ViewModels, and the UI (XML + Compose).

See [`CLAUDE.md`](CLAUDE.md) for a deeper breakdown of the package layout and architecture rules,
and [`.agent/plan.md`](.agent/plan.md) for the complete history of how the project evolved from
its MVP scope to its current state.

## Testing

Run the full unit test suite:

```powershell
.\gradlew.bat :domain:test :app:testDebugUnitTest
```

56 tests, covering domain use cases (against a hand-written `FakePropertyRepository`), DTO→domain
mappers, both ViewModels, the repository implementation (against hand-written `FakeIdealistaApi`/
`FakeFavoriteDao`), the list screen's `DiffUtil` callback, and the display-mode preference parsing.
No mocking framework is used — all test doubles are hand-written fakes.

## Building & running

```powershell
.\gradlew.bat :app:assembleDebug
```

Open the project in Android Studio and run the `app` configuration, or install the built APK
directly. The app requires no configuration/API keys — it talks to the challenge's public static
JSON endpoints.

## AI context files

Per the challenge's bonus request to include any AI context files used during development, this
repository includes:

- [`CLAUDE.md`](CLAUDE.md) — base knowledge for AI coding agents working in this repository
  (project structure, architecture rules, known gotchas, testing conventions, environment notes).
- [`.agent/plan.md`](.agent/plan.md) — the living implementation plan/changelog the AI agent
  maintained throughout the project, documenting the original MVP scope plus every subsequent
  addendum (Compose migration, Hilt DI, Clean Architecture refactor, `:domain` extraction,
  favorites tab, test coverage, crash fixes, UI/UX polish, dependency cleanup, splash screen...).

## Project context

This project was built for the idealista Android technical challenge and is not intended for
further long-term maintenance — see the "Project Context: Interview Project" section at the end
of [`CLAUDE.md`](CLAUDE.md) for the reasoning behind some deliberate scope trade-offs (e.g. no
CI/CD, no pagination/offline caching, no cert pinning).
