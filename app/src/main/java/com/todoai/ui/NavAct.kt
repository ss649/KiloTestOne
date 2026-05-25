package com.todoai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavAct : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavHostActivity() // rename your composable to this
        }
    }
}