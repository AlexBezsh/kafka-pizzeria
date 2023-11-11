package com.alexbezsh.kafka.client.controller.api;

import com.alexbezsh.kafka.client.model.api.response.MenuResponse;
import com.alexbezsh.kafka.client.swagger.Default500Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Tag(name = "Menu")
@RequestMapping("/api/v1/menu")
public interface MenuApi {

    @Default500Response
    @Operation(summary = "Get Menu")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    MenuResponse getMenu();

}
