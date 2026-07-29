import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi.js';
import { setAccessToken, setOnLogout } from "../../../api/client.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        tryRestoreSession();
        setOnLogout(() => clearSession());
    }, []);

    async function tryRestoreSession() {
        try {
            const res = await authApi.refresh();
            setAccessToken(res.data.accessToken);
            setUser(decodeUserFromToken(res.data.accessToken));
        } catch {
            clearSession();
        } finally {
            setIsLoading(false);
        }
    }

    async function login(email, password) {
        const res = await authApi.login(email, password);
        setAccessToken(res.data.accessToken);
        setUser(decodeUserFromToken(res.data.accessToken));
    }

    async function logout() {
        await authApi.logout();
        clearSession();
    }

    function clearSession() {
        setAccessToken(null);
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{user, isLoading, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}

function decodeUserFromToken(token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return { email: payload.email, role: payload.role };
}