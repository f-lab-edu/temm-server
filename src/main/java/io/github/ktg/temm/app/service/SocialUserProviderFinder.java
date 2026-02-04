package io.github.ktg.temm.app.service;

import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.provider.SocialUserProvider;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SocialUserProviderFinder {

    private final Map<SocialType, SocialUserProvider> providers;

    public SocialUserProviderFinder(List<SocialUserProvider> providers) {
        this.providers = new EnumMap<>(SocialType.class);
        for (SocialUserProvider provider : providers) {
            this.providers.put(provider.getType(), provider);
        }
    }

    public Optional<SocialUserProvider> find(SocialType socialType) {
        return Optional.ofNullable(providers.get(socialType));
    }

}
