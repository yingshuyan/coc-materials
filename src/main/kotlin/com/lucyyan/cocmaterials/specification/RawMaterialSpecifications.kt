package com.lucyyan.cocmaterials.specification

import com.lucyyan.cocmaterials.entity.RawMaterial
import com.lucyyan.cocmaterials.entity.StorageLocation
import com.lucyyan.cocmaterials.entity.Supplier
import com.lucyyan.cocmaterials.entity.User
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

object RawMaterialSpecifications {

    fun productCodeContains(value: String): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            cb.like(
                cb.lower(root.get("productCode")),
                "%${value.lowercase()}%"
            )
        }
    }

    fun lotNumberEquals(value: String): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            cb.equal(root.get<String>("lotNumber"), value)
        }
    }

    fun supplierIdEquals(id: Long): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            val supplierJoin = root.join<RawMaterial, Supplier>(
                "supplier",
                JoinType.INNER
            )
            cb.equal(supplierJoin.get<Long>("id"), id)
        }
    }

    fun ownerIdEquals(id: Long): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            val ownerJoin = root.join<RawMaterial, User>(
                "owner",
                JoinType.INNER
            )
            cb.equal(ownerJoin.get<Long>("id"), id)
        }
    }

    fun storageLocationIdEquals(id: Long): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            val storageJoin = root.join<RawMaterial, StorageLocation>(
                "storage",
                JoinType.INNER
            )
            cb.equal(storageJoin.get<Long>("id"), id)
        }
    }

    fun dateInGreaterThanOrEqual(value: Instant): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            cb.greaterThanOrEqualTo(root.get("dateIn"), value)
        }
    }

    fun dateInLessThanOrEqual(value: Instant): Specification<RawMaterial> {
        return Specification { root, _, cb ->
            cb.lessThanOrEqualTo(root.get("dateIn"), value)
        }
    }
}