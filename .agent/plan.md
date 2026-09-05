# Implementation Plan: Idealista Property Finder (MVP)

The following plan outlines the development process for the Idealista Property Finder app, which is now fully implemented and verified.

## Goal Description
Build a robust Android application in Kotlin using XML Views and a modern architectural approach (MVVM) to browse real estate listings, view property details, and manage favorites with local persistence and synchronization.

## Proposed Changes

### Infrastructure & Data Layer
*   [x] **Retrofit Setup**: Configured for `https://idealista.github.io/android-challenge/` to fetch listing and detail JSON data.
*   [x] **Room Database**: Implemented `IdealistaDatabase` with a `FavoriteEntity` to store `propertyCode` and `dateFavorited` (timestamp).
*   [x] **Repository Pattern**: `PropertyRepository` created to combine network results with local favorite status using Kotlin Flow for reactivity.

---

### UI & Navigation
*   [x] **Navigation Graph**: Jetpack Navigation Component set up for `PropertyListFragment` and `PropertyDetailFragment`.
*   [x] **Listing Screen**: XML-based `RecyclerView` with `ListAdapter`. Displays property previews, prices, and favorite status icons.
*   [x] **Detail Screen**: Comprehensive view using `CollapsingToolbarLayout`. Includes an image slider (ViewPager2), detailed specs, and a favorite toggle FAB.
*   [x] **UI Synchronization**: Ensured `PropertyListViewModel` reactively observes the database so that changes in the Detail screen are immediately visible in the List screen.

---

### Key Features
*   [x] **Local Persistence**: Favorites are saved in Room and persist across app restarts.
*   [x] **Favorite Timestamps**: The date/time a property was favorited is stored and displayed in both screens.
*   [x] **Image Loading**: Integrated **Coil** for smooth image rendering in XML views.

## Verification Plan

### Automated & Manual Testing
*   [x] **Stability**: Verified no crashes occur during navigation or data fetching.
*   [x] **Functional**: Confirmed that toggling a favorite in the Detail screen correctly updates the Listing screen icon and recorded date.
*   [x] **Persistence**: Verified data remains intact after killing and restarting the application.
*   [x] **UI Check**: Confirmed adherence to XML Views and Material Design 3 guidelines.

> [!NOTE]
> All tasks have been marked as COMPLETED and the final product has been verified by the Quality Assurance (Critic) agent.

---

## Addendum: Later Changes Not Covered Above

The following work was done after the initial MVP and is not reflected in the sections above.

### Compose Detail Presentation
*   [x] **Compose Detail Screen**: `PropertyDetailScreen` (Compose) implemented and shared between two presentations instead of a single XML-only detail screen.
*   [x] **Drawer Presentation**: `PropertyDetailDrawer` (Compose) shows the detail as a bottom-sheet-style panel on top of the XML list screen (animated in/out), reusing `PropertyDetailScreen`.
*   [x] **Full-Screen Presentation**: `PropertyDetailFragment` hosts the same `PropertyDetailScreen` via a `ComposeView` as the dedicated navigation destination.

### Display Mode Preference
*   [x] **`DetailDisplayMode` / `DetailDisplayPreferences`**: `SharedPreferences`-backed, exposed as a `StateFlow`, letting the user pick between `DRAWER` and `FULL_SCREEN` detail presentation from a toolbar gear-icon menu on the list screen.

### Use Case Layer
*   [x] **`domain/usecase` package**: Introduced `GetPropertyListUseCase`, `GetPropertyDetailUseCase`, `GetAllFavoritesUseCase`, `IsFavoriteUseCase`, `ToggleFavoriteUseCase` so ViewModels (and any future screens) call use cases instead of `PropertyRepository` directly.

### Dependency Injection with Hilt
*   [x] **Hilt Setup**: Added Hilt Gradle plugin/dependencies (KSP), `IdealistaApplication` (`@HiltAndroidApp`), manifest updated to reference it.
*   [x] **DI Modules**: `di/NetworkModule` (OkHttp/Retrofit/`IdealistaApi`) and `di/DatabaseModule` (Room `IdealistaDatabase`/`FavoriteDao`) replace the old manual `Dependencies` singleton object (removed).
*   [x] **Injectable Classes**: `PropertyRepository` and `DetailDisplayPreferences` now use `@Inject constructor`.
*   [x] **ViewModels via Hilt**: `PropertyListViewModel` is a `@HiltViewModel`; `PropertyDetailViewModel` uses Hilt assisted injection (`@AssistedInject`/`@AssistedFactory`) to inject the runtime-only `propertyCode`.
*   [x] **Fragments/Activity**: `MainActivity` and all fragments (`PropertyListFragment`, `PropertyOperationPageFragment`, `PropertyDetailFragment`) annotated `@AndroidEntryPoint`; manual `ViewModelProvider.Factory` classes removed in favor of `by viewModels()` and field injection.
*   [x] **Compose/Hilt Bridge**: `PropertyDetailDrawer` resolves `PropertyDetailViewModel.Factory` via a Hilt `@EntryPoint` (`PropertyDetailViewModelFactoryEntryPoint`) since Compose code can't use constructor injection directly.

