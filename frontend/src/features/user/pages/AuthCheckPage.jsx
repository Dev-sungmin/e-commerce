import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

// TODO: PHP 상품 API 완성되면 features/products/pages/ProductListPage.jsx로 교체하고 이 파일은 삭제
export default function AuthCheckPage() {
    const { user, logout, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="auth-page">
                <p className="auth-footer">확인 중...</p>
            </div>
        );
    }

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h2 className="auth-title">인증 상태 확인 (임시)</h2>

                {user ? (
                    <>
                        <p style={{ fontSize: 14, marginBottom: 4 }}>
                            <strong>{user.email}</strong>
                        </p>
                        <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 20 }}>
                            권한: {user.role}
                        </p>
                        <button className="auth-button" onClick={logout}>
                            로그아웃
                        </button>
                    </>
                ) : (
                    <>
                        <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 20 }}>
                            로그인되어 있지 않습니다.
                        </p>
                        <div style={{ display: 'flex', gap: 8 }}>
                            <Link to="/login" className="auth-button" style={{ flex: 1, textAlign: 'center', display: 'block' }}>
                                로그인
                            </Link>
                            <Link
                                to="/signup"
                                className="auth-button"
                                style={{
                                    flex: 1,
                                    textAlign: 'center',
                                    display: 'block',
                                    background: 'transparent',
                                    border: '1px solid var(--surface-border)',
                                    color: 'var(--text)',
                                }}
                            >
                                회원가입
                            </Link>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}