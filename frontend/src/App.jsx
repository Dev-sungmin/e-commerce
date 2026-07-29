import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './features/user/context/AuthContext';
import LoginPage from './features/user/pages/LoginPage';
import SignupPage from './features/user/pages/SignupPage';
import AuthCheckPage from './features/user/pages/AuthCheckPage';

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<AuthCheckPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;