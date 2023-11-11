package com.alexbezsh.kafka.client.controller;

import com.alexbezsh.kafka.client.exception.OrderNotFoundException;
import com.alexbezsh.kafka.client.exception.PizzaNotFoundException;
import com.alexbezsh.kafka.client.model.api.dto.OrderDto;
import com.alexbezsh.kafka.client.model.api.request.OrderRequest;
import com.alexbezsh.kafka.client.model.api.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static com.alexbezsh.kafka.client.TestUtils.UNEXPECTED_ERROR;
import static com.alexbezsh.kafka.client.TestUtils.orderDto;
import static com.alexbezsh.kafka.client.TestUtils.orderRequest;
import static com.alexbezsh.kafka.common.CommonTestUtils.ORDER_ID;
import static com.alexbezsh.kafka.common.CommonTestUtils.PIZZA_1_ID;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends AbstractControllerTest {

    public static final String ORDERS_URL = "/api/v1/orders";
    public static final String ORDER_URL = ORDERS_URL + "/" + ORDER_ID;

    @Test
    void placeOrder() throws Exception {
        OrderRequest request = orderRequest();
        OrderDto expected = orderDto();

        doReturn(expected).when(orderService).placeOrder(request);

        mockMvc.perform(post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void placeOrderShouldReturn400() throws Exception {
        OrderRequest request = orderRequest();
        request.getItems().get(0).setId(null);
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "items[0].id: must not be null");

        mockMvc.perform(post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void placeOrderShouldReturn404() throws Exception {
        OrderRequest request = orderRequest();
        ErrorResponse expected = new ErrorResponse(NOT_FOUND, "Pizza " + PIZZA_1_ID + " not found");

        doThrow(new PizzaNotFoundException(PIZZA_1_ID)).when(orderService).placeOrder(request);

        mockMvc.perform(post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect(status().isNotFound())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void placeOrderShouldReturn500() throws Exception {
        OrderRequest request = orderRequest();
        ErrorResponse expected = new ErrorResponse(INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR);

        doThrow(RuntimeException.class).when(orderService).placeOrder(request);

        mockMvc.perform(post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getOrder() throws Exception {
        OrderDto expected = orderDto();

        doReturn(expected).when(orderService).getOrder(ORDER_ID);

        mockMvc.perform(get(ORDER_URL))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getOrderShouldReturn400() throws Exception {
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "Failed to convert value of type " +
            "'java.lang.String' to required type 'java.lang.Long'; For input string: \"some\"");

        mockMvc.perform(get(ORDERS_URL + "/some"))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getOrderShouldReturn404() throws Exception {
        ErrorResponse expected = new ErrorResponse(NOT_FOUND, "Order " + ORDER_ID + " not found");

        doThrow(new OrderNotFoundException(ORDER_ID)).when(orderService).getOrder(ORDER_ID);

        mockMvc.perform(get(ORDER_URL))
            .andExpect(status().isNotFound())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getOrderShouldReturn500() throws Exception {
        ErrorResponse expected = new ErrorResponse(INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR);

        doThrow(RuntimeException.class).when(orderService).getOrder(ORDER_ID);

        mockMvc.perform(get(ORDER_URL))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

}
