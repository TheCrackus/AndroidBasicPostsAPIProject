package com.example.androidbasicpostsapiproject.core.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Collects one-off UI side effects from [flow] in a lifecycle-aware manner.
 *
 * Collection starts when the current `LifecycleOwner` reaches [Lifecycle.State.STARTED] and
 * suspends when stopped. Uses `Dispatchers.Main.immediate` (via [Dispatchers.Main]) so events
 * emitted from a buffered `Channel` are delivered immediately without being dropped across
 * configuration changes.
 *
 * @param T Type of the one-off effect emitted by [flow].
 * @param flow Cold or channel-backed [Flow] of one-off events (typically `BaseViewModel.uiEffects`).
 * @param key1 Optional extra key to restart collection when it changes.
 * @param key2 Optional second extra key to restart collection when it changes.
 * @param onEvent Callback invoked on the main thread for each emitted effect.
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = Unit,
    key2: Any? = Unit,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
        lifecycleOwner.repeatOnLifecycle(
            state = Lifecycle.State.STARTED
        ) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
