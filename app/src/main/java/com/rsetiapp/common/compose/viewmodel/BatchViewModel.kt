package com.rsetiapp.common.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsetiapp.common.compose.base.ApiResult
import com.rsetiapp.common.compose.model.BatchDto
import com.rsetiapp.common.compose.model.InstituteDto
import com.rsetiapp.common.compose.model.SaveRequest
import com.rsetiapp.common.compose.repo.RsetiRepository
import com.rsetiapp.common.compose.state.BatchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Rishi Porwal
 */

@HiltViewModel
class BatchViewModel @Inject constructor(
    private val repo: RsetiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BatchUiState())
    val state = _state.asStateFlow()

    fun load(stateCode:String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val res = repo.getInstitutes(stateCode)) {
            is ApiResult.Success ->
                _state.update { it.copy(isLoading = false, institutes = res.data) }

            is ApiResult.Error ->
                _state.update { it.copy(isLoading = false, error = res.message) }

            else -> {}
        }
    }

    fun selectInstitute(i: InstituteDto) {
        _state.update { it.copy(selectedInstitute = i) }

        viewModelScope.launch {
            when (val res = repo.getBatches(i.instituteId)) {
                is ApiResult.Success ->
                    _state.update { it.copy(batches = res.data) }

                is ApiResult.Error ->
                    _state.update { it.copy(error = res.message) }

                else -> {}
            }
        }
    }

    fun selectBatch(b: BatchDto) {
        val instituteId = state.value.selectedInstitute?.instituteId ?: return

        _state.update { it.copy(selectedBatch = b) }

        viewModelScope.launch {
            when (val res = repo.getBatchDetails(b.batchId, instituteId)) {
                is ApiResult.Success ->
                    _state.update { it.copy(batchDetails = res.data.firstOrNull()) }

                is ApiResult.Error ->
                    _state.update { it.copy(error = res.message) }
                else -> {}
            }
        }
    }

    fun updateYesNo(value: Boolean) {
        _state.update { it.copy(isYesSelected = value) }
    }

    fun onRemarksChanged(value: String) {
        _state.update { it.copy(remarks = value, error = null) }
    }

    fun save() = viewModelScope.launch {

//        val s = state.value
//
//        if (s.isYesSelected == false && s.remarks.isBlank()) {
//            _state.update { it.copy(error = "Remarks required") }
//            return@launch
//        }
//
//        _state.update { it.copy(isSaving = true) }
//
//        val res = repo.save(
//            SaveRequest(s.isYesSelected == true, s.remarks, s.lat, s.lng)
//        )
//
//        when (res) {
//            is ApiResult.Success ->
//                _state.update { it.copy(isSaving = false, success = res.data) }
//
//            is ApiResult.Error ->
//                _state.update { it.copy(isSaving = false, error = res.message) }
//        }
    }
}