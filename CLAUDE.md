# CLAUDE.md

Base knowledge for AI agents working in this repository. Read this before making changes.

## Project Overview

**PruebaIdealista** is an Android property-listing app. It fetches property listings/details
from a static demo API, lets the user browse Buy/Rent/Favorites tabs, view a detail screen,
and mark/unmark favorites (persisted locally with Room).

- **Language**: Kotlin
- **UI**: Hybrid — XML Views + View Binding for the list screen (RecyclerView/ViewPager2/Fragments),
  Jetpack Compose for the property detail screen (shared between a full-screen destination and an
  in-place "drawer" panel).
- **Architecture**: MVVM + Clean Architecture, with a standalone `:domain` Gradle module.
- **DI**: Hilt (KSP annotation processing).
- **Networking**: Retrofit + OkHttp + Moshi, against `https://idealista.github.io/android-challenge/`.
- **Persistence**: Room (`FavoriteEntity` — just `propertyCode` + `dateFavorited` timestamp;
  currently at DB `version = 2`, see `data/db/Migrations.kt`).
- **Images**: Coil.
- **Min/target/compile SDK**: 24 / 37 / 37. Java 11 source/target compatibility.

## Module Structure

```
:app       — Android application module. UI (XML + Compose), Hilt modules, data layer
             (Retrofit API, Room DB, repository impl, DTO↔domain mappers), SharedPreferences.
:domain    — Pure Kotlin/JVM module (org.jetbrains.kotlin.jvm plugin, NOT com.android.library).
             No Android/Room/Retrofit/Moshi dependencies allowed here — only
             kotlinx-coroutines-core and javax.inject. Contains:
               domain/model      — Property, Price, PropertyDetail, PropertyCharacteristics, Favorite
               domain/repository — PropertyRepository interface (abstraction only)
               domain/usecase    — GetPropertyListUseCase, GetPropertyDetailUseCase,
                                    GetAllFavoritesUseCase, IsFavoriteUseCase, ToggleFavoriteUseCase
```

`:app` depends on `:domain` via `implementation(project(":domain"))`. Package names are
identical across both modules' historical origin, so no imports needed to change when the
module was extracted. Hilt's KSP processor (which only runs in `:app`) still discovers
`@Inject`-annotated use case constructors declared in `:domain`, because JSR-330 annotations
are `RUNTIME`-retained and visible on the compile classpath.

**Rule: never add an Android, Room, Retrofit, or Moshi import to anything under `:domain`.**
If a use case needs a platform type, put a domain model in `:domain` and map to/from it in
`:app`'s `data/mapper/PropertyMappers.kt`.

## Key Package Layout (`:app`)

```
data/api        — IdealistaApi (Retrofit interface), DTOs (PropertyDTO, PropertyDetailDTO, ...)
data/db         — IdealistaDatabase (Room, v2), FavoriteEntity, FavoriteDao, Migrations (MIGRATION_1_2)
data/mapper     — PropertyMappers.kt: DTO/Entity -> domain model conversions
data/prefs      — DetailDisplayPreferences (SharedPreferences-backed StateFlow of
                   DetailDisplayMode: DRAWER vs FULL_SCREEN)
data/repository — PropertyRepositoryImpl (implements domain/repository/PropertyRepository)
di              — NetworkModule, DatabaseModule, RepositoryModule (Hilt @Module/@InstallIn)
ui/list         — PropertyListFragment (XML, hosts ViewPager2 tabs + the Compose drawer),
                   PropertyOperationPageFragment (one per tab: sale/rent/favorites),
                   PropertyAdapter (RecyclerView ListAdapter), PropertyListViewModel
ui/detail       — PropertyDetailFragment (hosts Compose full-screen destination),
                   PropertyDetailViewModel (Hilt assisted injection for runtime propertyCode)
ui/detail/compose — PropertyDetailScreen (shared Compose UI), PropertyDetailDrawer
                     (bottom-sheet-style panel embedded in the list screen)
ui/theme        — PruebaIdealistaTheme, Color.kt (Idealista brand palette)
```

