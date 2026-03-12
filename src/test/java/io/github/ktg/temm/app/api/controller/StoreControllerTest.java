package io.github.ktg.temm.app.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.temm.app.api.dto.StoreCreateRequest;
import io.github.ktg.temm.app.api.dto.StoreMemberAddRequest;
import io.github.ktg.temm.app.api.dto.StoreUpdateRequest;
import io.github.ktg.temm.app.service.StoreCreateService;
import io.github.ktg.temm.app.service.StoreMemberService;
import io.github.ktg.temm.app.service.StoreUpdateService;
import java.util.UUID;
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

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    StoreCreateService storeCreateService;

    @MockitoBean
    StoreUpdateService storeUpdateService;

    @MockitoBean
    StoreMemberService storeMemberService;

    @Test
    @DisplayName("상점 생성 성공")
    void create() throws Exception {
        // given
        StoreCreateRequest request = new StoreCreateRequest("새 상점");
        willDoNothing().given(storeCreateService).create(any());

        MockHttpServletRequestBuilder builder = post("/api/v1/stores")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("상점 수정 성공")
    void update() throws Exception {
        // given
        Long storeId = 1L;
        StoreUpdateRequest request = new StoreUpdateRequest("수정된 상점");
        willDoNothing().given(storeUpdateService).update(any(), any());

        MockHttpServletRequestBuilder builder = put("/api/v1/stores/{storeId}", storeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("멤버 추가 성공")
    void addMember() throws Exception {
        // given
        Long storeId = 1L;
        StoreMemberAddRequest request = new StoreMemberAddRequest(UUID.randomUUID());
        willDoNothing().given(storeMemberService).addMember(any(), any());

        MockHttpServletRequestBuilder builder = post("/api/v1/stores/{storeId}/members", storeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("멤버 삭제 성공")
    void removeMember() throws Exception {
        // given
        Long storeId = 1L;
        UUID userId = UUID.randomUUID();
        willDoNothing().given(storeMemberService).removeMember(any(), any());

        MockHttpServletRequestBuilder builder = delete("/api/v1/stores/{storeId}/members/{userId}", storeId, userId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("매니저 권한 부여 성공")
    void grantManagerRole() throws Exception {
        // given
        Long storeId = 1L;
        UUID userId = UUID.randomUUID();
        willDoNothing().given(storeMemberService).grantManagerRole(any(), any());

        MockHttpServletRequestBuilder builder = patch("/api/v1/stores/{storeId}/members/{userId}/manager", storeId, userId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("멤버 권한 부여 성공")
    void grantMemberRole() throws Exception {
        // given
        Long storeId = 1L;
        UUID userId = UUID.randomUUID();
        willDoNothing().given(storeMemberService).grantMemberRole(any(), any());

        MockHttpServletRequestBuilder builder = patch("/api/v1/stores/{storeId}/members/{userId}/member", storeId, userId);

        // when
        ResultActions perform = mockMvc.perform(builder);

        // then
        perform.andDo(print())
            .andExpect(status().isOk());
    }
}
