package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class WarehouseResourceTest {

    @Test
    public void testListWarehouses() {
        given()
                .when()
                .get("/warehouse")
                .then()
                .statusCode(200)
                .body(containsString("MWH.001"))
                .body(containsString("MWH.012"))
                .body(containsString("MWH.023"));
    }

    @Test
    public void testGetWarehouseById() {
        given()
                .when()
                .get("/warehouse/1")
                .then()
                .statusCode(200)
                .body("id", equalTo("1"))
                .body("businessUnitCode", equalTo("MWH.001"))
                .body("location", equalTo("ZWOLLE-001"));
    }

    @Test
    public void testGetWarehouseByUnknownId() {
        given()
                .when()
                .get("/warehouse/9999")
                .then()
                .statusCode(500);
    }

    @Test
    public void testCreateWarehouseWhenLocationHasMaximumWarehouses() {
        given()
                .contentType("application/json")
                .body("""
                    {
                      "businessUnitCode": "TEST.001",
                      "location": "TILBURG-001",
                      "capacity": 1,
                      "stock": 0
                    }
                    """)
                .when()
                .post("/warehouse")
                .then()
                .statusCode(500);
    }
    @Test
    public void testArchiveUnknownWarehouse() {
        given()
                .when()
                .delete("/warehouse/9999")
                .then()
                .statusCode(500);
    }
}