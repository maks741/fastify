import axios from 'axios';

const api = axios.create({
    baseURL: 'https://your-api.com',
});

api.interceptors.request.use(
    (config) => {
        config.headers['Content-Type'] = 'application/json';
        config.headers['Accept'] = 'application/json';

        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response) {
            const status = error.response.status;

            if (status === 401 || status === 403) {
                navigate('/auth')
            }
        }
        return Promise.reject(error);
    }
);

export function setCurrentUser(user) {
    localStorage.setItem('accessToken', user.token);
}

export default api;