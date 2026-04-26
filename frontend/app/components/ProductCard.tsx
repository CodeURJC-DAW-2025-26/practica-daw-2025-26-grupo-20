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
      <Link to={`/product/${product.id}`} className="product-link">
        <div className="recommended-card">
          <div className="recommended-img-wrapper">
            <img 
              src={getProductImage(product)} 
              alt={product.name} 
            />
          </div>
          <div className="recommended-info">
            <h5>{product.name}</h5>
            <div className="price-action-wrapper">
              <span className="price-tag">{(product.priceBase || 0).toFixed(2)}€</span>
              <button 
                className="btn-recommended-cart"
                onClick={(e) => {
                  e.preventDefault();
                  console.log("Añadir al carrito:", product.id);
                }}
              >
                <i className="fas fa-cart-plus me-2"></i>Añadir
              </button>
            </div>
          </div>
        </div>
      </Link>
    );
  }

  return (
    <Link to={`/product/${product.id}`} className="product-link">
      <div className="menu-card">
        <div className="card-img-wrapper">
          <img 
            src={getProductImage(product)} 
            alt={product.name} 
          />
        </div>
        <div className="card-body">
          <h5 className="card-title">{product.name}</h5>
          <div className="price-action-wrapper">
            <span className="price-tag">{(product.priceBase || 0).toFixed(2)}€</span>
            <button 
              className="btn-add-cart"
              onClick={(e) => {
                e.preventDefault();
                console.log("Añadir al carrito:", product.id);
              }}
            >
              <i className="fas fa-cart-plus me-2"></i>Añadir
            </button>
          </div>
        </div>
      </div>
    </Link>
  );
}