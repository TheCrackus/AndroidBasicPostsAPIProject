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
| **Lifecycle en Compose** | `androidx.lifecycle:lifecycle-runtime-compose` | `2.8.7` (o referenciada a `lifecycleRuntimeKtx`) |
| **Navegación** | Type-Safe Navigation Compose (`navigation-compose`) | `2.10.2` |
| **Serialización** | Kotlinx Serialization Plugin + JSON | `1.8.0` / Kotlin `2.2.10` |
| **Arquitectura** | Clean Architecture + MVI/MVVM Idiomático con UDF | - |
| **Inyección de Dependencias** | Koin (`koin-android`, `koin-androidx-compose`) | `4.0.0` |
| **Red / HTTP** | Retrofit + Converter Gson | `2.11.0` |
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
│   │   ├── DataConstants.kt            # Constantes de persistencia/mapeo (DTO keys, fallbacks de posts, timeouts)
│   │   ├── NetworkConstants.kt         # Constantes de red (BASE_URL, endpoints, mensajes de error HTTP)
│   │   └── UiConstants.kt              # (Próximo paso) Constantes de UI, dimensiones (Dp), espaciados y elevaciones
│   ├── network/
│   │   ├── NetworkResult.kt            # Tipo sellado genérico (Success<T>, Error, Loading)
│   │   └── SafeApiCall.kt              # Ejecución segura en Dispatchers.IO, parser de errores y rethrow de CancellationException
│   ├── utils/
│   │   └── DateUtils.kt                # Utilidades transversales (formatPostDate para fechas en UI)
│   └── ui/                             # Infraestructura y Clases Base para la Capa UI
│       ├── screen/
│       │   ├── BaseContainer.kt        # Scaffold global con TopAppBar temática MessageNode e innerPadding
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
│   │   └── Post.kt                     # Modelo puro de negocio (id, title, content, imageUrl, creator, createdAt)
│   ├── repository/
│   │   └── PostRepository.kt           # CONTRATO (Interface con Flow<NetworkResult<T>>)
│   └── usecase/
│       └── GetPostsUseCase.kt          # Caso de uso: Listar posts (CreatePostUseCase se abordará en una fase posterior)
│
├── data/                               # IMPLEMENTACIÓN (Acceso a red y fuentes externas)
│   ├── remote/
│   │   ├── api/
│   │   │   └── PostsApiService.kt      # Interfaz de endpoints con Retrofit
│   │   ├── model/
│   │   │   └── PostDto.kt              # DTOs (_id, title, content, imageUrl, creator, createdAt)
│   │   └── datasource/
│   │       ├── PostRemoteDataSource.kt     # Interface del origen de datos remoto
│   │       └── PostRemoteDataSourceImpl.kt # Implementación que invoca safeApiCall
│   ├── mapper/
│   │   └── PostMapper.kt               # Funciones de extensión: PostDto.toDomain()
│   └── repository/
│       └── PostRepositoryImpl.kt       # Implementa PostRepository de Domain
│
├── di/                                 # Módulos de Inyección de Dependencias (Koin)
│   ├── NetworkModule.kt                # OkHttp, Retrofit, PostsApiService
│   ├── DataModule.kt                   # PostRemoteDataSource, PostRepository
│   ├── DomainModule.kt                 # GetPostsUseCase
│   └── UiModule.kt                     # PostDashboardViewModel registrado con viewModelOf(::PostDashboardViewModel)
│
├── ui/                                 # Capa de Presentación (Compose)
│   ├── theme/                          # Color, Theme, Type
│   ├── navigation/
│   │   └── PostsNavController.kt       # NavHost con rutas tipadas (@Serializable object PostsDashboard)
│   └── dashboard/
│       ├── screen/
│       │   └── PostsDashboardScreen.kt # PostsDashboardRoot (Stateful) y PostsDashboardScreen (Stateless) + Preview
│       └── state/
│           ├── PostDashboardViewModel.kt # Inyecta GetPostsUseCase, hereda de BaseViewModel, procesa LoadPosts y DismissError
│           ├── PostsDashboardState.kt    # PostsDashboardState(baseData, posts) : BaseState
│           ├── PostsDashboardEvent.kt    # LoadPosts, DismissError : BaseEvent
│           └── PostsDashboardEffect.kt   # Efectos de navegación y avisos : BaseEffect
│
├── MainActivity.kt                     # Activity principal con enableEdgeToEdge(), BaseContainer y BaseNavController
└── PostsApplication.kt                 # Application Class: inicializa Koin con los 4 módulos (network, data, domain, ui)
```

---

## 5. Reglas de Diseño Estrictas para Agentes
1. **Regla de Oro de Git / Control de Versiones:**
   - **NUNCA** ejecutar `git commit` o `git push` de forma automática.
   - **Siempre** consultar y pedir confirmación explícita al usuario antes de hacer cualquier commit o push, detallando los archivos afectados.
2. **Inversión de Dependencias (SOLID):**
   - La capa `domain` es sagrada y pura Kotlin: **nunca** debe importar clases de Retrofit, Gson, Android SDK o DataSources de la capa `data`.
   - `domain/repository/PostRepository.kt` define el contrato que la capa `data/repository/PostRepositoryImpl.kt` debe implementar.
3. **Manejo de Errores con `NetworkResult<T>`:**
   - La clase `NetworkResult.Error` contiene `(code: Int?, errorTitle: String?, errorBody: String?)`.
   - Todas las llamadas remotas pasan por `safeApiCall` que captura excepciones de red (`SocketTimeoutException`, `IOException`, etc.) y maneja respuestas no exitosas de Node.js, propagando adecuadamente `CancellationException`.
   - Los repositorios devuelven `Flow<NetworkResult<T>>` emitiendo primero `NetworkResult.Loading` y ejecutándose bajo `flowOn(Dispatchers.IO)`.
4. **Mappers Obligatorios:**
   - Nunca expongas `PostDto` a los casos de uso ni a la UI. Siempre transforma los DTOs a modelos de `domain` mediante `PostMapper.kt`.
5. **Inyección de Dependencias (Koin):**
   - Todo servicio, datasource, repositorio, caso de uso y ViewModel debe registrarse en `di/` mediante Koin.
   - Las pantallas obtienen sus ViewModels usando `koinViewModel()`.
6. **Arquitectura de UI (MVI / MVVM con BaseViewModel Genérico):**
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
   - **Navegación:**
     - Utiliza Type-Safe Navigation oficial con destinos `@Serializable`.
     - El `NavHost` es el único que conoce `NavHostController`; las pantallas se comunican con navegación hacia arriba mediante lambdas (*State Hoisting*).
7. **Gestión de Textos y Constantes (Strings XML vs Constants Kotlin):**
   - **Textos de UI visibles para el usuario:** Deben centralizarse en `res/values/strings.xml` y consumirse mediante `stringResource(R.string.xxx)` en los Composables. Esto asegura soporte para internacionalización (i18n), accesibilidad y modificaciones centralizadas de textos en la interfaz.
   - **Constantes Técnicas, de Red y Mapeo:** Cadenas de conexión (`BASE_URL`), timeouts, endpoints (`POST_API_ENDPOINT`), nombres de campos en DTOs para `@SerializedName` (`POST_ID_DTO`), prefijos de error técnico y valores por defecto en mappers (`DEFAULT_POST_TITLE`) **deben declararse como constantes en Kotlin (`const val`)** dentro del paquete `core/constants/` (`NetworkConstants.kt`, `DataConstants.kt`, etc.).
   - **Independencia de Capas:** Las capas `domain`, `data` y `core/network` **nunca** deben usar `R.string` ni depender del `Context` de Android para obtener textos; deben consumir las constantes de Kotlin para mantener la pureza arquitectónica y facilitar las pruebas unitarias.
8. **Centralización de Dimensiones y Constantes de UI (Cero Magic Numbers):**
   - **Prohibido el uso de números mágicos dispersos** (`2.dp`, `16.dp`, `20.sp`, etc.) directamente en los archivos de pantallas o componentes de Compose.
   - **Centralización de Dimensiones:** Todos los valores de padding, márgenes, dimensiones de iconos/campos, espaciados de listas (`Arrangement.spacedBy`), radios de esquinas (`RoundedCornerShape`), grosores de bordes (`BorderStroke`) y elevaciones deben centralizarse en archivos de constantes dedicados (ej. `UiConstants.kt` o `Dimens.kt` en `core/constants/`).
   - Las constantes de dimensiones (`Dp`, `TextUnit`, etc.) deben poseer nombres descriptivos y semánticos (ej. `POST_CARD_PADDING`, `STATUS_INPUT_HEIGHT`, `CORNER_RADIUS_SMALL`) para garantizar coherencia en el diseño y facilitar futuras modificaciones globales.

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

---

## 7. Próximos Pasos Sugeridos (Para el siguiente Chat)
1. **Centralización de Constantes y Dimensiones de UI (Dp, Sp, etc.):**
   - Crear el archivo `UiConstants.kt` (o `Dimens.kt`) en `core/constants/`.
   - Extraer y reemplazar todos los valores numéricos directos / "magic numbers" (`2.dp`, `4.dp`, `10.dp`, `12.dp`, `16.dp`, `20.dp`, `24.dp`, `50.dp`, `13.sp`, `14.sp`, `20.sp`, etc.) dispersos en `PostsDashboardScreen.kt`, `BaseContainer.kt` y demás composables.
   - Definir constantes semánticas para paddings, márgenes, dimensiones de inputs, elevaciones, bordes y radios de esquinas.
2. **Flujo de Creación y Edición de Posts:**
   - Crear pantalla / diálogo modal para nuevo post (`POST /feed/post`).
   - Definir la ruta en `PostsNavController` y conectar el evento `NewPostClick` / `EditPostClick`.
3. **Flujo de Detalle de Post:**
   - Crear `PostDetailScreen` con ruta `@Serializable data class PostDetailRoute(val postId: String)`.
   - Conectar `ViewPostClick` para navegar a la pantalla de detalle.
4. **Integración de Eliminación de Post:**
   - Definir endpoint `DELETE /feed/post/{postId}` en `PostsApiService` y `DeletePostUseCase`.
5. **Carga de Imágenes Remotas:**
   - Integrar Coil (`io.coil-kt:coil-compose`) para renderizar `imageUrl` en caso de que los posts contengan imágenes.
