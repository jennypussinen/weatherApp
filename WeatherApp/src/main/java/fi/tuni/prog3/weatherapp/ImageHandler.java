package fi.tuni.prog3.weatherapp;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Class for handling images.
 */
public class ImageHandler {
    private static final int DEFAULT_IMAGE_SIZE = 30;

    /**
     * Default constructor for the ImageHandler class.
     */
    public ImageHandler() {
        // No initialization needed 
    }

    /**
     * Returns the path to the image for the given weather.
     * @param weather Weather to get the image for.
     * @return Path to the image (or default image if no weather is found).
     */
    private static String getImagePath(String weather) {

        switch (weather) {
            case "FAVORITE":
                return "/icons2/starSelected.png";
            case "NOT_FAVORITE":
                return "/icons2/starNotSelected.png";
            case "HUMIDITY":
                return "/icons2/HUMIDITY.png";
            case "UV":
                return "/icons2/UV.png";                
            case "TEMP":
                return "/icons2/TEMP.png";                
            case "WIND":
                return "/icons2/WIND.png";                
            case "RAIN":
                return "/icons2/RAIN.png";                
            case "DEFAULT_IMAGE":
                return "/38.png";
            case "01d":
                return "/01d.png";                
            case "01n":
                return "/01n.png";                
            case "02d":
                return "/02d.png";                
            case "02n":
                return "/02n.png";                
            case "03d":
                return "/03.png";            
            case "03n":
                return "/03.png";                
            case "04d":
                return "/03.png";                
            case "04n":
                return "/03.png";                
            case "09d":
                return "/09.png";                
            case "09n":
                return "/09.png";                
            case "10d":
                return "/10d.png";                
            case "10n":
                return "/10n.png";                
            case "11d":
                return "/11d.png";                
            case "11n":
                return "/11n.png";            
            case "13d":
                return "/13d.png";                
            case "13n":
                return "/13n.png";                
            case "50d":
                return "/50d.png";                
            case "50n":
                return "/50n.png";                
            default:
                return "/38.png";
        }
    }

    /**
     * Creates an ImageView for the given weather.
     * @param iconCode to create the ImageView for.
     * @return ImageView for the given weather.
     */
    public static ImageView createImageView(String iconCode) {
        return createImageView(iconCode, DEFAULT_IMAGE_SIZE);
    }

    /**
     * Updates the given ImageView to show the image for the given weather.
     *
     * @param imageView ImageView to update.
     * @param iconCode  to update the ImageView for.
     * @throws ImageNotFoundException if the image is not found.
     */
    public static void updateImage(ImageView imageView, String iconCode) throws ImageNotFoundException, ImageLoadException {
        String imagePath = getImagePath(iconCode);
        
        try (InputStream imageStream = ImageHandler.class.getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                Image newImage = new Image(imageStream);
                imageView.setImage(newImage);
            } else {
                throw new ImageNotFoundException("Image not found: " + imagePath);
            }
        } catch (Exception e) {
            throw new ImageLoadException("Error loading image: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an ImageView for the given weather with the given size.
     * @param weather Weather to create the ImageView for.
     * @param size Size of the ImageView.
     * @throws ImageNotFoundException if the image is not found.
     * @return ImageView for the given weather.
     */
    public static ImageView createImageView(String weather, int size) throws ImageNotFoundException, ImageLoadException {
        String imgString = getImagePath(weather);
        try {
            InputStream stream = ImageHandler.class.getResourceAsStream(imgString);
            if (stream != null) {
                Image image = new Image(stream);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(size);
                return imageView;
            } else {
                throw new ImageNotFoundException("Input stream is null for image path: " + imgString);
            }
        } catch (Exception e) {
            throw new ImageLoadException("Error loading image: " + e.getMessage(), e);
        }
    }

    /**
     * Exception for image not found errors.
     */
    public static class ImageNotFoundException extends RuntimeException {
        /**
         * Constructs an ImageNotFoundException with the specified detail message.
         * @param message The detail message.
         */
        public ImageNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception for image loading errors.
     */
    public static class ImageLoadException extends RuntimeException {
        /**
         * Constructs an ImageLoadException with the specified detail message and cause.
         * @param message The detail message.
         * @param cause The cause of the exception.
         */
        public ImageLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}