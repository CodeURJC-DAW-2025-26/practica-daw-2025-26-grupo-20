// Only use the full URL on the server (SSR). On the client browser, use an empty string
// to route traffic through the Vite proxy and avoid CORS errors.
export const API_BASE_URL = import.meta.env.SSR ? (import.meta.env.VITE_API_BASE_URL || "") : "";
