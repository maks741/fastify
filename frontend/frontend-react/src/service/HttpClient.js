import axios from 'axios';

const api = axios.create({
    baseURL: 'https://your-api.com',
});

const DEBUG_HTTP = false;

api.interceptors.request.use(
    (config) => {
        const token = 'eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsInVzZXJSb2xlIjoiVVNFUiIsInN1YiI6ImJlcnNlcmNlcjkzQGdtYWlsLmNvbSIsImlhdCI6MTc0ODUwMzYzMCwiZXhwIjoxNzQ4NTI1MjMwfQ.l1BjyW_c94Y6U1aTHSEjnW0dE4atkNpcEhz2HjJ8Zc4';

        // Add headers
        config.headers['Content-Type'] = 'application/json';
        config.headers['Accept'] = 'application/json';

        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }

        if (DEBUG_HTTP) {
            console.log('🚀 HTTP Request:', config);
        }

        return config;
    },
    (error) => {
        if (DEBUG_HTTP) {
            console.error('❌ Request Error:', error);
        }
        return Promise.reject(error);
    }
);

// Response interceptor
api.interceptors.response.use(
    (response) => {
        if (DEBUG_HTTP) {
            console.log('✅ HTTP Response:', response);
        }
        return response;
    },
    (error) => {
        if (DEBUG_HTTP) {
            console.error('❌ HTTP Error:', error);
        }
        return Promise.reject(error);
    }
);

export default api;