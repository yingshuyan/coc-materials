package com.lucyyan.cocmaterials.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.Instant

data class CreateRawMaterialRequest(

    @field:NotBlank
    val productCode: String,

    val corningPartNumber: String? = null,

    @field:NotBlank
    val lotNumber: String,

    @field:NotBlank
    val materialType: String,

    @field:NotBlank
    val materialClass: String,

    @field:NotBlank
    val form: String,

    @field:NotBlank
    val category: String,

    @field:DecimalMin(value = "0.0", inclusive = true)
    val quantity: BigDecimal,

    @field:NotBlank
    val uom: String,

    val notes: String? = null,

    val supplierId: Long,
    val ownerId: Long,
    val storageLocationId: Long,

    val dateIn: Instant,
    val dateOut: Instant? = null
)