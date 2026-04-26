import { useLoaderData, Link, Form, useActionData, useNavigation } from "react-router";
import { useState, useEffect } from "react";
import { API_BASE_URL } from "../config";
import { useCartStore } from "../store/cartStore";
import { useNotificationStore } from "../store/notificationStore";

export async function clientLoader({ params }: { params: { id: string } }) {
  const [productRes, reviewRes, userRes] = await Promise.all([
    fetch(`${API_BASE_URL}/api/v1/products/${params.id}`, { credentials: "include" }),
    fetch(`${API_BASE_URL}/api/v1/products/${params.id}/reviews?size=6`, { credentials: "include" }),
    fetch(`${API_BASE_URL}/api/v1/users/me`, { credentials: "include" })
  ]);

  if (!productRes.ok) throw new Response("Producto no encontrado", { status: 404 });

  const product = await productRes.json();
  const reviewsData = reviewRes.ok ? await reviewRes.json() : { content: [], last: true };
  const user = userRes.ok ? await userRes.json() : null;

  return { product, reviewsData, user };
}

export async function clientAction({ request, params }: { request: Request; params: { id: string } }) {
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
      return { success: true, message: "Añadido al carrito" };
    } catch (e) {
      return { error: "Error de red." };
    }
  }
  return null;
}

export default function ProductDetail() {
  const { product, reviewsData: initialReviewsData, user } = useLoaderData<typeof clientLoader>();
  const actionData = useActionData<typeof clientAction>();
   const navigation = useNavigation();
  const isSubmittingReview = navigation.formData?.get("intent") === "review";
  const updateItemCount = useCartStore((state) => state.updateItemCount);
  const showNotification = useNotificationStore((state) => state.showNotification);
  
  const [reviews, setReviews] = useState(initialReviewsData.content);
  const [page, setPage] = useState(0);
  const [isLast, setIsLast] = useState(initialReviewsData.last);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [qty, setQty] = useState(1);

  // Sync state when loader refreshes data (e.g. after posting review)
  useEffect(() => {
    setReviews(initialReviewsData.content);
    setPage(0);
    setIsLast(initialReviewsData.last);
  }, [initialReviewsData]);

  const loadMore = async () => {
    if (isLoadingMore || isLast) return;
    setIsLoadingMore(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/products/${product.id}/reviews?page=${page + 1}&size=6`, { credentials: "include" });
      if (res.ok) {
        const data = await res.json();
        setReviews((prev: any) => [...prev, ...data.content]);
        setPage((prev) => prev + 1);
        setIsLast(data.last);
      }
    } catch (err) {
      console.error("Error loading more reviews:", err);
    } finally {
      setIsLoadingMore(false);
    }
  };

  // Refresh counter on successful add to cart (clientAction)
  useEffect(() => {
    if (actionData?.success && actionData?.message === "Añadido al carrito") {
      updateItemCount();
      showNotification(`${product.name} añadido al carrito!`, 'success');
    }
  }, [actionData, updateItemCount, showNotification, product.name]);

  const getProductImage = (product: any) => {
    if (product.imageUrl) {
      if (product.imageUrl.startsWith("http")) return product.imageUrl;
      return `${API_BASE_URL}${product.imageUrl}`;
    }
    return "https://via.placeholder.com/900x700?text=Producto";
  };

  const productPrice = Number(product.priceBase || 0).toFixed(2);

  return (
    <main className="legacy-container product-page">
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
                {user && (
                  <div className="joined-actions">
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

                    <Form method="post" className="ajax-cart-form">
                      <input type="hidden" name="productId" value={product.id} />
                      <input type="hidden" name="qty" value={qty} />
                      <input type="hidden" name="intent" value="cart" />
                      <button className="btn btn-product-primary" type="submit">
                        <i className="fas fa-cart-plus me-2"></i>Añadir
                      </button>
                    </Form>
                  </div>
                )}
              </div>

              <div className="product-meta mt-4">
                <div className="meta-item">
                  <i className="fas fa-truck"></i>
                  <span>Entrega 24/48h</span>
                </div>
                <div className="meta-item">
                  <i className="fas fa-shield-alt"></i>
                  <span>Pago seguro</span>
                </div>
                <div className="meta-item">
                  <i className="fas fa-undo"></i>
                  <span>14 días de devolución</span>
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

        {!isLast && (
          <div className="text-center mt-4">
            <button 
              className="btn btn-product-secondary" 
              onClick={loadMore}
              disabled={isLoadingMore}
            >
              {isLoadingMore ? (
                <>
                  <i className="fas fa-spinner fa-spin me-2"></i>Cargando...
                </>
              ) : (
                <>
                  <i className="fas fa-plus me-2"></i>Cargar más reseñas
                </>
              )}
            </button>
          </div>
        )}

        {user ? (
          <div className="mt-4">
            <h4 className="section-title">Escribe una reseña</h4>
            <Form method="post">
              <input type="hidden" name="intent" value="review" />
              <div className="mb-3">
                <label className="form-label" style={{ display: 'block', marginBottom: '8px' }}>Puntuación</label>
                <select name="stars" className="form-select w-full bg-black/20 border border-white/20 p-2 rounded text-[#D7CCC8]" required>
                  <option value="5" className="bg-[#3A2A1D]">5 Estrellas</option>
                  <option value="4" className="bg-[#3A2A1D]">4 Estrellas</option>
                  <option value="3" className="bg-[#3A2A1D]">3 Estrellas</option>
                  <option value="2" className="bg-[#3A2A1D]">2 Estrellas</option>
                  <option value="1" className="bg-[#3A2A1D]">1 Estrella</option>
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label" style={{ display: 'block', marginBottom: '8px' }}>Comentario</label>
                <textarea 
                  name="text" 
                  className="form-control w-full bg-black/20 border border-white/20 p-2 rounded text-[#D7CCC8] placeholder:text-white/20" 
                  rows={4} 
                  placeholder="Escribe tu opinión aquí..."
                  required
                ></textarea>
              </div>

              <button 
                className="btn btn-product-primary w-full md:w-auto" 
                type="submit"
                disabled={isSubmittingReview}
              >
                {isSubmittingReview ? (
                  <>
                    <i className="fas fa-spinner fa-spin me-2"></i>Enviando...
                  </>
                ) : (
                  "Enviar reseña"
                )}
              </button>

              {actionData?.error && (
                <div className="mt-3 text-red-400 text-sm">
                  <i className="fas fa-exclamation-circle me-2"></i>{actionData.error}
                </div>
              )}
              {actionData?.success && actionData?.message !== "Añadido al carrito" && (
                <div className="mt-3 text-green-400 text-sm animate-fade-in">
                  <i className="fas fa-check-circle me-2"></i>¡Reseña publicada con éxito!
                </div>
              )}
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
