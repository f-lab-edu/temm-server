package io.github.ktg.temm.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserStore> userStores;

    private Store(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.userStores = new ArrayList<>();
        if (user != null) {
            addManager(user);
        }
    }

    public static Store create(String name, User user) {
        return new Store(null, name, user);
    }

    public void addManager(User user) {
        UserStore manager = UserStore.createManager(this, user);
        userStores.add(manager);
    }

}
