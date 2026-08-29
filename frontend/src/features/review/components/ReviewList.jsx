import { useEffect, useState } from 'react';
import { reviewApi } from '../api/reviewApi.js';
import { useAuth } from '../../user/hooks/useAuth';
import '../styles/reviewList.css';
import { useLocation } from 'react-router-dom';

export default function ReviewList({ productId }) {
    const { user } = useAuth();
    const location = useLocation();
    const [reviews, setReviews] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [cursorStack, setCursorStack] = useState([null]);
    const [pageIndex, setPageIndex] = useState(0);
    const [hasNext, setHasNext] = useState(false);

    useEffect(() => {
        loadReviews();
        // eslint-disable-next-line react-hooks/exhaustive-deps -- productId, pageIndex 변경 시에만 재조회
    }, [productId, pageIndex]);

    useEffect(() => {
        if (!location.hash || reviews.length === 0) return;
        const el = document.getElementById(location.hash.slice(1));
        if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, [reviews, location.hash]);

    async function loadReviews() {
        setIsLoading(true);
        try {
            const cursor = cursorStack[pageIndex];
            const res = await reviewApi.getReviews(productId, cursor, 10);
            setReviews(res.data.reviews);
            setHasNext(res.data.hasNext);
            setCursorStack((prev) => {
                if (res.data.hasNext && prev.length === pageIndex + 1) {
                    return [...prev, res.data.nextCursor];
                }
                return prev;
            });
        } finally {
            setIsLoading(false);
        }
    }

    async function handleToggleLike(review) {
        if (!user) return;
        try {
            if (review.likedByMe) {
                await reviewApi.unlikeReview(review.id);
            } else {
                await reviewApi.likeReview(review.id);
            }
            await loadReviews();
        } catch {
            // 좋아요 실패는 조용히 무시 (UX 상 치명적이지 않음)
        }
    }

    if (isLoading) return <p className="review-list-loading">리뷰를 불러오는 중...</p>;

    if (reviews.length === 0) {
        return <p className="review-list-empty">아직 작성된 리뷰가 없습니다.</p>;
    }

    return (
        <div className="review-list">
            {reviews.map((review) => (
                <div key={review.id} id={`review-${review.id}`} className="review-card">
                    <div className="review-card-header">
                        <span className="review-stars">{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span>
                        <span className="review-author">{review.userEmail}</span>
                    </div>
                    <p className="review-content">{review.content}</p>
                    {review.imageUrls.length > 0 && (
                        <div className="review-images">
                            {review.imageUrls.map((url) => (
                                <img key={url} src={url} alt="리뷰 이미지" />
                            ))}
                        </div>
                    )}
                    <div className="review-card-footer">
                        <button
                            className={`review-like-btn ${review.likedByMe ? 'liked' : ''}`}
                            onClick={() => handleToggleLike(review)}
                            disabled={!user}
                        >
                            ♥ {review.likeCount}
                        </button>
                        <span className="review-date">{new Date(review.createdAt).toLocaleDateString()}</span>
                    </div>
                </div>
            ))}
            {(pageIndex > 0 || hasNext) && (
                <div className="review-pagination">
                    <button disabled={pageIndex <= 0} onClick={() => setPageIndex(pageIndex - 1)}>이전</button>
                    <span>{pageIndex + 1} 페이지</span>
                    <button disabled={!hasNext} onClick={() => setPageIndex(pageIndex + 1)}>다음</button>
                </div>
            )}
        </div>
    );
}