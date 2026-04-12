package io.github.ktg.temm.app.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.temm.app.dto.UserStoreQueryResult;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.UserStoreQueryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserStoreController.class)
class UserStoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserStoreQueryService userStoreQueryService;

    @BeforeEach
    void setUp() {
        LoginContext.set(UUID.randomUUID().toString());
    }

    @AfterEach
    void tearDown() {
        LoginContext.remove();
    }

    @Test
    @DisplayName("내 스토어 목록 조회 성공")
    void getMyStores() throws Exception {
        // given
        List<UserStoreQueryResult> results = List.of(
            new UserStoreQueryResult(1L, "스토어A"),
            new UserStoreQueryResult(2L, "스토어B")
        );
        given(userStoreQueryService.searchByUserId(any())).willReturn(results);

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/users/stores"));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].storeId").value(1L))
            .andExpect(jsonPath("$[0].storeName").value("스토어A"))
            .andExpect(jsonPath("$[1].storeId").value(2L))
            .andExpect(jsonPath("$[1].storeName").value("스토어B"));
    }

    @Test
    @DisplayName("스토어가 없는 유저는 빈 목록 반환")
    void getMyStoresEmpty() throws Exception {
        // given
        given(userStoreQueryService.searchByUserId(any())).willReturn(List.of());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/users/stores"));

        // then
        perform.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}