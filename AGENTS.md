# Contexto del Proyecto y Reglas para Agentes (AGENTS.md)

Este documento contiene la arquitectura, tecnologías, reglas de diseño y el estado de avance del proyecto para que cualquier agente de IA o desarrollador pueda continuar el trabajo de forma consistente y sin fricciones.

---

## 1. Información General del Proyecto
- **Nombre de la App:** `AndroidBasicPostsAPIProject`
- **Package Base:** `com.example.androidbasicpostsapiproject`
- **Plataforma / SDK:** Android (Min SDK 24, Target SDK 37, Compile SDK 37)
- **Lenguaje:** Kotlin 2.2+ con Compose Compiler integrado
- **Control de Versiones (Git):** 
  - Rama principal: `main`
  - Repositorio remoto: `https://github.com/TheCrackus/AndroidBasicPostsAPIConsumerProject.git`
- **Propósito:** Aplicación Android que consume una API REST desde un backend local en Node.js (`NodeJSRestAPIPostsBasicProject`).

---

## 2. Configuración del Backend y Red Local
- **Puerto de despliegue:** `8080`
- **Rutas de conexión:**
  - **Emulador Android:** `http://10.0.2.2:8080/` *(alias del localhost de la máquina anfitriona)*.
  - **Dispositivo Físico:** `http://<IP_LOCAL_DE_TU_PC>:8080/` *(ambos en la misma red Wi-Fi)*.
- **Endpoints Actuales:**
  - `GET /feed/posts` -> Retorna lista de posts envuelta en `{ posts: [...] }`.
  - (Futuro) `POST /feed/post` -> Creación de post con multipart/json.
- **Tráfico HTTP (No seguro):** Habilitado en `AndroidManifest.xml` con `android:usesCleartextTraffic="true"` y permisos `INTERNET` / `ACCESS_NETWORK_STATE`.

---

## 3. Stack Tecnológico
| Capa / Función | Librería / Tecnología | Versión |
| :--- | :--- | :--- |
| **UI** | Jetpack Compose (Material 3) | BOM `2026.02.01` |
| **Lifecycle en Compose** | `androidx.lifecycle:lifecycle-runtime-compose` | `2.8.7` |
| **Navegación** | Type-Safe Navigation Compose (`navigation-compose`) | `2.10.2` |
| **Serialización** | Kotlinx Serialization Plugin + JSON | `1.8.0` / Kotlin `2.2.10` |
| **Arquitectura** | Clean Architecture + MVI/MVVM Idiomático con UDF | - |
| **Inyección de Dependencias** | Koin (`koin-android`, `koin-androidx-compose`) | `4.0.0` |
| **Red / HTTP** | Retrofit + Converter Kotlinx Serialization | `2.11.0` |
| **Cliente HTTP & Logs** | OkHttp Logging Interceptor | `4.12.0` |
| **Concurrencia / Streams** | Kotlin Coroutines & Cold Flows + Channels | - |

---

## 4. Estructura de Paquetes y Capas (Clean Architecture)

