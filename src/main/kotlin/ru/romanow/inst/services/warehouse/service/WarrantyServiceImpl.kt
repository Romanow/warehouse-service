package ru.romanow.inst.services.warehouse.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.inst.services.common.utils.buildEx
import ru.romanow.inst.services.warehouse.exceptions.WarrantyProcessException
import ru.romanow.inst.services.warehouse.model.ItemWarrantyRequest
import ru.romanow.inst.services.warehouse.model.OrderWarrantyRequest
import ru.romanow.inst.services.warehouse.model.OrderWarrantyResponse
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class WarrantyServiceImpl(
    private val warehouseService: WarehouseService,
    private val warrantyWebClient: WebClient
) : WarrantyService {
    private val logger = LoggerFactory.getLogger(WarehouseServiceImpl::class.java)

    override fun warrantyRequest(orderItemUid: UUID, request: OrderWarrantyRequest): OrderWarrantyResponse {
        logger.info("Warranty request (reason: {}) on item '{}'", request.reason, orderItemUid)
        val availableCount = warehouseService.checkItemAvailableCount(orderItemUid)
        val warrantyRequest = ItemWarrantyRequest(
            reason = request.reason,
            availableCount = availableCount
        )

        logger.info("Request to WarrantyService to check warranty on item '{}'", orderItemUid)
        return requestToWarranty(orderItemUid, warrantyRequest)
            .orElseThrow { WarrantyProcessException("Can't process warranty request for OrderItem '$orderItemUid'") }
    }

    private fun requestToWarranty(orderItemUid: UUID, request: ItemWarrantyRequest): Optional<OrderWarrantyResponse> {
        return warrantyWebClient
            .post()
            .uri("/{orderItemUid}/warranty", orderItemUid)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(OrderWarrantyResponse::class.java)
            .blockOptional()
    }
}