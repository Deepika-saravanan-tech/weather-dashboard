package MyPackage;

import jakarta.servlet.ServletException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new Gson();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("index.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String myApiKey = System.getenv("OPENWEATHER_API_KEY");
		String city = request.getParameter("city");
		String latitude = request.getParameter("latitude");
		String longitude = request.getParameter("longitude");
		String trimmedCity = city == null ? "" : city.trim();
		String trimmedLatitude = latitude == null ? "" : latitude.trim();
		String trimmedLongitude = longitude == null ? "" : longitude.trim();
		boolean useCoordinates = !trimmedLatitude.isEmpty() && !trimmedLongitude.isEmpty();

		request.setAttribute("city", trimmedCity);

		if (trimmedCity.isEmpty() && !useCoordinates) {
			request.setAttribute("errorMessage", "Please enter a city name.");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		if (myApiKey == null || myApiKey.isBlank()) {
			request.setAttribute("errorMessage",
					"Weather service is not configured. Please set the OPENWEATHER_API_KEY environment variable.");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		String query = useCoordinates
				? "lat=" + URLEncoder.encode(trimmedLatitude, StandardCharsets.UTF_8) + "&lon="
						+ URLEncoder.encode(trimmedLongitude, StandardCharsets.UTF_8)
				: "q=" + URLEncoder.encode(trimmedCity, StandardCharsets.UTF_8);
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?" + query + "&appid=" + myApiKey;
		String forecastApiUrl = "https://api.openweathermap.org/data/2.5/forecast?" + query + "&appid=" + myApiKey;
		
		HttpURLConnection currentWeatherConnection = null;
		HttpURLConnection forecastConnection = null;
		try {
			URL url = new URL(apiUrl);
			currentWeatherConnection = (HttpURLConnection)url.openConnection();
			currentWeatherConnection.setRequestMethod("GET");
			currentWeatherConnection.setConnectTimeout(10000);
			currentWeatherConnection.setReadTimeout(10000);
			
			int statusCode = currentWeatherConnection.getResponseCode();
			InputStream inpStream = statusCode >= 200 && statusCode < 300
					? currentWeatherConnection.getInputStream()
					: currentWeatherConnection.getErrorStream();

			String responseBody = readResponse(inpStream);
			JsonObject jsonObject = GSON.fromJson(responseBody, JsonObject.class);
			
			if (statusCode < 200 || statusCode >= 300 || jsonObject == null || !jsonObject.has("main")) {
				String apiMessage = jsonObject != null && jsonObject.has("message")
						? jsonObject.get("message").getAsString()
						: "Unable to fetch weather data right now.";
				request.setAttribute("errorMessage", formatApiError(apiMessage, trimmedCity));
				request.getRequestDispatcher("index.jsp").forward(request, response);
				return;
			}
			
			
			//Temperature
			double tempInKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int tempInCelsius = (int)(tempInKelvin - 273.15);
			double feelsLikeKelvin = jsonObject.getAsJsonObject("main").get("feels_like").getAsDouble();
			int feelsLikeCelsius = (int)(feelsLikeKelvin - 273.15);
			
			//Humidty
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
			int pressure = jsonObject.getAsJsonObject("main").get("pressure").getAsInt();
			
			//wind speed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
			//visibitity
			int visibilityInMeter = jsonObject.get("visibility").getAsInt();
			int visibility = visibilityInMeter / 1000;
			//weather condition
	        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
	        String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
	        //cloud condition
	        int cloudCover = jsonObject.getAsJsonObject("clouds").get("all").getAsInt();
	        String resolvedCity = jsonObject.has("name") ? jsonObject.get("name").getAsString() : trimmedCity;
	        String country = jsonObject.getAsJsonObject("sys").get("country").getAsString();
	        double resolvedLatitude = jsonObject.getAsJsonObject("coord").get("lat").getAsDouble();
	        double resolvedLongitude = jsonObject.getAsJsonObject("coord").get("lon").getAsDouble();
	        
	     	// Date & Time
	        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
	        SimpleDateFormat sdfDate = new SimpleDateFormat("EEE MMM dd yyyy");
	        String date = sdfDate.format(new Date(dateTimestamp));

	        // Fetching the current time
	        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
	        String formattedTime = sdfTime.format(new Date());

	        forecastConnection = (HttpURLConnection) new URL(forecastApiUrl).openConnection();
	        forecastConnection.setRequestMethod("GET");
	        forecastConnection.setConnectTimeout(10000);
	        forecastConnection.setReadTimeout(10000);

	        int forecastStatusCode = forecastConnection.getResponseCode();
	        InputStream forecastStream = forecastStatusCode >= 200 && forecastStatusCode < 300
	        		? forecastConnection.getInputStream()
	        		: forecastConnection.getErrorStream();
	        String forecastResponseBody = readResponse(forecastStream);
	        JsonObject forecastJson = GSON.fromJson(forecastResponseBody, JsonObject.class);
	        List<ForecastDay> forecastDays = buildForecastDays(forecastJson);
	        String suggestion = buildSuggestion(weatherCondition, weatherDescription, tempInCelsius, feelsLikeCelsius,
	        		windSpeed, humidity, cloudCover);


	        // Set the data as request attributes (for sending to the jsp page)
	        request.setAttribute("date", date);
	        request.setAttribute("city", resolvedCity);
	        request.setAttribute("country", country);
	        request.setAttribute("visibility",visibility);
	        request.setAttribute("temperature", tempInCelsius);
	        request.setAttribute("feelsLike", feelsLikeCelsius);
	        request.setAttribute("weatherCondition", weatherCondition); 
	        request.setAttribute("weatherDescription", weatherDescription);
	        request.setAttribute("humidity", humidity);    
	        request.setAttribute("pressure", pressure);
	        request.setAttribute("windSpeed", windSpeed);
	        request.setAttribute("cloudCover", cloudCover);
	        request.setAttribute("currentTime", formattedTime);
	        request.setAttribute("weatherData", responseBody);
	        request.setAttribute("latitude", resolvedLatitude);
	        request.setAttribute("longitude", resolvedLongitude);
	        request.setAttribute("forecastDays", forecastDays);
	        request.setAttribute("suggestion", suggestion);
	        request.setAttribute("isLocationSearch", useCoordinates);
		}catch (IOException e) {
			request.setAttribute("errorMessage", "Unable to fetch weather data right now. Please try again.");
		} finally {
			if (currentWeatherConnection != null) {
				currentWeatherConnection.disconnect();
			}
			if (forecastConnection != null) {
				forecastConnection.disconnect();
			}
		}
		
        
     // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);


	}

	private String readResponse(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return "";
		}

		StringBuilder responseContent = new StringBuilder();
		try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				Scanner scanner = new Scanner(reader)) {
			while (scanner.hasNextLine()) {
				responseContent.append(scanner.nextLine());
			}
		}
		return responseContent.toString();
	}

	private String formatApiError(String apiMessage, String city) {
		if (apiMessage == null || apiMessage.isBlank()) {
			return "Unable to fetch weather data right now. Please try again.";
		}
		if ("city not found".equalsIgnoreCase(apiMessage)) {
			return "City not found for \"" + city + "\". Please check the spelling and try again.";
		}
		return Character.toUpperCase(apiMessage.charAt(0)) + apiMessage.substring(1) + ".";
	}

	private List<ForecastDay> buildForecastDays(JsonObject forecastJson) {
		List<ForecastDay> forecastDays = new ArrayList<>();
		if (forecastJson == null || !forecastJson.has("list")) {
			return forecastDays;
		}

		JsonArray entries = forecastJson.getAsJsonArray("list");
		for (int i = 0; i < entries.size() && forecastDays.size() < 5; i++) {
			JsonObject entry = entries.get(i).getAsJsonObject();
			String dateTimeText = entry.get("dt_txt").getAsString();
			if (!dateTimeText.contains("12:00:00") && i != entries.size() - 1) {
				continue;
			}

			long timestamp = entry.get("dt").getAsLong() * 1000;
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
			SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM");
			double tempInKelvin = entry.getAsJsonObject("main").get("temp").getAsDouble();
			int tempInCelsius = (int) (tempInKelvin - 273.15);
			String condition = entry.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
			String description = entry.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

			forecastDays.add(new ForecastDay(dayFormat.format(new Date(timestamp)), fullDateFormat.format(new Date(timestamp)),
					tempInCelsius, condition, description));
		}
		return forecastDays;
	}

	private String buildSuggestion(String weatherCondition, String weatherDescription, int temperature, int feelsLike,
			double windSpeed, int humidity, int cloudCover) {
		String lowerCondition = weatherCondition.toLowerCase();
		String lowerDescription = weatherDescription.toLowerCase();

		if (lowerCondition.contains("rain") || lowerDescription.contains("drizzle") || lowerDescription.contains("storm")) {
			return "Best time to go out is after the rain settles. Carry an umbrella and avoid peak downpour hours.";
		}
		if (temperature >= 35 || feelsLike >= 37) {
			return "Plan outdoor activity early morning or after sunset. Stay hydrated because it feels quite hot today.";
		}
		if (windSpeed >= 9) {
			return "It is breezy outside. Light outdoor plans are fine, but secure loose items and carry a light layer.";
		}
		if (humidity >= 80) {
			return "The air feels humid today. Short outdoor trips are better than long walks in the afternoon.";
		}
		if (cloudCover <= 25 && temperature >= 22 && temperature <= 31) {
			return "This looks like a great time to go out. Late afternoon should feel especially comfortable.";
		}
		return "Conditions look fairly balanced today. Mid-morning or early evening should be a comfortable time to step out.";
	}

	public static class ForecastDay {
		private final String day;
		private final String fullDate;
		private final int temperature;
		private final String condition;
		private final String description;

		public ForecastDay(String day, String fullDate, int temperature, String condition, String description) {
			this.day = day;
			this.fullDate = fullDate;
			this.temperature = temperature;
			this.condition = condition;
			this.description = description;
		}

		public String getDay() {
			return day;
		}

		public String getFullDate() {
			return fullDate;
		}

		public int getTemperature() {
			return temperature;
		}

		public String getCondition() {
			return condition;
		}

		public String getDescription() {
			return description;
		}
	}

}
