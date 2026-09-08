package com.lucyyan.cocmaterials.mapper

import com.lucyyan.cocmaterials.dto.CreateRawMaterialRequest
import com.lucyyan.cocmaterials.dto.RawMaterialResponse
import com.lucyyan.cocmaterials.dto.UpdateRawMaterialRequest
import com.lucyyan.cocmaterials.entity.RawMaterial
import com.lucyyan.cocmaterials.entity.StorageLocation
import com.lucyyan.cocmaterials.entity.Supplier
import com.lucyyan.cocmaterials.entity.User

fun CreateRawMaterialRequest.toEntity(
    supplier: Supplier,
    owner: User,
    storageLocation: StorageLocation
): RawMaterial {
    return RawMaterial(
        productCode = productCode,
        corningPartNumber = corningPartNumber,
        lotNumber = lotNumber,
        materialType = materialType,
        materialClass = materialClass,
        form = form,
        category = category,
        quantity = quantity,
        uom = uom,
        notes = notes,
        supplier = supplier,
        owner = owner,
        storageLocation = storageLocation,
        dateIn = dateIn,
        dateOut = dateOut
    )
}

fun RawMaterial.toResponse(): RawMaterialResponse {
    return RawMaterialResponse(
        id = requireNotNull(id),
        productCode = productCode,
        corningPartNumber = corningPartNumber,
        lotNumber = lotNumber,
        materialType = materialType,
        materialClass = materialClass,
        form = form,
        category = category,
        quantity = quantity,
        uom = uom,
        notes = notes,

        supplierId = requireNotNull(supplier.id),
        supplierName = supplier.name,

        ownerId = requireNotNull(owner.id),
        ownerName = owner.name,

        storageLocationId = requireNotNull(storageLocation.id),
        storageLocationName = storageLocation.name,

        dateIn = dateIn,
        dateOut = dateOut,

        createdAt = createdAt,
        updatedAt = updatedAt,

        version = version
    )
}

fun RawMaterial.updateFrom(
    request: UpdateRawMaterialRequest,
    supplier: Supplier,
    owner: User,
    storageLocation: StorageLocation
) {
    productCode = request.productCode
    corningPartNumber = request.corningPartNumber
    lotNumber = request.lotNumber
    materialType = request.materialType
    materialClass = request.materialClass
    form = request.form
    category = request.category
    quantity = request.quantity
    uom = request.uom
    notes = request.notes

    this.supplier = supplier
    this.owner = owner
    this.storageLocation = storageLocation

    dateIn = request.dateIn
    dateOut = request.dateOut
}