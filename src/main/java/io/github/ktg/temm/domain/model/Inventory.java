package io.github.ktg.temm.domain.model;

import io.github.ktg.temm.domain.exception.InsufficientStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    private Inventory(Place place, Product product) {
        this.place = place;
        this.product = product;
        this.quantity = 0;
    }

    public static Inventory create(Place place, Product product) {
        return new Inventory(place, product);
    }

    public void inbound(int qty) {
        this.quantity += qty;
    }

    public void outbound(int qty) {
        if (this.quantity < qty) {
            throw new InsufficientStockException();
        }
        this.quantity -= qty;
    }

    public void adjust(int newQuantity) {
        this.quantity = newQuantity;
    }
}