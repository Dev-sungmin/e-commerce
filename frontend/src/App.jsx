import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './features/user/context/AuthContext';
import { CartProvider } from './features/cart/context/CartContext';
import { CartSync } from './features/cart/context/CartSync';
import MainLayout from './components/MainLayout';
import LoginPage from './features/user/pages/LoginPage';
import SignupPage from './features/user/pages/SignupPage';
import ProductListPage from './features/products/pages/ProductListPage';
import ProductDetailPage from './features/products/pages/ProductDetailPage';
import CartPage from './features/cart/pages/CartPage';
import CheckoutPage from './features/order/pages/CheckoutPage';
import OrderResultPage from './features/order/pages/OrderResultPage';
import OrderListPage from './features/order/pages/OrderListPage';
import ReviewFormPage from './features/review/pages/ReviewFormPage';

function App() {
    return (
        <AuthProvider>
            <CartProvider>
                <CartSync />
                <BrowserRouter>
                    <Routes>
                        <Route element={<MainLayout />}>
                            <Route path="/" element={<ProductListPage />} />
                            <Route path="/products/:id" element={<ProductDetailPage />} />
                            <Route path="/products/:id/review" element={<ReviewFormPage />} />
                            <Route path="/cart" element={<CartPage />} />
                            <Route path="/checkout" element={<CheckoutPage />} />
                            <Route path="/order/success" element={<OrderResultPage />} />
                            <Route path="/order/fail" element={<OrderResultPage />} />
                            <Route path="/orders" element={<OrderListPage />} />
                        </Route>
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/signup" element={<SignupPage />} />
                    </Routes>
                </BrowserRouter>
            </CartProvider>
        </AuthProvider>
    );
}

export default App;