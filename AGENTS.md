# Contexto del Proyecto y Reglas para Agentes (AGENTS.md)

Este documento contiene la arquitectura, tecnologías, reglas de diseño y el estado de avance del proyecto para que cualquier agente de IA o desarrollador pueda continuar el trabajo de forma consistente y sin fricciones.

---

## 1. Información General del Proyecto
- **Nombre de la App:** `AndroidBasicPostsAPIProject`
- **Package Base:** `com.example.androidbasicpostsapiproject`
- **Plataforma / SDK:** Android (Min SDK 24, Target SDK 37, Compile SDK 37)
- **Lenguaje:** Kotlin 2.2+ con Compose Compiler integrado
- **Propósito:** Aplicación Android que consume una API REST desde un backend local en Node.js (`NodeJSRestAPIPostsBasicProject`).

---

## 2. Configuración del Backend y Red Local
- **Puerto de despliegue:** `8080`
- **Rutas de conexión:**
  - **Emulador Android:** `http://10.0.2.2:8080/` *(alias del localhost de la máquina anfitriona)*.
  - **Dispositivo Físico:** `http://<IP_LOCAL_DE_TU_PC>:8080/` *(ambos en la misma red Wi-Fi)*.
- **Tráfico HTTP (No seguro):** Habilitado en `AndroidManifest.xml` con `android:usesCleartextTraffic="true"` y permisos `INTERNET` / `ACCESS_NETWORK_STATE`.

---

## 3. Stack Tecnológico
| Capa / Función | Librería / Tecnología | Versión |
| :--- | :--- | :--- |
| **UI** | Jetpack Compose (Material 3) | BOM `2026.02.01` |
| **Arquitectura** | Clean Architecture + Inversión de Dependencias (SOLID) | - |
| **Inyección de Dependencias** | Koin (`koin-android`, `koin-androidx-compose`) | `4.0.0` |
| **Red / HTTP** | Retrofit + Converter Gson | `2.11.0` |
| **Cliente HTTP & Logs** | OkHttp Logging Interceptor | `4.12.0` |
| **Concurrencia / Streams** | Kotlin Coroutines & Cold Flows | - |

---

## 4. Estructura de Paquetes y Capas (Clean Architecture)

```text
com.example.androidbasicpostsapiproject/
│
├── core/
│   └── network/
│       ├── NetworkResult.kt            # Tipo sellado genérico (Success<T>, Error, Loading)
│       └── SafeApiCall.kt              # Ejecución segura en Dispatchers.IO, parser de errores y rethrow de CancellationException
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
│   └── DomainModule.kt                 # GetPostsUseCase
│
├── ui/                                 # Capa de Presentación (Compose)
│   ├── theme/                          # Color, Theme, Type
│   └── (Pendiente: Screens y ViewModels)
│
└── PostsApplication.kt                 # Application Class: inicializa Koin con los 3 módulos
```

---

## 5. Reglas de Diseño Estrictas para Agentes
1. **Inversión de Dependencias (SOLID):**
   - La capa `domain` es sagrada y pura Kotlin: **nunca** debe importar clases de Retrofit, Gson, Android SDK o DataSources de la capa `data`.
   - `domain/repository/PostRepository.kt` define el contrato que la capa `data/repository/PostRepositoryImpl.kt` debe implementar.
2. **Manejo de Errores con `NetworkResult<T>`:**
   - La clase `NetworkResult.Error` contiene `(code: Int?, errorTitle: String?, errorBody: String?)`.
   - Todas las llamadas remotas pasan por `safeApiCall` que captura excepciones de red (`SocketTimeoutException`, `IOException`, etc.) y maneja respuestas no exitosas de Node.js, propagando adecuadamente `CancellationException`.
   - Los repositorios devuelven `Flow<NetworkResult<T>>` emitiendo primero `NetworkResult.Loading` y ejecutándose bajo `flowOn(Dispatchers.IO)`.
3. **Mappers Obligatorios:**
   - Nunca expongas `PostDto` a los casos de uso ni a la UI. Siempre transforma los DTOs a modelos de `domain` mediante `PostMapper.kt`.
4. **Inyección de Dependencias:**
   - Todo servicio, datasource, repositorio y caso de uso debe registrarse en `di/` mediante Koin.
   - Las futuras pantallas y ViewModels deben inyectar Use Cases (o Repositorios de domain) utilizando `koinViewModel()`.

---

## 6. Estado Actual del Avance (Bitácora)
- [x] Configuración de dependencias en `libs.versions.toml` y `app/build.gradle.kts`.
- [x] Refactorización y estandarización del namespace a `com.example.androidbasicpostsapiproject`.
- [x] Capa `core/network` (`NetworkResult.kt`, `SafeApiCall.kt` con soporte de cancelación estructurada).
- [x] Capa `domain` (`Post.kt`, `PostRepository.kt`, `GetPostsUseCase.kt` listos; flujo de creación pospuesto para fase posterior).
- [x] Capa `data` completa (`PostDto.kt` con tipos anulables seguros, `PostMapper.kt`, `PostsApiService.kt`, `PostRemoteDataSourceImpl.kt`, `PostRepositoryImpl.kt`).
- [x] Configuración de Koin en `NetworkModule.kt`, `DataModule.kt`, `DomainModule.kt`.
- [x] `PostsApplication.kt` registrado en `AndroidManifest.xml` con permisos de red y `usesCleartextTraffic` a nivel de aplicación.
- [x] Proyecto compilado exitosamente con Gradle (`:app:assembleDebug`).

---

## 7. Próximos Pasos Sugeridos
1. **Crear `PostsViewModel`:**
   - Inyectar `GetPostsUseCase`.
   - Manejar un estado observable `PostsUiState` (Loading, Success con lista de posts, Error con mensaje).
   - Registrar el ViewModel en un nuevo `PresentationModule.kt` o dentro de `di/`.
2. **Crear la interfaz de usuario en Jetpack Compose:**
   - Pantalla principal con lista de posts (`LazyColumn`).
   - Componente visual para estado de carga (`CircularProgressIndicator`).
   - Componente visual para estado de error con botón de reintento.
   - (Fase siguiente): Formulario/Diálogo y endpoint para crear un nuevo post.
