package com.lucyyan.cocmaterials.mapper

import com.lucyyan.cocmaterials.dto.PageResponse
import org.springframework.data.domain.Page

fun <T: Any> Page<T>.toPageResponse(): PageResponse<T> {
    return PageResponse(
        content = content,
        page = number,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
        first = isFirst,
        last = isLast
    )
}