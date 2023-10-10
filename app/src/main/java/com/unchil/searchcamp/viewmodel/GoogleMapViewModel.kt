package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import com.unchil.searchcamp.data.Repository

class GoogleMapViewModel (val repository: Repository) : ViewModel() {

    val currentListDataStateFlow  = repository.currentListDataStateFlow


}