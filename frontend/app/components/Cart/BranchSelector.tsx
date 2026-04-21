// components/BranchSelector.tsx
import { useEffect, useState } from 'react';
import { API_BASE_URL } from '../../config';

interface Branch {
  id: number;
  name: string;
  description: string;
  purchaseDiscountPercent: number;
}

export function BranchSelector({ currentBranchId, onBranchChange }: { 
  currentBranchId?: number; 
  onBranchChange: (branchId: number) => void;
}) {
  const [branches, setBranches] = useState<Branch[]>([]);
  const [selectedId, setSelectedId] = useState<number | undefined>(currentBranchId);

  useEffect(() => {
    fetch(`${API_BASE_URL}/api/v1/cart/branches`, { credentials: 'include' })
      .then(res => res.json())
      .then(setBranches);
  }, []);

  const handleChange = (branchId: number) => {
    setSelectedId(branchId);
    onBranchChange(branchId);
  };

  return (
    <div className="space-y-2">
      <label className="text-xs font-bold uppercase tracking-wider text-stone-400">
        Sucursal de recogida
      </label>
      <select
        value={selectedId}
        onChange={(e) => handleChange(Number(e.target.value))}
        className="w-full bg-stone-800 border border-stone-700 rounded-xl p-3 text-white"
      >
        {branches.map(branch => (
          <option key={branch.id} value={branch.id}>
            {branch.name} {branch.purchaseDiscountPercent > 0 && `(-${branch.purchaseDiscountPercent}%)`}
          </option>
        ))}
      </select>
    </div>
  );
}