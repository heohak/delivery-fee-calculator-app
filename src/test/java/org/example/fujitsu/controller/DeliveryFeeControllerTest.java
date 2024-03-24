package org.example.fujitsu.controller;

import org.example.fujitsu.service.DeliveryFeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DeliveryFeeController.class)
class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryFeeService deliveryFeeService;

    @Test
    void getDeliveryFee_ReturnsFeeSuccessfully() throws Exception {
        given(deliveryFeeService.calculateDeliveryFee(anyString(), anyString())).willReturn(4.0);

        mockMvc.perform(get("/api/delivery-fee/Tallinn/Car")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }

    @Test
    void getDeliveryFee_ReturnsErrorForInvalidInput() throws Exception {
        given(deliveryFeeService.calculateDeliveryFee(anyString(), anyString())).willThrow(new RuntimeException("An unexpected error occurred"));

        mockMvc.perform(get("/api/delivery-fee/InvalidCity/Car")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred"));
    }
}
