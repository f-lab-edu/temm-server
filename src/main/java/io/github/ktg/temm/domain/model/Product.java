package io.github.ktg.temm.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryProduct> categoryProducts;

    @Embedded
    private Sku sku;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "image_url")
    private String imageUrl;


    private Product(Long id, Long storeId, List<Category> categories, Sku sku,
        ProductStatus status, String name, String barcode, String imageUrl) {
        this.id = id;
        this.storeId = storeId;
        this.sku = sku;
        this.status = status;
        this.name = name;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.categoryProducts = new ArrayList<>();
        addCategories(categories);
    }

    private void addCategories(List<Category> categories) {
        Optional.ofNullable(categories)
            .orElse(Collections.emptyList())
            .forEach(this::addCategory);
    }

    private void addCategory(Category category) {
        this.categoryProducts.add(
            CategoryProduct.create(category, this)
        );
    }

    public static Product create(Long storeId, List<Category> categories, String sku, String name, String barcode, String imageUrl) {
        return new Product(null, storeId, categories, new Sku(sku), ProductStatus.REGISTERED, name, barcode, imageUrl);
    }

    public void register() {
        this.status = ProductStatus.REGISTERED;
    }

    public void stop() {
        this.status = ProductStatus.STOPPED;
    }

    public void delete() {
        this.status = ProductStatus.DELETED;
    }

    public void changeSku(String sku) {
        this.sku = new Sku(sku);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void changeImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeCategories(List<Category> categories) {
        this.categoryProducts.clear();
        addCategories(categories);
    }

}
