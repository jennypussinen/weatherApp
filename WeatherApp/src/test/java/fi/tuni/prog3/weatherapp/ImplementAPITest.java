package fi.tuni.prog3.weatherapp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Test class for ImplementAPI.
 */
class ImplementAPITest {

    private ImplementAPI api = new ImplementAPI();

    @Test
    void testLookUpLocation() throws ImplementAPI.LocationNotFoundException, IOException {
        ImplementAPI.Coordinates result = api.lookUpLocation("Tampere");

        assertEquals(new ImplementAPI.Coordinates(61.4980214, 23.7603118), result);
        assertEquals("Tampere", api.currentLocation);
        assertThrows(ImplementAPI.LocationNotFoundException.class, () -> api.lookUpLocation("asdfasdfasfdasdfasdfasdf"));
    }

    @Test
    void testGetForecast() throws ImplementAPI.ForecastNotFoundException, IOException {
        List<ImplementAPI.DailyForecast> result = api.getForecast(new ImplementAPI.Coordinates(61.4981, 23.7619));

        assertEquals(7, result.size());
        assertEquals(WeekdayExample(), result.get(0).getDate());

        assertThrows(ImplementAPI.ForecastNotFoundException.class, () -> api.getForecast(new ImplementAPI.Coordinates(-200, 200)));
    }

    @Test
    void testGetHourlyForecast() throws ImplementAPI.ForecastNotFoundException, IOException {
        List<ImplementAPI.HourlyForecast> result = api.getHourlyForecast(new ImplementAPI.Coordinates(61.4981, 23.7619));

        assertEquals(6, result.size());
        assertEquals(HourExample(), result.get(0).getTime());

        assertThrows(ImplementAPI.ForecastNotFoundException.class, () -> api.getHourlyForecast(new ImplementAPI.Coordinates(-200, 200)));
    }

    @Test
    void testWeatherApiCall() throws ImplementAPI.WeatherDataNotFoundException, IOException {
        ImplementAPI.CurrentCityWeather result = api.weatherApiCall(new ImplementAPI.Coordinates(61.4980214, 23.760311));

        assertNotNull(result);
        assertEquals("Tampere", result.getName());
        
        assertThrows(ImplementAPI.WeatherDataNotFoundException.class, () -> api.weatherApiCall(new ImplementAPI.Coordinates(-200, 200)));
    }




   public String WeekdayExample() {
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", java.util.Locale.ENGLISH);
        String weekdayName = capitalize(currentDate.format(formatter));

        return weekdayName;
    }

     public String HourExample() {
        int currentHour = java.time.LocalTime.now().getHour();

        String hourName = currentHour + ":00";

        return hourName;
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}