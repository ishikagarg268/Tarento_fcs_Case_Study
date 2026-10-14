package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.fulfilment.adapters.database.StoreProductWarehouseRepository;
import com.fulfilment.application.monolith.fulfilment.application.StoreProductWarehouseService;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class StoreProductWarehouseServiceTest {

    @Inject
    StoreProductWarehouseService service;

    @Inject
    StoreProductWarehouseRepository repository;

    @Inject
    ProductRepository productRepository;

    @Inject
    WarehouseRepository warehouseRepository;

    @BeforeEach
    @Transactional
    void cleanAssociations() {
        repository.deleteAll();
    }

    @Test
    @Transactional
    void shouldCreateAssociation() {
        service.associate(1L, 1L, 1L);

        assertEquals(1, repository.count());
    }

    @Test
    @Transactional
    void shouldNotAllowMoreThanTwoWarehousesForProductInStore() {
        service.associate(1L, 1L, 1L);
        service.associate(1L, 1L, 2L);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(1L, 1L, 3L));
    }

    @Test
    @Transactional
    void shouldNotAllowMoreThanThreeWarehousesForStore() {
        service.associate(1L, 1L, 1L);
        service.associate(1L, 2L, 2L);
        service.associate(1L, 3L, 3L);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(1L, 1L, 2L));
    }

    @Test
    @Transactional
    void shouldNotAllowMoreThanFiveProductsForWarehouse() {
        Product product4 = createProduct("PRODUCT-4");
        Product product5 = createProduct("PRODUCT-5");
        Product product6 = createProduct("PRODUCT-6");
        Product product7 = createProduct("PRODUCT-7");
        Product product8 = createProduct("PRODUCT-8");

        service.associate(1L, 1L, 1L);
        service.associate(1L, product4.id, 1L);
        service.associate(1L, product5.id, 1L);
        service.associate(1L, product6.id, 1L);
        service.associate(1L, product7.id, 1L);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(1L, product8.id, 1L));
    }

    @Test
    void shouldRejectDuplicateAssociation() {
        service.associate(1L, 1L, 1L);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(1L, 1L, 1L));
    }

    @Test
    void shouldRejectUnknownStore() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(9999L, 1L, 1L));
    }

    @Test
    void shouldRejectUnknownProduct() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(1L, 9999L, 1L));
    }

    @Test
    void shouldRejectUnknownWarehouse() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(1L, 1L, 9999L));
    }

    private Product createProduct(String name) {
        Product product = new Product();
        product.name = name;
        productRepository.persist(product);
        return product;
    }
}