```text
com.example.androidbasicpostsapiproject/
│
├── core/
│   ├── constants/
│   │   ├── BaseConstants.kt            # Constantes transversales base (BASE_EMPTY_STRING)
│   │   ├── DataConstants.kt            # Constantes de persistencia/mapeo (endpoint feed/posts, DTO keys, fallbacks, POST_TIMEOUT_SECONDS)
│   │   ├── NetworkConstants.kt         # Constantes de red (BASE_URL, JSON_MEDIA_TYPE, mensajes de error HTTP)
│   │   └── UiConstants.kt              # (Próximo paso) Constantes de UI, dimensiones (Dp), espaciados, bordes y elevaciones
│   ├── coroutines/
│   │   └── DispatcherProvider.kt       # Abstracción inyectable de CoroutineDispatchers (IO, Default, Main)
│   ├── network/
│   │   ├── NetworkResult.kt            # Tipo sellado genérico (Success<T>, Error, Loading) y GenericApiErrorResponse (@Serializable)
│   │   └── SafeApiCall.kt              # Ejecución segura con dispatcher inyectable, parser Json (kotlinx.serialization) y rethrow de CancellationException
│   ├── utils/
│   │   └── DateUtils.kt                # Utilidades transversales (formatPostDate para fechas en UI)
│   └── ui/                             # Infraestructura y Clases Base para la Capa UI
│       ├── screen/
│       │   ├── BaseContainer.kt        # Scaffold global con TopAppBar temática MessageNode (MaterialTheme) e innerPadding
│       │   ├── BaseScreenOverlay.kt    # Overlay reutilizable con CircularProgressIndicator y AlertDialog de error
│       │   └── ObserveAsEvents.kt      # Helper Composable que colecta Flow respetando Lifecycle.State.STARTED
│       └── state/
│           ├── BaseState.kt            # Interfaz BaseState y data class BaseData(isLoading, error, hasError)
│           ├── BaseEvent.kt            # Interfaz BaseEvent
│           ├── BaseEffect.kt           # Interfaz BaseEffect (one-off side effects)
│           ├── BaseError.kt            # Modelado de errores en UI (errorTitle, errorBody, BaseErrorType)
│           └── BaseViewModel.kt        # ViewModel genérico abstracto <S, E, EF> con _uiState, _uiEffects, updateState y sendEffect
│
├── domain/                             # NÚCLEO INDEPENDIENTE (Sin dependencias de Android ni Retrofit)
│   ├── model/
│   │   └── Post.kt                     # Modelo puro de negocio (id, title, content, imageUrl?, creator?, createdAt)
│   ├── repository/
│   │   └── PostRepository.kt           # CONTRATO (Interface con Flow<NetworkResult<T>>)
│   └── usecase/
│       └── GetPostsUseCase.kt          # Caso de uso: Listar posts (CreatePostUseCase se abordará en una fase posterior)
│
├── data/                               # IMPLEMENTACIÓN (Acceso a red y fuentes externas)
│   ├── remote/
│   │   ├── api/
│   │   │   └── PostsApiService.kt      # Interfaz de endpoints con Retrofit (GET feed/posts -> Response<PostsResponseDto>)
│   │   ├── model/
│   │   │   └── PostDto.kt              # DTOs @Serializable con @SerialName (PostsResponseDto y PostDto)
│   │   └── datasource/
│   │       ├── PostRemoteDataSource.kt     # Interface del origen de datos remoto
│   │       └── PostRemoteDataSourceImpl.kt # Implementación que invoca safeApiCall con DispatcherProvider y Json inyectados
│   ├── mapper/
│   │   └── PostMapper.kt               # Extensiones: PostDto.toDomain(), List<PostDto>.toDomain(), PostsResponseDto.toDomain()
│   └── repository/
│       └── PostRepositoryImpl.kt       # Implementa PostRepository de Domain usando DispatcherProvider inyectado
│
├── di/                                 # Módulos de Inyección de Dependencias (Koin)
│   ├── NetworkModule.kt                # DispatcherProvider, Json, HttpLoggingInterceptor (solo DEBUG), OkHttp, Retrofit, PostsApiService
│   ├── DataModule.kt                   # PostRemoteDataSource, PostRepository
│   ├── DomainModule.kt                 # GetPostsUseCase
│   └── UiModule.kt                     # PostsDashboardViewModel registrado con viewModelOf(::PostsDashboardViewModel)
│
├── ui/                                 # Capa de Presentación (Compose)
│   ├── theme/                          # Color (Light/Dark MessageNode), Theme (dynamicColor = false), Type (estilos semánticos M3)
│   ├── navigation/
│   │   └── PostsNavHost.kt             # PostsNavHost con rutas tipadas (@Serializable object PostsDashboard)
│   └── dashboard/
│       ├── screen/
│       │   └── PostsDashboardScreen.kt # PostsDashboardRoot (Stateful) y PostsDashboardScreen (Stateless) + Previews (Light, Dark, Empty)
│       └── state/
│           ├── PostsDashboardViewModel.kt # Inyecta GetPostsUseCase, hereda de BaseViewModel, cancela fetchPostsJob solapados
│           ├── PostsDashboardState.kt     # PostsDashboardState(baseData, posts, statusText) : BaseState
│           ├── PostsDashboardEvent.kt     # LoadPosts, DismissError, OnStatusChange, acciones de post : BaseEvent
│           └── PostsDashboardEffect.kt    # Efectos de navegación y avisos : BaseEffect
│
├── MainActivity.kt                     # Activity principal con enableEdgeToEdge(), PostsApp (Slot API + Light/Dark Preview) y PostsNavHost
└── PostsApplication.kt                 # Application Class: inicializa Koin (androidLogger solo en BuildConfig.DEBUG) con los 4 módulos
```

---

