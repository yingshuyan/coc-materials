package com.lucyyan.cocmaterials.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "raw_materials")
class RawMaterial(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "product_code", nullable = false)
    var productCode: String,

    @Column(name = "corning_part", nullable = true)
    var corningPartNumber: String? = null,

    @Column(name= "lot_number", nullable = false)
    var lotNumber: String,

    @Column(name = "material_type", nullable = false)
    var materialType: String,

    @Column(name = "class", nullable = false)
    var materialClass: String,

    @Column(nullable = false)
    var form: String,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false, precision = 12, scale = 3)
    var quantity: BigDecimal,

    @Column(nullable = false)
    var uom: String,

    @Column(nullable = true)
    var notes: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    var supplier: Supplier,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id", nullable = false)
    var storageLocation: StorageLocation,


    @Column(name = "date_in", nullable = false)
    var dateIn: Instant,

    @Column(name = "date_out", nullable = true)
    var dateOut: Instant? = null,

    @Version
    @Column(nullable = false)
    var version: Long = 0
    ) {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant
}