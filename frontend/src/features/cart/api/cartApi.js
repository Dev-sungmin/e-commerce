import client from '../../../api/client.js';

export const cartApi = {
    getCart: () => client.get('/api/cart'),
    addItem: (productId, quantity) => client.post('/api/cart/items', { productId, quantity }),
    updateQuantity: (productId, quantity) => client.patch(`/api/cart/items/${productId}`, { quantity }),
    removeItem: (productId) => client.delete(`/api/cart/items/${productId}`),
    clearCart: () => client.delete('/api/cart'),
};