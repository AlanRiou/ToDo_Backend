package com.itesm.interfaces.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class TaskListResourceTest {
    @Test
    void taskListAndTaskCrud_shouldWorkForAuthenticatedUser() {
        String taskListId = given()
                .contentType("application/json")
                .header("Authorization", "Bearer mock-token")
                .body("{\"title\":\"Computer Science\",\"description\":\"React Native Fundamentals\"}")
        .when()
                .post("/task-lists")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Computer Science"))
                .extract()
                .path("id");

        String taskId = given()
                .contentType("application/json")
                .header("Authorization", "Bearer mock-token")
                .body("{\"title\":\"Setup Navigation Stack\",\"description\":\"Configure Expo Router\",\"priority\":\"high\"}")
        .when()
                .post("/task-lists/" + taskListId + "/tasks")
        .then()
                .statusCode(201)
                .body("title", equalTo("Setup Navigation Stack"))
                .body("completed", equalTo(false))
                .extract()
                .path("uuid");

        given()
                .header("Authorization", "Bearer mock-token")
        .when()
                .get("/search?q=Navigation")
        .then()
                .statusCode(200)
                .body("tasks.size()", greaterThanOrEqualTo(1));

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer mock-token")
                .body("{\"title\":\"Setup Navigation Stack\",\"description\":\"Done\",\"priority\":\"high\",\"completed\":true}")
        .when()
                .put("/tasks/" + taskId)
        .then()
                .statusCode(200)
                .body("completed", equalTo(true));

        given()
                .header("Authorization", "Bearer mock-token")
        .when()
                .delete("/task-lists/" + taskListId)
        .then()
                .statusCode(204);
    }

    @Test
    void protectedEndpoint_shouldReturn401WithoutToken() {
        given()
        .when()
                .get("/task-lists")
        .then()
                .statusCode(401);
    }
}
