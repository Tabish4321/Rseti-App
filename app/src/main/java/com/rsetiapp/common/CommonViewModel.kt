package com.rsetiapp.common

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CommonViewModel @Inject constructor(private val commonRepository: CommonRepository) :
    ViewModel() {





}