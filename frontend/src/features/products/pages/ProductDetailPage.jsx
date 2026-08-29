import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productApi } from '../api/productApi';
import { useAuth } from '../../user/hooks/useAuth';
import { useCart } from '../../cart/hooks/useCart';
import '../styles/products.css';
import ReviewList from '../../review/components/ReviewList';

export default function ProductDetailPage() {
    const { id } = useParams();
    const { user } = useAuth();
    const { addItem } = useCart();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [error, setError] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        productApi.getById(id)
            .then((res) => setProduct(res.data))
            .catch(() => setError('상품을 찾을 수 없습니다.'));
    }, [id]);

    function requireLogin() {
        if (!user) {
            navigate('/login');
            return true;
        }
        return false;
    }

    function handleQuantityChange(delta) {
        setQuantity((prev) => {
            const next = prev + delta;
            if (next < 1) return 1;
            if (product && next > product.stockQuantity) return product.stockQuantity;
            return next;
        });
    }

    async function handleAddToCart() {
        if (requireLogin()) return;
        setIsSubmitting(true);
        try {
            await addItem(product.id, quantity);
            alert('장바구니에 담았습니다.');
        } catch {
            alert('장바구니 담기에 실패했습니다.');
        } finally {
            setIsSubmitting(false);
        }
    }

    function handleOrder() {
        if (requireLogin()) return;
        navigate('/checkout', {
            state: {
                items: [
                    {
                        productId: product.id,
                        quantity,
                        product,
                        isUnavailable: quantity > product.stockQuantity,
                    },
                ],
            },
        });
    }

    if (error) return <p style={{ padding: 32 }}>{error}</p>;
    if (!product) return <p style={{ padding: 32 }}>불러오는 중...</p>;

    const isOutOfStock = product.stockQuantity <= 0;

    return (
        <div className="product-detail">
            {product.imageUrl && <img src={product.imageUrl} alt={product.name} />}
            <h2>{product.name}</h2>
            <p className="product-detail-price">{product.price.toLocaleString()}원</p>
            <p className="product-detail-rating">
                ★ {product.averageRating.toFixed(1)} ({product.reviewCount}개 리뷰)
            </p>

            {isOutOfStock ? (
                <p className="product-detail-stock">품절된 상품입니다</p>
            ) : (
                <div className="quantity-selector">
                    <button className="btn-outline" onClick={() => handleQuantityChange(-1)} disabled={quantity <= 1}>-</button>
                    <span>{quantity}</span>
                    <button className="btn-outline" onClick={() => handleQuantityChange(1)} disabled={quantity >= product.stockQuantity}>+</button>
                </div>
            )}

            <div className="product-detail-actions">
                <button
                    className="btn-outline"
                    style={{ flex: 1 }}
                    onClick={handleAddToCart}
                    disabled={isOutOfStock || isSubmitting}
                >
                    장바구니 담기
                </button>
                <button
                    className="auth-button"
                    style={{ flex: 1, marginTop: 0 }}
                    onClick={handleOrder}
                    disabled={isOutOfStock}
                >
                    주문하기
                </button>
            </div>

            <ReviewList key={id} productId={product.id} />
        </div>
    );
}