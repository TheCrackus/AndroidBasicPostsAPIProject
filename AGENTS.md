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
  - `GET /feed/posts` -> Retorna lista de posts envuelta en `{ posts: [{ _id, title, content, imageUrl, creator: { name }, createdAt }] }`.
  - `POST /feed/post` -> Creación de post enviando `{ title, content }` y retornando `{ message, post }`.
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
│   │   ├── BaseConstants.kt            # Constantes transversales base (BASE_EMPTY_STRING, patrones de fecha ISO/Display)
│   │   ├── DataConstants.kt            # Constantes de persistencia/mapeo (endpoints, DTO keys, fallbacks, POST_TIMEOUT_SECONDS)
│   │   ├── NetworkConstants.kt         # Constantes de red (BASE_URL, JSON_MEDIA_TYPE, mensajes de error HTTP)
│   │   └── UiConstants.kt              # Constantes de UI: dimensiones (Dp), opacidades (alpha), pesos, límites de líneas, keys de LazyColumn y resultados de navegación
│   ├── coroutines/
│   │   └── DispatcherProvider.kt       # Abstracción inyectable de CoroutineDispatchers (IO, Default, Main)
│   ├── network/
│   │   ├── NetworkResult.kt            # Tipo sellado genérico (Success<T>, Error, Loading) y GenericApiErrorResponse (@Serializable)
│   │   └── SafeApiCall.kt              # Ejecución segura con dispatcher inyectable, parser Json (kotlinx.serialization) y rethrow de CancellationException
│   ├── utils/
│   │   └── DateUtils.kt                # Utilidades transversales (formatPostDate para fechas en UI, retorna null si está vacía)
│   └── ui/                             # Infraestructura y Clases Base para la Capa UI
│       ├── screen/
│       │   ├── BaseContainer.kt        # Scaffold global con TopAppBar temática MessageNode (MaterialTheme) e innerPadding
│       │   ├── BaseNotificationDialog.kt # Componente genérico reutilizable para diálogos emergentes informativos/éxito con onDismiss y onConfirm
│       │   ├── BaseScreenOverlay.kt    # Overlay reutilizable con CircularProgressIndicator, AlertDialog de error y BaseNotificationDialog
│       │   └── ObserveAsEvents.kt      # Helper Composable que colecta Flow respetando Lifecycle.State.STARTED
│       └── state/
│           ├── BaseState.kt            # Interfaz BaseState y data class BaseData(isLoading, error, dialog, hasError, hasDialog)
│           ├── BaseEvent.kt            # Interfaz BaseEvent
│           ├── BaseEffect.kt           # Interfaz BaseEffect (one-off side effects)
│           ├── BaseError.kt            # Modelado de errores en UI (errorTitle, errorBody, BaseErrorType)
│           ├── BaseDialog.kt           # Modelado genérico de diálogos de notificación (título, mensaje, botón aceptar, @StringRes y BaseDialogType)
│           └── BaseViewModel.kt        # ViewModel genérico abstracto <S, E, EF> con _uiState, _uiEffects, updateState y sendEffect
│
├── domain/                             # NÚCLEO INDEPENDIENTE (Sin dependencias de Android ni Retrofit)
│   ├── model/
│   │   └── Post.kt                     # Modelos puros de negocio: Post(id, title, content, imageUrl?, creator?, createdAt) y Creator(name)
│   ├── repository/
│   │   └── PostRepository.kt           # CONTRATO (Interface con Flow<NetworkResult<T>>)
│   └── usecase/
│       ├── GetPostsUseCase.kt          # Caso de uso: Listar posts
│       └── CreatePostUseCase.kt        # Caso de uso: Crear post
│
├── data/                               # IMPLEMENTACIÓN (Acceso a red y fuentes externas)
│   ├── remote/
│   │   ├── api/
│   │   │   └── PostsApiService.kt      # Interfaz de endpoints con Retrofit (GET feed/posts, POST feed/post)
│   │   ├── model/
│   │   │   └── PostDto.kt              # DTOs @Serializable con @SerialName (PostsResponseDto, CreatorDto, PostDto, CreatePostRequest/ResponseDto)
│   │   └── datasource/
│   │       ├── PostRemoteDataSource.kt     # Interface del origen de datos remoto
│   │       └── PostRemoteDataSourceImpl.kt # Implementación que invoca safeApiCall con DispatcherProvider y Json inyectados
│   ├── mapper/
│   │   └── PostMapper.kt               # Extensiones: CreatorDto/PostDto/List<PostDto>/PostsResponseDto/CreatePostResponseDto.toDomain()
│   └── repository/
│       └── PostRepositoryImpl.kt       # Implementa PostRepository de Domain usando DispatcherProvider inyectado
│
├── di/                                 # Módulos de Inyección de Dependencias (Koin)
│   ├── NetworkModule.kt                # DispatcherProvider, Json, HttpLoggingInterceptor (solo DEBUG), OkHttp, Retrofit, PostsApiService
│   ├── DataModule.kt                   # PostRemoteDataSource, PostRepository
│   ├── DomainModule.kt                 # GetPostsUseCase, CreatePostUseCase
│   └── UiModule.kt                     # PostsDashboardViewModel y CreatePostViewModel registrados con viewModelOf
│
├── ui/                                 # Capa de Presentación (Compose)
│   ├── theme/                          # Color (Light/Dark MessageNode), Theme (dynamicColor = false), Type (estilos semánticos M3)
│   ├── navigation/
│   │   └── PostsNavHost.kt             # PostsNavHost con rutas tipadas (PostsDashboard y dialog<CreatePost>) y resultados vía SavedStateHandle (sin ViewModels)
│   ├── create/
│   │   ├── screen/
│   │   │   └── CreatePostScreen.kt     # CreatePostRoot (Stateful) y CreatePostScreen (Stateless modal con ImagePickerSection) + Previews Light/Dark
│   │   └── state/
│   │       ├── CreatePostViewModel.kt  # Inyecta CreatePostUseCase, cancela submitJob solapados y expone BaseDialog de éxito
│   │       ├── CreatePostState.kt      # CreatePostState(baseData, title, content, isSubmitEnabled) : BaseState
│   │       ├── CreatePostEvent.kt      # OnTitleChange, OnContentChange, SelectImageClick, SubmitPost, DismissError, DismissDialog, ConfirmSuccessDialog, CancelClick : BaseEvent
│   │       └── CreatePostEffect.kt     # NavigateBack, NavigateBackWithSuccess : BaseEffect
│   └── dashboard/
│       ├── screen/
│       │   └── PostsDashboardScreen.kt # PostsDashboardRoot (Stateful con soporte shouldRefreshPosts) y PostsDashboardScreen (Stateless) + Previews (Light, Dark, Empty)
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
   - **Manejo de Errores, Diálogos Genéricos y Overlays:**
     - Cada pantalla maneja su propio evento `DismissError` para limpiar `baseData.error = null` en el ViewModel y conectarlo a `onErrorDismiss` de `BaseScreenOverlay`.
     - Para notificaciones emergentes reutilizables (informativas o de éxito), usar `BaseDialog` en `BaseData.dialog`, renderizado automáticamente por `BaseNotificationDialog` dentro de `BaseScreenOverlay`, exponiendo `onDialogDismiss` (dismiss genérico) y `onDialogConfirm` (acción específica al pulsar Aceptar, por defecto igual a `onDialogDismiss`).
   - **Navegación y Previews:**
     - Utiliza Type-Safe Navigation oficial con destinos `@Serializable`.
     - El `NavHost` es el único que conoce `NavHostController`; las pantallas se comunican con navegación hacia arriba mediante lambdas (*State Hoisting*).
     - `PostsNavHost` **no** instancia ni comparte `ViewModel`s entre destinos; cada `*Root` obtiene su propio `ViewModel` vía `koinViewModel()`. Para devolver resultados entre destinos (ej. recargar el feed tras un diálogo), se usa el patrón oficial con `SavedStateHandle` (`previousBackStackEntry?.savedStateHandle`) observado en `Lifecycle.State.RESUMED` y reseteado al consumirse.
     - Los `@Preview` siguen la guía oficial de Jetpack Compose: previsualizan composables stateless (`*Screen`, `BaseContainer`) o el shell completo (`PostsApp` en `MainActivity.kt` vía Slot API) en funciones `private`, sin instanciar `ViewModel`/Koin ni acoplar datos falsos dentro de `PostsNavHost` o producción.
