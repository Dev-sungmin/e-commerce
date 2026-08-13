import { useState } from 'react';
import { useLocation, useNavigate, Navigate } from 'react-router-dom';
import { reviewApi } from '../api/reviewApi.js';
import '../styles/reviewForm.css';

export default function ReviewFormPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { orderId, productId, productName } = location.state || {};

    const [rating, setRating] = useState(5);
    const [content, setContent] = useState('');
    const [imageFile, setImageFile] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    if (!orderId || !productId) {
        return <Navigate to="/orders" replace />;
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        setIsSubmitting(true);

        try {
            let imageUrls = [];

            if (imageFile) {
                const uploadRes = await reviewApi.getUploadUrl(imageFile.name);
                const { uploadUrl, publicUrl } = uploadRes.data;

                await fetch(uploadUrl, {
                    method: 'PUT',
                    body: imageFile,
                    headers: { 'Content-Type': imageFile.type },
                });

                imageUrls = [publicUrl];
            }

            await reviewApi.createReview({
                orderId,
                productId,
                rating,
                content,
                imageUrls,
            });

            navigate(`/products/${productId}`);
        } catch (err) {
            if (err.response?.status === 409) {
                setError('이미 이 상품에 대한 리뷰를 작성했습니다.');
            } else if (err.response?.status === 403) {
                setError('구매하지 않은 상품에는 리뷰를 작성할 수 없습니다.');
            } else {
                setError('리뷰 작성 중 오류가 발생했습니다.');
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="review-form-page">
            <h2>리뷰 작성{productName ? ` - ${productName}` : ''}</h2>
            <form onSubmit={handleSubmit}>
                <div className="rating-selector">
                    {[1, 2, 3, 4, 5].map((n) => (
                        <button
                            type="button"
                            key={n}
                            className={n <= rating ? 'star filled' : 'star'}
                            onClick={() => setRating(n)}
                        >
                            ★
                        </button>
                    ))}
                </div>
                <textarea
                    className="review-textarea"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder="상품에 대한 솔직한 후기를 남겨주세요"
                    required
                    minLength={1}
                />
                <input
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    onChange={(e) => setImageFile(e.target.files[0] || null)}
                />
                {error && <p className="review-form-error">{error}</p>}
                <button className="auth-button" type="submit" disabled={isSubmitting}>
                    {isSubmitting ? '등록 중...' : '리뷰 등록'}
                </button>
            </form>
        </div>
    );
}