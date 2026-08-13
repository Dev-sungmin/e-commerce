import { Link } from 'react-router-dom';
import { useAuth } from '../features/user/hooks/useAuth';
import { useCart } from '../features/cart/hooks/useCart';
import './Header.css';

export default function Header() {
    const { user, logout } = useAuth();
    const { totalQuantity } = useCart();

    return (
        <header className="page-header">
            <Link to="/" className="header-logo">
                <h1>쇼핑몰</h1>
            </Link>
            <div className="user-area">
                {user && (
                    <Link to="/cart" className="cart-link">
                        장바구니
                        {totalQuantity > 0 && <span className="cart-badge">{totalQuantity}</span>}
                    </Link>
                )}
                {user && <Link to="/orders">주문내역</Link>}
                {user ? (
                    <>
                        <span>{user.email}님</span>
                        <button className="btn-outline" onClick={logout}>로그아웃</button>
                    </>
                ) : (
                    <>
                        <Link to="/login">로그인</Link>
                        <Link to="/signup">회원가입</Link>
                    </>
                )}
            </div>
        </header>
    );
}