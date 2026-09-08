package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.RawMaterial
import org.hibernate.annotations.processing.SQL
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface RawMaterialRepository :
    JpaRepository<RawMaterial, Long>,
    JpaSpecificationExecutor<RawMaterial>,
    RawMaterialRepositoryCustom
{

    @EntityGraph(
        attributePaths = [
            "supplier",
            "owner",
            "storageLocation"
        ]
    )
    fun findWithRelationsById(id: Long): RawMaterial?
}