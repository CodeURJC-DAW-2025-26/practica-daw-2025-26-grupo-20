import { useLoaderData, Link, Form } from "react-router";
import { useState } from "react";
import { API_BASE_URL } from "../config";
import { addToCart } from "./cart";

export async function loader({ params }: { params: { id: string } }) {
  const [productRes, reviewRes, userRes] = await Promise.all([
    fetch(`${API_BASE_URL}/api/v1/products/${params.id}`, { credentials: "include" }),
    fetch(`${API_BASE_URL}/api/v1/products/${params.id}/reviews`, { credentials: "include" }),
    fetch(`${API_BASE_URL}/api/v1/users/me`, { credentials: "include" })
  ]);

  if (!productRes.ok) throw new Response("Producto no encontrado", { status: 404 });

  const product = await productRes.json();
  const reviewsData = reviewRes.ok ? await reviewRes.json() : { content: [] };
  const user = userRes.ok ? await userRes.json() : null;

  return { product, reviews: reviewsData.content, user };
}

export async function action({ request, params }: { request: Request; params: { id: string } }) {
  const formData = await request.formData();
  const intent = formData.get("intent");

  if (intent === "review") {
     const data = Object.fromEntries(formData);
     const response = await fetch(`${API_BASE_URL}/api/v1/products/${params.id}/reviews`, {
       method: "POST",
       headers: { "Content-Type": "application/json" },
       credentials: "include",
       body: JSON.stringify({ stars: Number(data.stars), text: data.text }),
     });
     if (!response.ok) return { error: "Error al publicar la reseña." };
     return { success: true };
  }
  return null;
}