### Clean Architecture Compliance
*   [x] **Domain models**: Added framework-free `domain/model` classes (`Property`, `Price`, `PropertyDetail`, `PropertyCharacteristics`, `Favorite`) so DTOs (Moshi) and Room entities no longer leak into `domain`/`ui`.
*   [x] **Repository abstraction**: `domain/repository/PropertyRepository` is now an interface; the old concrete class was renamed to `data/repository/PropertyRepositoryImpl`, which implements it and maps data-layer types to domain models via `data/mapper/PropertyMappers.kt`.
*   [x] **Hilt binding**: `di/RepositoryModule` uses `@Binds` to wire `PropertyRepositoryImpl` to the `PropertyRepository` interface, so use cases depend only on the abstraction.
*   [x] **Use cases & ViewModels updated**: All use cases and both ViewModels (`PropertyListViewModel`, `PropertyDetailViewModel`) now operate on domain models exclusively.
*   [x] **UI updated**: `PropertyAdapter` and the Compose `PropertyDetailScreen`/`PropertyDetailDrawer` consume domain models (`Property`, `PropertyDetail`, `Favorite`) instead of DTOs/entities.

### Modularization: `:domain` Gradle Module
*   [x] **Extracted `:domain` module**: `domain/model`, `domain/repository` (interface), and `domain/usecase` moved from `:app`'s source set into a standalone `:domain` Gradle module.
*   [x] **Pure Kotlin/JVM, no Android dependency**: Uses the `org.jetbrains.kotlin.jvm` plugin (not `com.android.library`), depending only on `kotlinx-coroutines-core` and `javax.inject` — physically enforcing that domain code can never import Android, Room, Retrofit, or Moshi types.
*   [x] **Wiring**: `:app` depends on `:domain` via `implementation(project(":domain"))`; package names unchanged so no call-site imports had to change. Hilt's annotation processor (running only in `:app`) still discovers the `@Inject`-annotated use case constructors across the module boundary, since JSR-330 annotations are `RUNTIME`-retained and visible on the compile classpath.
*   [x] **Verified**: Clean `assembleDebug` build succeeds, with `:domain:compileKotlin`/`:domain:jar` running as an independent task ahead of `:app`'s compilation.

### Repository Hygiene After Modularization
*   [x] **`domain/.gitignore`**: Added `/build` (mirroring `app/.gitignore`) so the new `:domain` module's Gradle/Kotlin build output (compiled classes, incremental caches, packaged jar) isn't tracked.
*   [x] **`.gitignore`**: Added `/.idea/kotlinc.xml`, an IDE-generated Kotlin-plugin-version marker created after adding the JVM module, consistent with the other machine-generated `.idea/*` entries already ignored.
*   [x] **Untracked stray file**: Ran `git rm --cached .idea/kotlinc.xml` to remove it from the index since the IDE had staged it before the ignore rule existed.

### Favorites Tab
*   [x] **Third list tab**: Added a "Favorites" tab alongside Buy/Rent on the property list screen, showing every favorited property regardless of its sale/rent operation.
*   [x] **`PropertyOperation.FAVORITES`**: New pseudo-operation constant distinguishing this tab from the real `sale`/`rent` API operations.
*   [x] **`PropertyListViewModel.observeProperties`**: Filters by favorite status instead of `Property.operation` when the requested tab is `FAVORITES`, reusing the same favorites `Flow` all tabs already observe (so favoriting/unfavoriting anywhere updates all tabs live).
*   [x] **`PropertyPagerAdapter`**: Now backs 3 ViewPager2 pages (sale, rent, favorites); `PropertyOperationPageFragment` required no changes since it's already parameterized by operation string.
*   [x] **UI**: `PropertyListFragment`'s `TabLayoutMediator` labels the third tab via the new `tab_favorites` string resource.