## 5. Reglas de Diseño Estrictas para Agentes
1. **Regla de Oro de Git / Control de Versiones:**
   - **NUNCA** ejecutar `git commit` o `git push` de forma automática.
   - **Siempre** consultar y pedir confirmación explícita al usuario antes de hacer cualquier commit o push, detallando los archivos afectados.
2. **Regla de Oro — Alineación con Documentación Oficial de Google y Buenas Prácticas:**
   - Todo diseño arquitectónico, implementación, refactorización, manejo de concurrencia, UI en Compose, navegación, testing y documentación debe alinearse lo más posible con las **guías y documentación oficial de Google** (Android Developers, Architecture Recommendations, Jetpack Compose API Guidelines, Kotlin Style Guide, Coroutines Best Practices, Material 3) y las mejores prácticas modernas de la industria.
   - Ante dudas técnicas sobre APIs o patrones, consultar primero la documentación oficial de Android antes de introducir soluciones ad-hoc.
3. **Inversión de Dependencias (SOLID):**
   - La capa `domain` es sagrada y pura Kotlin: **nunca** debe importar clases de Retrofit, Kotlinx Serialization, Android SDK o DataSources de la capa `data`.
   - `domain/repository/PostRepository.kt` define el contrato que la capa `data/repository/PostRepositoryImpl.kt` debe implementar.
4. **Manejo de Errores con `NetworkResult<T>`:**
   - La clase `NetworkResult.Error` contiene `(code: Int?, errorTitle: String?, errorBody: String?)`.
   - Todas las llamadas remotas pasan por `safeApiCall` que captura excepciones de red (`SocketTimeoutException`, `IOException`, etc.), parsea errores con `Json` de `kotlinx.serialization`, y propaga adecuadamente `CancellationException`.
   - Los repositorios devuelven `Flow<NetworkResult<T>>` emitiendo primero `NetworkResult.Loading` y ejecutándose bajo `flowOn(dispatchers.io)` mediante `DispatcherProvider` inyectado.
5. **Mappers Obligatorios:**
   - Nunca expongas `PostDto` a los casos de uso ni a la UI. Siempre transforma los DTOs a modelos de `domain` mediante `PostMapper.kt`.
6. **Inyección de Dependencias (Koin):**
   - Todo servicio, datasource, repositorio, caso de uso y ViewModel debe registrarse en `di/` mediante Koin.
   - Las pantallas obtienen sus ViewModels usando `koinViewModel()`.
7. **Arquitectura de UI (MVI / MVVM con BaseViewModel Genérico):**
   - **`BaseViewModel<S : BaseState, E : BaseEvent, EF : BaseEffect>`:**
     - Expone `uiState: StateFlow<S>` y `uiEffects: Flow<EF>`.
     - Mutaciones de estado mediante `updateState { copy(...) }`.
     - Emisión de efectos mediante `sendEffect(effect)`.
     - Procesa acciones de la UI implementando `abstract fun onAction(event: E)`.
   - **Separación Stateful vs Stateless:**
     - `*Root`: Composable stateful que inyecta el ViewModel, colecta el estado con `collectAsStateWithLifecycle()`, observa efectos con `ObserveAsEvents`, y pasa callbacks/lambdas hacia arriba (al `NavHost`).
     - `*Screen`: Composable stateless que recibe `uiState` y `onAction`, envuelto en `BaseScreenOverlay`. Es ideal para testing y `@Preview`.
   - **Manejo de Ciclo de Vida para Efectos:**
     - Todo consumo de `uiEffects` debe hacerse a través del helper `ObserveAsEvents` (`repeatOnLifecycle(Lifecycle.State.STARTED)`).
   - **Manejo de Errores y Overlays:**
     - Cada pantalla maneja su propio evento `DismissError` para limpiar `baseData.error = null` en el ViewModel y conectarlo a `onErrorDismiss` de `BaseScreenOverlay`.
   - **Navegación y Previews:**
     - Utiliza Type-Safe Navigation oficial con destinos `@Serializable`.
     - El `NavHost` es el único que conoce `NavHostController`; las pantallas se comunican con navegación hacia arriba mediante lambdas (*State Hoisting*).
     - Los `@Preview` siguen la guía oficial de Jetpack Compose: previsualizan composables stateless (`*Screen`, `BaseContainer`) o el shell completo (`PostsApp` en `MainActivity.kt` vía Slot API) en funciones `private`, sin instanciar `ViewModel`/Koin ni acoplar datos falsos dentro de `PostsNavHost` o producción.
