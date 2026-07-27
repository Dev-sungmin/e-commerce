// TODO: 로그인 여부 + role 체크 후 접근 허용/리다이렉트

export default function ProtectedRoute({ allowedRoles, children }) {
    // TODO: 인증 상태(Context/store)에서 현재 사용자 role을 가져와 allowedRoles와 비교
    return children;
}