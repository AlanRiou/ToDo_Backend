package com.itesm.interfaces.rest;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "We could not find this item."))
                    .build();
        }
        if (exception instanceof ConstraintViolationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Review the information you entered."))
                    .build();
        }
        return Response.serverError()
                .entity(Map.of("message", "We could not complete the action. Try again."))
                .build();
    }
}
