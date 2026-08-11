import { useState, useCallback, useEffect } from 'react';
import { cartApi } from '../api/cartApi.js';
import { addOnLogout } from '../../../api/client.js';
import { CartContext } from './CartContextObject.js';

export function CartProvider({ children }) {
    const [items, setItems] = useState([]);
    const [totalQuantity, setTotalQuantity] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    const clearCartState = useCallback(() => {
        setItems([]);
        setTotalQuantity(0);
    }, []);

    const refreshCart = useCallback(async () => {
        setIsLoading(true);
        try {
            const res = await cartApi.getCart();
            setItems(res.data.items);
            setTotalQuantity(res.data.totalQuantity);
        } catch {
            clearCartState();
        } finally {
            setIsLoading(false);
        }
    }, [clearCartState]);

    useEffect(() => {
        addOnLogout(() => clearCartState());
    }, [clearCartState]);

    async function addItem(productId, quantity) {
        await cartApi.addItem(productId, quantity);
        await refreshCart();
    }

    async function updateQuantity(productId, quantity) {
        await cartApi.updateQuantity(productId, quantity);
        await refreshCart();
    }

    async function removeItem(productId) {
        await cartApi.removeItem(productId);
        await refreshCart();
    }

    async function clearCart() {
        await cartApi.clearCart();
        clearCartState();
    }

    return (
        <CartContext.Provider
            value={{ items, totalQuantity, isLoading, refreshCart, addItem, updateQuantity, removeItem, clearCart }}
        >
            {children}
        </CartContext.Provider>
    );
}