## Architecture Rules (Clean Architecture)

1. **UI → ViewModel → UseCase → Repository (interface) → RepositoryImpl → API/DAO.**
   ViewModels and Compose UI must never call `PropertyRepositoryImpl`, `IdealistaApi`, or
   `FavoriteDao` directly — always go through a use case.
2. **Domain models only above the repository layer.** `PropertyListViewModel`,
   `PropertyDetailViewModel`, `PropertyAdapter`, and the Compose detail screens work with
   `Property`/`PropertyDetail`/`Favorite` (domain models), never DTOs or Room entities.
3. **Hilt module discovery is annotation-processing-only.** `@Module @InstallIn(...)` classes
   like `NetworkModule`/`DatabaseModule`/`RepositoryModule` show zero "usages" under a
   code-reference search — that's expected, not dead code. Don't remove them based on a
   grep-for-references check.
4. **Compose can't use constructor injection.** Where Compose needs a Hilt-provided
   assisted factory (`PropertyDetailDrawer` needs `PropertyDetailViewModel.Factory`), it goes
   through a Hilt `@EntryPoint` (see `PropertyDetailViewModelFactoryEntryPoint`), not a raw
   `hiltViewModel()` call, because the drawer isn't hosted by a NavGraph destination.

## Known Gotchas

- **`LocalContext.current` inside a Fragment-hosted `ComposeView` is not a raw `Activity`.**
  It's Hilt's `ViewComponentManager$FragmentContextWrapper` (a `ContextWrapper`). Casting it
  directly `as Activity` throws `ClassCastException`. Use the `Context.findActivity()` tailrec
  extension (in `PropertyDetailDrawer.kt`) that unwraps `ContextWrapper.baseContext` until it
  finds the real `Activity`. Needed wherever `EntryPointAccessors.fromActivity(...)` is called
  from Compose code embedded in a Fragment.
- **`Dispatchers.setMain`/`resetMain` (kotlinx-coroutines-test) require the explicit
  `Dispatchers.` receiver** — they're extension functions on the `Dispatchers` object, not
  free top-level functions.
- **AGP 8+ disables `BuildConfig` generation by default.** `buildFeatures.buildConfig = true`
  must be set explicitly in `app/build.gradle.kts` for `BuildConfig.DEBUG` to resolve (used to
  gate the OkHttp logging interceptor to debug builds only).
- **Gradle on this machine may report a non-zero exit code even on `BUILD SUCCESSFUL`**
  (stderr/incubating-feature warnings from PowerShell). Always check the build output for the
  literal string `BUILD SUCCESSFUL`, not just the exit code.
- **`getPropertyDetail()` is a static, non-parameterized endpoint** (`IdealistaApi`) — it
  always returns the same `detail.json` regardless of `propertyCode`. This is a known
  limitation of the demo API, not a bug to silently "fix" by inventing a real endpoint.
- **Room migrations recreate the table rather than using `ALTER TABLE ... DROP COLUMN`.**
  `DROP COLUMN` was only added in SQLite 3.35.0 (2021); this app's `minSdk = 24` devices bundle
  much older SQLite versions that don't support it. `data/db/Migrations.kt`'s `MIGRATION_1_2`
  (which dropped the dead `FavoriteEntity.isFavorite` column) instead does
  create-new-table → copy data → drop old → rename, which only needs `CREATE TABLE`/`RENAME TO`
  — supported since early SQLite and Room's own documented approach for such migrations. Follow
  this pattern for any future destructive schema change, don't reach for `DROP COLUMN` directly.
- **`MainActivity` is locked to `android:screenOrientation="portrait"`** in the manifest — the
  XML list screen and Compose detail/drawer were never built or tested for landscape/rotation,
  so don't remove this without also addressing rotation support.

## Testing

