

// DTO from the backend (what the API returns)
export interface CartItemDTO {
  id: number;
  productId: number;
  name: string;
  quantity: number;
  unitPrice: string | number;
  lineTotal: string | number;
  imageUrl?: string;
}

export interface CartSummaryDTO {
  items: CartItemDTO[];
  subtotal: string;
  tax: string;
  total: string;
  discountInfo?: string;
  hasDiscount: boolean;
  itemCount: number;
  totalUnits: number;
}

// Models from the frontend (what the frontend uses internally)
export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
  totalPrice: number;
  productImageUrl?: string;
}

export interface CartSummary {
  items: CartItem[];
  subtotal: number;
  tax: number;
  total: number;
  itemCount: number;
  totalUnits: number;
}

// DTO to Model transformation functions
export function transformCartItem(dto: CartItemDTO): CartItem {
  const parsePrice = (price: string | number): number => {
    if (typeof price === 'number') return price;
    // Remove '€' and spaces, convert to number
    const cleaned = price.toString().replace('€', '').trim();
    return parseFloat(cleaned) || 0;
  };

  return {
    id: dto.id,
    productId: dto.productId,
    productName: dto.name,
    productPrice: parsePrice(dto.unitPrice),
    quantity: dto.quantity,
    totalPrice: parsePrice(dto.lineTotal),
    productImageUrl: dto.imageUrl,
  };
}

// Function to transform CartSummaryDTO to CartSummary
export function transformCartSummary(dto: CartSummaryDTO): CartSummary {
  return {
    items: dto.items.map(transformCartItem),
    subtotal: parseFloat(dto.subtotal.replace('€', '').trim()) || 0,
    tax: parseFloat(dto.tax.replace('€', '').trim()) || 0,
    total: parseFloat(dto.total.replace('€', '').trim()) || 0,
    itemCount: dto.itemCount,
    totalUnits: dto.totalUnits,
  };
}