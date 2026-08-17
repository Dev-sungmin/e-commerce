import { useEffect, useState } from 'react';
import { useSearchParams, useLocation, Link } from 'react-router-dom';
import { paymentApi } from '../api/paymentApi.js';
import { orderApi } from '../api/orderApi.js';
import { useCart } from '../../cart/hooks/useCart';
import { useAuth } from '../../user/hooks/useAuth';
import '../styles/orderResult.css';

export default function OrderResultPage() {
    const location = useLocation();
    const [searchParams] = useSearchParams();
    const { clearCart } = useCart();
    const { isLoading: authLoading } = useAuth();
    const [status, setStatus] = useState('loading'); // loading | success | fail
    const [message, setMessage] = useState('');

    const isFailRoute = location.pathname === '/order/fail';

    useEffect(() => {
        if (authLoading) return;

        const orderId = searchParams.get('orderId');

        if (isFailRoute) {
            // eslint-disable-next-line react-hooks/set-state-in-effect -- URL 기반 실패
            setStatus('fail');
            setMessage(searchParams.get('message') || '결제가 취소되었습니다.');

            if (orderId) {
                orderApi.cancelOrder(orderId).catch(() => {
                    // 취소 API 실패는 화면에 별도로 안 알림 (재고는 타임아웃으로도 복구되니 치명적이지 않음)
                });
            }
            return;
        }

        const paymentKey = searchParams.get('paymentKey');
        const amount = searchParams.get('amount');

        if (!paymentKey || !orderId || !amount) {
            setStatus('fail');
            setMessage('잘못된 접근입니다.');
            return;
        }

        async function confirmPayment() {
            try {
                await paymentApi.confirm({ paymentKey, orderId, amount: Number(amount) });
                setStatus('success');
                try {
                    await clearCart();
                } catch {
                    // 카트 비우기 실패는 결제 성공 여부에 영향 없음
                }
            } catch {
                setStatus('fail');
                setMessage('결제 승인에 실패했습니다.');
                orderApi.cancelOrder(orderId).catch(() => {
                    // 취소 API 실패는 화면에 별도로 안 알림
                });
            }
        }

        confirmPayment();
        // eslint-disable-next-line react-hooks/exhaustive-deps -- authLoading 완료 시 1회만 실행
    }, [authLoading]);

    return (
        <div className="order-result-page">
            {status === 'loading' && <p>결제를 확인하는 중입니다...</p>}
            {status === 'success' && (
                <>
                    <h2>결제가 완료되었습니다</h2>
                    <Link to="/" className="auth-button">쇼핑 계속하기</Link>
                </>
            )}
            {status === 'fail' && (
                <>
                    <h2>결제에 실패했습니다</h2>
                    <p>{message}</p>
                    <Link to="/cart" className="auth-button">장바구니로 돌아가기</Link>
                </>
            )}
        </div>
    );
}