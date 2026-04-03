# Weather Dashboard

## Project Idea
Weather Dashboard is a full-stack Java web application built as an interview-ready project using Java Servlets, JSP, HTML, CSS, JavaScript, Maven, and Apache Tomcat. The goal of the project is to go beyond a basic weather search app by combining real-time weather data, a 5-day forecast, location-based search, smart suggestions, recent searches, and favorite cities in one polished dashboard.

## Why This Project Is Good For Interviews
- It demonstrates backend development with Java Servlets and request handling.
- It shows frontend integration using JSP, HTML, CSS, and JavaScript.
- It includes third-party API integration with OpenWeather.
- It uses Maven for dependency management and build packaging.
- It is deployable locally on Tomcat and publicly through Render.
- It includes product-thinking features like favorites, recent searches, and smart recommendations.

## Features
- Current weather search by city name
- 5-day forecast
- Current location weather using browser geolocation
- Smart suggestion section such as best time to go out
- Recent searches stored in the browser
- Favorite cities stored in the browser
- Friendly error handling for invalid city names or API issues
- Responsive blue-and-white dashboard UI

## Tech Stack
- Java
- Java Servlets
- JSP
- HTML
- CSS
- JavaScript
- Maven
- Apache Tomcat 10
- OpenWeather API
- Gson

## Architecture Summary
- `MyServlet.java` handles request processing, OpenWeather API calls, error handling, and forecast preparation.
- `index.html` is the landing page for search and saved city shortcuts.
- `index.jsp` renders current weather, forecast, suggestions, and interactive weather details.
- `script.js` manages client-side validation, geolocation, recent searches, and favorites.
- `style.css` and `jsp-style.css` provide the responsive frontend design.

## Interview Explanation
If an interviewer asks you to explain this project, you can say:

`I built a Weather Dashboard using Java Servlet and JSP as a full-stack web application. The app integrates with the OpenWeather API to fetch current weather and 5-day forecast data. I added location-based weather search, smart suggestions based on weather conditions, recent searches, and favorite cities to make the project more user-focused and unique. I used Maven for build management, Tomcat for deployment, and Render for hosting.`

## Key Technical Highlights
- Used `HttpURLConnection` to call external REST APIs from a Java Servlet
- Parsed JSON responses using Gson
- Added secure API-key handling through the `OPENWEATHER_API_KEY` environment variable
- Packaged the app as a WAR file using Maven
- Containerized deployment setup using Docker for Render hosting
- Added browser-side persistence with `localStorage`

## How To Run Locally
1. Install JDK 17 or above.
2. Install Apache Tomcat 10.x.
3. Clone this repository.
4. Set the environment variable `OPENWEATHER_API_KEY`.
   - Command Prompt:
     `set OPENWEATHER_API_KEY=your_api_key_here`
   - PowerShell:
     `$env:OPENWEATHER_API_KEY="your_api_key_here"`
5. Build the project:
   `mvn clean package`
6. Deploy the generated WAR to Tomcat or run it from your IDE with Tomcat configured.

## Deployment
This project includes:
- `Dockerfile`
- `render.yaml`

These files make the project deployable on Render as a Docker web service.

## Render Deployment Steps
1. Push the project to GitHub.
2. Create a new Web Service in Render.
3. Select the GitHub repository.
4. Keep the Docker environment selected.
5. Add the environment variable `OPENWEATHER_API_KEY`.
6. Deploy the service and use the generated public URL.

## Screenshots
![Weather Dashboard Screenshot](screenshots/1l.jpeg)
![Weather Dashboard Screenshot](screenshots/2l.jpeg)
![Weather Dashboard Screenshot](screenshots/3s.jpeg)

## Resume-Friendly Project Description
`Built a full-stack Weather Dashboard using Java Servlet, JSP, JavaScript, Maven, and Tomcat. Integrated OpenWeather API to provide current weather, 5-day forecast, geolocation-based search, smart weather suggestions, recent searches, and favorite cities. Secured API configuration with environment variables and prepared the project for cloud deployment using Docker and Render.`

## Future Improvements
- Dynamic weather icons based on condition
- Air quality index integration
- Temperature unit toggle between Celsius and Fahrenheit
- User authentication for cloud-synced favorites
- Charts for hourly temperature trends

## Author
Deepika Saravanan
