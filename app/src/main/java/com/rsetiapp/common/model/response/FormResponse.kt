package com.rsetiapp.common.model.response


data class FormResponse(
    val wrappedList: List<Module>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String?
)
data class Module(
    val moduleCd: String,
    val moduleName: String,
    val forms: List<Form>,
    var isExpanded: Boolean = false  // ✅ Track if module is expanded
)

data class Form(
    val formUrl: String,
    val formName: String,
    val formCd: String
)
