package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class ProductTest {

    @Test
    @DisplayName("물품 생성 시 등록 상태")
    void createProduct() {
        // given
        Long storeId = 1L;
        String sku = "KC-12";
        String name = "물품";
        String barcode = "barcode";
        String imageUrl = "s3://image/url";

        // when
        Product registerProduct = Product.create(storeId, mock(List.class), sku, name, barcode,
            imageUrl);

        // then
        assertThat(registerProduct.getStatus()).isEqualTo(ProductStatus.REGISTERED);
    }

    @Test
    @DisplayName("물품 등록")
    void register() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");
        product.stop();

        // when
        product.register();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.REGISTERED);
    }

    @Test
    @DisplayName("물품 판매 중지")
    void stop() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");

        // when
        product.stop();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.STOPPED);
    }

    @Test
    @DisplayName("물품 삭제")
    void delete() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");

        // when
        product.delete();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
    }

    @Test
    @DisplayName("SKU 변경")
    void changeSku() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");
        String newSku = "NEW-SKU";

        // when
        product.changeSku(newSku);

        // then
        assertThat(product.getSku().value()).isEqualTo(newSku);
    }

    @Test
    @DisplayName("이름 변경")
    void changeName() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");
        String newName = "새로운 이름";

        // when
        product.changeName(newName);

        // then
        assertThat(product.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("바코드 변경")
    void changeBarcode() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");
        String newBarcode = "new-barcode";

        // when
        product.changeBarcode(newBarcode);

        // then
        assertThat(product.getBarcode()).isEqualTo(newBarcode);
    }

    @Test
    @DisplayName("이미지 변경")
    void changeImage() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");
        String newImageUrl = "s3://new/image/url";

        // when
        product.changeImage(newImageUrl);

        // then
        assertThat(product.getImageUrl()).isEqualTo(newImageUrl);
    }

    @Test
    @DisplayName("카테고리 변경")
    void changeCategories() {
        // given
        Product product = Product.create(1L, mock(List.class), "KC-12", "물품", "barcode",
            "s3://image/url");
        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);
        List<Category> newCategories = List.of(category1, category2);

        // when
        product.changeCategories(newCategories);

        // then
        assertThat(product.getCategoryProducts()).hasSize(2);
    }
}
