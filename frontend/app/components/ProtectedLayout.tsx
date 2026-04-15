// layouts/ProtectedLayout.tsx
// Layout para rutas que requieren estar autenticado (cualquier rol)
import ProtectedRoute from './ProtectedRoute';

export default function ProtectedLayout() {
  return <ProtectedRoute />;
}
