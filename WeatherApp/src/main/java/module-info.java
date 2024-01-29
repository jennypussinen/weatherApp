/**
 * The "fi.tuni.prog3.weatherapp" module provides a weather application
 * with a graphical user interface (GUI) for displaying current weather,
 * forecasts, and other related information.
 *
 * <p>This module exports the main package "fi.tuni.prog3.weatherapp" for external use.
 * It requires the following modules:
 * - javafx.controls: JavaFX controls for building the GUI.
 * - com.google.gson: Gson library for JSON processing.
 * - javafx.graphics: JavaFX graphics components.
 * - com.fasterxml.jackson.databind: Jackson library for JSON data binding.
 */
module fi.tuni.prog3.weatherapp {
    exports fi.tuni.prog3.weatherapp;
    requires javafx.controls;
    requires com.google.gson;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
}



