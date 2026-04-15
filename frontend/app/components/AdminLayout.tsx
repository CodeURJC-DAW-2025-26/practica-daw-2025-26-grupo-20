// layouts/AdminLayout.tsx
// Layout para rutas exclusivas de administrador
import ProtectedRoute from './ProtectedRoute';

export default function AdminLayout() {
  return <ProtectedRoute requiredRole="ADMIN" />;
}
