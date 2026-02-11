package io.github.ktg.temm.domain.provider;

import io.github.ktg.temm.domain.dto.SocialUserInfo;
import io.github.ktg.temm.domain.model.SocialType;

public interface SocialUserProvider {

    SocialType getType();
    SocialUserInfo getUserInfo(String socialToken);
}
