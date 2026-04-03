const RECENT_KEY = 'weather_recent_cities';
const FAVORITES_KEY = 'weather_favorite_cities';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('weatherForm');
    const cityInput = document.getElementById('city');
    const errorMsg = document.getElementById('errorMsg');
    const latitudeInput = document.getElementById('latitude');
    const longitudeInput = document.getElementById('longitude');
    const locationBtn = document.getElementById('locationBtn');
    const favoriteToggle = document.getElementById('favoriteToggle');
    const favoriteStatus = document.getElementById('favoriteStatus');

    if (form && cityInput && errorMsg) {
        form.addEventListener('submit', function(event) {
            const hasCoordinates = latitudeInput && longitudeInput
                && latitudeInput.value.trim() !== ''
                && longitudeInput.value.trim() !== '';

            if (cityInput.value.trim() === '' && !hasCoordinates) {
                event.preventDefault();
                errorMsg.style.display = 'block';
                return;
            }

            errorMsg.style.display = 'none';

            if (cityInput.value.trim() !== '') {
                saveCity(RECENT_KEY, cityInput.value.trim());
            }
        });

        cityInput.addEventListener('input', () => {
            if (latitudeInput) latitudeInput.value = '';
            if (longitudeInput) longitudeInput.value = '';
            errorMsg.style.display = 'none';
        });
    }

    if (locationBtn && form) {
        locationBtn.addEventListener('click', () => {
            if (!navigator.geolocation) {
                showInlineError(errorMsg, 'Geolocation is not supported in this browser.');
                return;
            }

            locationBtn.disabled = true;
            locationBtn.textContent = 'Detecting Location...';

            navigator.geolocation.getCurrentPosition((position) => {
                if (latitudeInput) latitudeInput.value = position.coords.latitude;
                if (longitudeInput) longitudeInput.value = position.coords.longitude;
                if (cityInput) cityInput.value = '';
                form.submit();
            }, () => {
                locationBtn.disabled = false;
                locationBtn.textContent = 'Use My Location';
                showInlineError(errorMsg, 'Unable to detect your location. Please allow location access.');
            });
        });
    }

    renderSavedCities('recentCities', getSavedCities(RECENT_KEY));
    renderSavedCities('favoriteCities', getSavedCities(FAVORITES_KEY));

    document.querySelectorAll('[data-clear]').forEach((button) => {
        button.addEventListener('click', () => {
            const key = button.dataset.clear === 'favorites' ? FAVORITES_KEY : RECENT_KEY;
            localStorage.removeItem(key);
            renderSavedCities('recentCities', getSavedCities(RECENT_KEY));
            renderSavedCities('favoriteCities', getSavedCities(FAVORITES_KEY));
            syncFavoriteButton(favoriteToggle);
        });
    });

    document.addEventListener('click', (event) => {
        const cityChip = event.target.closest('[data-city-chip]');
        if (cityChip && form && cityInput) {
            cityInput.value = cityChip.dataset.cityChip;
            if (latitudeInput) latitudeInput.value = '';
            if (longitudeInput) longitudeInput.value = '';
            form.submit();
        }
    });

    const body = document.body;
    if (body && body.dataset.currentCity && body.dataset.hasError !== 'true') {
        const cityLabel = body.dataset.currentCountry
            ? `${body.dataset.currentCity}, ${body.dataset.currentCountry}`
            : body.dataset.currentCity;
        saveCity(RECENT_KEY, cityLabel);
        renderSavedCities('recentCities', getSavedCities(RECENT_KEY));
    }

    if (favoriteToggle) {
        syncFavoriteButton(favoriteToggle);
        favoriteToggle.addEventListener('click', () => {
            const city = favoriteToggle.dataset.country
                ? `${favoriteToggle.dataset.city}, ${favoriteToggle.dataset.country}`
                : favoriteToggle.dataset.city;
            const result = toggleFavorite(city);
            renderSavedCities('favoriteCities', getSavedCities(FAVORITES_KEY));
            syncFavoriteButton(favoriteToggle);
            showFavoriteStatus(favoriteStatus, result.failed
                ? 'Unable to save favorites in this browser right now.'
                : result.saved
                    ? `${city} added to favorites.`
                    : `${city} removed from favorites.`);
        });
    }
});

function showInlineError(errorNode, message) {
    if (!errorNode) return;
    errorNode.textContent = message;
    errorNode.style.display = 'block';
}

function getSavedCities(key) {
    try {
        return JSON.parse(localStorage.getItem(key) || '[]');
    } catch (error) {
        return [];
    }
}

function saveCity(key, city) {
    if (!city) return;
    const cities = getSavedCities(key).filter((item) => item !== city);
    cities.unshift(city);
    localStorage.setItem(key, JSON.stringify(cities.slice(0, 6)));
}

function toggleFavorite(city) {
    try {
        const favorites = getSavedCities(FAVORITES_KEY);
        const saved = !favorites.includes(city);
        const nextFavorites = saved
            ? [city, ...favorites].slice(0, 6)
            : favorites.filter((item) => item !== city);
        localStorage.setItem(FAVORITES_KEY, JSON.stringify(nextFavorites));
        return { saved };
    } catch (error) {
        return { saved: false, failed: true };
    }
}

function renderSavedCities(containerId, cities) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (!cities.length) {
        container.innerHTML = '<p class="empty-state">No cities saved yet.</p>';
        return;
    }

    container.innerHTML = cities.map((city) =>
        `<button type="button" class="city-chip" data-city-chip="${escapeHtml(city)}">${escapeHtml(city)}</button>`
    ).join('');
}

function syncFavoriteButton(button) {
    if (!button) return;
    const city = button.dataset.country
        ? `${button.dataset.city}, ${button.dataset.country}`
        : button.dataset.city;
    const isFavorite = getSavedCities(FAVORITES_KEY).includes(city);
    button.innerHTML = isFavorite
        ? '<i class="fas fa-star"></i> Saved Favorite'
        : '<i class="far fa-star"></i> Save Favorite';
}

function showFavoriteStatus(node, message) {
    if (!node) return;
    node.textContent = message;
    node.style.display = 'block';
}

function escapeHtml(value) {
    return value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}