8. **Gestión de Textos y Constantes (Strings XML vs Constants Kotlin):**
   - **Textos de UI visibles para el usuario:** Deben centralizarse en `res/values/strings.xml` y consumirse mediante `stringResource(R.string.xxx)` en los Composables. Esto asegura soporte para internacionalización (i18n), accesibilidad y modificaciones centralizadas de textos en la interfaz.
   - **Constantes Técnicas, de Red y Mapeo:** Cadenas de conexión (`BASE_URL`), timeouts, endpoints (`POST_API_ENDPOINT`), nombres de campos en DTOs para `@SerialName` (`POST_ID_DTO`), prefijos de error técnico y valores por defecto en mappers (`DEFAULT_POST_TITLE`) **deben declararse como constantes en Kotlin (`const val`)** dentro del paquete `core/constants/` (`NetworkConstants.kt`, `DataConstants.kt`, etc.).
   - **Independencia de Capas:** Las capas `domain`, `data` y `core/network` **nunca** deben usar `R.string` ni depender del `Context` de Android para obtener textos; deben consumir las constantes de Kotlin para mantener la pureza arquitectónica y facilitar las pruebas unitarias.
9. **Centralización de Dimensiones y Constantes de UI (Cero Magic Numbers):**
   - **Prohibido el uso de números mágicos dispersos** (`2.dp`, `16.dp`, `20.sp`, etc.) directamente en los archivos de pantallas o componentes de Compose.
   - **Centralización de Dimensiones:** Todos los valores de padding, márgenes, dimensiones de iconos/campos, espaciados de listas (`Arrangement.spacedBy`), radios de esquinas (`RoundedCornerShape`), grosores de bordes (`BorderStroke`) y elevaciones deben centralizarse en archivos de constantes dedicados (ej. `UiConstants.kt` o `Dimens.kt` en `core/constants/`).
   - Las constantes de dimensiones (`Dp`, `TextUnit`, etc.) deben poseer nombres descriptivos y semánticos (ej. `POST_CARD_PADDING`, `STATUS_INPUT_HEIGHT`, `CORNER_RADIUS_SMALL`) para garantizar coherencia en el diseño y facilitar futuras modificaciones globales.
10. **Documentación KDoc (`/** ... */`) Estratégica y Continua:**
    - **Documentación inmediata en cada cambio:** Al crear o modificar cualquier pieza importante o esencial del proyecto, se debe agregar o actualizar su KDoc en el mismo cambio (nunca dejarlo pendiente para después).
    - **Criterio de alcance:** Queda a criterio técnico del agente decidir qué piezas merecen documentación, priorizando siempre componentes esenciales de arquitectura, contratos y utilidades reutilizables (`core/`, contratos de `domain/`, casos de uso, mappers, repositorios, `PostsNavHost`, ViewModels y composables públicos `*Root`/`*Screen`).
    - **Formato estándar:** Usar KDoc en inglés (`/** ... */` con `@param`, `@return`, `@property` y referencias `[Symbol]`) explicando propósito, contrato, garantías de concurrencia/ciclo de vida y comportamientos de fallback.
    - **Sin ruido:** Evitar KDoc redundante en código trivial o getters autodescriptivos; los `TODO` internos dentro de bloques de código deben usar `// TODO:` y nunca `/** ... */`.

---

## 6. Estado Actual del Avance (Bitácora)
- [x] Configuración de dependencias en `libs.versions.toml` y `app/build.gradle.kts` (incluyendo `kotlin-serialization` y `lifecycle-runtime-compose`).
- [x] Refactorización y estandarización del namespace a `com.example.androidbasicpostsapiproject`.
- [x] Configuración y optimización de `.gitignore` para Kotlin 2.x (`.kotlin/`, `*.log`, `.idea/gradle.xml` desindexado).
- [x] Capa `core/network` (`NetworkResult.kt`, `SafeApiCall.kt` con soporte de cancelación estructurada).
- [x] Capa `core/constants` (`BaseConstants.kt`, `DataConstants.kt`, `NetworkConstants.kt`) para desacoplar strings técnicos de `res/values`.
- [x] Capa `domain` (`Post.kt`, `PostRepository.kt`, `GetPostsUseCase.kt` listos).
- [x] Capa `data` completa (`PostDto.kt` con constantes de serialización, `PostMapper.kt` con fallbacks constantes, `PostsApiService.kt`, `PostRemoteDataSourceImpl.kt`, `PostRepositoryImpl.kt`).
- [x] Capa `core/ui` completa:
  - `BaseContainer.kt` con `TopAppBar` tematizada al estilo MessageNode (banner morado, borde en título y botones Feed/Logout), `innerPadding` y strings centralizados en `strings.xml`.
  - `BaseScreenOverlay.kt` con `CircularProgressIndicator` y `AlertDialog` con textos de `R.string`.
  - `BaseState.kt`, `BaseEvent.kt`, `BaseEffect.kt`, `BaseError.kt`.
  - `BaseViewModel.kt` genérico con `updateState`, `sendEffect` y `onAction`.
  - `ObserveAsEvents.kt` seguro ante el ciclo de vida (alojado en `core/ui/screen/`).
