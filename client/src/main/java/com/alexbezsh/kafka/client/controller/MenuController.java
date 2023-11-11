package com.alexbezsh.kafka.client.controller;

import com.alexbezsh.kafka.client.controller.api.MenuApi;
import com.alexbezsh.kafka.client.model.api.response.MenuResponse;
import com.alexbezsh.kafka.client.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController implements MenuApi {

    private final MenuService menuService;

    @Override
    public MenuResponse getMenu() {
        return menuService.getMenu();
    }

}
