package io.github.ktg.temm.domain.model;

import io.github.ktg.temm.domain.exception.PlaceHasStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "place")
    private List<Inventory> inventories = new ArrayList<>();

    private Place(Store store, String name) {
        this.store = store;
        this.name = name;
    }

    public static Place create(Store store, String name) {
        return new Place(store, name);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void validateDeletable() {
        boolean hasStock = inventories.stream().anyMatch(i -> i.getQuantity() > 0);
        if (hasStock) {
            throw new PlaceHasStockException();
        }
    }
}