package io.github.ktg.temm.app.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.temm.app.api.dto.PlaceCreateRequest;
import io.github.ktg.temm.app.api.dto.PlaceUpdateRequest;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.PlaceCreateService;
import io.github.ktg.temm.app.service.PlaceDeleteService;
import io.github.ktg.temm.app.service.PlaceQueryService;
import io.github.ktg.temm.app.service.PlaceUpdateService;
import io.github.ktg.temm.domain.model.Place;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PlaceCreateService placeCreateService;

    @MockitoBean
    PlaceUpdateService placeUpdateService;

    @MockitoBean
    PlaceDeleteService placeDeleteService;

    @MockitoBean
    PlaceQueryService placeQueryService;

    @BeforeEach
    void setUp() {
        LoginContext.set(UUID.randomUUID().toString());
    }

    @AfterEach
    void tearDown() {
        LoginContext.remove();
    }

    @Test
    @DisplayName("장소 생성 성공")
    void create() throws Exception {
        // given
        PlaceCreateRequest request = new PlaceCreateRequest(1L, "창고 A");
        willDoNothing().given(placeCreateService).create(any());

        MockHttpServletRequestBuilder builder = post("/api/v1/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("장소 생성 실패 - 필수 값 누락")
    void createFail() throws Exception {
        // given
        PlaceCreateRequest request = new PlaceCreateRequest(null, "");

        MockHttpServletRequestBuilder builder = post("/api/v1/places")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장소 수정 성공")
    void update() throws Exception {
        // given
        Long placeId = 1L;
        PlaceUpdateRequest request = new PlaceUpdateRequest("창고 B");
        willDoNothing().given(placeUpdateService).update(anyLong(), any());

        MockHttpServletRequestBuilder builder = put("/api/v1/places/{placeId}", placeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장소 수정 실패 - 필수 값 누락")
    void updateFail() throws Exception {
        // given
        Long placeId = 1L;
        PlaceUpdateRequest request = new PlaceUpdateRequest("");

        MockHttpServletRequestBuilder builder = put("/api/v1/places/{placeId}", placeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장소 삭제 성공")
    void deletePlace() throws Exception {
        // given
        Long placeId = 1L;
        willDoNothing().given(placeDeleteService).delete(placeId);

        MockHttpServletRequestBuilder builder = delete("/api/v1/places/{placeId}", placeId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장소 목록 조회 성공")
    void list() throws Exception {
        // given
        Long storeId = 1L;
        Place place = mock(Place.class);
        given(place.getId()).willReturn(1L);
        given(place.getName()).willReturn("창고 A");
        given(placeQueryService.findByStoreId(storeId)).willReturn(List.of(place));

        MockHttpServletRequestBuilder builder = get("/api/v1/places")
            .param("storeId", String.valueOf(storeId));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("창고 A"));
    }

    @Test
    @DisplayName("장소 목록 조회 실패 - storeId 없음")
    void listFail() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/api/v1/places");

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isBadRequest());
    }
}
