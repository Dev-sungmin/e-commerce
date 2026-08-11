import { useEffect } from 'react';
import { useAuth } from '../../user/hooks/useAuth';
import { useCart } from '../hooks/useCart';

export function CartSync() {
    const { user, isLoading: authLoading } = useAuth();
    const { refreshCart } = useCart();

    useEffect(() => {
        if (authLoading) return;
        if (user) {
            refreshCart();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps -- user 변경 시에만 반응
    }, [user, authLoading]);

    return null;
}