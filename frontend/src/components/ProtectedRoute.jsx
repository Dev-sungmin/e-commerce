import { Navigate } from 'react-router-dom';
import { useAuth } from '../features/user/hooks/useAuth';

export default function ProtectedRoute({ allowedRoles, children }) {
    const { user, isLoading } = useAuth();

    if (isLoading) return <p>로딩 중...</p>;
    if (!user) return <Navigate to="/login" replace />;
    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <Navigate to="/" replace />;
    }

    return children;
}