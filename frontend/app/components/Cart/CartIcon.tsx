// components/CartIcon.tsx
import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useAuthStore } from '../../store/authStore';
import { API_BASE_URL } from '../../config';

export function CartIcon() {
  const [itemCount, setItemCount] = useState(0);
  const isLogged = useAuthStore(state => state.isLogged);

  useEffect(() => {
    if (!isLogged) {
      setItemCount(0);
      return;
    }

    const fetchCartCount = async () => {
      try {
        const response = await fetch('/api/v1/cart', {
          credentials: 'include',
        });
        if (response.ok) {
          const data = await response.json();
          setItemCount(data.totalUnits || 0);
        }
      } catch (error) {
        console.error('Error fetching cart count:', error);
      }
    };

    fetchCartCount();
    
    // Listen for cart update events
    window.addEventListener('cart-updated', fetchCartCount);
    return () => window.removeEventListener('cart-updated', fetchCartCount);
  }, [isLogged]);

  return (
    <Link to="/cart" className="relative">
      <i className="fas fa-shopping-cart text-2xl"></i>
      {itemCount > 0 && (
        <span className="absolute -top-2 -right-2 bg-amber-600 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
          {itemCount > 9 ? '9+' : itemCount}
        </span>
      )}
    </Link>
  );
}