package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.PlaceHasStockException;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceTest {

    @Test
    @DisplayName("장소 생성")
    void createPlace() {
        // given
        Store store = mock(Store.class);
        String name = "창고 A";

        // when
        Place place = Place.create(store, name);

        // then
        assertThat(place.getName()).isEqualTo(name);
        assertThat(place.getStore()).isEqualTo(store);
    }

    @Test
    @DisplayName("장소 이름 변경")
    void changeName() {
        // given
        Place place = Place.create(mock(Store.class), "창고 A");
        String newName = "창고 B";

        // when
        place.changeName(newName);

        // then
        assertThat(place.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("재고가 없으면 장소 삭제 가능")
    void validateDeletableSuccess() throws Exception {
        // given
        Place place = Place.create(mock(Store.class), "창고 A");
        Inventory emptyInventory = mock(Inventory.class);
        given(emptyInventory.getQuantity()).willReturn(0);
        setInventories(place, List.of(emptyInventory));

        // when
        place.validateDeletable();

        // then
    }

    @Test
    @DisplayName("재고가 남아있으면 장소 삭제 불가")
    void validateDeletableFailWhenHasStock() throws Exception {
        // given
        Place place = Place.create(mock(Store.class), "창고 A");
        Inventory inventoryWithStock = mock(Inventory.class);
        given(inventoryWithStock.getQuantity()).willReturn(5);
        setInventories(place, List.of(inventoryWithStock));

        // when
        // then
        assertThatThrownBy(place::validateDeletable)
            .isInstanceOf(PlaceHasStockException.class)
            .hasMessageContaining(ErrorCode.PLACE_HAS_STOCK.getMessage());
    }

    private void setInventories(Place place, List<Inventory> inventories) throws Exception {
        Field field = Place.class.getDeclaredField("inventories");
        field.setAccessible(true);
        field.set(place, inventories);
    }
}
