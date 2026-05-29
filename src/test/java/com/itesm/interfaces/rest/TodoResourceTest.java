package com.itesm.interfaces.rest;

import com.itesm.application.dto.CreateTodoDto;
import com.itesm.application.usecase.CreateTodoUseCase;
import com.itesm.domain.models.Todo;
import com.itesm.domain.repository.TodoRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class TodoResourceTest {

    @Inject
    CreateTodoUseCase createTodoUseCase;
    @Inject
    TodoRepository  todoRepository;

    @Test
    void createTodo_shouldReturn200WithTodo() {
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer mock-token")
            .body("{\"title\":\"Mi tarea\",\"description\":\"Descripcion\"}")
        .when()
            .post("/todo")
        .then()
            .statusCode(200)
            .body("title", equalTo("Mi tarea"))
            .body("description", equalTo("Descripcion"))
            .body("completed", equalTo(false));
    }
    /*
    @Test
    void createTodo_shouldReturn401WithoutToken() {
        given()
            .contentType("application/json")
            .body("{\"title\":\"Mi tarea\",\"description\":\"Descripcion\"}")
        .when()
            .post("/todo")
        .then()
            .statusCode(401);
    }

    @Test
    void getTodoTest_shouldReturn200WithToken() {
        given()
            .header("Authorization", "Bearer mock-token")
        .when()
            .get("/todo/test")
        .then()
            .statusCode(200)
            .body(equalTo("TEST"));
    }

    @Test
    void getTodoTest_shouldReturn401WithoutToken() {
        given()
        .when()
            .get("/todo/test")
        .then()
            .statusCode(401);
    }

     */
}
