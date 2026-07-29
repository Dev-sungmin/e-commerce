export function decodeUserFromToken(token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return { email: payload.email, role: payload.role };
}