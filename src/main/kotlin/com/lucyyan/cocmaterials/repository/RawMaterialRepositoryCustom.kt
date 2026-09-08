package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.RawMaterial
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

interface RawMaterialRepositoryCustom {

    fun findAllWithRelations(
        specification: Specification<RawMaterial>,
        pageable: Pageable
    ): Page<RawMaterial>
}