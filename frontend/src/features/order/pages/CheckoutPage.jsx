import { useState } from 'react';
import { useLocation, useNavigate, Navigate } from 'react-router-dom';
import { loadTossPayments } from '@tosspayments/tosspayments-sdk';
import { orderApi } from '../api/orderApi.js';
import { useAuth } from '../../user/hooks/useAuth';
import '../styles/checkout.css';

const TOSS_CLIENT_KEY = import.meta.env.VITE_TOSS_CLIENT_KEY;

export default function CheckoutPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    const items = location.state?.items;

    if (!items || items.length === 0) {
        return <Navigate to="/cart" replace />;
    }

    const totalPrice = items.reduce((sum, item) => {
        if (!item.product) return sum;
        return sum + item.product.price * item.quantity;
    }, 0);

    async function handlePayment() {
        setError('');
        setIsSubmitting(true);
        let createdOrderId = null;
        try {
            const orderRes = await orderApi.createOrder(
                items.map((item) => ({ productId: item.productId, quantity: item.quantity }))
            );
            const { id: orderId, totalAmount, items: orderItems } = orderRes.data;
            createdOrderId = orderId;

            const tossPayments = await loadTossPayments(TOSS_CLIENT_KEY);
            const payment = tossPayments.payment({ customerKey: `user_${user.id}` });

            const orderName =
                orderItems.length > 1
                    ? `${orderItems[0].productName} 외 ${orderItems.length - 1}건`
                    : orderItems[0].productName;

            await payment.requestPayment({
                method: 'CARD',
                amount: { currency: 'KRW', value: totalAmount },
                orderId,
                orderName,
                successUrl: window.location.origin + '/order/success',
                failUrl: window.location.origin + `/order/fail?orderId=${orderId}`,
            });
        } catch (err) {
            console.error('결제 처리 오류:', err);
            if (err.response?.status === 409) {
                setError('재고가 부족한 상품이 있습니다.');
            } else if (err.code === 'USER_CANCEL') {
                setError('결제가 취소되었습니다.');
                if (createdOrderId) {
                    orderApi.cancelOrder(createdOrderId).catch(() => {
                        // 취소 API 실패는 조용히 무시, 타임아웃 로직이 백업
                    });
                }
            } else {
                setError('주문 처리 중 오류가 발생했습니다.');
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="checkout-page">
            <h2>주문 확인</h2>
            <ul className="checkout-list">
                {items.map((item) => (
                    <li key={item.productId} className="checkout-item">
                        {item.product && (
                            <>
                                <img src={item.product.imageUrl} alt={item.product.name} />
                                <div>
                                    <p>{item.product.name}</p>
                                    <p>{item.quantity}개 × {item.product.price.toLocaleString()}원</p>
                                </div>
                            </>
                        )}
                    </li>
                ))}
            </ul>
            <div className="checkout-summary">
                <p>총 결제금액: {totalPrice.toLocaleString()}원</p>
            </div>
            {error && <p className="checkout-error">{error}</p>}
            <button className="auth-button" onClick={handlePayment} disabled={isSubmitting}>
                {isSubmitting ? '처리 중...' : '결제하기'}
            </button>
            <button className="btn-outline" onClick={() => navigate(-1)}>취소</button>
        </div>
    );
}