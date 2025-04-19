# Weather Forecast App

An Android mobile application that displays **`real-time weather updates`** for your current location or any selected location on the map. You can add `favorite places`, `receive alerts` about severe weather conditions, and switch between various `display units and languages`.

---

## Features

- 🌦 Get current weather, temperature, humidity, pressure, wind speed, and more
- 🌍 Choose location using GPS, map selection, or search with autocomplete
- ❤️ Add favorite places and access weather forecasts for them
- 🔔 Set custom alerts for specific weather conditions
- 📈 View past hourly and daily weather information
- ⚙️ Choose between temperature units: Celsius, Fahrenheit, Kelvin
- 🌬 Choose wind speed units: m/s or mph
- 🌐 Support for English and Arabic languages

---

## Screens

### 🔧 Settings Screen
- Select your preferred `temperature` and `wind` units.
- Switch between `English` and `Arabic`.
- Choose location method: `GPS` or `manual map` selection.

### 🏠 Home Screen
- View current weather details such as `temperature, humidity, wind speed, pressure, and weather conditions`.
- Displays `icons` and `descriptions` (e.g., clear sky, rain).
- Includes past `hourly data for today` and `daily data` for the past 5 days.

### 🚨 Weather Alerts Screen
- Add `weather alerts` based on type (e.g., rain, wind, fog, temperature).
- Set alert duration and notification style (silent or with alarm sound).
- Option to `stop or remove` active alerts.

### ⭐ Favorite Locations Screen
- List of saved favorite places.
- Tap any location to see its weather forecast.
- Add new places via map or search bar.
- Remove locations from the favorites list.

---

## Tech Stack & Tools

- **Kotlin** – Primary development language
- **MVVM Architecture** – Clean separation of concerns
- **Retrofit** – Network layer for API calls
- **Room Database** – Local storage for favorite places and alerts
- **Coroutines** – Asynchronous operations
- **WorkManager** – Background tasks and alert scheduling
- **Google Maps & Places SDK** – Map interaction and location autocomplete

---

## Getting Started

1. **Clone the repository**
```bash
git clone https://github.com/AbdelazizMaher/ElTaqs.git
