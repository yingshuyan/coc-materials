package com.lucyyan.cocmaterials.dto

import java.time.Instant

data class RawMaterialFilter(
    val productCode: String? = null,
    val lotNumber: String? = null,
    val supplierId: Long? = null,
    val ownerId: Long? = null,
    val storageLocationId: Long? = null,
    val dateInFrom: Instant? = null,
    val dateInTo: Instant? = null
)