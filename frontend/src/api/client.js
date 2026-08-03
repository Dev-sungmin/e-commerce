import axios from 'axios';

const client = axios.create({
    baseURL: import.meta.env.VITE_API_BASE,  // 이제 Gateway 주소
    withCredentials: true,
});

let accessToken = null;
let onLogout = null;

export function setAccessToken(token) {
    accessToken = token;
}

export function setOnLogout(callback) {
    onLogout = callback;
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
                original.headers.Authorization = `Bearer ${res.data.accessToken}`;
                return client(original);
            } catch (refreshError) {
                setAccessToken(null);
                if (onLogout) onLogout();
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default client;