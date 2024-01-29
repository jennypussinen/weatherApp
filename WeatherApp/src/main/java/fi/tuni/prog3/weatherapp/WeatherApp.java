package fi.tuni.prog3.weatherapp;

import javafx.collections.FXCollections;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.List;

import fi.tuni.prog3.weatherapp.iAPI.Coordinates;
import fi.tuni.prog3.weatherapp.iAPI.CurrentCityWeather;
import fi.tuni.prog3.weatherapp.iAPI.DailyForecast;
import fi.tuni.prog3.weatherapp.iAPI.HourlyForecast;
import fi.tuni.prog3.weatherapp.ImplementAPI.ForecastNotFoundException;
import fi.tuni.prog3.weatherapp.ImplementAPI.LocationNotFoundException;
import fi.tuni.prog3.weatherapp.ImplementAPI.WeatherDataNotFoundException;

/**
 * JavaFX Sisu
 */
public class WeatherApp extends Application {

    private static final String BACKGROUND_COLOR = "#0b121e";
    private static final String BOX_BACKGROUND_COLOR = "#202b3c";
    private static final String COMPONENT_BACKGROUND_COLOR = "#1d2736";
    private static final int COMPONENT_RADIUS = 15;
    private static final Insets COMPONENT_PADDING = new Insets(15);
    private static final int SPACING = 10;
    private static final int PREF_HEIGHT = 200;

    private final WeatherUI weatherUI = new WeatherUI();

    /**
     * Default constructor for the WeatherApp class.
     * Initializes the application with default settings.
     */
    public WeatherApp() {
        // no implementation
    }

    /**
     * Starts the application.
     * @param stage stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            weatherUI.initialize(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the application.
     * @param args args.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Class for the UI.
     */
    private class WeatherUI {
        private Label cityName, currentDescription, currentTemp, feelsLike, currentWind, rainMM, currentHumidity;
        private Label[] hourlyTimes, hourlyTemps, dayLabels, dayWeathers, dayMinMaxs;
        private final ImageView[] hourlyImages;
        private final ImageView[] dailyImages;
        private ImageView mainImg;
        private ImageView starImg;

        private TextField searchBar;
        private ListView<String> searchHistoryListView;

        private ImplementAPI api = new ImplementAPI();
        private WeatherDataStorage dataStorage = new WeatherDataStorage();

        private boolean isMetric = true;

