package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.fulfilment.adapters.database.StoreProductWarehouseRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class StoreProductWarehouseResourceTest {

    @Inject
    StoreProductWarehouseRepository repository;

    @BeforeEach
    @Transactional
    void cleanAssociations() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAssociation() {
        given()
                .when()
                .post("/store/1/product/1/warehouse/1")
                .then()
                .statusCode(201);
    }

    @Test
    void shouldRejectDuplicateAssociation() {
        given()
                .when()
                .post("/store/1/product/1/warehouse/1")
                .then()
                .statusCode(201);

        given()
                .when()
                .post("/store/1/product/1/warehouse/1")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectUnknownStore() {
        given()
                .when()
                .post("/store/9999/product/1/warehouse/1")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectUnknownProduct() {
        given()
                .when()
                .post("/store/1/product/9999/warehouse/1")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectUnknownWarehouse() {
        given()
                .when()
                .post("/store/1/product/1/warehouse/9999")
                .then()
                .statusCode(400);
    }
}