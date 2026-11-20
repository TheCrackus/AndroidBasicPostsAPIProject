package com.example.androidbasicpostsapiproject.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Abstraction over [CoroutineDispatcher] instances to keep coroutines testable and avoid
 * hardcoding [Dispatchers] in repositories, data sources, or ViewModels.
 *
 * @property io Dispatcher optimized for blocking I/O work such as network or disk operations.
 * @property default Dispatcher optimized for CPU-intensive work such as JSON parsing or sorting.
 * @property main Dispatcher bound to the Android main (UI) thread.
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}

/**
 * Production [DispatcherProvider] backed by standard kotlinx.coroutines [Dispatchers].
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}
