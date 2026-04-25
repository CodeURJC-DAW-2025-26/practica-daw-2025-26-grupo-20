import { Link } from "react-router";

interface Allergen {
  id: number;
  name: string;
}

interface Product {
  id: number;
  name: string;
  priceBase: number;
  description: string;
  category: string;
  imageId?: number;
  imageUrl?: string;
  allergens?: Allergen[];
}

interface ProductCardProps {
  product: Product;
  variant?: "recommended" | "grid";
}

export default function ProductCard({ product, variant = "grid" }: ProductCardProps) {
  const getProductImage = (product: Product) => {
    if (product.imageUrl) return product.imageUrl;
    if (product.imageId) return `/images/${product.imageId}`;
    return "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500";
  };

  if (variant === "recommended") {
    return (
      <Link 
        to={`/product/${product.id}`}
        className="group bg-[#0c0b0b] border border-[#d4b88d]/5 p-5 rounded-2xl flex flex-col transition-all duration-700 hover:bg-[#121111] hover:border-[#d4b88d]/30 hover:-translate-y-2 shadow-[0_15px_40px_rgba(0,0,0,0.4)]"
      >
        <div className="aspect-square rounded-xl overflow-hidden mb-6 bg-black/40 ring-1 ring-white/5 relative">
          <img 
            src={getProductImage(product)} 
            alt={product.name} 
            className="w-full h-full object-contain p-3 transform group-hover:scale-110 transition-transform duration-1000"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
        </div>
        <div className="px-1">
          <h3 className="text-[14px] font-normal text-stone-200 mb-2 truncate group-hover:text-[#d4b88d] transition-colors">{product.name}</h3>
          <p className="text-[17px] font-extrabold text-[#f3e5d0] tracking-tight">{(product.priceBase || 0).toFixed(2)}€</p>
        </div>
      </Link>
    );
  }

  return (
    <Link 
      to={`/product/${product.id}`}
      className="group flex flex-col bg-[#0b0a0a] border border-[#d4b88d]/10 rounded-[2.5rem] p-10 transition-all duration-1000 hover:border-[#d4b88d]/40 hover:bg-[#0e0d0d] shadow-2xl relative"
    >
      <div className="aspect-[4/5] rounded-[2rem] overflow-hidden mb-10 bg-black/40 relative ring-1 ring-white/5">
        <img 
          src={getProductImage(product)} 
          alt={product.name} 
          className="w-full h-full object-contain p-8 transition-transform duration-[1.5s] group-hover:scale-110"
        />
      </div>
      
      <div className="flex flex-col flex-grow px-2">
        <h3 className="text-3xl text-white mb-6 group-hover:text-[#d4b88d] transition-colors tracking-tighter">{product.name}</h3>
        <p className="text-stone-200 text-[16px] font-light leading-relaxed mb-12 line-clamp-3 opacity-100 transition-opacity">
          {product.description}
        </p>
        <div className="mt-auto flex justify-between items-center border-t border-white/10 pt-10">
          <p className="text-4xl font-black text-[#f3e5d0] tracking-tighter">{(product.priceBase || 0).toFixed(2)}€</p>
          <div className="w-16 h-16 rounded-full border border-[#d4b88d]/40 flex items-center justify-center text-[#d4b88d] group-hover:bg-[#d4b88d] group-hover:text-black group-hover:scale-110 shadow-[0_0_30px_rgba(212,184,141,0.2)] transition-all duration-700">
            <i className="fas fa-plus text-sm"></i>
          </div>
        </div>
      </div>
    </Link>
  );
}
