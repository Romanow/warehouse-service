package ru.romanow.inst.services.warehouse.model

import javax.validation.constraints.NotEmpty

data class OrderWarrantyRequest(
    @field:NotEmpty(message = "{field.is.empty}")
    val reason: String
)