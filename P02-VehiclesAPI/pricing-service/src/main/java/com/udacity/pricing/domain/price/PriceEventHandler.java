package com.udacity.pricing.domain.price;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@RepositoryEventHandler
public class PriceEventHandler {

    @HandleBeforeCreate
    public void handlePriceBeforeCreate(Price entity) {
        if (entity.getVehicleId() != null) {
            entity.setCurrency("USD");
            entity.setPrice(this.randomPrice());
        }
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     *
     * @return random price for a vehicle
     */
    private static BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
            .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }
}
