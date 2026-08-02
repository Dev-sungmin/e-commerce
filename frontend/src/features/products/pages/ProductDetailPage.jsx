import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productApi } from '../api/productApi';
import { useAuth } from '../../user/hooks/useAuth';
import '../styles/products.css';

export default function ProductDetailPage() {
    const { id } = useParams();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        productApi.getById(id)
            .then((res) => setProduct(res.data))
            .catch(() => setError('상품을 찾을 수 없습니다.'));
    }, [id]);

    function handleAddToCart() {
        if (!user) {
            navigate('/login');
            return;
        }
        alert('TODO: 장바구니 담기 (Order Service 연동 예정)');
    }

    if (error) return <p style={{ padding: 32 }}>{error}</p>;
    if (!product) return <p style={{ padding: 32 }}>불러오는 중...</p>;

    return (
        <div className="product-detail">
            {product.imageUrl && <img src={product.imageUrl} alt={product.name} />}
            <h2>{product.name}</h2>
            <p className="product-detail-price">{product.price.toLocaleString()}원</p>
            <p className="product-detail-stock">재고: {product.stockQuantity}개</p>
            <button className="auth-button" style={{ width: '100%' }} onClick={handleAddToCart}>
                장바구니 담기
            </button>
        </div>
    );
}