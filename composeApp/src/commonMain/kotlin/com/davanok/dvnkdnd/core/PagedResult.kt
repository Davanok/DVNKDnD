package com.davanok.dvnkdnd.core

data class PagedResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
