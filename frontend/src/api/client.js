import axios from 'axios';

const client = axios.create({
    baseURL: import.meta.env.VITE_API_BASE,
    withCredentials: true,
});

let accessToken = null;
let logoutCallbacks = [];
let refreshCallbacks = [];

export function setAccessToken(token) {
    accessToken = token;
}

export function addOnLogout(callback) {
    logoutCallbacks.push(callback);
}

export function addOnRefresh(callback) {
    refreshCallbacks.push(callback);
}

client.interceptors.request.use((config) => {
    if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
});

client.interceptors.response.use(
    (response) => response,
    async (error) => {
        const original = error.config;

        if (original.url === '/api/auth/refresh') {
            return Promise.reject(error);
        }

        if (error.response?.status === 401 && !original._retry) {
            original._retry = true;
            try {
                const res = await client.post('/api/auth/refresh', {}, { withCredentials: true });
                setAccessToken(res.data.accessToken);
                refreshCallbacks.forEach((cb) => cb(res.data.accessToken));
                original.headers.Authorization = `Bearer ${res.data.accessToken}`;
                return client(original);
            } catch (refreshError) {
                setAccessToken(null);
                logoutCallbacks.forEach((cb) => cb());
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default client;