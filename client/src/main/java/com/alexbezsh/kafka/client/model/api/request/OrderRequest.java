package com.alexbezsh.kafka.client.model.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotBlank
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\d{10,15}$")
    private String phoneNumber;

    @Valid
    @NotNull
    @NotEmpty
    private List<OrderRequestItem> items;

}
