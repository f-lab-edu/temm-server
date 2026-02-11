package io.github.ktg.temm.app.security;

import io.github.ktg.temm.app.api.exception.ErrorResponse;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class LoginCheckFilter extends OncePerRequestFilter {

    private final PathPatternParser pathPatternParser;
    private final List<PathPattern> whiteListPathPatterns;
    private final ObjectMapper objectMapper;

    public LoginCheckFilter(String[] whiteListPathPatterns, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.pathPatternParser = new PathPatternParser();
        this.whiteListPathPatterns = Arrays.stream(whiteListPathPatterns)
            .map(pathPatternParser::parse)
            .toList();
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        PathContainer path = PathContainer.parsePath(requestURI);

        if (!isWhitelisted(path) && LoginContext.get() == null) {
            writeUnauthorizedResponse(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isWhitelisted(PathContainer path) {
        for (PathPattern pattern : whiteListPathPatterns) {
            if (pattern.matches(path)) {
                return true;
            }
        }
        return false;
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
            objectMapper.writeValueAsString(
                new ErrorResponse(
                    SecurityErrorCode.UNAUTHORIZED.name(),
                    SecurityErrorCode.UNAUTHORIZED.getMessage()
                )
            )
        );
    }
}
