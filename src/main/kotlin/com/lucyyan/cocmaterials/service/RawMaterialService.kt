package com.lucyyan.cocmaterials.service

import com.lucyyan.cocmaterials.dto.CreateRawMaterialRequest
import com.lucyyan.cocmaterials.dto.PageResponse
import com.lucyyan.cocmaterials.dto.RawMaterialFilter
import com.lucyyan.cocmaterials.dto.RawMaterialResponse
import com.lucyyan.cocmaterials.dto.UpdateRawMaterialRequest
import com.lucyyan.cocmaterials.entity.RawMaterial
import com.lucyyan.cocmaterials.exception.DuplicateRawMaterialException
import com.lucyyan.cocmaterials.exception.ResourceNotFoundException
import com.lucyyan.cocmaterials.mapper.RawMaterialPatchMapper
import com.lucyyan.cocmaterials.mapper.toEntity
import com.lucyyan.cocmaterials.mapper.toPageResponse
import com.lucyyan.cocmaterials.mapper.toResponse
import com.lucyyan.cocmaterials.mapper.updateFrom
import com.lucyyan.cocmaterials.repository.RawMaterialRepository
import com.lucyyan.cocmaterials.repository.StorageLocationRepository
import com.lucyyan.cocmaterials.repository.SupplierRepository
import com.lucyyan.cocmaterials.repository.UserRepository
import com.lucyyan.cocmaterials.specification.RawMaterialSpecifications
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.JsonNode

