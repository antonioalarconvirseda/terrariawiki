# Terraria Wiki

App Android nativa (Kotlin + Jetpack Compose) que consume la API pública de la wiki oficial de Terraria ([terraria.wiki.gg](https://terraria.wiki.gg)) para ofrecer una **wiki de bolsillo** con búsqueda, ficha de detalle e imágenes de cada item.

> ⚠️ **Proyecto educativo no oficial.** Terraria es marca y propiedad de [Re-Logic](https://re-logic.com). Esta app consume datos públicos de la wiki comunitaria y no está afiliada con Re-Logic.

## Estado

**MVP funcional.** Implementa la sección de **Items** con:

- Lista de 50 items por defecto.
- Búsqueda local por nombre o tipo (con debounce 250 ms).
- Ficha de detalle con imagen (Coil), rareza color-coded, tipos, descripción, estadísticas (daño, defensa, retroceso, velocidad de uso), precio de venta e identificadores.
- Manejo de estados de UI: Loading / Ready / Empty / Error (con botón Reintentar).
- Tema personalizado con paleta inspirada en Terraria 1.4 (Sky Teal, Jungle Green, Gold Gem, Slime Red, Hell Orange, Cave Dark).

> Roadmap inmediato (siguientes iteraciones): NPCs, Enemigos, Bosses, Biomas.

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.0.20 |
| UI | Jetpack Compose (BOM 2024.09.02) + Material 3 |
| Arquitectura | MVVM + Clean Architecture (data / domain / ui) por feature |
| HTTP | Ktor 2.3.12 + engine OkHttp + kotlinx.serialization |
| DI | Koin 3.5.6 |
| Imágenes | Coil 2.6.0 |
| Navegación | Navigation Compose 2.7.7 |
| Async | Coroutines 1.8.1 + StateFlow |
| Tests | JUnit 4 + MockK + Turbine + kotlinx-coroutines-test |

Todas las dependencias son **KMP-ready** (sin KAPT/KSP), pensadas para una futura migración a Kotlin Multiplatform.

## Arquitectura

```
app/src/main/java/com/terrariawiki/
├── TerrariaWikiApp.kt           # Application + startKoin
├── MainActivity.kt              # NavHost root
├── core/
│   ├── network/                 # HttpClientFactory (Ktor + OkHttp)
│   ├── di/                      # networkModule (Koin)
│   ├── ui/theme/                # Color, Theme, Type (paleta Terraria 1.4)
│   └── util/
└── features/items/
    ├── data/                    # ItemsApi, ItemsDto, ItemsMapper, ItemsRepository
    ├── domain/                  # Item, GetItemsUseCase, SearchItemsUseCase, GetItemByNameUseCase
    ├── di/                      # itemsModule
    └── ui/                      # ItemsScreen, ItemDetailScreen, ViewModels, navigation
```

Regla de dependencias: `ui → domain ← data`. La capa `domain` no conoce Ktor ni Compose.

## Cómo compilar

```bash
# requisitos: Android Studio Hedgehog+ y JDK 17/21
git clone https://github.com/antonioalarconvirseda/terrariawiki.git
cd terrariawiki
./gradlew :app:assembleDebug
```

APK generado: `app/build/outputs/apk/debug/app-debug.apk`

## Cómo instalar en dispositivo físico (Android 8.0+)

1. Activa **Opciones de desarrollador** en el móvil (Ajustes → Acerca del teléfono → 7 toques en "Número de compilación").
2. Activa **Depuración USB**.
3. Conecta por USB y acepta el diálogo de huella RSA.
4. Verifica con `~/Library/Android/sdk/platform-tools/adb devices` (debe aparecer como `device`).
5. Instala con:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
   o directamente desde Android Studio con **Run ▶**.

## Tests

```bash
./gradlew :app:testDebugUnitTest
```

15 tests unitarios:
- `ItemsMapperTest` (9): parsing de rareza, tipos con delimitador `^`, stats nulas, HTML stripping, regex de imagen, fallback a 0.
- `ItemsViewModelTest` (6): estados Ready / Error / Empty de UiState, búsqueda con debounce, ItemDetailViewModel Ready / Error.

## Créditos y atribuciones

- **Datos** — proporcionados por [terraria.wiki.gg](https://terraria.wiki.gg) a través de su [MediaWiki API](https://www.mediawiki.org/wiki/API:Main_page) y la extensión [Cargo](https://www.mediawiki.org/wiki/Extension:Cargo).
- **Terraria** — marca registrada de [Re-Logic](https://re-logic.com). Este proyecto es un trabajo derivado no oficial sin ánimo de lucro.

## Licencia

[MIT](./LICENSE) — Copyright (c) 2026 Antonio Alarcón Virseda.

## Documentación del proyecto

Las notas de arquitectura, decisiones y patrones están en una bóveda de Obsidian paralela (`obsidian-kmp/Kotlin Multiplatform/TerrariaWiki/`). El [[00-Index]] contiene el mapa completo.
