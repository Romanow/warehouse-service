package ru.romanow.inst.services.warehouse.model

data class OrderWarrantyResponse(
    var warrantyDate: String,
    val decision: String
)