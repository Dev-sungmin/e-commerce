import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './features/user/context/AuthContext';
import LoginPage from './features/user/pages/LoginPage';
import SignupPage from './features/user/pages/SignupPage';
import ProductListPage from './features/products/pages/ProductListPage';
import ProductDetailPage from './features/products/pages/ProductDetailPage';

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<ProductListPage />} />
                    <Route path="/products/:id" element={<ProductDetailPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;