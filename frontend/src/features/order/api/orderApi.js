import client from '../../../api/client.js';

export const orderApi = {
    createOrder: (items) => client.post('/api/orders', { items }),
    getOrder: (id) => client.get(`/api/orders/${id}`),
    getMyOrders: () => client.get('/api/orders'),
};