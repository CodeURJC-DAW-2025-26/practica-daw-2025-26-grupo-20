import { useState, useMemo } from "react";
import { useLoaderData, useSearchParams } from "react-router";
import { API_BASE_URL } from "../config";
import ProductCard from "../components/ProductCard";
import MenuFilters from "../components/MenuFilters";
import Pagination from "../components/Pagination";

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
      if (category !== 'all' && product.category !== category) return false;

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
    window.scrollTo({ top: 300, behavior: 'smooth' });
  };

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32">
      <div className="container mx-auto px-4 pt-10 max-w-7xl">
        <div className="animate-fade-in bg-[#080707] border border-[#d4b88d]/10 rounded-[2rem] shadow-[0_40px_100px_rgba(0,0,0,0.8)] relative overflow-hidden">
          
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-[#d4b88d]/5 blur-[120px] rounded-full pointer-events-none"></div>
          
          <div className="text-center mb-24 relative">
             <h1 className="text-5xl md:text-6xl font-serif text-[#d4b88d] italic tracking-tighter mb-6 drop-shadow-sm">Nuestro Menú</h1>
             <div className="w-20 h-[1px] bg-gradient-to-r from-transparent via-[#d4b88d]/40 to-transparent mx-auto"></div>
          </div>

          <section className="mb-32 relative">
             <div className="flex flex-col items-center mb-16">
                <div className="flex items-center gap-3 mb-3">
                   <span className="text-[#d4b88d]/60 text-[10px] transform rotate-12">★</span>
                   <h2 className="text-[13px] font-extrabold tracking-[0.5em] text-[#d4b88d] uppercase">Recomendados para ti</h2>
                </div>
                <div className="w-40 h-[1px] bg-stone-700/50"></div>
             </div>

             <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                {recommended.slice(0, 4).map((product) => (
                  <ProductCard key={product.id} product={product} variant="recommended" />
                ))}
             </div>
          </section>

          <MenuFilters 
            categories={categories}
            activeCategory={category}
            onCategoryChange={handleCategoryChange}
            allergens={allergensData}
            hiddenAllergens={hiddenAllergens}
            onToggleAllergen={toggleAllergen}
          />

          <main className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-12">
            {paginatedProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </main>
          <Pagination 
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </div>
    </div>
  );
}
