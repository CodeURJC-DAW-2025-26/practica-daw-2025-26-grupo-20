import { useState, useMemo } from "react";
import { useLoaderData, Link, useSearchParams } from "react-router";
import { API_BASE_URL } from "../config";

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

export async function loader({ request }: { request: Request }) {
  const url = new URL(request.url);
  const category = url.searchParams.get("category") || "all";

  try {
    const productRes = await fetch(`${API_BASE_URL}/api/v1/products?page=0&size=100`, { credentials: "include" });
    const productData = productRes.ok ? await productRes.json() : { content: [] };

    const recRes = await fetch(`${API_BASE_URL}/api/v1/products?page=0&size=8`, { credentials: "include" });
    const recData = recRes.ok ? await recRes.json() : { content: [] };

    return { 
      allProducts: Array.isArray(productData?.content) ? (productData.content as Product[]) : [], 
      initialCategory: category,
      recommended: Array.isArray(recData?.content) ? (recData.content as Product[]) : []
    };
  } catch (err) {
    console.error("Fetch failed in loader:", err);
    return { allProducts: [], initialCategory: "all", recommended: [] };
  }
}

const categories = [
  { id: 'all', label: 'Todos' },
  { id: 'HOT', label: 'Calientes' },
  { id: 'COLD', label: 'Fríos' },
  { id: 'BLENDED', label: 'Mezclados' },
  { id: 'DESSERTS', label: 'Postres' },
  { id: 'NON_COFFEE', label: 'Sin Café' }
];

const allergensData = [
  { name: "Cacahuetes" },
  { name: "Frutos secos" },
  { name: "Gluten" },
  { name: "Huevos" },
  { name: "Lácteos" },
  { name: "Sésamo" },
  { name: "Soja" },
  { name: "Sulfitos" },
];

