package fi.tuni.prog3.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class for extracting data from the OpenWeatherMap API.
 */
public class ImplementAPI implements iAPI {

    /**
     * Name of the current location.
     */
    String currentLocation = "Tampere";

    /**
     * Default constructor for ImplementAPI.
     * Initializes the class with default settings.
     */
    public ImplementAPI() {
        // no implementation
    }

    /**
     * Looks up the coordinates for the given location.
     * @param location Location to look up.
     * @throws LocationNotFoundException If the location is not found.
     * @return Coordinates for the location.
     */
    @Override
    public Coordinates lookUpLocation(String location) throws LocationNotFoundException {
        try {
            String geocodingUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + location + "&limit=1&appid=" + API_KEY;
            String geocodingResponse = makeApiCall(geocodingUrl);
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode geocodingNode = objectMapper.readTree(geocodingResponse);

            if (geocodingNode.isArray() && geocodingNode.size() > 0) {
                JsonNode cityNode = geocodingNode.get(0);
                double lat = cityNode.path("lat").asDouble();
                double lon = cityNode.path("lon").asDouble();
                currentLocation = cityNode.path("name").asText();

                return new Coordinates(lat, lon);
            } else {
                throw new LocationNotFoundException("No coordinates found for the city: " + location);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new LocationNotFoundException("Error retrieving city coordinates: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a list of 7 daily forecasts for the given coordinates.
     * @param coordinates Coordinates to get the forecast for.
     * @throws ForecastNotFoundException If the forecast is not found.
     * @return List of daily forecasts. 
     */
    public List<DailyForecast> getForecast(Coordinates coordinates) throws ForecastNotFoundException {
        try {
            String apiUrl = "http://api.openweathermap.org/data/2.5/onecall?lat=" +
                    coordinates.getLatitude() + "&lon=" + coordinates.getLongitude() + "&exclude=current,minutely,hourly,alerts&appid=" + API_KEY;
            String forecastResponse = makeApiCall(apiUrl);

            // Parse the forecastResponse
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(forecastResponse);

            // Extract daily forecast data
            List<DailyForecast> dailyForecasts = new ArrayList<>();
            JsonNode dailyNode = jsonNode.path("daily");
            for (int i = 1; i <= 7; i++) {
                long timestamp = Instant.now().getEpochSecond() + i * 86400;
                String date = getDayName(timestamp);

                String icon = dailyNode.get(i).get("weather").get(0).get("icon").asText();
                String weather = dailyNode.get(i).get("weather").get(0).get("main").asText();
                String description = dailyNode.get(i).get("weather").get(0).get("description").asText();
                

                double minTemp = dailyNode.get(i).get("temp").get("min").asDouble();
                double maxTemp = dailyNode.get(i).get("temp").get("max").asDouble();

                int minTempInCelsius = (int) (Math.round(minTemp - 273.15));
                int maxTempInCelsius = (int) (Math.round(maxTemp - 273.15));

                DailyForecast dailyForecast = new DailyForecast(date, icon, weather, description, minTempInCelsius, maxTempInCelsius);
                dailyForecasts.add(dailyForecast);
            }

            return dailyForecasts;
        } catch (IOException e) {
            throw new ForecastNotFoundException("Error retrieving forecast data: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a list of 6 hourly forecasts for the given coordinates.
     * @param coordinates Coordinates to get the forecast for.
     * @throws ForecastNotFoundException If the forecast is not found.
     * @return List of hourly forecasts.
     */
    public List<HourlyForecast> getHourlyForecast(Coordinates coordinates) throws ForecastNotFoundException {
        try {
            String apiUrl = "http://api.openweathermap.org/data/2.5/onecall?lat=" +
                    coordinates.getLatitude() + "&lon=" + coordinates.getLongitude() + "&exclude=current,minutely,daily,alerts&appid=" + API_KEY;
            String forecastResponse = makeApiCall(apiUrl);

            // Parse the forecastResponse
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(forecastResponse);

            String timezone = jsonNode.get("timezone").asText();

            // Extract hourly forecast data
            List<HourlyForecast> hourlyForecasts = new ArrayList<>();
            JsonNode hourlyNode = jsonNode.path("hourly");
            for (int i = 0; i < 6; i++) {
                long timestamp = hourlyNode.get(i).get("dt").asLong();
                String time = getHour(timestamp, timezone);

                String icon = hourlyNode.get(i).get("weather").get(0).get("icon").asText();
                String weather = hourlyNode.get(i).get("weather").get(0).get("main").asText();
                String description = hourlyNode.get(i).get("weather").get(0).get("description").asText();    
                double pop = hourlyNode.get(i).get("pop").asDouble();
                double temp = hourlyNode.get(i).get("temp").asDouble();
                double tempInCelsius = Math.round((temp - 273.15) * 10.0) / 10.0;

                HourlyForecast hourlyForecast = new HourlyForecast(time, icon, weather, description, tempInCelsius, pop);

                hourlyForecasts.add(hourlyForecast);
            }

            return hourlyForecasts;
        } catch (IOException e) {
            throw new ForecastNotFoundException("Error retrieving forecast data: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a CurrentCityWeather object for the given coordinates.
     * @param coordinates Coordinates to get the weather for.
     * @throws WeatherDataNotFoundException If the weather data is not found.
     * @return CurrentCityWeather object.
     */
    public CurrentCityWeather weatherApiCall(Coordinates coordinates) throws WeatherDataNotFoundException {
        try {
            // Make the weather API call using the coordinates 
            // - Jennu: lisäsin &units=metric niin ei tarvii vaihtaa eriksee
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" +
                    coordinates.getLatitude() + "&lon=" + coordinates.getLongitude() + "&units=metric" + "&appid=" + API_KEY;
            String weatherResponse = makeApiCall(apiUrl);
    
            // Parse the weather API response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(weatherResponse);
    
            // Extract specific fields from the response        
            String name = currentLocation;
            String icon = jsonNode.get("weather").get(0).get("icon").asText();
            String weather = jsonNode.get("weather").get(0).get("main").asText();
            String description = jsonNode.get("weather").get(0).get("description").asText();;
            double temp = jsonNode.get("main").get("temp").asDouble();
            double feelsLike = jsonNode.get("main").get("feels_like").asDouble();
            double wind = jsonNode.get("wind").get("speed").asDouble();
            int humidity = jsonNode.get("main").get("humidity").asInt();
    
            // Capitalize the first letter of the description
            description = description.substring(0, 1).toUpperCase() + description.substring(1);

            CurrentCityWeather currentCityWeather = new CurrentCityWeather(coordinates, name, weather, description, temp, icon, feelsLike, wind, humidity);

            return currentCityWeather;

        } catch (IOException e) {
            throw new WeatherDataNotFoundException("Error retrieving weather data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Makes an API call to the given URL and returns the response as a String.
     * @param apiUrl URL to make the API call to.
     * @throws IOException If there's an error in the API call.
     * @return Response from the API call as a String.
     */
    private String makeApiCall(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } else {
            throw new IOException("Error in API request. Response Code: " + responseCode);
        }
    }

    /**
     * Helper method to convert timestamp to day name.
     * @param timestamp timestamp to convert.
     * @return converted day name.
     */
    public static String getDayName(long timestamp) {
        Date date = new Date(timestamp * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(date);
    }

    /**  
     * Helper method to convert timestamp to hour.
     * @param timestamp timestamp to convert.
     * @param timezone timezone to convert to.
     * @return converted hour.
     */
    private String getHour(long timestamp, String timezone) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of(timezone));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * Exception for when the location is not found.
     */
    public class LocationNotFoundException extends Exception {
        /**
         * Constructs a new LocationNotFoundException with the specified detail message.
         *
         * @param message the detail message.
         */
        public LocationNotFoundException(String message) {
            super(message);
        }
        
        /**
         * Constructs a new LocationNotFoundException with the specified detail message and cause.
         *
         * @param message the detail message.
         * @param cause   the cause (which is saved for later retrieval by the getCause() method).
         */
        public LocationNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception for when the forecast is not found.
     */
    public class ForecastNotFoundException extends Exception {
        /**
         * Constructs a new ForecastNotFoundException with the specified detail message.
         *
         * @param message the detail message.
         */
        public ForecastNotFoundException(String message) {
            super(message);
        }

        /**
         * Constructs a new ForecastNotFoundException with the specified detail message and cause.
         *
         * @param message the detail message.
         * @param cause   the cause (which is saved for later retrieval by the getCause() method).
         */
        public ForecastNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception for when the weather data is not found.
     */
    public class WeatherDataNotFoundException extends Exception {
        /**
         * Constructs a new WeatherDataNotFoundException with the specified detail message.
         *
         * @param message the detail message.
         */
        public WeatherDataNotFoundException(String message) {
            super(message);
        }
        
        /**
         * Constructs a new WeatherDataNotFoundException with the specified detail message and cause.
         *
         * @param message the detail message.
         * @param cause   the cause (which is saved for later retrieval by the getCause() method).
         */
        public WeatherDataNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}