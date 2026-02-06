package io.github.ktg.temm.app.service;

import io.github.ktg.temm.domain.model.SocialType;

public interface SocialTokenService {

    String getToken(SocialType socialType, String code);

}
