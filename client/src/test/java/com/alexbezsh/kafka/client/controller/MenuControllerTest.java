package com.alexbezsh.kafka.client.controller;

import com.alexbezsh.kafka.client.model.api.response.ErrorResponse;
import com.alexbezsh.kafka.client.model.api.response.MenuResponse;
import org.junit.jupiter.api.Test;
import static com.alexbezsh.kafka.client.TestUtils.UNEXPECTED_ERROR;
import static com.alexbezsh.kafka.client.TestUtils.menuResponse;
import static com.alexbezsh.kafka.common.utils.JsonUtils.toJson;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuControllerTest extends AbstractControllerTest {

    public static final String MENU_URL = "/api/v1/menu";

    @Test
    void getMenu() throws Exception {
        MenuResponse expected = menuResponse();

        doReturn(expected).when(menuService).getMenu();

        mockMvc.perform(get(MENU_URL))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getMenuShouldReturn500() throws Exception {
        ErrorResponse expected = new ErrorResponse(INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR);

        doThrow(RuntimeException.class).when(menuService).getMenu();

        mockMvc.perform(get(MENU_URL))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

}
