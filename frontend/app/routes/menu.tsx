import { useState, useMemo } from "react";
import { useLoaderData, useSearchParams } from "react-router";
import { API_BASE_URL } from "../config";
import ProductCard from "../components/ProductCard";
import MenuFilters from "../components/MenuFilters";
import Pagination from "../components/Pagination";
import "../app_menu.css";

interface Allergen { id: number; name: string; }
interface Product {
  id: number; name: string; priceBase: number; description: string;
  category: string; imageId?: number; imageUrl?: string; allergens?: Allergen[];
}

export async function clientLoader({ request }: { request: Request }) {
  const url = new URL(request.url);
  const category = url.searchParams.get("category") || "all";
  try {
    const [productRes, recRes] = await Promise.all([
      fetch(`${API_BASE_URL}/api/v1/products?page=0&size=100`, { credentials: "include" }),
      fetch(`${API_BASE_URL}/api/v1/products?page=0&size=8`, { credentials: "include" }),
    ]);
    const productData = productRes.ok ? await productRes.json() : { content: [] };
    const recData = recRes.ok ? await recRes.json() : { content: [] };
    return {
      allProducts: Array.isArray(productData?.content) ? (productData.content as Product[]) : [],
      initialCategory: category,
      recommended: Array.isArray(recData?.content) ? (recData.content as Product[]) : []
    };
  } catch (err) {
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
  { name: "Cacahuetes" }, { name: "Frutos secos" }, { name: "Gluten" },
  { name: "Huevos" }, { name: "Lácteos" }, { name: "Sésamo" },
  { name: "Soja" }, { name: "Sulfitos" },
];

export default function Menu() {
  const data = useLoaderData<typeof clientLoader>();
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
      setSearchParams(params => { params.set("page", "0"); return params; });
      return next;
    });
  };

  const filteredProducts = useMemo(() => {
    return allProducts.filter(product => {
      if (category !== 'all' && product.category !== category) return false;
      if (hiddenAllergens.length > 0 && product.allergens) {
        const hiddenLower = hiddenAllergens.map(h => h.toLowerCase());
        if (product.allergens.some(a => a.name && hiddenLower.includes(a.name.toLowerCase().trim()))) return false;
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
    setSearchParams(params => { params.set("page", newPage.toString()); return params; });
    window.scrollTo({ top: 300, behavior: 'smooth' });
  };

  return (
    <div>
      <div className="menu-wrapper">
        <div className="container mx-auto px-4 py-5">
          <h1 className="text-center mb-4" style={{ color: "var(--dorado)" }}>
            Nuestro Menú
          </h1>

          {/* Sección de recomendados */}
          {recommended.length > 0 && (
            <section className="recommended-section mb-5">
              <div className="recommended-header">
                <i className="fas fa-star"></i>
                <h3>Recomendados para ti</h3>
                <i className="fas fa-star"></i>
              </div>
              <div className="recommended-grid">
                {recommended.slice(0, 4).map((product) => (
                  <ProductCard key={product.id} product={product} variant="recommended" />
                ))}
              </div>
            </section>
          )}

          {/* Filtros */}
          <MenuFilters
            categories={categories}
            activeCategory={category}
            onCategoryChange={handleCategoryChange}
            allergens={allergensData}
            hiddenAllergens={hiddenAllergens}
            onToggleAllergen={toggleAllergen}
          />

          
          <div className="stats-grid">
            {paginatedProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {/* Paginación */}
          {totalPages > 1 && (
            <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={handlePageChange} />
          )}
        </div>
      </div>
    </div>
  );
}