package com.lucyyan.cocmaterials.dto

import java.math.BigDecimal
import java.time.Instant

data class RawMaterialResponse(
    val id: Long,
    val productCode: String,
    val corningPartNumber: String?,
    val lotNumber: String,
    val materialType: String,
    val materialClass: String,
    val form: String,
    val category: String,
    val quantity: BigDecimal,
    val uom: String,
    val notes: String?,

    val supplierId: Long,
    val supplierName: String,

    val ownerId: Long,
    val ownerName: String,

    val storageLocationId: Long,
    val storageLocationName: String,

    val dateIn: Instant,
    val dateOut: Instant?,

    val createdAt: Instant,
    val updatedAt: Instant,

    val version: Long
)