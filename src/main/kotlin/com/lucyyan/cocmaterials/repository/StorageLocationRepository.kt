package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.StorageLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StorageLocationRepository : JpaRepository<StorageLocation, Long>