        /**
         * Constructor for WeatherUI.
         */
        private WeatherUI() {
            // Initialize maininfo components 
            this.cityName = createLabel("CITY_NAME", 25, Color.LIGHTGRAY, true);
            this.starImg = ImageHandler.createImageView("NOT_FAVORITE", 30);
            this.cityName.setGraphic(starImg);
            this.cityName.setContentDisplay(ContentDisplay.RIGHT);
            this.currentDescription = createLabel("CURRENT_DESCRIPTION", 12, Color.GRAY);
            this.currentTemp = createLabel("CURRENT_TEMP" + "°C", 30, Color.LIGHTGRAY, true);
            this.mainImg = ImageHandler.createImageView("DEFAULT_IMAGE", 200);

            // Initialize air quality components
            this.feelsLike = createLabel("FEELS_LIKE", 20, Color.LIGHTGRAY, true);
            this.currentWind = createLabel("CURRENT_WIND", 20, Color.LIGHTGRAY, true);
            this.rainMM = createLabel("POP", 20, Color.LIGHTGRAY, true);
            this.currentHumidity = createLabel("HUMIDITY", 20, Color.LIGHTGRAY, true);

            // Initialize hourly forecast components
            this.hourlyTimes = createLabels(6, "TIME(H)", 15, Color.LIGHTGRAY, false);
            this.hourlyTemps = createLabels(6, "TEMP(H)", 15, Color.LIGHTGRAY, true);
            this.hourlyImages = new ImageView[6];
            for (int i = 0; i < 6; i++) {
                this.hourlyImages[i] = ImageHandler.createImageView("DEFAULT_IMAGE", 40);
            }

             // Initialize 7 day forecast components
             this.dayLabels = createLabels(7, "DAY_OF_THE_WEEK", 12, Color.LIGHTGRAY, false);
             this.dayWeathers = createLabels(7, "WEATHER", 12, Color.LIGHTGRAY, false);
             this.dayMinMaxs = createLabels(7, "MIN/MAX", 12, Color.LIGHTGRAY, true);
             this.dailyImages = new ImageView[7];
             for (int i = 0; i < 7; i++) {
                 this.dailyImages[i] = ImageHandler.createImageView("DEFAULT_IMAGE");
             } 

            // Search for city to update components
            String searchCity = "Tampere";
            if (dataStorage.getCurrentCity() != null) {
                searchCity = dataStorage.getCurrentCity();
            }

            // Look up the coordinates for the city
            try {
                Coordinates loc = api.lookUpLocation(searchCity);
                if (loc == null) {
                    loc = api.lookUpLocation("Tampere");
                }

                // Update components
                updateUI(loc);
            } catch (LocationNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        /**
         * Initializes the stage.
         * @param stage stage.
         */
        private void initialize(Stage stage) {
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: " + BACKGROUND_COLOR);
            root.setPadding(new Insets(10));
            root.setCenter(createCenterBox());
            root.setLeft(createLeftBox());
            root.setRight(createRightBox());

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("WeatherApp");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/38.png")));
        }

        /**
         * Updates the UI.
         * @throws WeatherDataNotFoundException if the weather data is not found.
         * @throws ForecastNotFoundException if the forecast is not found.
         * @throws ImageHandler.ImageNotFoundException if the image is not found.
         * @throws ImageHandler.ImageLoadException if the image cannot be loaded.
         * @throws Exception if an unexpected error occurs.
         * @param coord coordinates.
         */
        private void updateUI(Coordinates coord) {
            try {
                CurrentCityWeather currentCityWeather = api.weatherApiCall(coord);

                // Update maininfo
                cityName.setText(currentCityWeather.getName());
                if (!isMetric) {
                    currentTemp.setText(Math.round(celsiusToFahrenheit(currentCityWeather.getTemperature())) + "°F");
                } else {
                    currentTemp.setText(Math.round(currentCityWeather.getTemperature()) + "°C");
                }

                updateFavoriteGraphic(dataStorage.isFavoriteCity(cityName.getText()));

                currentDescription.setText(currentCityWeather.getDescription());
                updateImages(mainImg, currentCityWeather.getIcon());

                if (isMetric) {
                    feelsLike.setText(Math.round(currentCityWeather.getFeelsLike()) + "°C");
                } else {
                    feelsLike.setText(Math.round(celsiusToFahrenheit(currentCityWeather.getFeelsLike())) + "°F");
                }

                if (isMetric) {
                    currentWind.setText(Math.round(currentCityWeather.getWind() * 10.0) / 10.0  + "m/s");
                } else {
                    currentWind.setText(Math.round(metricToImperial(currentCityWeather.getWind()) * 10.0) / 10.0 + "mph");
                }

                currentHumidity.setText(currentCityWeather.getHumidity() + "%");

                // Update hourly forecast
                List<HourlyForecast> hourlyForecast = api.getHourlyForecast(coord);
                int i = 0;
                for (HourlyForecast hoursForecast : hourlyForecast) {
                    if (i == 0) {
                        rainMM.setText(hoursForecast.getPop() + "mm");
                    }
                    hourlyTimes[i].setText(hoursForecast.getTime());
                    if (!isMetric) {
                        double fahrenheit = celsiusToFahrenheit(hoursForecast.getTemperature());
                        hourlyTemps[i].setText(Math.round(fahrenheit * 10.0) / 10.0 + "°F");
                    } else {
                        hourlyTemps[i].setText(hoursForecast.getTemperature() + "°C");
                    }
                    ImageHandler.updateImage(hourlyImages[i], hoursForecast.getIcon());
                    i++;
                }

                // Update 7 day forecast
                List<DailyForecast> dailyForecasts = api.getForecast(coord);
                int j = 0;
                for (DailyForecast daysForecast : dailyForecasts) {
                    dayLabels[j].setText(daysForecast.getDate());
                    dayWeathers[j].setText(daysForecast.getWeather());
                    if (!isMetric) {
                        dayMinMaxs[j].setText(Math.round(celsiusToFahrenheit(daysForecast.getMaxTemp())) + "°F / "
                                + Math.round(celsiusToFahrenheit(daysForecast.getMinTemp())) + "°F");
                    } else {
                        dayMinMaxs[j].setText(daysForecast.getMaxTemp() + "°C / " + daysForecast.getMinTemp() + "°C");
                    }
                    updateImages(dailyImages[j], daysForecast.getIcon());
                    j++;
                }
            } catch (WeatherDataNotFoundException e) {
                System.err.println("Error updating UI: " + e.getMessage());
            } catch (ForecastNotFoundException e) {
                System.err.println("Error updating UI: " + e.getMessage());
            } catch (ImageHandler.ImageNotFoundException | ImageHandler.ImageLoadException e) {
                System.err.println("Error updating UI: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error updating UI: " + e.getMessage());
            }
        }

        /**
         * Converts celsius to fahrenheit.
         * @param celsius celsius.
         * @return fahrenheit.
         */
        private double celsiusToFahrenheit(double celsius) {
            return celsius * 9 / 5 + 32;
        }

        /**
         * Converts metric to imperial.
         * @param metric metric.
         * @return imperial.
         */
        private double metricToImperial(double metric) {
            return metric * 2.237;
        }

        /**
         * Creates the center box.
         * @return center box.
         */
        private VBox createCenterBox() {
            BorderPane borderPane = new BorderPane(createHourlyWeather(), createMainInfo(), null,
                    createCurrentAirConditions(), null);

            VBox centerBox = new VBox(borderPane);
            centerBox.setMaxWidth(800);
            centerBox.setPrefHeight(800);
            borderPane.prefHeightProperty().bind(centerBox.heightProperty());
            borderPane.setMinHeight(730);
            centerBox.setPadding(COMPONENT_PADDING);

            return centerBox;
        }

        /**
         * Creates the left box.
         * @return left box.
         */
        private HBox createLeftBox() {
            VBox menu = new VBox(SPACING);
            menu.setPadding(new Insets(20, 10, 10, 10));
            menu.setPrefWidth(170);
            menu.setAlignment(Pos.TOP_CENTER);
            menu.setStyle("-fx-background-color: " + BOX_BACKGROUND_COLOR + "; -fx-background-radius: "
                    + COMPONENT_RADIUS + ";");

            Button exitButton = createButton("EXIT", 50);
            exitButton.setOnAction(event -> Platform.exit());

            Button clearHistory = createButton("CLEAR HISTORY", 50);
            clearHistory.setOnAction(event -> {
                dataStorage.clearSearchHistory();
                searchHistoryListView.getItems().clear();
                searchHistoryListView.getItems().addAll(dataStorage.getSearchHistory());     
            });

            Button changeUnit = createButton("METRIC", 50);
            changeUnit.setOnAction(event -> {
                if (changeUnit.getText().equals("METRIC")) {
                    changeUnit.setText("IMPERIAL");

                    isMetric = false;
                    updateSearch(dataStorage.getCurrentCity());
                } else {
                    changeUnit.setText("METRIC");

                    isMetric = true;
                    updateSearch(dataStorage.getCurrentCity());
                }
            });

            Label favLabel = createLabel("FAVORITES", 15, Color.LIGHTGRAY);
            favLabel.setGraphic(ImageHandler.createImageView("FAVORITE", 20));
            menu.getChildren().addAll(exitButton, clearHistory, changeUnit, favLabel, createFavoriteCities());

            HBox leftBox = new HBox(menu);
            leftBox.setPadding(COMPONENT_PADDING);
            return leftBox;
        }

        /**
         * Creates favorite cities listview.
         * @return ListView of favrotie cities.
         */
        private ListView<String> createFavoriteCities() {
            ListView<String> favorListView = new ListView<>(FXCollections.observableArrayList(dataStorage.getFavoriteCities()));
            favorListView.setMaxHeight(200);
            favorListView.setPrefWidth(70);

            favorListView.setStyle("-fx-control-inner-background: #202b3c; -fx-background-color: #202b3c;");
            // Add an event handler for when an item in the history is clicked
            favorListView.setOnMouseClicked(event -> {
                String selectedCity = favorListView.getSelectionModel().getSelectedItem();
                if (selectedCity != null) {
                    updateSearch(selectedCity);
                    favorListView.getSelectionModel().clearSelection();
                }
            });

            cityName.setOnMouseClicked(event -> {
                if (dataStorage.isFavoriteCity(cityName.getText())) {
                    updateFavoriteGraphic(false);
                    dataStorage.removeFavoriteCity(cityName.getText());
                    favorListView.getItems().remove(cityName.getText());
                } else {
                    dataStorage.addFavoriteCity(cityName.getText());
                    favorListView.getItems().add(cityName.getText());
                    updateFavoriteGraphic(true);
                }
            });

            return favorListView;
        }

        /**
         * Creates the right box.
         * @return right box.
         */
        private HBox createRightBox() {
            Label label = createLabel("7 DAY FORECAST", 12, Color.LIGHTGRAY);
            label.setTextFill(Color.GRAY);
            VBox box = new VBox(30, label);

            for (int i = 0; i < 7; i++) {
                box.getChildren().add(get7DayForecast(dayLabels[i], dailyImages[i], dayWeathers[i], dayMinMaxs[i]));
            }

            box.setStyle("-fx-background-color: " + BOX_BACKGROUND_COLOR + "; -fx-background-radius: "
                    + COMPONENT_RADIUS + ";");
            box.setPadding(COMPONENT_PADDING);
            box.setPrefWidth(400);

            HBox rightHBox = new HBox(box);
            rightHBox.setPadding(COMPONENT_PADDING);
            return rightHBox;
        }

        /**
         * Creates a ListView for search history.
         * @return ListView for search history.
         */
        private ListView<String> createSearchHistoryListView() {
            searchHistoryListView = new ListView<>(FXCollections.observableArrayList(dataStorage.getSearchHistory()));
            searchHistoryListView.setMaxHeight(100);

            // Add an event handler for when an item in the history is clicked
            searchHistoryListView.setOnMouseClicked(event -> {
                String selectedCity = searchHistoryListView.getSelectionModel().getSelectedItem();
                if (selectedCity != null) {
                    searchBar.setText(selectedCity);
                    searchBar.requestFocus(); // To keep the focus on the search bar
                }
            });

            return searchHistoryListView;
        }


        /**
         * Creates a search bar.
         * @return search bar.
         */
        private HBox createSearchBar() {
            searchBar = new TextField();
            searchBar.setPrefHeight(30);
            searchBar.setPrefWidth(700);
            searchBar.setPromptText("Search for city...");
            ToggleButton button = new ToggleButton("☰");
            button.setPrefHeight(30);
            button.setMinWidth(30);

            button.setOnAction(event -> {
                if (searchHistoryListView.isVisible()) {
                    searchHistoryListView.setVisible(false);
                } else {
                    searchHistoryListView.setVisible(true);
                }
            });

            HBox searchBox = new HBox(searchBar, button);

            searchBar.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !searchHistoryListView.isFocused() && !button.isPressed()) {
                    searchHistoryListView.setVisible(false);
                } else {
                    searchHistoryListView.setVisible(true);
                }
            });

