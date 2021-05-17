package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Implements testing of the CarController class.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarRepository carRepository;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @BeforeEach
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carRepository.save(any())).willReturn(car);
        given(carRepository.findById(any())).willReturn(Optional.of(car));
        given(carRepository.findAll()).willReturn(Collections.singletonList(car));

        Price price = new Price();
        given(priceClient.getPrice(any())).willReturn("1000.20 USD");
        given(priceClient.createPrice(any())).willReturn(price);

        Location location = new Location(car.getLocation().getLat(), car.getLocation().getLon());
        location.setAddress("2575 Us Hwy 43");
        location.setCity("Winfield");
        location.setState("AL");
        location.setZip("35594");
        given(mapsClient.getAddress(any())).willReturn(location);
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
            post(new URI("/cars"))
                .content(json.write(car).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
            .andExpect(status().isCreated());
    }

    /**
     * Tests for successful change of car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void changeCar() throws Exception {
        Car car = getCar();
        car.setCondition(Condition.NEW);
        mvc.perform(
            put(new URI("/cars/1"))
                .content(json.write(car).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.condition").value(car.getCondition().toString()));
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        Car car = getCar();
        mvc.perform(get(new URI("/cars")))
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.carList").isArray())
            .andExpect(jsonPath("$._embedded.carList[?(@.id == 1)]").exists())
            .andExpect(jsonPath("$._embedded.carList[0].details.model").value(car.getDetails().getModel()))
            .andExpect(jsonPath("$._embedded.carList[0].details.mileage").value(car.getDetails().getMileage()))
            .andExpect(jsonPath("$._embedded.carList[0].details.externalColor").value(car.getDetails().getExternalColor()));
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        Car car = getCar();
        mvc.perform(get(new URI("/cars/1")))
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.details.model").value(car.getDetails().getModel()))
            .andExpect(jsonPath("$.details.mileage").value(car.getDetails().getMileage()))
            .andExpect(jsonPath("$.details.externalColor").value(car.getDetails().getExternalColor()))
            .andExpect(jsonPath("$.price").isString())
            .andExpect(jsonPath("$.location.address").isString())
            .andExpect(jsonPath("$.location.city").isString())
            .andExpect(jsonPath("$.location.state").isString())
            .andExpect(jsonPath("$.location.zip").isString());
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        mvc.perform(delete(new URI("/cars/1")))
            .andExpect(status().isNoContent());
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}