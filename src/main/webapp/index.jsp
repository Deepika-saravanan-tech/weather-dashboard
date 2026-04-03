<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List,MyPackage.MyServlet.ForecastDay" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Weather App - Weather Details</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
<link rel="stylesheet" href="jsp-style.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

</head>
<body data-current-city="${city}" data-current-country="${country}"
	data-current-temp="${temperature}" data-has-error="<%= request.getAttribute("errorMessage") != null %>">

	<div class="container forecast-shell">
		<div class="page-header">
			<p class="eyebrow">Live Forecast Result</p>
			<h1>Weather Details</h1>
		</div>

		<% if (request.getAttribute("errorMessage") != null) { %>
		<div class="api-error">
			<i class="fas fa-exclamation-circle"></i>
			<span><%= request.getAttribute("errorMessage") %></span>
		</div>
		<% } %>

		<% if (request.getAttribute("errorMessage") == null) { %>
		<div class="weather-image-container">
			<img id="weather-icon" src="images/img1.png" alt="Weather Image">
			<div class="temp-city">
				<div class="title-row">
				<h2>
					<i class="fas fa-city"></i> ${city}, ${country}
				</h2>
				<button type="button" id="favoriteToggle" class="icon-btn"
					data-city="${city}" data-country="${country}">
					<i class="far fa-star"></i> Save Favorite
				</button>
				</div>
				<p id="favoriteStatus" class="favorite-status" style="display: none;"></p>
				<h2>
					<i class="fas fa-thermometer-half"></i> ${temperature}&deg;C
				</h2>
				<p class="weather-description">${weatherDescription}</p>
			</div>
		</div>

		<div class="highlight-row">
			<div class="highlight-card suggestion-card">
				<h3>Best Time To Go Out</h3>
				<p>${suggestion}</p>
			</div>
			<div class="highlight-card">
				<h3>Quick Snapshot</h3>
				<p>Feels like ${feelsLike}&deg;C with ${humidity}% humidity and
					${pressure} hPa pressure.</p>
			</div>
		</div>

		<div class="weather-info">
			<p>
				<i class="fas fa-calendar-alt"></i> Date: ${date}
			</p>
			<p>
				<i class="fas fa-clock"></i> CurrentTime: ${currentTime}
			</p>
			<p>
				<i class="fas fa-cloud-sun"></i> Condition: ${weatherCondition}
			</p>
			<p>
				<i class="fas fa-temperature-low"></i> Feels Like: ${feelsLike}&deg;C
			</p>
			<p>
				<i class="fas fa-eye"></i> Visibility: ${visibility}km
			</p>
			<p>
				<i class="fas fa-wind"></i> WindSpeed: ${windSpeed}km/hr
			</p>
			<p>
				<i class="fas fa-cloud"></i> CloudCover: ${cloudCover}%
			</p>
			<p>
				<i class="fas fa-droplet"></i> Humidity: ${humidity}%
			</p>
			<p>
				<i class="fas fa-gauge-high"></i> Pressure: ${pressure} hPa
			</p>
		</div>

		<div class="forecast-grid">
			<h2>5-Day Forecast</h2>
			<div class="forecast-cards">
				<%
				List<ForecastDay> forecastDays = (List<ForecastDay>) request.getAttribute("forecastDays");
				if (forecastDays != null) {
					for (ForecastDay day : forecastDays) {
				%>
				<div class="forecast-card">
					<p class="forecast-day"><%= day.getDay() %></p>
					<p class="forecast-date"><%= day.getFullDate() %></p>
					<p class="forecast-temp"><%= day.getTemperature() %>&deg;C</p>
					<p class="forecast-condition"><%= day.getCondition() %></p>
					<p class="forecast-description"><%= day.getDescription() %></p>
				</div>
				<%
					}
				}
				%>
			</div>
		</div>

		<div class="search-card">
			<h2>Search Another City</h2>
			<form id="weatherForm" action="MyServlet" method="post">
				<input type="hidden" id="latitude" name="latitude"
					value="${latitude}">
				<input type="hidden" id="longitude" name="longitude"
					value="${longitude}">
				<input type="text" id="city" name="city"
					placeholder="E.g., Chennai, New York, London">
				<div class="action-row">
					<button type="submit">Check Weather</button>
					<button type="button" id="locationBtn" class="secondary-btn">
						Use My Location</button>
				</div>
				<p id="errorMsg"
					style="color: red; padding: 6px 6px; display: none;">Please
					enter the name of the place.</p>

			</form>
		</div>

		<div class="saved-section">
			<div class="saved-block">
				<div class="section-heading">
					<h2>Recent Searches</h2>
					<button type="button" class="text-btn" data-clear="recent">Clear</button>
				</div>
				<div id="recentCities" class="city-chip-list"></div>
			</div>
			<div class="saved-block">
				<div class="section-heading">
					<h2>Favorite Cities</h2>
					<button type="button" class="text-btn" data-clear="favorites">Clear</button>
				</div>
				<div id="favoriteCities" class="city-chip-list"></div>
			</div>
		</div>
		<% } %>
	</div>

	<script src="script.js"></script>
</body>

<footer>
	<div class="footer-container">
		<p>Developed by Deepika Saravanan @ 2026 | JAVA Servlet JSP Dynamic
			Web Project</p>
	</div>
</footer>
</html>
