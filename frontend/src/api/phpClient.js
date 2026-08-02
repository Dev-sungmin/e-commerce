import axios from 'axios';

const phpClient = axios.create({
    baseURL: import.meta.env.VITE_PHP_API_BASE,
});

export default phpClient;