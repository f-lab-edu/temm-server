package io.github.ktg.temm.domain.provider;

import io.github.ktg.temm.domain.model.SocialType;

public interface SocialTokenProvider {

    String getToken(SocialType socialType, String code);

}