8. **Gestión de Textos y Constantes (Strings XML vs Constants Kotlin):**
   - **Textos de UI visibles para el usuario:** Deben centralizarse en `res/values/strings.xml` y consumirse mediante `stringResource(R.string.xxx)` en los Composables. Esto asegura soporte para internacionalización (i18n), accesibilidad y modificaciones centralizadas de textos en la interfaz.
   - **Constantes Técnicas, de Red y Mapeo:** Cadenas de conexión (`BASE_URL`), timeouts, endpoints (`POST_API_ENDPOINT`), nombres de campos en DTOs para `@SerialName` (`POST_ID_DTO`), prefijos de error técnico y valores por defecto en mappers (`DEFAULT_POST_TITLE`) **deben declararse como constantes en Kotlin (`const val`)** dentro del paquete `core/constants/` (`NetworkConstants.kt`, `DataConstants.kt`, etc.).
   - **Independencia de Capas:** Las capas `domain`, `data` y `core/network` **nunca** deben usar `R.string` ni depender del `Context` de Android para obtener textos; deben consumir las constantes de Kotlin para mantener la pureza arquitectónica y facilitar las pruebas unitarias.
9. **Centralización Estricta de Dimensiones, Colores, Opacidades y Constantes de UI (Cero Magic Numbers):**
   - **Prohibido el uso de números mágicos, literales de color o opacidades dispersas** (`2.dp`, `16.dp`, `22.sp`, `alpha = 0.5f`, `Color(0xFF141218)`, `Color.White`, `weight(1f)`, etc.) directamente en los archivos de pantallas o componentes de Compose.
   - **Centralización Obligatoria:**
     - Todos los valores de padding, márgenes, dimensiones de campos/iconos, espaciados (`Arrangement.spacedBy`), radios de esquinas (`RoundedCornerShape`), grosores de bordes/divisores, elevaciones, opacidades (`ALPHA_*`), pesos (`WEIGHT_*`), límites de líneas y keys técnicas de `LazyColumn` **deben declararse en `core/constants/UiConstants.kt`**.
     - Todos los colores deben declararse en `ui/theme/Color.kt` y consumirse en las pantallas mediante `MaterialTheme.colorScheme` (definido en `ui/theme/Theme.kt`).
     - Todos los tamaños y estilos tipográficos (`sp`) deben declararse en `ui/theme/Type.kt` y consumirse mediante `MaterialTheme.typography`.
     - Las constantes técnicas transversales (como `BASE_EMPTY_STRING` y patrones de fecha) deben declararse en `core/constants/BaseConstants.kt`.
   - Las constantes deben poseer nombres descriptivos y semánticos (ej. `SPACING_DEFAULT`, `CORNER_RADIUS_SMALL`, `ALPHA_DISABLED_CONTAINER`, `POST_CONTENT_INPUT_HEIGHT`) para garantizar coherencia y facilitar modificaciones globales.
