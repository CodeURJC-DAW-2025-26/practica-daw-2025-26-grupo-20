// hooks/useCart.ts
import { useFetcher } from 'react-router-dom';
import { useState } from 'react';

export function useCart() {
  const fetcher = useFetcher();
  const [error, setError] = useState<string | null>(null);

  const updateQuantity = (itemId: number, quantity: number) => {
    const formData = new FormData();
    formData.append('intent', 'update');
    formData.append('itemId', String(itemId));
    formData.append('quantity', String(quantity));
    
    fetcher.submit(formData, { method: 'post' });
  };

  const removeItem = (itemId: number) => {
    const formData = new FormData();
    formData.append('intent', 'delete');
    formData.append('itemId', String(itemId));
    
    fetcher.submit(formData, { method: 'post' });
  };

  const checkout = (paymentMethod: string = 'CARD') => {
    const formData = new FormData();
    formData.append('intent', 'checkout');
    formData.append('paymentMethod', paymentMethod);
    
    fetcher.submit(formData, { method: 'post' });
  };

  return {
    updateQuantity,
    removeItem,
    checkout,
    isLoading: fetcher.state !== 'idle',
    error,
  };
}