            searchBar.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    String searchQuery = searchBar.getText().trim();
                    if (!searchQuery.isEmpty()) {
                        updateSearch(searchQuery);
                        searchHistoryListView.getItems().clear();
                        searchHistoryListView.getItems().addAll(dataStorage.getSearchHistory()); // Update the ListView
                        searchHistoryListView.setVisible(false); // Close ListView after search
                    }
                    searchBar.clear();

                    // Use Platform.runLater to run the code on the JavaFX application thread
                    Platform.runLater(() -> {
                        // Request focus on another element to make searchBar lose focus
                        searchBox.requestFocus();
                    });
                }
            });

            return searchBox;
        }

        /**
         * Creates the main info section.
         * @return main info section box.
         */
        private VBox createMainInfo() {
            BorderPane currentInfo = new BorderPane(null, null, mainImg,
                    currentTemp, new VBox(5, cityName, currentDescription));

            BorderPane mainInfo = new BorderPane(currentInfo);
            mainInfo.setMinHeight(250);
            mainInfo.setPadding(new Insets(20));
            mainInfo.setPrefWidth(600);

            // Create a StackPane to overlay search history list
            StackPane stackPane = new StackPane(mainInfo, createSearchHistoryListView());
            stackPane.setAlignment(Pos.TOP_LEFT);
            VBox box = new VBox(createSearchBar(), stackPane);
            box.prefHeightProperty().bind(mainInfo.heightProperty());

            return box;
        }

        /**
         * Creates the hourly weather section.
         * @return hourly weather section box.
         */
        private VBox createHourlyWeather() {
            GridPane gridHourlyWeather = new GridPane();
            gridHourlyWeather.setHgap(30);
            gridHourlyWeather.setVgap(5);
            gridHourlyWeather.setPadding(COMPONENT_PADDING);

            for (int i = 0; i < 6; i++) {
                gridHourlyWeather.add(getHourlyWeather(hourlyTimes[i], hourlyTemps[i], hourlyImages[i]), i + 1, 0);
            }

            VBox box = new VBox(SPACING, createLabel("TODAYS FORECAST", 12, Color.GRAY), gridHourlyWeather);
            box.setStyle(
                    "-fx-background-color: " + BOX_BACKGROUND_COLOR + "; -fx-background-radius: " + COMPONENT_RADIUS
                            + ";");
            box.setPadding(COMPONENT_PADDING);
            box.setPrefHeight(PREF_HEIGHT);
            box.setMaxHeight(PREF_HEIGHT);

            return box;
        }

        /**
         * Creates a BorderPane for a single hour in the hourly forecast.
         * @param time time label.
         * @param temp temperature label.
         * @param img image.
         * @return BorderPane for a single hour in the hourly forecast.
         */
        private VBox getHourlyWeather(Label time, Label temp, ImageView img) {
            VBox box = new VBox(10,time, img, temp);
            box.setAlignment(Pos.TOP_CENTER);
            box.setMinHeight(110);
            box.setMinWidth(80);
            box.setStyle("-fx-background-color: " + COMPONENT_BACKGROUND_COLOR + "; -fx-background-radius: 10;");
            box.setPadding(new Insets(10));
            return box;
        }

        /**
         * Creates the current air conditions section.
         * @return current air conditions section box.
         */
        private VBox createCurrentAirConditions() {
            GridPane conditionsGPane = new GridPane();
            conditionsGPane.setHgap(100);
            conditionsGPane.setVgap(20);
            conditionsGPane.setPadding(COMPONENT_PADDING);

            conditionsGPane.add(createAirConditionProp("Real Feel", ImageHandler.createImageView("TEMP", 20), 
                            feelsLike), 1, 0);
            conditionsGPane.add(createAirConditionProp("Wind", ImageHandler.createImageView("WIND", 20), 
                            currentWind), 2, 0);
            conditionsGPane.add(createAirConditionProp("Chance of rain", ImageHandler.createImageView("RAIN", 20),
                             rainMM), 1, 1);
            conditionsGPane.add(createAirConditionProp("Humidity", ImageHandler.createImageView("HUMIDITY", 20), 
                        currentHumidity), 2, 1);

            VBox box = new VBox(SPACING, createLabel("CURRENT AIR CONDITIONS", 12, Color.GRAY), conditionsGPane);
            box.setStyle(
                    "-fx-background-color: " + BOX_BACKGROUND_COLOR + "; -fx-background-radius: " + COMPONENT_RADIUS
                            + ";");
            box.setPadding(COMPONENT_PADDING);
            box.setPrefHeight(PREF_HEIGHT);

            return box;
        }

        /**
         * Creates a GridPane for a single air condition property.
         * @param text text for the label.
         * @param img image.
         * @param label label.
         * @return GridPane for a single air condition property.
         */
        private GridPane createAirConditionProp(String text, ImageView img, Label label) {
            Label label2 = createLabel(text, 15, Color.LIGHTGRAY);
            GridPane g = new GridPane();
            g.setHgap(10);
            g.setVgap(10);
            g.add(img, 1, 1);
            g.add(label2, 2,1);
            g.add(label, 2, 2);
            
            return g;
        }

        /**
         * Creates a BorderPane for a single day in the 7 day forecast.
         * @param day day label.
         * @param img image.
         * @param weather weather label.
         * @param minMax min/max temperature label.
         * @return BorderPane for a single day in the 7 day forecast.
         */
        private BorderPane get7DayForecast(Label day, ImageView img, Label weather, Label minMax) {
            HBox box = new HBox(5, weather, img);
            box.setAlignment(Pos.CENTER);

            BorderPane borderPane = new BorderPane(box, null, minMax, null, day);
            borderPane.setPrefHeight(60);
            borderPane.setPadding(COMPONENT_PADDING);
            borderPane.setStyle("-fx-background-color: " + COMPONENT_BACKGROUND_COLOR + "; -fx-background-radius: "
                    + COMPONENT_RADIUS + ";");
            return borderPane;

        }

        /**
         * Creates multiple labels.
         * @param count count.
         * @param text text.
         * @param size size.
         * @param color color.
         * @param bold bold.
         * @return multiple labels.
         */
        private Label[] createLabels(int count, String text, int size, Color color, boolean bold) {
            Label[] labels = new Label[count];
            for (int i = 0; i < count; i++) {
                labels[i] = createLabel(text, size, color, bold);
            }
            return labels;
        }

        /**
         * Creates a label.
         * @param text text.
         * @param size size.
         * @param color color.
         * @return label.
         */
        private Label createLabel(String text, int size, Color color) {
            return createLabel(text, size, color, false);
        }

        /**
         * Creates a label.
         * @param text text.
         * @param size size.
         * @param color color.
         * @param bold bold.
         * @return label.
         */
        private Label createLabel(String text, int size, Color color, boolean bold) {
            Label label = new Label(text);
            label.setTextFill(color);
            if (bold) {
                label.setFont(Font.font("Helvetica", FontWeight.BOLD, size));
            } else {
                label.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, size));
            }
            return label;
        }

        /**
         * Updates the UI based on the search query.
         * @throws LocationNotFoundException if the location is not found.
         * @throws WeatherDataNotFoundException if the weather data is not found.
         * @throws Exception if an unexpected error occurs.
         * @param searchQuery search query.
         */
        private void updateSearch(String searchQuery) {
            try {
                Coordinates coord = api.lookUpLocation(searchQuery);
                if (coord == null) {
                    System.out.println("City was not found");
                    return;
                }

                CurrentCityWeather currentCityWeather = api.weatherApiCall(coord);
                
                // Add search to history and current jsons
                dataStorage.setCurrentCity(searchQuery);
                dataStorage.addSearchToHistory(currentCityWeather.getName()); // Update the data storage

                updateUI(coord);
            } catch (LocationNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (WeatherDataNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }

        /**
         * Updates the images.
         * @param img image.
         * @param link link for the image.
         */
        private void updateImages(ImageView img, String link) {
            Platform.runLater(() -> {
                ImageHandler.updateImage(img, link);
            });
        }

        /**
         * Updates favorite star image.
         * @param boolean isFavorite
         */
        private void updateFavoriteGraphic(boolean isFavorite) {
            String favoriteImage = isFavorite ? "FAVORITE" : "NOT_FAVORITE";
            ImageHandler.updateImage(starImg, favoriteImage);
        }

        /**
         * Creates a button.
         * @param text text.
         * @param size size.
         * @return button.
         */
        private Button createButton(String text, int size) {
            Button button = new Button(text);
            button.setStyle("-fx-background-color: " + BACKGROUND_COLOR + "; -fx-background-radius: "
                    + COMPONENT_RADIUS + ";");
            button.setTextFill(Color.ALICEBLUE);
            button.setPrefWidth(140);
            button.setPrefHeight(size);

            // Create a scale transition for the button
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.2);
            scaleTransition.setToY(1.2);

            // Create an inverse scale transition for when the mouse exits
            ScaleTransition reverseTransition = new ScaleTransition(Duration.millis(200), button);
            reverseTransition.setFromX(1.2);
            reverseTransition.setFromY(1.2);
            reverseTransition.setToX(1.0);
            reverseTransition.setToY(1.0);

            // Add event handlers for mouse enter and exit
            button.setOnMouseEntered(event -> scaleTransition.play());
            button.setOnMouseExited(event -> reverseTransition.play());

            return button;
        }
    }
}
