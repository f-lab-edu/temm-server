package io.github.ktg.temm.domain.provider;

public interface TokenProvider {

    String generateAccessToken(String userId);
    String generateRefreshToken(String userId);
    boolean validateAccessToken(String accessToken);
    String getUserIdByAccessToken(String accessToken);

}
