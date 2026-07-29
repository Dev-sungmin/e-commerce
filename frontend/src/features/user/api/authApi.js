import client from '../../../api/client.js';

export const authApi = {
    signup: (email, password) =>
        client.post('/auth/signup', { email, password }),

    login: (email, password) =>
        client.post('/auth/login', { email, password }),

    refresh: () =>
        client.post('/auth/refresh'),

    logout: () =>
        client.post('/auth/logout'),
};