package com.lucyyan.cocmaterials.mapper

import com.lucyyan.cocmaterials.entity.RawMaterial
import com.lucyyan.cocmaterials.exception.ResourceNotFoundException
import com.lucyyan.cocmaterials.repository.StorageLocationRepository
import com.lucyyan.cocmaterials.repository.SupplierRepository
import com.lucyyan.cocmaterials.repository.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import tools.jackson.databind.JsonNode
import java.math.BigDecimal
import java.time.Instant

@Component
class RawMaterialPatchMapper(
    private val supplierRepository: SupplierRepository,
    private val userRepository: UserRepository,
    private val storageLocationRepository: StorageLocationRepository
) {

    companion object {
        private val SUPPORTED_FIELDS = setOf(
            "productCode",
            "corningPartNumber",
            "lotNumber",
            "materialType",
            "materialClass",
            "form",
            "category",
            "quantity",
            "uom",
            "notes",
            "supplierId",
            "ownerId",
            "storageLocationId",
            "dateIn",
            "dateOut",
            "version"
        )
    }

    fun apply(
        patch: JsonNode,
        rawMaterial: RawMaterial
    ) {
        validatePatchStructure(patch)

        patchString(patch, "productCode", false) {
            rawMaterial.productCode = requireNotNull(it)
        }

        patchString(patch, "corningPartNumber", true) {
            rawMaterial.corningPartNumber = it
        }

        patchString(patch, "lotNumber", false) {
            rawMaterial.lotNumber = requireNotNull(it)
        }

        patchString(patch, "materialType", false) {
            rawMaterial.materialType = requireNotNull(it)
        }

        patchString(patch, "materialClass", false) {
            rawMaterial.materialClass = requireNotNull(it)
        }

        patchString(patch, "form", false) {
            rawMaterial.form = requireNotNull(it)
        }

        patchString(patch, "category", false) {
            rawMaterial.category = requireNotNull(it)
        }

        patchBigDecimal(patch, "quantity") {
            rawMaterial.quantity = it
        }

        patchString(patch, "uom", false) {
            rawMaterial.uom = requireNotNull(it)
        }

        patchString(patch, "notes", true) {
            rawMaterial.notes = it
        }

        patchInstant(patch, "dateIn", false) {
            rawMaterial.dateIn = requireNotNull(it)
        }

        patchInstant(patch, "dateOut", true) {
            rawMaterial.dateOut = it
        }

        patchRelation(
            patch = patch,
            field = "supplierId",
            repository = supplierRepository,
            resourceName = "Supplier"
        ) {
            rawMaterial.supplier = it
        }

        patchRelation(
            patch = patch,
            field = "ownerId",
            repository = userRepository,
            resourceName = "User"
        ) {
            rawMaterial.owner = it
        }

        patchRelation(
            patch = patch,
            field = "storageLocationId",
            repository = storageLocationRepository,
            resourceName = "Storage location"
        ) {
            rawMaterial.storageLocation = it
        }

        validateResult(rawMaterial)
    }

    private fun validatePatchStructure(patch: JsonNode) {
        if (!patch.isObject) {
            throw IllegalArgumentException(
                "PATCH body must be a JSON object"
            )
        }

        val unknownFields = patch.propertyNames()
            .asSequence()
            .filter { it !in SUPPORTED_FIELDS }
            .toList()

        if (unknownFields.isNotEmpty()) {
            throw IllegalArgumentException(
                "Unsupported field(s): ${unknownFields.joinToString(", ")}"
            )
        }
    }

    private fun patchString(
        patch: JsonNode,
        field: String,
        nullable: Boolean,
        update: (String?) -> Unit
    ) {
        val node = getPresentField(patch, field) ?: return

        if (node.isNull) {
            requireNullable(field, nullable)
            update(null)
            return
        }

        if (!node.isTextual) {
            throw IllegalArgumentException("$field must be a string")
        }

        val value = node.asText()

        if (!nullable && value.isBlank()) {
            throw IllegalArgumentException("$field cannot be blank")
        }

        update(value)
    }

    private fun patchBigDecimal(
        patch: JsonNode,
        field: String,
        update: (BigDecimal) -> Unit
    ) {
        val node = getPresentField(patch, field) ?: return

        if (!node.isNumber) {
            throw IllegalArgumentException("$field must be a number")
        }

        val value = node.decimalValue()

        if (value.signum() < 0) {
            throw IllegalArgumentException("$field cannot be negative")
        }

        update(value)
    }

    private fun patchInstant(
        patch: JsonNode,
        field: String,
        nullable: Boolean,
        update: (Instant?) -> Unit
    ) {
        val node = getPresentField(patch, field) ?: return

        if (node.isNull) {
            requireNullable(field, nullable)
            update(null)
            return
        }

        if (!node.isTextual) {
            throw IllegalArgumentException(
                "$field must be an ISO-8601 timestamp string"
            )
        }

        val value = try {
            Instant.parse(node.asText())
        } catch (ex: Exception) {
            throw IllegalArgumentException(
                "$field must be a valid ISO-8601 timestamp"
            )
        }

        update(value)
    }

    private fun <T : Any> patchRelation(
        patch: JsonNode,
        field: String,
        repository: JpaRepository<T, Long>,
        resourceName: String,
        update: (T) -> Unit
    ) {
        val node = getPresentField(patch, field) ?: return

        if (!node.isIntegralNumber) {
            throw IllegalArgumentException(
                "$field must be an integer"
            )
        }

        val id = node.asLong()

        if (id <= 0) {
            throw IllegalArgumentException(
                "$field must be greater than 0"
            )
        }

        val entity = repository.findById(id)
            .orElseThrow {
                ResourceNotFoundException(
                    "$resourceName $id not found"
                )
            }

        update(entity)
    }

    private fun getPresentField(
        patch: JsonNode,
        field: String
    ): JsonNode? {
        return if (patch.has(field)) {
            patch.get(field)
        } else {
            null
        }
    }

    private fun requireNullable(
        field: String,
        nullable: Boolean
    ) {
        if (!nullable) {
            throw IllegalArgumentException(
                "$field cannot be null"
            )
        }
    }

    private fun validateResult(
        rawMaterial: RawMaterial
    ) {
        val dateOut = rawMaterial.dateOut

        if (
            dateOut != null &&
            dateOut.isBefore(rawMaterial.dateIn)
        ) {
            throw IllegalArgumentException(
                "dateOut cannot be before dateIn"
            )
        }
    }
}