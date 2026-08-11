import { useEffect, useMemo, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useCart } from '../hooks/useCart';
import { productApi } from '../../products/api/productApi.js';
import '../styles/cart.css';

export default function CartPage() {
    const { items, isLoading: cartLoading, updateQuantity, removeItem, clearCart } = useCart();
    const [products, setProducts] = useState({}); // { [productId]: { name, price, stockQuantity, imageUrl } }
    const [isLoadingProducts, setIsLoadingProducts] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (items.length === 0) return;

        async function loadProducts() {
            setIsLoadingProducts(true);
            try {
                const ids = items.map((item) => item.productId);
                const res = await productApi.getByIds(ids);
                const map = {};
                res.data.products.forEach((p) => {
                    map[p.id] = p;
                });
                setProducts(map);
            } finally {
                setIsLoadingProducts(false);
            }
        }

        loadProducts();
    }, [items]);

    const enrichedItems = useMemo(() => {
        return items.map((item) => {
            const product = products[item.productId];
            return {
                ...item,
                product, // undefined면 품절/삭제된 상품
                isUnavailable: !product || product.stockQuantity < item.quantity,
            };
        });
    }, [items, products]);

    const hasUnavailableItem = enrichedItems.some((item) => item.isUnavailable);

    const totalPrice = useMemo(() => {
        return enrichedItems.reduce((sum, item) => {
            if (!item.product) return sum;
            return sum + item.product.price * item.quantity;
        }, 0);
    }, [enrichedItems]);

    async function handleQuantityChange(productId, quantity) {
        if (quantity < 1) return;
        await updateQuantity(productId, quantity);
    }

    async function handleRemove(productId) {
        await removeItem(productId);
    }

    function handleOrder() {
        navigate('/checkout', { state: { items: enrichedItems } });
    }

    if (cartLoading || isLoadingProducts) {
        return <div className="cart-page">불러오는 중...</div>;
    }

    if (items.length === 0) {
        return (
            <div className="cart-page">
                <p>장바구니가 비어있습니다.</p>
                <Link to="/">쇼핑 계속하기</Link>
            </div>
        );
    }

    return (
        <div className="cart-page">
            <h2>장바구니</h2>
            <ul className="cart-list">
                {enrichedItems.map((item) => (
                    <li key={item.productId} className={`cart-item ${item.isUnavailable ? 'unavailable' : ''}`}>
                        {item.product ? (
                            <>
                                <Link to={`/products/${item.productId}`}>
                                    <img src={item.product.imageUrl} alt={item.product.name} className="cart-item-image" />
                                </Link>
                                <div className="cart-item-info">
                                    <Link to={`/products/${item.productId}`}>{item.product.name}</Link>
                                    <p>{item.product.price.toLocaleString()}원</p>
                                    {item.isUnavailable && (
                                        <p className="cart-item-warning">재고가 부족합니다 (남은 수량: {item.product.stockQuantity})</p>
                                    )}
                                </div>
                            </>
                        ) : (
                            <div className="cart-item-info">
                                <p className="cart-item-warning">판매 종료된 상품입니다</p>
                            </div>
                        )}
                        <div className="cart-item-quantity">
                            <button onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}>-</button>
                            <span>{item.quantity}</span>
                            <button onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}>+</button>
                        </div>
                        <button className="cart-item-remove" onClick={() => handleRemove(item.productId)}>삭제</button>
                    </li>
                ))}
            </ul>
            <div className="cart-summary">
                <p>총 금액: {totalPrice.toLocaleString()}원</p>
                <button onClick={clearCart}>전체 비우기</button>
                <button onClick={handleOrder} disabled={hasUnavailableItem}>
                    주문하기
                </button>
            </div>
        </div>
    );
}