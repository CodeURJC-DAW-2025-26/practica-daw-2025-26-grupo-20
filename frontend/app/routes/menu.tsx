import { useState, useMemo, useEffect } from "react";
import { useLoaderData, useSearchParams, useActionData, useNavigation } from "react-router";
import { useCartStore } from "../store/cartStore";
import { useNotificationStore } from "../store/notificationStore";
import { API_BASE_URL } from "../config";
import ProductCard from "../components/ProductCard";
import MenuFilters from "../components/MenuFilters";
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

export async function clientAction({ request }: { request: Request }) {
  const formData = await request.formData();
  const intent = formData.get("intent");

  if (intent === "cart") {
    const productId = formData.get("productId");
    const qty = formData.get("qty");

    try {
      const fd = new FormData();
      fd.append("productId", String(productId));
      fd.append("quantity", String(qty));

      const response = await fetch(`${API_BASE_URL}/api/v1/cart/items`, {
        method: "POST",
        credentials: "include",
        body: fd,
      });

      if (!response.ok) return { error: "Error al añadir al carrito." };
      return { success: true, message: "Añadido al carrito", productId };
    } catch (e) {
      return { error: "Error de red." };
    }
  }
  return null;
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
  const actionData = useActionData<typeof clientAction>();
  const allProducts = data?.allProducts || [];
  const initialCategory = data?.initialCategory || "all";
  const recommended = data?.recommended || [];

  const updateItemCount = useCartStore((state) => state.updateItemCount);
  const showNotification = useNotificationStore((state) => state.showNotification);

  const [searchParams, setSearchParams] = useSearchParams();
  const [hiddenAllergens, setHiddenAllergens] = useState<string[]>([]);
  const category = searchParams.get("category") || initialCategory;

  // Handle notifications and cart update
  useEffect(() => {
    if (actionData?.success && actionData?.message === "Añadido al carrito") {
      updateItemCount();
      const product = allProducts.find(p => p.id === Number(actionData.productId));
      const productName = product ? product.name : "Producto";
      showNotification(`${productName} añadido al carrito!`, 'success');
    }
  }, [actionData, updateItemCount, showNotification, allProducts]);

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
    return filteredProducts.slice(0, (currentPage + 1) * ITEMS_PER_PAGE);
  }, [filteredProducts, currentPage]);

  const handleLoadMore = () => {
    setSearchParams(params => { 
      params.set("page", (currentPage + 1).toString()); 
      return params; 
    }, { scroll: false });
  };

  const hasMore = (currentPage + 1) < totalPages;

  return (
    <div>
      <div className="menu-wrapper">
        <div className="container mx-auto px-4 py-5">
          <h1 className="text-center mb-4" style={{ color: "var(--dorado)" }}>
            Nuestro Menú
          </h1>

          {/* Recommended section */}
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

          {/* Filters */}
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

          {/* Load More button */}
          {hasMore && (
            <div className="mt-16 flex justify-center">
              <button 
                onClick={handleLoadMore}
                className="px-16 py-4 rounded-full border-2 border-[#d4b88d]/40 text-[14px] font-bold uppercase tracking-[0.3em] text-[#d4b88d] hover:bg-[#d4b88d] hover:text-black transition-all duration-500 shadow-lg hover:shadow-[#d4b88d]/20"
              >
                Cargar más
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}