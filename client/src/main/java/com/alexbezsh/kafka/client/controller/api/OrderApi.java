package com.alexbezsh.kafka.client.controller.api;

import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.swagger.Default400And404And500Responses;
import com.alexbezsh.kafka.client.swagger.Default404And500Responses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Tag(name = "Orders")
@RequestMapping("/api/v1/orders")
public interface OrderApi {

    @Default400And404And500Responses
    @Operation(summary = "Place New Order")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    OrderDto placeOrder(@RequestBody @Valid OrderRequest request);

    @Default404And500Responses
    @Operation(summary = "Get Order by ID")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    OrderDto getOrder(@PathVariable("id") Long id);

}
