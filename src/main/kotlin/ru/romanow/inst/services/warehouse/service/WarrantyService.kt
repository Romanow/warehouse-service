package ru.romanow.inst.services.warehouse.service

import ru.romanow.inst.services.warehouse.model.OrderWarrantyRequest
import ru.romanow.inst.services.warehouse.model.OrderWarrantyResponse
import java.util.*

interface WarrantyService {
    fun warrantyRequest(orderItemUid: UUID, request: OrderWarrantyRequest): OrderWarrantyResponse
}
