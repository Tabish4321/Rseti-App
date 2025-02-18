package com.rsetiapp.common.model.response


data class ProgramResponse(
    val wrappedList: List<Program>,  // List of program objects
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String? // Nullable since it can be null
)

data class Program(
    val programCode: String,
    val programName: String
)

