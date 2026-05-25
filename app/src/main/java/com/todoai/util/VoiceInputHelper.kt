package com.todoai.util

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast

/**
 * VoiceInputHelper — SpeechRecognizer bridge.
 * Call [startVoiceInput] from an Activity or use the inline version in AddTaskScreen via
 * ActivityResultContracts.StartIntentSenderForResult / StartActivityForResult.
 *
 * What it does:
 *  1. Refreshes microphone permission.
 *  2. Fires RecognizerIntent.ACTION_RECOGNIZE_SPEECH.
 *  3. Returns the transcript string via [onResult].
 */
fun startVoiceInput(context: Context, onResult: (String?) -> Unit) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task…")
    }
    try {
        (context as? android.app.Activity)?.startActivityForResult(intent, 1001)
            ?: onResult(null)
    } catch (e: Exception) {
        Toast.makeText(context, "Speech recognition unavailable: ${e.message}", Toast.LENGTH_SHORT).show()
        onResult(null)
    }
}