10. **Documentación KDoc (`/** ... */`) Estratégica y Continua:**
    - **Documentación inmediata en cada cambio:** Al crear o modificar cualquier pieza importante o esencial del proyecto, se debe agregar o actualizar su KDoc en el mismo cambio (nunca dejarlo pendiente para después).
    - **Criterio de alcance:** Queda a criterio técnico del agente decidir qué piezas merecen documentación, priorizando siempre componentes esenciales de arquitectura, contratos y utilidades reutilizables (`core/`, contratos de `domain/`, casos de uso, mappers, repositorios, `PostsNavHost`, ViewModels y composables públicos `*Root`/`*Screen`).
    - **Formato estándar:** Usar KDoc en inglés (`/** ... */` con `@param`, `@return`, `@property` y referencias `[Symbol]`) explicando propósito, contrato, garantías de concurrencia/ciclo de vida y comportamientos de fallback.
    - **Sin ruido:** Evitar KDoc redundante en código trivial o getters autodescriptivos; los `TODO` internos dentro de bloques de código deben usar `// TODO:` y nunca `/** ... */`.
11. **Estilo de Código — Cero Comas Finales (No Trailing Commas):**
    - **Nunca** colocar una coma al final del último elemento/parámetro/argumento en llamadas a funciones, declaraciones de funciones, constructores, `data class`, anotaciones (`@Preview`, etc.) o listas (`listOf(...)`).
    - Esta regla aplica estrictamente a todo código nuevo o modificado.
