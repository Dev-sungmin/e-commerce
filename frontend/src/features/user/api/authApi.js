import client from '../../../api/client.js';

export const authApi = {
    signup: (email, password) =>
        client.post('/api/auth/signup', { email, password }),

    login: (email, password) =>
        client.post('/api/auth/login', { email, password }),

    refresh: () =>
        client.post('/api/auth/refresh'),

    logout: () =>
        client.post('/api/auth/logout'),
};