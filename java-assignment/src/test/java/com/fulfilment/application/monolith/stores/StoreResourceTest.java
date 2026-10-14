package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class StoreResourceTest {

    @Test
    public void testGetStores() {
        given()
                .when()
                .get("/store")
                .then()
                .statusCode(200)
                .body("size()", equalTo(3));
    }

    @Test
    public void testGetSingleStore() {
        given()
                .when()
                .get("/store/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("TONSTAD"));
    }

    @Test
    public void testGetSingleStoreNotFound() {
        given()
                .when()
                .get("/store/9999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    public void testCreateUpdatePatchAndDeleteStore() {
        String storeId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                {
                                  "name": "TEST-STORE",
                                  "quantityProductsInStock": 20
                                }
                                """)
                        .when()
                        .post("/store")
                        .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("name", equalTo("TEST-STORE"))
                        .body("quantityProductsInStock", equalTo(20))
                        .extract()
                        .path("id")
                        .toString();

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "UPDATED-STORE",
                          "quantityProductsInStock": 30
                        }
                        """)
                .when()
                .put("/store/" + storeId)
                .then()
                .statusCode(200)
                .body("name", equalTo("UPDATED-STORE"))
                .body("quantityProductsInStock", equalTo(30));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "PATCHED-STORE",
                          "quantityProductsInStock": 40
                        }
                        """)
                .when()
                .patch("/store/" + storeId)
                .then()
                .statusCode(200)
                .body("name", equalTo("PATCHED-STORE"))
                .body("quantityProductsInStock", equalTo(40));

        given()
                .when()
                .delete("/store/" + storeId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/store/" + storeId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateStoreWithIdReturnsUnprocessableEntity() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "id": 100,
                          "name": "INVALID-STORE",
                          "quantityProductsInStock": 10
                        }
                        """)
                .when()
                .post("/store")
                .then()
                .statusCode(422)
                .body("code", equalTo(422))
                .body("error", containsString("Id was invalidly set"));
    }

    @Test
    public void testUpdateStoreWithoutNameReturnsUnprocessableEntity() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "quantityProductsInStock": 10
                        }
                        """)
                .when()
                .put("/store/1")
                .then()
                .statusCode(422)
                .body("code", equalTo(422))
                .body("error", containsString("Store Name was not set"));
    }

    @Test
    public void testPatchStoreWithoutNameReturnsUnprocessableEntity() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "quantityProductsInStock": 10
                        }
                        """)
                .when()
                .patch("/store/1")
                .then()
                .statusCode(422)
                .body("code", equalTo(422))
                .body("error", containsString("Store Name was not set"));
    }

    @Test
    public void testUpdateStoreNotFound() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "MISSING-STORE",
                          "quantityProductsInStock": 10
                        }
                        """)
                .when()
                .put("/store/9999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    public void testPatchStoreNotFound() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "MISSING-STORE",
                          "quantityProductsInStock": 10
                        }
                        """)
                .when()
                .patch("/store/9999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    public void testDeleteStoreNotFound() {
        given()
                .when()
                .delete("/store/9999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }
}