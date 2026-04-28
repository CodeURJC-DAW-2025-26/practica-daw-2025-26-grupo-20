import { useEffect } from 'react';
import { useNavigate, Outlet } from 'react-router';
import { useAuthStore } from '../store/authStore';

interface ProtectedRouteProps {
  /** If specified, only users with this role can access */
  requiredRole?: 'CUSTOMER' | 'ADMIN';
  /** Route to redirect to if no session. Defaults to /login */
  redirectTo?: string;
}

/**
 * Wraps routes that require authentication.
 *
 * Usage in routes.ts:
 *   route("profile", "routes/profile.tsx", { element: <ProtectedRoute /> })
 *
 * Or as parent layout:
 *   route("admin", "layouts/AdminLayout.tsx")   <- this uses <ProtectedRoute requiredRole="ADMIN" />
 */
export default function ProtectedRoute({
  requiredRole,
  redirectTo = '/login',
}: ProtectedRouteProps) {
  const { user, isLogged, isInitialized } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    // Wait for initializeAuth to finish before deciding
    if (!isInitialized) return;

    if (!isLogged || !user) {
      navigate(redirectTo, { replace: true });
      return;
    }

    if (requiredRole && user.role !== requiredRole) {
      // Authenticated user but wrong role -> redirect to profile
      navigate(user.role === 'ADMIN' ? '/profile-admin' : '/profile', { replace: true });
    }
  }, [isInitialized, isLogged, user, requiredRole, redirectTo, navigate]);

  // While initializeAuth is running, show a minimal loader
  if (!isInitialized) {
    return <AuthLoader />;
  }

  // If no session or wrong role, render nothing (useEffect redirects)
  if (!isLogged || !user) return null;
  if (requiredRole && user.role !== requiredRole) return null;

  return <Outlet />;
}

// ─── Minimalist loader matching Mokaf style ────────────────────────────────
function AuthLoader() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-[#050404] gap-6">
      <div className="w-16 h-16 bg-[#0c0b0b] border border-[#d4b88d]/20 rounded-[1.5rem] flex items-center justify-center text-[#d4b88d] text-2xl animate-pulse">
        <i className="fas fa-mug-hot"></i>
      </div>
      <p className="text-[10px] font-bold uppercase tracking-[0.5em] text-stone-700">
        Preparando tu café…
      </p>
    </div>
  );
}