- [x] Capa `di` completa (`NetworkModule.kt`, `DataModule.kt`, `DomainModule.kt`, `UiModule.kt` con `PostDashboardViewModel`).
- [x] `PostsApplication.kt` registrado en `AndroidManifest.xml` con permisos de red, `usesCleartextTraffic` y los 4 módulos Koin.
- [x] Configuración de navegación base con `PostsNavController.kt` y `MainActivity.kt`.
- [x] `PostDashboardViewModel.kt` dispara `LoadPosts` en su `init`, consume `GetPostsUseCase`, maneja `DismissError`, actualiza el texto de estado y envía efectos de navegación para creación y detalle/edición.
- [x] **Implementación de la UI en `PostsDashboardScreen.kt` basada en el diseño de MessageNode:**
  - Sección de estado (`StatusUpdateSection`) con campo de entrada `OutlinedTextField` ("Your status") y botón "UPDATE".
  - Botón de acción principal centrado (`NewPostActionButton`) con color ámbar ("NEW POST").
  - Tarjetas de posts (`PostItemCard`) con borde morado (`MessageNodePurple`), encabezado `Posted by <creator> on <date>`, título en negrita y botones de acción ("VIEW", "EDIT", "DELETE" en rojo).
  - Estado vacío (`EmptyPostsSection`) con opción para recargar.
  - Paleta de colores oficial MessageNode en `Color.kt`.
  - Todos los textos visibles centralizados en `res/values/strings.xml`.
- [x] **Barrido, formateo y optimización por el usuario:**
  - Extracción de utilidades de fecha a `core/utils/DateUtils.kt` (`formatPostDate`).
  - Reubicación de `ObserveAsEvents.kt` dentro de `core/ui/screen/`.
  - Adopción exhaustiva de parámetros nombrados en llamadas de Compose y eventos MVI.
  - Incorporación de vista previa para estado vacío (`PostsDashboardEmptyPreview`).
- [x] **Resolución de discrepancias técnicas (Bloque A):**
  - Alineación del endpoint `GET feed/posts` y agregado de `PostsResponseDto` (`{ "posts": [...] }`) con mapper `PostsResponseDto.toDomain()`.
  - Renombrado de `POST_TIME_OUT_MILLIS` a `POST_TIMEOUT_SECONDS` en `DataConstants.kt` y `NetworkModule.kt`.
  - Unificación de `androidx-lifecycle-runtime-ktx` y `androidx-lifecycle-runtime-compose` a `lifecycle = "2.8.7"` en `libs.versions.toml`.
  - Prevención de colisiones de keys en `LazyColumn` (`itemsIndexed` con fallback para IDs vacíos) y remoción de `.height(50.dp)` en `OutlinedTextField` para respetar accesibilidad Material 3.
- [x] **Mejoras de alineación con Google y buenas prácticas (Bloque B, puntos 1–4):**
  - Theming Material 3 completo (`LightColorScheme` / `DarkColorScheme` con paleta MessageNode, `dynamicColor = false`, estilos semánticos en `Type.kt`) consumido por `BaseContainer` y `PostsDashboardScreen` + `PostsDashboardDarkPreview`.
  - Migración completa de Gson a `kotlinx.serialization` (`retrofit-converter-kotlinx-serialization`, `@Serializable`/`@SerialName` en `PostDto`, `PostsResponseDto` y `GenericApiErrorResponse`, y parser `Json` inyectado en `SafeApiCall`).
  - Concurrencia testeable y segura: `DispatcherProvider` inyectado vía Koin en `PostRemoteDataSourceImpl` y `PostRepositoryImpl`, y cancelación de jobs solapados (`fetchPostsJob`) en `PostDashboardViewModel`.
  - Logs condicionados a `BuildConfig.DEBUG` en `NetworkModule` (`HttpLoggingInterceptor`) y `PostsApplication` (`androidLogger`), con `buildConfig = true` en `app/build.gradle.kts`.
