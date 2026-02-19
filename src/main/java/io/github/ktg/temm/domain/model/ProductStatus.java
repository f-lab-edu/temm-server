package io.github.ktg.temm.domain.model;

import lombok.Getter;

@Getter
public enum ProductStatus {
    REGISTERED("등록"),
    STOPPED("중단"),
    DELETED("삭제");

    private final String desc;

    ProductStatus(String desc) {
        this.desc = desc;
    }

}
