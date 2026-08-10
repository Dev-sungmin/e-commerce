import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { productApi } from '../api/productApi';
import '../styles/products.css';

export default function ProductListPage() {
    const [products, setProducts] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [keyword, setKeyword] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        void fetchProducts();
    }, [page]);

    async function fetchProducts() {
        setIsLoading(true);
        setError('');
        try {
            const params = { page, size: 12 };
            if (keyword) params.keyword = keyword;

            const res = await productApi.getList(params);
            setProducts(res.data.products);
            setTotalPages(res.data.totalPages);
        } catch {
            setError('상품 목록을 불러오지 못했습니다.');
        } finally {
            setIsLoading(false);
        }
    }

    function handleSearch(e) {
        e.preventDefault();
        setPage(1);
        void fetchProducts();
    }

    return (
        <div>
            <form onSubmit={handleSearch} style={{ padding: '0 24px', display: 'flex', gap: 8 }}>
                <input
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                    placeholder="상품 검색"
                    className="auth-input"
                    style={{ maxWidth: 240 }}
                />
                <button type="submit" className="auth-button" style={{ marginTop: 0, width: 'auto', padding: '11px 20px' }}>
                    검색
                </button>
            </form>

            {error && <p style={{ color: 'var(--error)', padding: '0 24px' }}>{error}</p>}

            {isLoading ? (
                <p style={{ padding: '0 24px' }}>불러오는 중...</p>
            ) : (
                <div
                    style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                        gap: 16,
                        padding: 24,
                    }}
                >
                    {products.map((p) => (
                        <Link
                            key={p.id}
                            to={`/products/${p.id}`}
                            style={{ textDecoration: 'none', color: 'inherit' }}
                        >
                            <div style={{ border: '1px solid var(--surface-border)', borderRadius: 8, overflow: 'hidden', background: 'var(--surface)' }}>
                                {p.imageUrl && (
                                    <img
                                        src={p.imageUrl}
                                        alt={p.name}
                                        style={{ width: '100%', height: 160, objectFit: 'cover' }}
                                    />
                                )}
                                <div style={{ padding: 16 }}>
                                    <p style={{ fontWeight: 600, margin: '0 0 6px' }}>{p.name}</p>
                                    <p style={{ margin: '0 0 6px' }}>{p.price.toLocaleString()}원</p>
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 12, padding: 24 }}>
                <button disabled={page <= 1} onClick={() => setPage(page - 1)}>이전</button>
                <span>{page} / {totalPages}</span>
                <button disabled={page >= totalPages} onClick={() => setPage(page + 1)}>다음</button>
            </div>
        </div>
    );
}