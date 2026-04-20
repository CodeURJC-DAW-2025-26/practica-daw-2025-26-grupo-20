/// components/AddToCartButton.tsx
import { useState } from 'react';
import { useAuthStore } from '../../store/authStore';
import { API_BASE_URL } from '../../config';
import { useNavigate } from 'react-router';

interface AddToCartButtonProps {
  productId: number;
  productName: string;
}

export function AddToCartButton({ productId, productName }: AddToCartButtonProps) {
  const [isAdding, setIsAdding] = useState(false);
  const isLogged = useAuthStore(state => state.isLogged);
  const navigate = useNavigate();

  const handleAddToCart = async () => {
    if (!isLogged) {
      navigate('/login', { state: { from: `/product/${productId}` } });
      return;
    }

    setIsAdding(true);
    try {
      const response = await fetch(`${API_BASE_URL}/api/v1/cart/items?productId=${productId}&quantity=1`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        // Mostrar toast o notificación
        console.log(`${productName} añadido al carrito`);
      }
    } catch (error) {
      console.error('Error adding to cart:', error);
    } finally {
      setIsAdding(false);
    }
  };

  return (
    <button
      onClick={handleAddToCart}
      disabled={isAdding}
      className="bg-amber-800 hover:bg-amber-700 text-white px-6 py-3 rounded-full font-bold transition-all disabled:opacity-50"
    >
      {isAdding ? 'Añadiendo...' : 'Añadir al Carrito'}
    </button>
  );
}