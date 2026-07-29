import { useContext } from 'react';
import { AuthContext } from '../context/AuthContextObject.js';

export function useAuth() {
    return useContext(AuthContext);
}