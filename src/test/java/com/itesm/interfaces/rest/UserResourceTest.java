package com.itesm.interfaces.rest;

import com.itesm.application.dto.RegisterUserDto;
import com.itesm.application.usecase.RegisterUserUseCase;
import com.itesm.domain.models.User;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class UserResourceTest {

    @Inject
    RegisterUserUseCase registerUserUseCase;

    @Test
    void registerUser_shouldReturn200WithUser() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User mockUser = new User(userId, null, "Juan Perez", "juan@test.com", true, "fb-123", "USER");

        given()
            .contentType("application/json")
            .body("{\"email\":\"juan@test.com\",\"password\":\"123456\",\"fullName\":\"Juan Perez\"}")
        .when()
            .post("/user")
        .then()
            .statusCode(200)
            .body("fullName", equalTo("Juan Perez"))
            .body("email", equalTo("juan@test.com"))
            .body("active", equalTo(true));
    }

}
