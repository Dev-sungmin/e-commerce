import { useState, useEffect, useCallback } from 'react';
import { authApi } from '../api/authApi.js';
import {setAccessToken, addOnLogout, addOnRefresh} from '../../../api/client.js';
import { decodeUserFromToken } from '../utils/decodeToken.js';
import { AuthContext } from './AuthContextObject.js';

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const clearSession = useCallback(() => {
        setAccessToken(null);
        setUser(null);
    }, []);

    const tryRestoreSession = useCallback(async () => {
        try {
            const res = await authApi.refresh();
            setAccessToken(res.data.accessToken);
            setUser(decodeUserFromToken(res.data.accessToken));
        } catch {
            clearSession();
        } finally {
            setIsLoading(false);
        }
    }, [clearSession]);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect -- 앱 마운트 시 세션 복원을 위한 의도된 초기화
        tryRestoreSession();
        addOnLogout(() => clearSession());
        addOnRefresh((newAccessToken) => setUser(decodeUserFromToken(newAccessToken)));
    }, [tryRestoreSession, clearSession]);

    async function login(email, password) {
        const res = await authApi.login(email, password);
        setAccessToken(res.data.accessToken);
        setUser(decodeUserFromToken(res.data.accessToken));
    }

    async function logout() {
        await authApi.logout();
        clearSession();
    }

    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}