export default function Menu() {
  const data = useLoaderData<typeof loader>();
  const allProducts = data?.allProducts || [];
  const initialCategory = data?.initialCategory || "all";
  const recommended = data?.recommended || [];

  const [searchParams, setSearchParams] = useSearchParams();
  const [hiddenAllergens, setHiddenAllergens] = useState<string[]>([]);
  
  const category = searchParams.get("category") || initialCategory;

  const handleCategoryChange = (catId: string) => {
    setSearchParams({ category: catId, page: "0" });
  };

  const toggleAllergen = (name: string) => {
    setHiddenAllergens(prev => {
      const next = prev.includes(name) ? prev.filter(a => a !== name) : [...prev, name];
      setSearchParams(params => {
        params.set("page", "0");
        return params;
      });
      return next;
    });
  };

  const filteredProducts = useMemo(() => {
    return (allProducts || []).filter(product => {
      // 1. Category Filter
      if (category !== 'all' && product.category !== category) return false;

      // 2. Allergen Filter
      if (hiddenAllergens.length > 0 && product.allergens) {
        const hiddenLower = hiddenAllergens.map(h => h.toLowerCase());
        const hasHiddenAllergen = product.allergens.some(a => 
          a.name && hiddenLower.includes(a.name.toLowerCase().trim())
        );
        if (hasHiddenAllergen) return false;
      }
      return true;
    });
  }, [allProducts, category, hiddenAllergens]);

  // Pagination Logic (Frontend)
  const ITEMS_PER_PAGE = 9;
  const currentPage = parseInt(searchParams.get("page") || "0");
  const totalPages = Math.ceil(filteredProducts.length / ITEMS_PER_PAGE);
  const paginatedProducts = useMemo(() => {
    const start = currentPage * ITEMS_PER_PAGE;
    return filteredProducts.slice(start, start + ITEMS_PER_PAGE);
  }, [filteredProducts, currentPage]);

  const handlePageChange = (newPage: number) => {
    setSearchParams(params => {
      params.set("page", newPage.toString());
      return params;
    });
    // Scroll to top of categories when changing page
    window.scrollTo({ top: 300, behavior: 'smooth' });
  };

  const getProductImage = (product: Product) => {
    if (product.imageUrl) return product.imageUrl;
    if (product.imageId) return `/images/${product.imageId}`;
    return "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500";
  };

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32">
      <div className="container mx-auto px-4 pt-10 max-w-7xl">
        {/* Main Menu Frame with subtle golden border */}
        <div className="animate-fade-in bg-[#080707] border border-[#d4b88d]/10 rounded-[2rem] shadow-[0_40px_100px_rgba(0,0,0,0.8)] relative overflow-hidden">
          
          {/* Subtle background glow */}
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>
          
          <div className="text-center mb-24 relative">
             <h1 className="text-5xl md:text-6xl font-serif text-[#d4b88d] italic tracking-tighter mb-6 drop-shadow-sm">Nuestro Menú</h1>
             <div className="w-20 h-[1px] bg-gradient-to-r from-transparent via-[#d4b88d]/40 to-transparent mx-auto"></div>
          </div>

          {/* Recommended Section - High Fidelity */}
          <section className="mb-32 relative">
             <div className="flex flex-col items-center mb-16">
                <div className="flex items-center gap-3 mb-3">
                   <span className="text-[#d4b88d]/60 text-[10px] transform rotate-12">★</span>
                   <h2 className="text-[12px] font-bold tracking-[0.5em] text-[#d4b88d] uppercase opacity-90">Recomendados para ti</h2>
                </div>
                <div className="w-32 h-[1px] bg-stone-800"></div>
             </div>

             <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                {recommended.slice(0, 4).map((product) => (
                  <Link 
                    key={product.id} 
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
                ))}
             </div>
          </section>

          {/* Filters Section */}
          <section className="mb-20 bg-white/[0.02] p-10 rounded-3xl border border-white/5">
             <div className="flex flex-wrap justify-center gap-5 mb-20">
                {categories.map((cat) => (
                   <button
                     key={cat.id}
                     onClick={() => handleCategoryChange(cat.id)}
                     className={`px-12 py-4 rounded-xl text-[11px] font-bold uppercase tracking-[0.25em] transition-all duration-700 ${category === cat.id ? 'bg-[#d4b88d] text-black shadow-[0_15px_40px_rgba(212,184,141,0.3)] scale-105' : 'bg-transparent border border-[#d4b88d]/20 text-[#d4b88d]/60 hover:text-[#d4b88d] hover:border-[#d4b88d]/50'}`}
                   >
                     {cat.label}
                   </button>
                ))}
             </div>

             <div className="flex flex-col items-center gap-8">
                <div className="flex items-center gap-4">
                  <div className="h-[1px] w-8 bg-stone-700"></div>
                  <span className="text-[11px] text-stone-400 font-black uppercase tracking-[0.2em]">Ocultar productos que contengan</span>
                  <div className="h-[1px] w-8 bg-stone-700"></div>
                </div>
                <div className="flex flex-wrap justify-center gap-3 max-w-5xl">
                   {allergensData.map((allergen) => {
                     // Normalize "Lácteos" -> "lacteos", "Frutos secos" -> "frutos-secos"
                     const allergenKey = allergen.name
                       .toLowerCase()
                       .normalize("NFD")
                       .replace(/[\u0300-\u036f]/g, "")
                       .replace(/\s+/g, "-")
                       .trim();
                       
                     const allergenColor = `var(--color-allergen-${allergenKey})`;
                      return (
                        <button 
                          key={allergen.name} 
                          onClick={() => toggleAllergen(allergen.name)}
                          style={{ backgroundColor: allergenColor }}
                          className={`flex items-center gap-2.5 px-7 py-3 rounded-full text-[12px] font-bold text-white transition-all duration-500 ${hiddenAllergens.includes(allergen.name) ? 'opacity-100 scale-110 shadow-[0_0_30px_rgba(255,255,255,0.1)] ring-2 ring-white/70' : 'opacity-80 hover:opacity-100 grayscale-[0.2] hover:grayscale-0'}`}
                        >
                           {hiddenAllergens.includes(allergen.name) && <i className="fas fa-ban text-[11px] animate-pulse"></i>}
                           {allergen.name}
                        </button>
                      );
                   })}
                </div>
             </div>
          </section>

          {/* Main Product Grid - Balanced Contrast */}
          <main className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-12">
            {paginatedProducts.map((product) => (
              <Link 
                key={product.id} 
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
                   <h3 className="text-3xl font-serif italic text-white mb-6 group-hover:text-[#d4b88d] transition-colors tracking-tighter">{product.name}</h3>
                   <p className="text-stone-300 text-[16px] font-light leading-relaxed mb-12 line-clamp-3 opacity-100 transition-opacity">
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
            ))}
          </main>

          {/* Improved Pagination Controls */}
          {totalPages > 1 && (
            <div className="mt-32 flex justify-between items-center border-t border-white/10 pt-20">
              <button 
                disabled={currentPage === 0}
                onClick={() => handlePageChange(currentPage - 1)}
                className="px-12 py-4 rounded-full border border-white/10 text-[11px] font-bold uppercase tracking-[0.3em] text-[#d4b88d] hover:bg-[#d4b88d] hover:text-black disabled:opacity-5 disabled:cursor-not-allowed transition-all duration-700"
              >
                Anterior
              </button>
              
              <div className="flex flex-col items-center gap-4">
                <span className="text-stone-600 text-[10px] font-bold uppercase tracking-[0.5em]">Vista</span>
                <span className="text-[#d4b88d] text-2xl font-serif italic font-bold">Página {currentPage + 1} de {totalPages}</span>
              </div>

              <button 
                disabled={currentPage >= totalPages - 1}
                onClick={() => handlePageChange(currentPage + 1)}
                className="px-12 py-4 rounded-full border border-white/10 text-[11px] font-bold uppercase tracking-[0.3em] text-[#d4b88d] hover:bg-[#d4b88d] hover:text-black disabled:opacity-5 disabled:cursor-not-allowed transition-all duration-700"
              >
                Siguiente
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
