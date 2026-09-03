import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi.js';
import { reviewApi } from '../../review/api/reviewApi.js';
import '../styles/orderList.css';

export default function OrderListPage() {
    const [orders, setOrders] = useState([]);
    const [reviewMap, setReviewMap] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    async function loadReviewStatuses(orderList) {
        const paidItems = orderList
            .filter((o) => o.status === 'PAID')
            .flatMap((o) => o.items.map((item) => ({ orderId: o.id, productId: item.productId })));

        const results = await Promise.all(
            paidItems.map(({ orderId, productId }) =>
                reviewApi.getReviewByOrder(orderId, productId)
                    .then((res) => (res.data.exists ? { key: `${orderId}_${productId}`, reviewId: res.data.review.id } : null))
                    .catch(() => null)
            )
        );

        const map = {};
        results.forEach((r) => {
            if (r) map[r.key] = r.reviewId;
        });
        setReviewMap(map);
    }

    async function loadOrders() {
        setIsLoading(true);
        try {
            const res = await orderApi.getMyOrders();
            setOrders(res.data);
            await loadReviewStatuses(res.data);
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        loadOrders();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    if (isLoading) return <div className="order-list-page">불러오는 중...</div>;

    if (orders.length === 0) {
        return (
            <div className="order-list-page">
                <p>주문 내역이 없습니다.</p>
                <Link to="/">쇼핑 계속하기</Link>
            </div>
        );
    }

    return (
        <div className="order-list-page">
            <h2>내 주문 목록</h2>
            <ul className="order-list">
                {orders.map((order) => (
                    <li key={order.id} className="order-card">
                        <div className="order-card-header">
                            <span>{new Date(order.createdAt).toLocaleDateString()}</span>
                            <span className={`order-status order-status-${order.status.toLowerCase()}`}>
                                {order.status === 'PAID' ? '결제완료' : order.status === 'PENDING' ? '결제대기' : '취소됨'}
                            </span>
                        </div>
                        <ul className="order-card-items">
                            {order.items.map((item) => {
                                const reviewId = reviewMap[`${order.id}_${item.productId}`];
                                return (
                                    <li key={item.productId} className="order-card-item">
                                        <span>{item.productName} × {item.quantity}</span>
                                        {order.status === 'PAID' && (
                                            reviewId ? (
                                                <Link to={`/products/${item.productId}#review-${reviewId}`}>
                                                    작성한 리뷰 보기
                                                </Link>
                                            ) : (
                                                <Link
                                                    to={`/products/${item.productId}/review`}
                                                    state={{ orderId: order.id, productId: item.productId, productName: item.productName }}
                                                >
                                                    리뷰 쓰기
                                                </Link>
                                            )
                                        )}
                                    </li>
                                );
                            })}
                        </ul>
                        <p className="order-card-total">{order.totalAmount.toLocaleString()}원</p>
                    </li>
                ))}
            </ul>
        </div>
    );
}