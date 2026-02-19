package io.github.ktg.temm.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "name", nullable = false)
    private String name;

    private Category(Long id, Long storeId, String name) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
    }

    public static Category create(Long storeId, String name) {
        return new Category(null, storeId, name);
    }

}
