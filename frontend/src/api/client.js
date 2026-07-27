import axios from 'axios';

const client = axios.create({
    baseURL: import.meta.env.VITE_API_BASE,
});

client.interceptors.request.use((config) => {
    // TODO: 저장된 access_token을 Authorization 헤더에 첨부
    return config;
});

client.interceptors.response.use(
    (response) => response,
    async (error) => {
        // TODO: 401 응답 시 refresh_token으로 재발급 시도 -> 원래 요청 재시도
        return Promise.reject(error);
    }
);

export default client;