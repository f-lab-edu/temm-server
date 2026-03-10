package io.github.ktg.temm.domain.provider;

import io.github.ktg.temm.domain.model.UserStore;
import java.util.List;

public interface TokenProvider {

    String generateAccessToken(String userId, List<UserStore> storeInfos);
    String generateRefreshToken(String userId);
    boolean validateAccessToken(String accessToken);
    String getUserIdByAccessToken(String accessToken);
    List<UserStore> getUserStoresByAccessToken(String accessToken);

}
