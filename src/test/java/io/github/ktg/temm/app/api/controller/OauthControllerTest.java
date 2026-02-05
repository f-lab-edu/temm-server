package io.github.ktg.temm.app.api.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.temm.app.api.dto.OauthLoginRequest;
import io.github.ktg.temm.app.dto.LoginResult;
import io.github.ktg.temm.app.exception.SocialLoginFailedException;
import io.github.ktg.temm.app.security.JwtAuthenticationFilter;
import io.github.ktg.temm.app.service.OAuthLoginService;
import io.github.ktg.temm.domain.model.SocialType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(
    value = OauthController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class OauthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OAuthLoginService oAuthLoginService;

    @Test
    @DisplayName("OAuth 로그인 요청 시 토큰 정보를 응답")
    void willResponseToken() throws Exception {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String code = "code";
        OauthLoginRequest loginRequest = new OauthLoginRequest(code);
        given(oAuthLoginService.login(SocialType.GOOGLE, loginRequest.code())).willReturn(new LoginResult(
            accessToken,
            refreshToken));
        MockHttpServletRequestBuilder builder = post(
                "/api/v1/auth/google/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest));
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value(accessToken))
            .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("ID Token 이 빈 값일 경우 Bad Request (400)")
    void idTokenBlankWillBadRequest() throws Exception {
        // given
        OauthLoginRequest loginRequest = new OauthLoginRequest("");

        MockHttpServletRequestBuilder builder = post(
            "/api/v1/auth/google/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest));
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ID Token 이 유효하지 않을 경우 Unauthorized (401)")
    void idTokenInvalidWillUnauthorized() throws Exception {
        // given
        String code = "code";
        OauthLoginRequest loginRequest = new OauthLoginRequest(code);
        given(oAuthLoginService.login(SocialType.KAKAO, loginRequest.code()))
            .willThrow(new SocialLoginFailedException());
        MockHttpServletRequestBuilder builder = post(
            "/api/v1/auth/kakao/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest));
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("지원 하지 않은 소셜 타입일 경우 Bad Request (400)")
    void notSupportSocialWillBadRequest() throws Exception {
        // given
        String code = "code";
        OauthLoginRequest loginRequest = new OauthLoginRequest(code);
        MockHttpServletRequestBuilder builder = post(
            "/api/v1/auth/naver/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest));
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andDo(print());
    }

}