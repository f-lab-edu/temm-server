package io.github.ktg.temm.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record SocialInfo(
    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false, length = 30)
    SocialType type,
    @Column(name = "social_id", nullable = false, length = 200)
    String id
) {

}
