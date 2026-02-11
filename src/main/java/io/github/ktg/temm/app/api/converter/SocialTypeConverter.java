package io.github.ktg.temm.app.api.converter;

import io.github.ktg.temm.domain.model.SocialType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SocialTypeConverter implements Converter<String, SocialType> {

    @Override
    public SocialType convert(String source) {
        return SocialType.valueOf(source.toUpperCase());
    }
}
