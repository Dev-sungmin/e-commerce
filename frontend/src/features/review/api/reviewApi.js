import client from '../../../api/client.js';

export const reviewApi = {
    getReviews: (productId, page = 0, size = 10) =>
        client.get('/api/reviews', { params: { productId, page, size } }),
    createReview: (data) => client.post('/api/reviews', data),
    likeReview: (reviewId) => client.post(`/api/reviews/${reviewId}/like`),
    unlikeReview: (reviewId) => client.delete(`/api/reviews/${reviewId}/like`),
    getUploadUrl: (filename) => client.post('/api/reviews/upload-url', null, { params: { filename } }),
    getReviewByOrder: (orderId, productId) =>
        client.get('/api/reviews/by-order', { params: { orderId, productId } }),
};