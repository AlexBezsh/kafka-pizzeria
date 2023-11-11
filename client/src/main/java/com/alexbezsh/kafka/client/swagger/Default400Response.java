package com.alexbezsh.kafka.client.swagger;

import com.alexbezsh.kafka.client.model.api.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@ApiResponse(
    responseCode = "400",
    description = "Validation failed",
    content = @Content(
        mediaType = APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorResponse.class)))
public @interface Default400Response {
}
