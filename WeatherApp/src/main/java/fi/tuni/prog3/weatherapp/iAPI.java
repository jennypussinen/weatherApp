/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

import java.util.List;
import java.util.Objects;


import fi.tuni.prog3.weatherapp.ImplementAPI.ForecastNotFoundException;
import fi.tuni.prog3.weatherapp.ImplementAPI.LocationNotFoundException;
import fi.tuni.prog3.weatherapp.ImplementAPI.WeatherDataNotFoundException;

/**
 * Interface for extracting data from the OpenWeatherMap API.
 */
public interface iAPI {
    /**
     * API key for the OpenWeatherMap API. Add API key here, if you want to test the program
     */
    String API_KEY = "";

    /**
     * Class for storing coordinates
     */
    public class Coordinates {
        private final double latitude;
        private final double longitude;

        /**
         * sets coordinates
         * @param latitude latitude
         * @param longitude longitude
         */
        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

         /**
         * return latitude
         * @return double latitude
         */
        public double getLatitude() {
            return latitude;
        }

         /**
         * return longitude
         * @return double longitude
         */
        public double getLongitude() {
            return longitude;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Coordinates that = (Coordinates) obj;

            return Double.compare(that.latitude, latitude) == 0 &&
                    Double.compare(that.longitude, longitude) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }
    }

    /**
     * Class for storing current weather data.
     */
    public class CurrentCityWeather {
        private String name;
        private Coordinates coord;
        private String weather;
        private String description;
        private double temperature;
        private String icon;
        private double feelsLike;
        private double wind;
        private int humidity;

        /**
         * Constructor.
         * @param coord Coordinates.
         * @param name Name of the location.
         * @param weather Weather.
         * @param description Description of the weather.
         * @param temperature Temperature.
         * @param icon Icon.
         * @param feelsLike Feels like temperature.
         * @param wind Wind speed.
         * @param humidity Humidity.
         */
        public CurrentCityWeather(Coordinates coord, String name, String weather, String description, double temperature, String icon, double feelsLike, double wind, int humidity) {
            this.coord = coord;
            this.name = name;
            this.weather = weather;
            this.description = description;
            this.temperature = temperature;
            this.icon = icon;
            this.feelsLike = feelsLike;
            this.wind = wind;
            this.humidity = humidity;
        }

        /**
         * Returns feels like temperature.
         * @return feels like temperature.
         */
        public double getFeelsLike() {
            return feelsLike;
        }

        /**
         * Returns wind speed.
         * @return wind speed.
         */
        public double getWind() {
            return wind;
        }

        /**
         * Returns name of the location.
         * @return name of the location.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns coordinates.
         * @return coordinates.
         */
        public Coordinates getCoordinates() {
            return coord;
        }

        /**
         * Returns weather.
         * @return weather.
         */
        public String getWeather() {
            return weather;
        }

        /**
         * Returns description of the weather.
         * @return description of the weather.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns temperature.
         * @return temperature.
         */
        public double getTemperature() {
            return temperature;
        }

        /**
         * Returns icon.
         * @return icon.
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Returns humidity.
         * @return humidity.
         */
        public int getHumidity() {
            return humidity;
        }
    }

     /**
     * Class for storing daily forecast data.
     */
    public class DailyForecast {
        private String date;
        private String weather;
        private String description;
        private int min;
        private int max;
        private String icon;

        /**
         * Constructor.
         * @param date Date.
         * @param icon Icon.
         * @param weather Weather.
         * @param description Description of the weather.
         * @param min Minimum temperature.
         * @param max Maximum temperature.
         */
        public DailyForecast(String date, String icon, String weather, String description, int min, int max) {
            this.date = date;
            this.weather = weather;
            this.description = description;
            this.min = min;
            this.max = max;
            this.icon = icon;
        }

        /**
         * Returns icon.
         * @return icon.
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Returns date.
         * @return date.
         */
        public String getDate() {
            return date;
        }

        /**
         * Returns weather.
         * @return weather.
         */
        public String getWeather() {
            return weather;
        }

        /**
         * Returns description of the weather.
         * @return description of the weather.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns minimum temperature.
         * @return minimum temperature.
         */
        public int getMinTemp() {
            return min;
        }

        /**
         * Returns maximum temperature.
         * @return maximum temperature.
         */
        public int getMaxTemp() {
            return max;
        }

        /**
         * Sets minimum temperature.
         * @param min minimum temperature.
         */
        public void setMinTemp(int min) {
            this.min = min;
        }

        /**
         * Sets maximum temperature.
         * @param max maximum temperature.
         */
        public void setMaxTemp(int max) {
            this.max = max;
        }
    }

    /**
     * Class for storing hourly forecast data.
     */
    public class HourlyForecast {
        private String time;
        private String weather;
        private String description;
        private double temperature;
        private String icon;
        private double pop;

        /**
         * Constructor.
         * @param time Time.
         * @param icon Icon.
         * @param weather Weather.
         * @param description Description of the weather.
         * @param temperature Temperature.
         * @param pop Probability of precipitation.
         */
        public HourlyForecast(String time, String icon, String weather, String description, double temperature, double pop) {
            this.time = time;
            this.weather = weather;
            this.description = description;
            this.temperature = temperature;
            this.icon = icon;
            this.pop = pop;
        }

        /**
         * Returns icon.
         * @return icon.
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Returns time.
         * @return time.
         */
        public String getTime() {
            return time;
        }

        /**
         * Returns weather.
         * @return weather.
         */
        public String getWeather() {
            return weather;
        }

        /**
         * Returns description of the weather.
         * @return description of the weather.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns temperature.
         * @return temperature.
         */
        public double getTemperature() {
            return temperature;
        }

        /**
         * Returns probability of precipitation.
         * @return probability of precipitation.
         */
        public double getPop() {
            return pop;
        }

        /**
         * Sets temperature.
         * @param temperature temperature.
         */
        public void setTemp(double temperature) {
            this.temperature = temperature;
        }
    }

    /**
     * Returns coordinates for a location.
     * @param location Name of the location for which coordinates should be fetched.
     * @return Coordinates for the location.
     * @throws LocationNotFoundException if location not found
     */
    public Coordinates lookUpLocation(String location) throws LocationNotFoundException;

    /**
     * Returns weather data of next seven days for a location.
     * @param coordinates Coordinates of the location for which forecast should be fetched.
     * @return List of DailyForecast objects.
     * @throws ForecastNotFoundException if forecast not found
     */
    public List<DailyForecast>getForecast(Coordinates coordinates) throws ForecastNotFoundException;

    /**
     * Returns weather data of next 6 hours for a location.
     * @param coordinates Coordinates of the location for which forecast should be fetched.
     * @return List of HourlyForecast objects.
     * @throws ForecastNotFoundException if forecast nto found
     */
    public List<HourlyForecast>getHourlyForecast(Coordinates coordinates) throws ForecastNotFoundException;

    /**
     * Returns current weather data for a location
     * @param coordinates Coordinates of the location for which weather should be fetched.
     * @return CurrentCityWeather object.
     * @throws WeatherDataNotFoundException if weatherdata not found
     */
    public CurrentCityWeather weatherApiCall(Coordinates coordinates) throws WeatherDataNotFoundException;
}
