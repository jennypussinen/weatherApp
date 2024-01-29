package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataStorageTest {

    private WeatherDataStorage weatherDataStorage;

    @BeforeEach
    void setUp() {
        weatherDataStorage = Mockito.spy(new WeatherDataStorage());
    }

    @Test
    void testAddFavoriteCity() {
        weatherDataStorage.addFavoriteCity("City3");

        assertTrue(weatherDataStorage.isFavoriteCity("City3"));
    }

    @Test
    void testRemoveFavoriteCity() {
        weatherDataStorage.removeFavoriteCity("City1");

        assertFalse(weatherDataStorage.isFavoriteCity("City1"));
    }

    @Test
    void testSetCurrentCity() {
        weatherDataStorage.setCurrentCity("City1");

        assertEquals("City1", weatherDataStorage.getCurrentCity());
    }

    @Test
    void testAddSearchToHistory() {
        weatherDataStorage.addSearchToHistory("Query3");

        assertTrue(weatherDataStorage.getSearchHistory().contains("Query3"));

        weatherDataStorage.clearSearchHistory();
    }

    @Test
    void testClearSearchHistory() {
        weatherDataStorage.clearSearchHistory();

        assertTrue(weatherDataStorage.getSearchHistory().isEmpty());
    }
}