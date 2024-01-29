package fi.tuni.prog3.weatherapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing weather data.
 */
public class WeatherDataStorage implements iReadAndWriteToFile {
    private static final String FILE_SEPARATOR = File.separator;
    private static final String DATA_DIRECTORY = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "resources" + FILE_SEPARATOR + "weatherData" + FILE_SEPARATOR;

    private static final String FAVORITE_CITIES_FILE = DATA_DIRECTORY + "favorite_cities.json";
    private static final String CURRENT_CITY_FILE = DATA_DIRECTORY + "current_city.json";
    private static final String SEARCH_HISTORY_FILE = DATA_DIRECTORY + "search_history.json";

    private List<String> favoriteCities = new ArrayList<>();
    private String currentCity;
    private List<String> searchHistory = new ArrayList<>();

    /**
     * Constructor.
     * Initializes the WeatherDataStorage object by reading data from files.
     */
    public WeatherDataStorage() {
        try {
            String favoritesJson = readFromFile(FAVORITE_CITIES_FILE);
            if (favoritesJson != null) {
                ObjectMapper favoritesMapper = new ObjectMapper();
                favoriteCities = favoritesMapper.readValue(favoritesJson, new TypeReference<List<String>>() {});
            }

            currentCity = readFromFile(CURRENT_CITY_FILE);

            String historyJson = readFromFile(SEARCH_HISTORY_FILE);
            if (historyJson != null) {
                ObjectMapper historyMapper = new ObjectMapper();
                searchHistory = historyMapper.readValue(historyJson, new TypeReference<List<String>>() {});
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading or parsing data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Returns the list of favorite cities.
     * @return List of favorite cities.
     */
    public List<String> getFavoriteCities() {
        return favoriteCities;
    }

    /**
     * Returns true if the city is in favorites, otherwise false.
     * @param city The city to check.
     * @return true if the city is a favorite, false otherwise.
     */
    public boolean isFavoriteCity(String city) {
        if (favoriteCities.contains(city)) {
            return true;
        } 
        return false;
    }

    /**
     * Returns current city.
     * @return String, current city.
     */
    public String getCurrentCity() {
        return currentCity;
    }

    /**
     * Returns the search history.
     * @return Search history.
     */
    public List<String> getSearchHistory() {
        return searchHistory;
    }

    /**
     * Adds the given city to the list of favorite cities.
     * @param city City to add.
     */
    public void addFavoriteCity(String city) {
        if (!favoriteCities.contains(city)) {
            favoriteCities.add(city);
            saveDataToFile(FAVORITE_CITIES_FILE, favoriteCities);
        }
    }



    /**
     * Removes the given city from the list of favorite cities.
     * @param city City to remove.
     */
    public void removeFavoriteCity(String city) {
        try {
            if (favoriteCities.contains(city)) {
                favoriteCities.remove(city);
                saveDataToFile(FAVORITE_CITIES_FILE, favoriteCities);
            }
        } catch (Exception e) {
            System.err.println("Error removing city from favorites: " + e.getMessage());
        }
    }

    /**
     * Sets the current city.
     * @param city City to set as the current city.
     */
    public void setCurrentCity(String city) {
        try {
            currentCity = city;
            saveDataToFile(CURRENT_CITY_FILE, currentCity);
        } catch (Exception e) {
            System.err.println("Error setting current city: " + e.getMessage());
        }
    }

    /**
     * Adds the given search query to the search history.
     * @param searchQuery Search query to add.
     */
    public void addSearchToHistory(String searchQuery) {
        // Check if the search query is already in the history
        if (searchHistory.contains(searchQuery)) {
            return; 
        }

        if (searchHistory.size() >= 10) {
            // Remove the oldest entry if the history has reached its maximum size
            searchHistory.remove(9);
        }

        searchHistory.add(0, searchQuery);

        saveDataToFile(SEARCH_HISTORY_FILE, searchHistory);
    }

    /**
     * Clears the search history.
     */
    public void clearSearchHistory() {
        searchHistory.clear();
        saveDataToFile(SEARCH_HISTORY_FILE, searchHistory);
    }

    /**
     * Saves the given data to the given file.
     * @param fileName Name of the file to save the data to.
     * @param data Data to save.
     */
    private void saveDataToFile(String fileName, Object data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            File file = new File(fileName);
    
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
    
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    /**
     * Reads JSON from the given file.
     * @param fileName name of the file to read from.
     * @throws IOException if the method e.g, cannot find the file. 
     * @throws FileNotFoundException if the method e.g., cannot write to a file.
     * @return true if the read was successful, otherwise false.
     */
    @Override
    public String readFromFile(String fileName) throws IOException {
        try {
            File file = new File(fileName);
    
            if (file.exists()) {
                ObjectMapper objectMapper = new ObjectMapper();
                TypeReference<?> typeReference = new TypeReference<>() {};
                Object data = objectMapper.readValue(file, typeReference);
    
                if (data instanceof String) {
                    return (String) data;
                } else if (data instanceof List) {
                    return objectMapper.writeValueAsString(data);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading data from file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error reading data from file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Writes the data to the given file.
     * @param fileName Name of the file to write to.
     * @return true if the write was successful, otherwise false.
     */
    @Override
    public boolean writeToFile(String fileName) {
        try {
            String filePath = DATA_DIRECTORY + fileName;
            saveDataToFile(filePath, getDataForFileName(fileName));
            return true;
        } catch (Exception e) {
            System.err.println("Unexpected error writing data to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the data for the given file name.
     * @param fileName Name of the file to get the data for.
     * @throws UnsupportedOperationException if the given file name is not supported.
     * @return Data for the given file name.
     */
    private Object getDataForFileName(String fileName) {
        if (FAVORITE_CITIES_FILE.equals(fileName)) {
            return favoriteCities;
        } else if (CURRENT_CITY_FILE.equals(fileName)) {
            return currentCity;
        } else if (SEARCH_HISTORY_FILE.equals(fileName)) {
            return searchHistory;
        } else {
            throw new UnsupportedOperationException("Unsupported file name: " + fileName);
        }
    }
}

