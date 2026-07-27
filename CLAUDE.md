# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Role

This is a **Kotlin/KMP learning project** for the user, not just a build target. Act as a senior Kotlin architect who also teaches: explain *why* behind non-trivial choices, not just *what* (Contexto/Decisión/Alternativas descartadas — same shape as the vault notes below), and favor patterns already established in this repo over introducing new ones.

## Project

Native Android app (Kotlin + Jetpack Compose) — pocket wiki for Terraria, consuming the public MediaWiki/Cargo API of terraria.wiki.gg. Educational, unofficial, not affiliated with Re-Logic.

## Extended documentation (source of truth)

`/Users/aav/gits/obsidian-kmp/Kotlin Multiplatform/TerrariaWiki/00-Index.md` is the map of a separate Obsidian vault with the full decision log: 7 founding architecture decisions, 22 per-pattern notes (`03-Patrones-Kotlin/`), and the complete bug history with root causes. This CLAUDE.md only summarizes what's needed day-to-day — check the vault for depth or history.

Documentation cadence is **just-in-time**: a pattern/decision note is written the moment it's implemented, not before. When a real architecture decision gets made while working in this repo, propose updating the corresponding vault note (new pattern → new note using `_template-patron.md`'s shape: Contexto/Decisión/Implementación Kotlin/Alternativas descartadas/Riesgos & mitigación).

## Commands

```bash
./gradlew :app:assembleDebug              # build debug APK
./gradlew :app:testDebugUnitTest          # run all unit tests
./gradlew :app:testDebugUnitTest --tests "com.terrariawiki.features.items.data.ItemsMapperTest"   # single test class
./gradlew :app:testDebugUnitTest --tests "*.ItemsViewModelTest.some test name"                     # single test method
adb install -r app/build/outputs/apk/debug/app-debug.apk   # install on connected device
```

No lint/ktlint task configured. JDK 17/21, `compileSdk`/`targetSdk` 35, `minSdk` 26.

## Architecture

Package root: `app/src/main/java/com/terrariawiki/`

```
TerrariaWikiApp.kt        # Application, startKoin
MainActivity.kt           # NavHost root
core/
  network/                # HttpClientFactory (Ktor+OkHttp client), CoilImageLoaderFactory (image loader + interceptors)
  di/                      # networkModule (Koin)
  ui/
    theme/                 # Color, Theme, Type — Terraria 1.4 palette
    components/            # StateScreens, InventorySlotCard, WikiThumbnail, DetailSection — shared across features
  util/
features/items/
  data/                    # ItemsApi/ItemsApiImpl (Ktor calls to Cargo API), *Dto, *Mapper (dto -> domain), ItemsRepositoryImpl
  domain/                  # Item, ItemCategory, Recipe, SearchResult, ItemsRepository (interface), *UseCase — no Ktor/Compose deps
  di/                      # itemsModule
  ui/                      # Screens, ViewModels, components/, navigation/
features/bosses/
  data/                    # BossesApi/BossesApiImpl, *Dto, *Mapper, BossesRepositoryImpl
  domain/                  # Boss, BossesRepository (interface), *UseCase
  di/                      # bossesModule
  ui/                      # Screens, ViewModels, components/
features/events/
  domain/                  # Event, EventCatalog — static catalog, no API/data layer (no Cargo endpoint backs it yet)
  ui/                      # EventListScreen
```

Dependency rule: `ui → domain ← data`. `domain` must stay free of Ktor and Compose imports. All feature deps wired through Koin modules (`networkModule`, `itemsModule`, `bossesModule`), not manual construction.

MVVM + Clean Architecture, organized by feature (`items`, `bosses`, `events` so far; roadmap: NPCs, Enemies, Biomes as sibling feature packages under `features/`). Features must not import from each other (`items` never imports `npcs`).

### Established conventions

