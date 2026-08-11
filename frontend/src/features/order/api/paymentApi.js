import client from '../../../api/client.js';

export const paymentApi = {
    confirm: ({ paymentKey, orderId, amount }) =>
        client.post('/api/payments/confirm', { paymentKey, orderId, amount }),
};