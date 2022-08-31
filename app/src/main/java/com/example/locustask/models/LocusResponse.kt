package com.example.locustask.models

enum class LocusType {
    PHOTO, SINGLE_CHOICE, COMMENT
}

data class LocusResponse(
    val dataMap: DataMap,
    val id: String,
    val type: String,
    var title: String,
    var imagePath: String = ""
)

data class DataMap(
    var options: List<String> = emptyList()
) {
    var list = mutableListOf<Options>()
}

data class Options(
    val text: String,
    var isChecked: Boolean
)

