package io.github.ktg.temm.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Embedded
    private Sku sku;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "image_url")
    private String imageUrl;

    private Product(Long id, Long storeId, ProductStatus status, Sku sku, String name,
        String barcode,
        String imageUrl) {
        this.id = id;
        this.storeId = storeId;
        this.status = status;
        this.sku = sku;
        this.name = name;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
    }

    public static Product register(Long storeId, String sku, String name, String barcode, String imageUrl) {
        return new Product(null, storeId, ProductStatus.REGISTERED, new Sku(sku), name, barcode, imageUrl);
    }

    public void reRegister() {
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

}
