package io.github.ktg.temm.app.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.verify;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LoginCheckFilterTest {
    
    @Mock
    FilterChain filterChain;

    ObjectMapper objectMapper;
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @DisplayName("화이트 리스트에 등록된 요청 URL은 통과")
    void whiteListPathWillPass() throws Exception {
        // given
        LoginCheckFilter loginCheckFilter = new LoginCheckFilter(
            new String[]{"/api/v1/auth/**"},
            objectMapper
        );
        request.setRequestURI("/api/v1/auth/login");
        // when
        loginCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("인증된 상태면 통과")
    void authenticatedWillPass() throws Exception {
        // given
        LoginCheckFilter loginCheckFilter = new LoginCheckFilter(
            new String[]{"/api/v1/auth/**"},
            objectMapper
        );
        request.setRequestURI("/api/v1/resource");
        LoginContext.set("login!");

        // when
        loginCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }


    @Test
    @DisplayName("미인증된 상태면 UNAUTHORIZED 리턴")
    void unauthenticatedWillReturnUuAuthorized() throws Exception {
        // given
        LoginCheckFilter loginCheckFilter = new LoginCheckFilter(
            new String[]{"/api/v1/auth/**"},
            objectMapper
        );
        request.setRequestURI("/api/v1/resource");

        // when
        loginCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        String content = response.getContentAsString();
        assertThat(content).contains("UNAUTHORIZED");
    }


}