@Service
@Transactional
class RawMaterialService(
    private val rawMaterialRepository: RawMaterialRepository,
    private val supplierRepository: SupplierRepository,
    private val userRepository: UserRepository,
    private val storageLocationRepository: StorageLocationRepository,
    private val rawMaterialPatchMapper: RawMaterialPatchMapper
) {

    companion object {
        private const val SUPPLIER_LOT_UNIQUE_CONSTRAINT =
            "uq_raw_materials_supplier_lot"
    }

    private fun findConstraintName(ex: Throwable): String? {
        var current: Throwable? = ex

        while (current != null) {
            if (current is org.hibernate.exception.ConstraintViolationException) {
                return current.constraintName
            }

            current = current.cause
        }

        return null
    }

    private fun extractVersion(patch: JsonNode): Long {

        if (!patch.has("version")) {
            throw IllegalArgumentException(
                "version is required for PATCH"
            )
        }

        val node = patch.get("version")

        if (!node.isIntegralNumber) {
            throw IllegalArgumentException(
                "version must be an integer"
            )
        }

        val version = node.asLong()

        if (version < 0) {
            throw IllegalArgumentException(
                "version cannot be negative"
            )
        }

        return version
    }

    @Transactional(readOnly = true)
    fun getAllRawMaterials(
        pageable: Pageable,
        filter: RawMaterialFilter
    ): PageResponse<RawMaterialResponse> {

        var spec: Specification<RawMaterial> =
            Specification.unrestricted()

        filter.productCode?.takeIf { it.isNotBlank() }?.let { spec = spec.and(
            RawMaterialSpecifications.productCodeContains(it)
        )}

        filter.lotNumber?.takeIf { it.isNotBlank() }?.let { spec = spec.and(
            RawMaterialSpecifications.lotNumberEquals(it)
        )}

        filter.supplierId?.let { spec = spec.and(
            RawMaterialSpecifications.supplierIdEquals(it)
        )}

        filter.ownerId?.let { spec = spec.and(
            RawMaterialSpecifications.ownerIdEquals(it)
        )}

        filter.storageLocationId?.let { spec = spec.and(
            RawMaterialSpecifications.storageLocationIdEquals(it)
        )}

        filter.dateInFrom?.let { spec = spec.and(
            RawMaterialSpecifications.dateInGreaterThanOrEqual(it)
        )}

        filter.dateInTo?.let { spec = spec.and(
            RawMaterialSpecifications.dateInLessThanOrEqual(it)
        )}


        return rawMaterialRepository.
            findAllWithRelations(spec, pageable)
            .map{ it.toResponse() }
            .toPageResponse()
    }

    @Transactional(readOnly = true)
    fun getRawMaterialById(id: Long): RawMaterialResponse {
        val rawMaterial = rawMaterialRepository.findWithRelationsById(id)
            ?: throw ResourceNotFoundException("Material with id $id not found")

        return rawMaterial.toResponse()
    }

    fun createRawMaterial(request: CreateRawMaterialRequest) : RawMaterialResponse {
        val supplier = supplierRepository.findById(request.supplierId).orElseThrow {
            IllegalArgumentException("Supplier with ${request.supplierId} not found") }

        val owner = userRepository.findById(request.ownerId).orElseThrow {
            IllegalArgumentException("User with ${request.ownerId} not found")
        }
        val storageLocation =
            storageLocationRepository.findById(request.storageLocationId)
                .orElseThrow {
                    IllegalArgumentException("Storage location with ${request.storageLocationId} not found")
                }

        val rawMaterial = request.toEntity(supplier, owner, storageLocation)

        try {
            return rawMaterialRepository
                .save(rawMaterial)
                .toResponse()
        } catch (ex: DataIntegrityViolationException) {

            val constraintName = findConstraintName(ex)

            if (constraintName == SUPPLIER_LOT_UNIQUE_CONSTRAINT) {
                throw DuplicateRawMaterialException(
                    "A raw material with supplier ${request.supplierId} " +
                            "and lot number ${request.lotNumber} already exists"
                )
            }

            throw ex
        }
    }

    @Transactional
    fun updateRawMaterial(
        id: Long,
        request: UpdateRawMaterialRequest
    ): RawMaterialResponse {

        val rawMaterial = rawMaterialRepository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("Raw material $id not found")
            }

        if (rawMaterial.version != request.version) {
            throw ObjectOptimisticLockingFailureException(
                RawMaterial::class.java,
                id
            )
        }

        val supplier = supplierRepository.findById(request.supplierId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Supplier ${request.supplierId} not found"
                )
            }

        val owner = userRepository.findById(request.ownerId)
            .orElseThrow {
                ResourceNotFoundException(
                    "User ${request.ownerId} not found"
                )
            }

        val storageLocation =
            storageLocationRepository.findById(request.storageLocationId)
                .orElseThrow {
                    ResourceNotFoundException(
                        "Storage location ${request.storageLocationId} not found"
                    )
                }

        rawMaterial.updateFrom(
            request,
            supplier,
            owner,
            storageLocation
        )

        try {
            rawMaterialRepository.flush()

            return rawMaterial.toResponse()
        } catch (ex: DataIntegrityViolationException) {

            val constraintName = findConstraintName(ex)

            if (constraintName == SUPPLIER_LOT_UNIQUE_CONSTRAINT) {
                throw DuplicateRawMaterialException(
                    "A raw material with supplier ${request.supplierId} " +
                            "and lot number ${request.lotNumber} already exists"
                )
            }

            throw ex
        }
    }

    @Transactional
    fun patchRawMaterial(
        id: Long,
        patch: JsonNode
    ): RawMaterialResponse {

        val rawMaterial = rawMaterialRepository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("Raw material $id not found")
            }

        val requestedVersion = extractVersion(patch)

        if (rawMaterial.version != requestedVersion) {
            throw ObjectOptimisticLockingFailureException(
                RawMaterial::class.java,
                id
            )
        }

        rawMaterialPatchMapper.apply(
            patch,
            rawMaterial
        )

        try {
            rawMaterialRepository.flush()
            return rawMaterial.toResponse()
        } catch (ex: DataIntegrityViolationException) {

            val constraintName = findConstraintName(ex)

            if (constraintName == SUPPLIER_LOT_UNIQUE_CONSTRAINT) {
                throw DuplicateRawMaterialException(
                    "A raw material with supplier ${rawMaterial.supplier.id} " +
                            "and lot number ${rawMaterial.lotNumber} already exists"
                )
            }

            throw ex
        }
    }

    fun deleteById(id: Long) {
        if (!rawMaterialRepository.existsById(id)) {
            throw ResourceNotFoundException("Raw material with $id not found")
        }

        rawMaterialRepository.deleteById(id)
    }
}