export default function ProductDetail() {
  const { product, reviews, user } = useLoaderData<typeof loader>();
  const [qty, setQty] = useState(1);

  const getProductImage = (product: any) => {
    if (product.imageUrl) {
      if (product.imageUrl.startsWith("http")) return product.imageUrl;
      return `${API_BASE_URL}${product.imageUrl}`;
    }
    return "https://via.placeholder.com/900x700?text=Producto";
  };

  const productPrice = Number(product.priceBase || 0).toFixed(2);

  return (
    <main className="container my-5 product-page">
      <div className="product-shell">
        <div className="row g-4 align-items-stretch flex flex-col lg:flex-row">
          <div className="col-lg-6 lg:w-1/2">
            <div className="product-image-wrap">
              <img
                src={getProductImage(product)}
                className="product-image"
                alt={product.name}
              />
            </div>
          </div>

          <div className="col-lg-6 lg:w-1/2">
            <div className="product-info ml-lg-4">
              <div className="product-top">
                <span className="product-category">{product.category}</span>
                <div className="product-allergens">
                  {product.allergens?.map((a: any) => (
                    <span key={a.id} className="allergen-badge">{a.name}</span>
                  ))}
                </div>
              </div>

              <h1 className="product-title">{product.name}</h1>
              <p className="product-subtitle">{product.description}</p>

              <div className="product-price-row">
                <div className="product-price">{productPrice}€</div>
              </div>

              <hr className="product-divider" />
              
              <div className="product-actions">
                {user ? (
                  <>
                    <div className="qty-control">
                      <button
                        className="qty-btn"
                        type="button"
                        onClick={() => setQty(Math.max(1, qty - 1))}
                      >
                        −
                      </button>

                      <input
                        className="qty-input"
                        type="number"
                        value={qty}
                        readOnly
                      />
                      <button
                        className="qty-btn"
                        type="button"
                        onClick={() => setQty(qty + 1)}
                      >
                        +
                      </button>
                    </div>

                    <Form method="post" className="ajax-cart-form" style={{ display: 'inline' }}>
                      <input type="hidden" name="productId" value={product.id} />
                      <input type="hidden" name="qty" value={qty} />
                      <button className="btn btn-product-primary" type="submit">
                        <i className="fas fa-cart-plus me-2"></i>Añadir
                      </button>
                    </Form>
                  </>
                ) : (
                  <Link to="/login" className="btn btn-product-primary">
                    Inicia sesión para comprar
                  </Link>
                )}
              </div>

              <div className="product-meta">
                <div className="meta-item">
                  <i className="fas fa-truck"></i>
                  <span>Entrega 24/48h</span>
              <button 
                onClick={async () => {
                  try {
                    await addToCart(product.id, qty);
                    alert("Producto añadido al carrito");
                  } catch (error) {
                    alert("Error al añadir el producto al carrito");
                  }
                }}
                className="flex-grow group/btn relative h-16 bg-amber-800 rounded-3xl overflow-hidden shadow-2xl shadow-amber-900/40 active:scale-95 transition-all"
              >
                <div className="absolute inset-0 bg-amber-900 transform -translate-x-full group-hover/btn:translate-x-0 transition-transform duration-500"></div>
                <div className="relative z-10 flex items-center justify-center gap-4 text-white">
                  <i className="fas fa-cart-plus text-2xl animate-pulse"></i>
                  <span className="font-black uppercase tracking-[0.2em] text-sm">Añadir al Carrito</span>
                </div>
                <div className="meta-item">
                  <i className="fas fa-shield-alt"></i>
                  <span>Pago seguro</span>
                </div>
                <div className="meta-item">
                  <i className="fas fa-undo"></i>
                  <span>14 días</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <section className="product-section mt-4">
        <h3 className="section-title">Descripción</h3>
        <p className="section-text">{product.description}</p>
      </section>

      <section className="product-section">
        <h3 className="section-title">Detalles</h3>
        <ul className="details-list">
          <li><strong>Categoría:</strong> {product.category}</li>
          <li><strong>Precio:</strong> {productPrice}€</li>
        </ul>
      </section>

      <section className="product-section" id="reviews">
        <h3 className="section-title">Reseñas</h3>

        <div id="reviewsList">
          {reviews.map((review: any) => (
            <div key={review.id} className="review-card">
               <div className="review-head">
                  <div className="review-user" style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <div className="avatar">{review.user?.name?.[0] || 'U'}</div>
                    <span className="review-name">{review.user?.name || 'Usuario'}</span>
                  </div>
                  <div className="review-stars">
                    {[1, 2, 3, 4, 5].map(i => (
                      <i key={i} className={`fas fa-star ${i <= review.stars ? "" : "opacity-30"}`}></i>
                    ))}
                  </div>
               </div>
               <p className="review-text">{review.text}</p>
            </div>
          ))}
          {reviews.length === 0 && <p className="section-text mt-3">No hay reseñas aún.</p>}
        </div>

        {user ? (
          <div className="mt-4">
            <h4 className="section-title">Escribe una reseña</h4>
            <Form method="post">
              <input type="hidden" name="intent" value="review" />
              <div className="mb-3">
                <label className="form-label" style={{ display: 'block', marginBottom: '8px' }}>Puntuación</label>
                <select name="stars" className="form-select w-full bg-transparent border border-white/20 p-2 rounded" required>
                  <option value="5">5</option>
                  <option value="4">4</option>
                  <option value="3">3</option>
                  <option value="2">2</option>
                  <option value="1">1</option>
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label" style={{ display: 'block', marginBottom: '8px' }}>Comentario</label>
                <textarea name="text" className="form-control w-full bg-transparent border border-white/20 p-2 rounded" rows={4} required></textarea>
              </div>

              <button className="btn btn-product-primary" type="submit">
                Enviar reseña
              </button>
            </Form>
          </div>
        ) : (
          <p className="section-text mt-4">
            <Link to="/login" className="text-decoration-none" style={{ color: 'var(--dorado)' }}>Inicia sesión</Link> para escribir una reseña.
          </p>
        )}
      </section>
    </main>
  );
}
