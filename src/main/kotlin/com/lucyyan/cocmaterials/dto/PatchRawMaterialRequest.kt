package com.lucyyan.cocmaterials.dto

import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal
import java.time.Instant

data class PatchRawMaterialRequest(
    val productCode: String? = null,
    val corningPartNumber: String? = null,
    val lotNumber: String? = null,
    val materialType: String? = null,
    val materialClass: String? = null,
    val form: String? = null,
    val category: String? = null,

    @field:DecimalMin(value = "0.0", inclusive = true)
    val quantity: BigDecimal? = null,

    val uom: String? = null,
    val notes: String? = null,

    val supplierId: Long? = null,
    val ownerId: Long? = null,
    val storageLocationId: Long? = null,

    val dateIn: Instant? = null,
    val dateOut: Instant? = null
)