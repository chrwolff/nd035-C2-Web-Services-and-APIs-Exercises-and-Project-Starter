package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

    final private Long VALUE_ID = 1L;
    final private String VALUE_CURRENCY = "USD";
    final private BigDecimal VALUE_PRICE = new BigDecimal(10000.20d);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PriceRepository repository;

    @AfterEach
    public void afterEach() {
        this.repository.deleteAll();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void readPrice() throws Exception {
        this.repository.save(new Price(VALUE_CURRENCY, VALUE_PRICE, VALUE_ID));

        this.mockMvc.perform(get("/services/price/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("$.currency").value(VALUE_CURRENCY))
            .andExpect(jsonPath("$.price").value(VALUE_PRICE.doubleValue()));
    }

    @Test
    public void createPrice() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vehicleId", VALUE_ID);
        jsonObject.put("currency", VALUE_CURRENCY);
        jsonObject.put("price", VALUE_PRICE);

        this.mockMvc.perform(post("/services/price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString()))
            .andExpect(status().is2xxSuccessful());

        Optional<Price> entityOptional = this.repository.findById(VALUE_ID);
        assertTrue(entityOptional.isPresent());

        Price entity = entityOptional.get();
        assertEquals(VALUE_CURRENCY, entity.getCurrency());
        assertEquals(VALUE_PRICE.doubleValue(), entity.getPrice().doubleValue());
    }

    @Test
    public void deletePrice() throws Exception {
        this.repository.save(new Price(VALUE_CURRENCY, VALUE_PRICE, VALUE_ID));

        this.mockMvc.perform(delete("/services/price/1"))
            .andExpect(status().is2xxSuccessful());

        assertEquals(0, this.repository.count());
    }

    @Test
    public void updatePrice() throws Exception {
        this.repository.save(new Price("EUR", new BigDecimal(20000.40d), VALUE_ID));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currency", VALUE_CURRENCY);
        jsonObject.put("price", VALUE_PRICE);

        this.mockMvc.perform(put("/services/price/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject.toString()))
            .andExpect(status().isNoContent());

        Optional<Price> entityOptional = this.repository.findById(VALUE_ID);
        assertTrue(entityOptional.isPresent());

        Price entity = entityOptional.get();
        assertEquals(VALUE_CURRENCY, entity.getCurrency());
        assertEquals(VALUE_PRICE.doubleValue(), entity.getPrice().doubleValue());
    }
}