12. **Estilo de Código — Importaciones Limpias (Sin FQN Inline):**
    - Siempre declarar las importaciones necesarias con `import` en la cabecera del archivo.
    - **Nunca** usar nombres completamente calificados (FQN) inline en el cuerpo del código (ej. `com.example....CreatePostResponseDto.toDomain()`) ni innecesariamente en KDoc de símbolos del mismo paquete, salvo que exista una colisión real de nombres que obligue a desambiguar.

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
- [x] **Alineación del contrato de `creator` anidado con la API REST:**
  - Modelado de `CreatorDto` (`POST_CREATOR_NAME_DTO = "name"`) en `PostDto.kt` y modelo de dominio `Creator(name)` en `Post.kt` manteniendo `Post.creator: Creator?`.
  - Agregado de `CreatorDto.toDomain()` con KDoc y normalización `trim()` / `isNotBlank()` a `null` en `PostMapper.kt`, delegando el fallback localizado a `R.string.default_post_creator` en `PostsDashboardScreen.kt` (sin duplicar constantes de texto visible en `DataConstants.kt`).
  - Previews de `PostsDashboardScreen.kt` (`PostsDashboardPreview`, `PostsDashboardDarkPreview`, `PostsDashboardEmptyPreview`) restringidas a `private` conforme a la guía de Compose y `AGENTS.md`.
- [x] **Implementación del Flujo de Creación de Post (`POST /feed/post`) como Modal / Overlay:**
  - Constantes de endpoint y serialización en `DataConstants.kt` (`CREATE_POST_API_ENDPOINT`, `POST_RESPONSE_MESSAGE_DTO`, `POST_RESPONSE_DATA_DTO`).
  - DTOs `@Serializable` `CreatePostRequestDto` y `CreatePostResponseDto` en `PostDto.kt`.
  - Endpoint `createPost` en `PostsApiService.kt` y `PostRemoteDataSource` / `PostRemoteDataSourceImpl` con `safeApiCall` y `dispatchers.io`.
  - Mapeo `CreatePostResponseDto.toDomain()` en `PostMapper.kt`.
  - Contrato en `PostRepository` e implementación en `PostRepositoryImpl` retornando `Flow<NetworkResult<Post>>` con `.flowOn(dispatchers.io)`.
  - Caso de uso `CreatePostUseCase` registrado como `factory` en `DomainModule.kt`.
  - `CreatePostViewModel` registrado con `viewModelOf` en `UiModule.kt`, cancelando jobs solapados (`submitJob`).
  - Estado, eventos y efectos MVI: `CreatePostState` (con validación `isSubmitEnabled`), `CreatePostEvent` y `CreatePostEffect`.
  - **Fidelidad al diseño MessageNode (React modal/overlay):** UI `CreatePostScreen` como modal emergente con backdrop oscurecido, encabezado morado en negrita con divisor horizontal, etiquetas en mayúsculas (`TITLE`, `CONTENT`), botón de texto rojo `CANCEL` y botón morado sólido `ACCEPT`.
  - **Navegación tipo diálogo (`dialog<CreatePost>`):** Registrado como destino de diálogo en `PostsNavHost.kt` sobre `PostsDashboard`.
  - Textos de UI centralizados en `res/values/strings.xml`.
