package io.github.ktg.temm.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "user_stores")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserStore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    private Authorization authorization;

    private UserStore(Long id, User user, Store store, Authorization authorization) {
        this.id = id;
        this.user = user;
        this.store = store;
        this.authorization = authorization;
    }

    public static UserStore createManager(Store store, User user) {
        return new UserStore(null, user, store, Authorization.MANAGER);
    }

}
