package io.github.ktg.temm.app.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.temm.app.api.dto.ProductRegisterRequest;
import io.github.ktg.temm.app.api.dto.ProductUpdateRequest;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.ProductLifecycleService;
import io.github.ktg.temm.app.service.ProductQueryService;
import io.github.ktg.temm.app.service.ProductRegisterService;
import io.github.ktg.temm.app.service.ProductUpdateService;
import io.github.ktg.temm.domain.dto.ProductDetailCategoryResult;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductDetailStatusResult;
import io.github.ktg.temm.domain.dto.ProductSearchCategoryResult;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import io.github.ktg.temm.domain.dto.ProductSearchStatusResult;
import io.github.ktg.temm.domain.model.ProductStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductRegisterService productRegisterService;

    @MockitoBean
    ProductUpdateService productUpdateService;

    @MockitoBean
    ProductLifecycleService productLifecycleService;

    @MockitoBean
    ProductQueryService productQueryService;

    @BeforeEach
    void setUp() {
        LoginContext.set("loginUser");
    }

    @Test
    @DisplayName("상품 등록 성공")
    void register() throws Exception {
        // given
        ProductRegisterRequest request = new ProductRegisterRequest(
            1L,
            "상품명",
            "SKU-123",
            "BARCODE-123",
            "http://image.url",
            List.of(1L, 2L)
        );

        willDoNothing().given(productRegisterService).register(any());

        MockHttpServletRequestBuilder builder = post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(request1 -> {
                request1.setRemoteUser("testUser");
                return request1;
            });

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("상품 등록 실패 - 필수 값 누락")
    void registerFail() throws Exception {
        // given
        ProductRegisterRequest request = new ProductRegisterRequest(
            null,
            "",
            "",
            "BARCODE-123",
            "http://image.url",
            List.of(1L, 2L)
        );

        MockHttpServletRequestBuilder builder = post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(request1 -> {
                request1.setRemoteUser("testUser");
                return request1;
            });

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void update() throws Exception {
        // given
        Long productId = 1L;
        ProductUpdateRequest request = new ProductUpdateRequest(
            "수정된 상품명",
            "NEW-SKU",
            "NEW-BARCODE",
            "http://new.image.url",
            List.of(3L)
        );

        willDoNothing().given(productUpdateService).update(anyLong(), any());

        MockHttpServletRequestBuilder builder = put("/api/v1/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 수정 실패 - 필수 값 누락")
    void updateFail() throws Exception {
        // given
        Long productId = 1L;
        ProductUpdateRequest request = new ProductUpdateRequest(
            "", // name 누락
            "", // sku 누락
            "NEW-BARCODE",
            "http://new.image.url",
            List.of(3L)
        );

        MockHttpServletRequestBuilder builder = put("/api/v1/products/{productId}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 판매 중지 성공")
    void stop() throws Exception {
        // given
        Long productId = 1L;
        willDoNothing().given(productLifecycleService).stop(productId);

        MockHttpServletRequestBuilder builder = patch("/api/v1/products/{productId}/stop", productId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 재등록 성공")
    void reRegister() throws Exception {
        // given
        Long productId = 1L;
        willDoNothing().given(productLifecycleService).register(productId);

        MockHttpServletRequestBuilder builder = patch("/api/v1/products/{productId}/register", productId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct() throws Exception {
        // given
        Long productId = 1L;
        willDoNothing().given(productLifecycleService).delete(productId);

        MockHttpServletRequestBuilder builder = delete("/api/v1/products/{productId}", productId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getDetail() throws Exception {
        // given
        Long productId = 1L;
        ProductDetailResult result = new ProductDetailResult(
            productId,
            "상품명",
            new ProductDetailStatusResult("REGISTERED", "등록"),
            "SKU-123",
            "BARCODE-123",
            "http://image.url",
            List.of(new ProductDetailCategoryResult(1L, "카테고리1"))
        );

        given(productQueryService.getDetail(productId)).willReturn(result);

        MockHttpServletRequestBuilder builder = get("/api/v1/products/{productId}", productId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(productId))
            .andExpect(jsonPath("$.name").value("상품명"))
            .andExpect(jsonPath("$.status.code").value("REGISTERED"));
    }

    @Test
    @DisplayName("상품 검색 성공")
    void search() throws Exception {
        // given
        ProductSearchResult searchResult = new ProductSearchResult(
            1L,
            "상품명",
            new ProductSearchStatusResult(ProductStatus.REGISTERED.name(), ProductStatus.REGISTERED.getDesc()),
            "SKU-123",
            "BARCODE-123",
            "http://image.url",
            List.of(new ProductSearchCategoryResult(1L, "카테고리1"))
        );
        Page<ProductSearchResult> pageResult = new PageImpl<>(List.of(searchResult));

        given(productQueryService.search(any(), anyInt(), anyInt())).willReturn(pageResult);

        MockHttpServletRequestBuilder builder = get("/api/v1/products")
            .param("storeId", "1")
            .param("keyword", "상품")
            .param("page", "0")
            .param("size", "10");

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L))
            .andExpect(jsonPath("$.content[0].name").value("상품명"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    @DisplayName("상품 검색 실패 - 필수 값 누락")
    void searchFail() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/api/v1/products")
            .param("keyword", "상품")
            .param("page", "0")
            .param("size", "10");

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isBadRequest());
    }
}
