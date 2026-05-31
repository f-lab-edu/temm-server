package io.github.ktg.temm.app.api.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.temm.app.service.InventoryAdjustService;
import io.github.ktg.temm.app.service.InventoryCreateService;
import io.github.ktg.temm.app.service.InventoryDeleteService;
import io.github.ktg.temm.app.service.InventoryInboundService;
import io.github.ktg.temm.app.service.InventoryOutboundService;
import io.github.ktg.temm.app.service.InventoryQueryService;
import io.github.ktg.temm.app.service.InventoryTransactionQueryService;
import io.github.ktg.temm.app.service.InventoryTransferService;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.model.TransactionType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InventoryCreateService inventoryCreateService;

    @MockitoBean
    InventoryQueryService inventoryQueryService;

    @MockitoBean
    InventoryInboundService inventoryInboundService;

    @MockitoBean
    InventoryOutboundService inventoryOutboundService;

    @MockitoBean
    InventoryAdjustService inventoryAdjustService;

    @MockitoBean
    InventoryTransferService inventoryTransferService;

    @MockitoBean
    InventoryDeleteService inventoryDeleteService;

    @MockitoBean
    InventoryTransactionQueryService inventoryTransactionQueryService;

    @Test
    @DisplayName("장소별 재고 거래 이력 조회")
    void listTransactions() throws Exception {
        // given
        Long placeId = 1L;
        InventoryTransaction transaction = mock(InventoryTransaction.class);
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 25, 22, 0);
        given(transaction.getId()).willReturn(10L);
        given(transaction.getType()).willReturn(TransactionType.TRANSFER);
        given(transaction.getProductId()).willReturn(20L);
        given(transaction.getQuantity()).willReturn(5);
        given(transaction.getFromPlaceId()).willReturn(placeId);
        given(transaction.getToPlaceId()).willReturn(2L);
        given(transaction.getCreatedBy()).willReturn("user-id");
        given(transaction.getCreatedAt()).willReturn(createdAt);
        given(inventoryTransactionQueryService.findByPlaceId(placeId, 1, 10))
            .willReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 10), 1));

        // when
        // then
        mockMvc.perform(get("/api/v1/places/{placeId}/inventories/transactions", placeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(10L))
            .andExpect(jsonPath("$.content[0].type").value("TRANSFER"))
            .andExpect(jsonPath("$.content[0].productId").value(20L))
            .andExpect(jsonPath("$.content[0].fromPlaceId").value(placeId))
            .andExpect(jsonPath("$.content[0].toPlaceId").value(2L))
            .andExpect(jsonPath("$.content[0].quantity").value(5))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(10));
    }
}
