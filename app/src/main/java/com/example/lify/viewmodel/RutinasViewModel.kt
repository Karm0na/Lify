package com.example.lify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.example.lify.data.Rutina

class RutinasViewModel : ViewModel(){
    val rutinas = mutableStateListOf<Rutina>()

}