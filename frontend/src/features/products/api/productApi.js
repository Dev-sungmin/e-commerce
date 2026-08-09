import phpClient from "../../../api/client.js";

export const productApi = {
    getList: (params) =>
        phpClient.get('/api/products.php', { params }),

    getById: (id) =>
        phpClient.get('/api/products.php', { params: { id } }),

    getByIds: (ids) =>
        phpClient.get('/api/products.php', { params: { ids: ids.join(','), size: 100 } }),
};