- **DI (Koin)**: `single{}` for stateful deps, `factory{}` for stateless (UseCases, mappers), `viewModel{}` for ViewModels. One module per feature.
- **UseCase**: one UseCase = one user/system action, called via `operator fun invoke()`. Don't add a UseCase that adds nothing over a direct Repository call.
- **Error handling, two tiers**: `Result<T>` / `runCatching` in Repository/UseCase for one-shot suspend calls; `sealed interface UiState` (`Loading`/`Ready`/`Empty`/`Error`) in ViewModel/UI for reactive screen state. No custom error-class hierarchies — `kotlin.Result` already covers it for this MVP, and `runCatching` doesn't swallow `CancellationException` since Kotlin 1.5+.
- **Repository**: interface (the port) lives in `domain/`, implementation in `data/` (e.g. `ItemsRepository` in `domain/`, `ItemsRepositoryImpl` in `data/`) — keeps the contract fully portable to `commonMain` ahead of a KMP split. Exposes `Flow<T>` for reactive reads and `Result<T>` for one-shot writes. ViewModels depend on UseCases, never directly on Repository or Api.
- **DTO vs domain**: DTOs are permissive (`String?` fields mirroring the JSON 1:1); a pure mapper function concentrates all cleanup; `domain` never imports anything from `data`.
- **Testing**: MockK (not Mockito), Turbine for Flow/StateFlow, `Dispatchers.setMain`/`resetMain` in `@Before`/`@After`, prefer `UnconfinedTestDispatcher`. Never `Thread.sleep` in tests.
- **Commits**: Conventional Commits (`feat:`/`fix:`/`docs:`/`refactor:`/`test:`/`chore:`/`build:`), English messages, subject ≤72 chars, direct to `main` (solo project, no feature branches).
- **KMP-readiness**: only an Android target exists today, but keep `domain` and the pure `data` layer (DTO/Mapper/Repository interface) free of Android imports — they're already portable to `commonMain`. Known, accepted Android anchors for now: `HttpClient(OkHttp)` + `android.util.Log` in `HttpClientFactory.kt`, `koin-android`/`koin-androidx-compose`, and the single `app/` module (no `:shared` + `:android-app` split yet).

### Lessons from past bugs

- **Never iterate on a networking/perf bug without real logs.** Capture `adb logcat -c` then `adb logcat -d -s <Tag>:D` and read ≥30 lines before hypothesizing — two whole iterations were once burned guessing (throughput limit, then thread starvation) before real logs revealed the actual cause in under one commit.
- **Plain OkHttp does not add a `User-Agent` by default**, unlike Ktor's `defaultRequest{}`. Any new HTTP client in this project must be checked for this explicitly — this was the actual root cause of the Coil image rate-limiting saga (`CoilImageLoaderFactory.kt`), misdiagnosed twice before someone diffed headers between the two clients.
- If two HTTP clients behave differently against the same host, diff their headers before assuming rate-limiting or IP blocking.
- Image URL encoding: spaces must become `_` (not `%20`) for direct `/images/...` URLs; apostrophes need to be forced to `%27` (`Uri.encode` treats `'` as unreserved per RFC 3986 and won't encode it).

### Roadmap

Founding order was Items → NPCs → Enemies → Bosses → Biomes, but Crafting/Recipes, Bosses, and Events were pulled forward and are already implemented ahead of schedule (dark mode "Underworld" and a full visual redesign also shipped). As of the last hand-off (2026-07-26), the next step is undecided among: **NPCs**, **Enemies**, **offline cache with Room**, or **real KMP migration** (`:shared` + `:android-app` split). If asked to "continue" without specifics, ask which of these four before assuming.

### Networking

- `HttpClientFactory.kt`: builds the Ktor `HttpClient` (OkHttp engine) used for the Cargo API — base host `terraria.wiki.gg`, custom `User-Agent` (`TerrariaApiConfig.USER_AGENT`), kotlinx.serialization JSON with `ignoreUnknownKeys`/`isLenient`, 15s timeouts.
- `CoilImageLoaderFactory.kt`: separate OkHttp client for Coil image loading (item images are on a CF-fronted host and 429-prone). Has its own `TokenBucketInterceptor` (spaces requests via a token-bucket, default 100ms period) and `UserAgentInterceptor` (adds UA + retries once on HTTP 429 honoring `Retry-After`). Disk cache 50MB, memory cache 25% of available, crossfade enabled. When touching image loading or hitting rate-limit issues, this is the file to check first — don't reintroduce a retry-interceptor-only approach, the token bucket replaced that (see git history: c590a49, f1ad381, dadc37d).
- `ItemsRepositoryImpl` caches items in `MutableStateFlow`s guarded by `Mutex`; per-category flows/pagination state are lazily created and kept in in-memory maps (`byCategoryFlows`, `hasMoreMap`) — no persistence layer, cache is process-lifetime only.

### Testing

`app/build.gradle.kts` sets `unitTests.isReturnDefaultValues = true` — required for `android.util.Log` calls (used in `CoilImageLoaderFactory`) to work in local JVM unit tests without a full Android mock.

Test stack: JUnit 4, MockK, Turbine (for Flow/StateFlow assertions), kotlinx-coroutines-test, OkHttp MockWebServer (for API-layer tests). Existing coverage: `ItemsMapperTest` (rarity parsing, `^`-delimited types, HTML stripping, image regex, null-stat fallback), `ItemsViewModelTest`/`SearchViewModel` tests (UiState Ready/Error/Empty, debounced search via Turbine).

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

Rules:
- Always check the graph first for questions about cross-module dependencies or overall architecture — `graphify god-nodes` for hubs, `graphify path "<A>" "<B>"` for how two things connect, `graphify explain "<concept>"` for a focused subgraph.
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost). If the change touched architecture or dependencies (new dependency, new feature/module, a domain/data/ui layer change, a new relationship between packages), run `graphify update .` immediately — don't rely on the post-commit hook alone.