- [x] **Centralización Estricta de Constantes de UI, Eliminación de Magic Numbers y Reglas de Estilo:**
  - Creación de `core/constants/UiConstants.kt` con dimensiones (`Dp`), opacidades (`ALPHA_*`), peso (`WEIGHT_FILL`), límites de líneas (`POST_CONTENT_MIN_LINES`/`MAX_LINES`), keys de `LazyColumn` y key de resultado de navegación (`NAV_RESULT_POST_CREATED`).
  - Adición de `headlineSmall` en `ui/theme/Type.kt` para encabezados de modal y migración total de `CreatePostScreen.kt`, `PostsDashboardScreen.kt` y `BaseContainer.kt` a `UiConstants`, `MaterialTheme.colorScheme` (eliminando `Color(0xFF141218)`, `Color.White` y colores directos en `CreatePostScreen`) y `MaterialTheme.typography`.
  - Centralización de patrones de fecha (`ISO_DATE_TIME_PATTERN`, `DISPLAY_DATE_PATTERN`) en `BaseConstants.kt`, refactorización de `formatPostDate` para retornar `String?` con fallback localizado `R.string.invalid_post_date` en UI, unificación de `BASE_EMPTY_STRING` (eliminando `EMPTY_STRING` duplicado en `DataConstants.kt`) en estados y mappers.
  - Eliminación global de comas finales (*trailing commas*) y limpieza de referencia FQN redundante en `CreatePostEffect.kt`, documentando ambas como reglas estrictas en `AGENTS.md`.
- [x] **Desacoplamiento de `PostsNavHost` y Patrón Oficial `SavedStateHandle` para Resultados entre Destinos:**
  - Eliminada la instanciación redundante de `PostsDashboardViewModel` en `composable<PostsDashboard>` y la obtención de `getBackStackEntry` / `koinViewModel` dentro de `dialog<CreatePost>` en `PostsNavHost.kt`.
  - Implementado el paso de resultado `NAV_RESULT_POST_CREATED` mediante `previousBackStackEntry?.savedStateHandle?.set(...)` en `dialog<CreatePost>` y observado en `composable<PostsDashboard>` vía `getStateFlow(...).collectAsStateWithLifecycle(lifecycle = backStackEntry.lifecycle, minActiveState = Lifecycle.State.RESUMED)`.
  - `PostsDashboardRoot` recibe `shouldRefreshPosts` y `onRefreshConsumed`, disparando `PostsDashboardEvent.LoadPosts` en `LaunchedEffect` y reseteando el flag a `false` tras consumirlo.
- [x] **Diálogo Genérico de Notificaciones y Componente UI de Image Picker en Create Post:**
  - Creación de `BaseDialog.kt` (`core/ui/state/`) con soporte para `@StringRes` y texto dinámico + `BaseDialogType`, integrado en `BaseData.dialog`.
  - Creación del componente reutilizable `BaseNotificationDialog.kt` (`core/ui/screen/`, con `@PreviewLightDark`) e integración en `BaseScreenOverlay.kt` con callbacks `onDialogDismiss` y `onDialogConfirm`.
  - Actualización de `CreatePostViewModel` / `CreatePostEvent` / `CreatePostScreen`: al crearse el post con éxito se muestra el diálogo de confirmación y al pulsar "Aceptar" (`ConfirmSuccessDialog`) se hace dismiss del diálogo y se ejecuta `NavigateBackWithSuccess`.
  - Agregada sección `ImagePickerSection` (`IMAGE` / `Choose File` / `No file chosen`) en `CreatePostScreen.kt` con `TODO` pendiente de conexión a Photo Picker y envío multipart al backend Node.js.

---

## 7. Próximos Pasos Sugeridos (Para el siguiente Chat)
1. **Flujo de Detalle de Post:**
   - Crear `PostDetailScreen` con ruta `@Serializable data class PostDetailRoute(val postId: String)`.
   - Conectar `ViewPostClick` para navegar a la pantalla de detalle.
2. **Flujo de Edición de Post (`PUT /feed/post/:postId`):**
   - Extender el formulario de post o crear componentes reutilizables para edición.
   - Conectar `EditPostClick` desde `PostsDashboardScreen`.
3. **Integración de Eliminación de Post:**
   - Definir endpoint `DELETE /feed/post/{postId}` en `PostsApiService` y `DeletePostUseCase`.
4. **Carga de Imágenes Remotas:**
   - Integrar Coil (`io.coil-kt:coil-compose`) para renderizar `imageUrl` cuando no sea `null`.
5. **Cobertura de Pruebas Unitarias (Pendiente Bloque B.5):**
   - Agregar tests unitarios para `PostMapper`, `SafeApiCall`, `PostRepositoryImpl`, `CreatePostUseCase`, `PostsDashboardViewModel` y `CreatePostViewModel` aprovechando `DispatcherProvider` inyectado.
