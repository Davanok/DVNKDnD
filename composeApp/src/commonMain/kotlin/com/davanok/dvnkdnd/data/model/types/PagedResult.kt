package com.davanok.dvnkdnd.data.model.types

data class PagedResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
