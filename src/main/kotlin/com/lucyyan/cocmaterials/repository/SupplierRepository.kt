package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.Supplier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : JpaRepository<Supplier, Long>