- **No mocking framework** (no MockK/Mockito) — all test doubles are hand-written fakes
  implementing the real interfaces (`FakePropertyRepository`, `FakeIdealistaApi`,
  `FakeFavoriteDao`), recording call arguments in mutable fields for assertions. Follow this
  pattern for any new test double; don't introduce a mocking library.
- **Test naming convention**: `` `when {statement} then {result}` `` (backtick method names).
- **Test locations**:
  - `domain/src/test/kotlin/.../usecase/` — one test file per use case.
  - `app/src/test/.../data/mapper/` — DTO→domain mapper tests.
  - `app/src/test/.../data/repository/` — `PropertyRepositoryImplTest`.
  - `app/src/test/.../data/prefs/` — `DetailDisplayModeTest`.
  - `app/src/test/.../ui/list/`, `ui/detail/` — ViewModel tests + `PropertyDiffCallbackTest`.
  - `app/src/test/.../testutil/` — shared fakes/rules (`MainDispatcherRule`, etc.).
- **Run the full suite**: `.\gradlew.bat :domain:test :app:testDebugUnitTest` (56 tests as of
  the last full run — all green). Set `JAVA_HOME` to the Android Studio JBR before invoking
  Gradle on this machine (see Environment Notes below).
- No Compose UI tests or Room instrumented tests exist yet (deliberately deprioritized for an
  interview-scoped project — see `.agent/plan.md` for the reasoning).

## Environment Notes (Windows)

- Shell is PowerShell — **no** `&&`/`||` operators; use `;` and explicit `if ($?) { ... }` checks.
- `JAVA_HOME` must be set per-command for Gradle:
  `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`.
- Android SDK: `C:\Users\<user>\AppData\Local\Android\Sdk`. `adb.exe` is not on PATH — invoke
  via full path: `...\Sdk\platform-tools\adb.exe`.
- Use Windows-style backslash paths everywhere.

## Documentation

- **`.agent/plan.md`** is the authoritative, continuously-updated changelog/plan for this
  project — it documents the original MVP plan plus every addendum since (Compose migration,
  Hilt DI, Clean Architecture refactor, `:domain` module extraction, favorites tab, test
  coverage, crash fixes, UI/UX polish, cleanup). **Update it whenever you complete a
  non-trivial change**, following the existing addendum-section style (grouped by theme, `[x]`
  checkboxes, one bullet per concrete change).

## Git Workflow Notes

- Many short-lived feature branches exist from earlier iterative work
  (`clean-arch-changes`, `dependency-injection-Hilt`, `modular-domain-test`, `fav-tab`,
  `testing-coverage`, `crash-on-click-item`, `confirm-remove-dialog`, `idealista-style*`,
  `detail-compose-migration`). Treat branch history as informational; the active line of work
  is whatever branch the user has currently checked out (check `git status` /
  `git branch --show-current` — don't assume `master`/`main`).
- Never switch branches, commit, merge, rebase, or force-update refs on your own initiative.
  Only do so when the user explicitly asks. If a merge or branch operation is paused mid-way
  by the user ("don't continue"), do not resume it without a new explicit instruction.
- If the user reports "lost" changes, check `git reflog` before assuming data loss — commits
  are rarely actually gone; a deleted/renamed local branch's tip is almost always still
  reachable via reflog until GC runs.

## Project Context: Interview Project

This is an **interview/portfolio project with no planned future maintenance** — a single
developer built it to show known skills, and it isn't expected to have a production
lifecycle after evaluation. Keep this in mind whenever giving advice or making changes:

- When suggesting improvements, prioritize changes that are short term and visible in a code
  review or live demo (UX polish, test coverage, accessibility, release hygiene) over
  infrastructure that only pays off long-term (CI/CD, further modularization, Paging/offline
  caching, cert pinning). Mention the latter as trade-offs discussed verbally rather than
  implementing them unprompted.
- Don't over-invest in production-grade hardening (e.g. full migration strategies, extensive
  monitoring/observability, multi-environment build flavors) unless explicitly asked — the
  goal is a polished, well-architected demonstration, not a shippable long-lived product.
