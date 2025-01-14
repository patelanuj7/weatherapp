import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
// java -cp ".;lib\*" WeatherApp

public class WeatherApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // UI Components
        TextField cityField = new TextField();
        cityField.setPromptText("Enter City");

        Button getWeatherButton = new Button("Get Weather");
        getWeatherButton.setMaxWidth(Double.MAX_VALUE);

        Text weatherInfo = new Text();
        weatherInfo.setFont(javafx.scene.text.Font.font("Arial", 14));
        weatherInfo.setVisible(false); // Initially hide weather info

        // Button action to fetch weather data
        getWeatherButton.setOnAction(event -> fetchWeather(cityField.getText(), weatherInfo));

        // Allow Enter key to submit
        cityField.setOnAction(event -> getWeatherButton.fire());

        // Layout setup
        VBox inputLayout = new VBox(10, cityField, getWeatherButton);
        inputLayout.setAlignment(Pos.TOP_CENTER); // Align inputs at the top

        VBox mainLayout = new VBox(10, inputLayout, weatherInfo);
        mainLayout.setAlignment(Pos.TOP_CENTER); // Align main layout at the top
        mainLayout.setSpacing(20); // Space between input and weather info

        Scene scene = new Scene(mainLayout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Weather App");
        primaryStage.show();
    }

    private void fetchWeather(String city, Text weatherInfo) {
        if (!city.isEmpty()) {
            String weatherData = getWeather(city);
            if (weatherData != null) {
                JSONObject weatherJson = new JSONObject(weatherData);
                String temperature = weatherJson.getJSONObject("main").getBigDecimal("temp").toString();
                
                temperature = temperature.replaceAll("[^0-9.-]", "").trim();

                try {
                    double temp = Double.parseDouble(temperature);
                    temperature = String.format("%.1f", temp);
                } catch (NumberFormatException e) {
                    temperature = "N/A";
                }

                String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("description");
                weatherInfo.setText("Temperature: " + temperature + " \u00B0C\nDescription: " + description);
                weatherInfo.setVisible(true); // Show weather info after fetching
            } else {
                weatherInfo.setText("Could not fetch weather data.");
                weatherInfo.setVisible(true); // Show error message
            }
        } else {
            weatherInfo.setText("Please enter a city.");
            weatherInfo.setVisible(true); // Show error message
        }
    }

    // Function to get weather data from OpenWeather API
    private String getWeather(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            String apiKey = "b86599155a9eb4cc46847a6580275e0b";  // Replace with your OpenWeatherMap API key
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
            StringBuilder response = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                response.append((char) ch);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
