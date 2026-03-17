package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.LoginResult;
import io.github.ktg.temm.app.exception.NotSupportSocialTypeException;
import io.github.ktg.temm.domain.dto.SocialUserInfo;
import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.provider.SocialTokenProvider;
import io.github.ktg.temm.domain.provider.SocialUserProvider;
import io.github.ktg.temm.domain.provider.TokenProvider;
import io.github.ktg.temm.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final SocialUserProviderFinder socialUserProviderFinder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final SocialTokenProvider socialTokenProvider;

    @Transactional
    public LoginResult login(SocialType socialType, String code) {

        String socialToken = socialTokenProvider.getToken(socialType, code);
        SocialUserProvider socialUserProvider = getSocialUserProvider(socialType);
        SocialUserInfo socialUserInfo = socialUserProvider.getUserInfo(socialToken);

        User user = findOrJoin(socialType, socialUserInfo);
        String userId = String.valueOf(user.getId());

        return new LoginResult(
            tokenProvider.generateAccessToken(userId),
            tokenProvider.generateRefreshToken(userId)
        );
    }

    private SocialUserProvider getSocialUserProvider(SocialType socialType) {
        return socialUserProviderFinder.find(socialType)
            .orElseThrow(
                () -> new NotSupportSocialTypeException(socialType)
            );
    }

    private User findOrJoin(SocialType socialType, SocialUserInfo socialUserInfo) {
        return userRepository.findBySocialInfoTypeAndSocialInfoId(socialType,
                socialUserInfo.socialId())
            .orElseGet(() -> join(socialType, socialUserInfo));
    }

    private User join(SocialType socialType, SocialUserInfo socialUserInfo) {
        String name = socialUserInfo.name();
        String email = socialUserInfo.email();
        String socialId = socialUserInfo.socialId();
        User user = User.create(name, email, socialType, socialId);
        return userRepository.save(user);
    }

}