- [x] **Limpieza y consistencia general (Bloque C):**
  - Renombrado `PostsNavController.kt` / `BaseNavController` a `PostsNavHost.kt` / `PostsNavHost` y `PostDashboardViewModel` a `PostsDashboardViewModel` (con actualización en `MainActivity`, `UiModule` y `PostsDashboardScreen`).
  - Eliminación del doble fallback de `creator` en `PostMapper.kt` (normalizando `creator` e `imageUrl` vacíos a `null` para que la UI resuelva `R.string.default_post_creator` localizable) y remoción de `DEFAULT_POST_CREATOR` en `DataConstants.kt`.
  - Extracción de `interface BaseEffect` a su propio archivo `core/ui/state/BaseEffect.kt`.
  - Eliminación del recurso sin uso `posts_feed_title` en `res/values/strings.xml`.
- [x] **Previews del App Shell y Documentación KDoc estratégica:**
  - Extracción del composable `PostsApp` (patrón Slot API de Compose) en `MainActivity.kt` con `PostsAppPreview` (Light y Dark con `showSystemUi = true`) y `@PreviewLightDark` en `BaseContainer.kt`, siguiendo las guías oficiales de Compose para Previews sin acoplar `ViewModel`/Koin.
  - Documentación KDoc (`/** ... */`) agregada en toda la infraestructura `core/` (`DispatcherProvider`, `NetworkResult`, `safeApiCall`, `formatPostDate`, `BaseContainer`, `BaseScreenOverlay`, `ObserveAsEvents`, `BaseState`/`BaseData`, `BaseEvent`, `BaseEffect`, `BaseError`, `BaseViewModel`) y puntos clave (`MainActivity`/`PostsApp`, `PostsNavHost`, `PostsDashboardRoot`/`PostsDashboardScreen`, `PostsDashboardViewModel`, `PostMapper`, `PostRepository`, `GetPostsUseCase`, `PostRepositoryImpl`).
  - Incorporación de la Regla de Oro de alineación con la documentación oficial de Google y estándares KDoc/Previews en `AGENTS.md`.

---

## 7. Próximos Pasos Sugeridos (Para el siguiente Chat)
1. **Centralización de Constantes, Dimensiones (`Dp`) y Textos Técnicos (Cero Magic Numbers):**
   - Crear `UiConstants.kt` (o `Dimens.kt`) en `core/constants/`.
   - Extraer y reemplazar las dimensiones `Dp` directas (`1.dp`, `2.dp`, `4.dp`, `6.dp`, `8.dp`, `10.dp`, `12.dp`, `16.dp`, `20.dp`, `24.dp`, `40.dp`) y pesos (`1f`) en `PostsDashboardScreen.kt` y `BaseContainer.kt`. *(Nota: los tamaños `sp` y colores ya quedaron centralizados en `Type.kt`, `Color.kt` y `Theme.kt`.)*
   - Centralizar textos técnicos no-UI pendientes: keys de `LazyColumn` (`"status_section"`, `"new_post_button"`, `"empty_posts_state"`, prefijo `"post_fallback_"`), patrones de fecha en `DateUtils.kt` (`"yyyy-MM-dd'T'HH:mm:ss"`, `"dd/MM/yyyy"` + mover el fallback visible `"Invalid Date"` a `R.string` en UI) y usar `BASE_EMPTY_STRING` en `PostsDashboardState.statusText`.
2. **Flujo de Creación y Edición de Posts:**
   - Crear pantalla / diálogo modal para nuevo post (`POST /feed/post`).
   - Definir la ruta en `PostsNavHost` y conectar el evento `NewPostClick` / `EditPostClick`.
3. **Flujo de Detalle de Post:**
   - Crear `PostDetailScreen` con ruta `@Serializable data class PostDetailRoute(val postId: String)`.
   - Conectar `ViewPostClick` para navegar a la pantalla de detalle.
4. **Integración de Eliminación de Post:**
   - Definir endpoint `DELETE /feed/post/{postId}` en `PostsApiService` y `DeletePostUseCase`.
5. **Carga de Imágenes Remotas:**
   - Integrar Coil (`io.coil-kt:coil-compose`) para renderizar `imageUrl` cuando no sea `null`.
6. **Cobertura de Pruebas Unitarias (Pendiente Bloque B.5):**
   - Agregar tests unitarios para `PostMapper`, `SafeApiCall`, `PostRepositoryImpl` y `PostsDashboardViewModel` aprovechando `DispatcherProvider` inyectado.
