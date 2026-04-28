import { useLoaderData, useNavigate } from "react-router";
import { useEffect, useRef } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

declare global {
  var Chart: any;
}

export async function clientLoader() {
  console.log("🔵 1. Iniciando petición al dashboard...");
  const response = await fetch(`${API_BASE_URL}/api/v1/statistics/dashboard`, {
    credentials: "include"
  });

  if (response.status === 401) return { isUnauthorized: true };
  if (response.status === 403) return { isForbidden: true };
  if (!response.ok) return { stats: null };

  const stats = await response.json();
  
  if (stats.topRatedProduct?.id) {
    try {
      const productResponse = await fetch(`${API_BASE_URL}/api/v1/statistics/top-rated-product`, {
        credentials: "include"
      });
      
      if (productResponse.ok) {
        const fullProduct = await productResponse.json();
        stats.topRatedProduct = fullProduct;
      }
    } catch (error) {
      console.error("🔴 Error al obtener detalles del producto mejor valorado:", error);
    }
  } else {
    console.warn("🟡 No hay ID en topRatedProduct, no se puede hacer la segunda petición");
  }

  return { stats };
}

export default function Statistics() {
  const { stats, isUnauthorized, isForbidden } = useLoaderData<typeof clientLoader>();
  const { user, isLogged } = useAuthStore();
  const navigate = useNavigate();

  const categoryChartRef = useRef<HTMLCanvasElement>(null);
  const branchesChartRef = useRef<HTMLCanvasElement>(null);
  const categoryInstance = useRef<any>(null);
  const branchesInstance = useRef<any>(null);

  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
    else if (isForbidden || user?.role !== 'ADMIN') navigate("/menu");
  }, [isUnauthorized, isForbidden, isLogged, user, navigate]);

  useEffect(() => {
    if (!stats) return;

    if (categoryInstance.current) {
      categoryInstance.current.destroy();
      categoryInstance.current = null;
    }

    if (stats.allCategories && stats.allCategories.length > 0 && categoryChartRef.current) {
      const ctx = categoryChartRef.current.getContext("2d");
      if (ctx) {
        categoryInstance.current = new Chart(ctx, {
          type: "doughnut",
          data: {
            labels: stats.allCategories.map((c: any) => c.category || c.name),
            datasets: [{
              data: stats.allCategories.map((c: any) => c.units || 0),
              backgroundColor: stats.allCategories.map((c: any) => c.color || "#c6a87d"),
              borderColor: "#1a1a1a",
              borderWidth: 2,
              hoverOffset: 8
            }]
          },
     options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: "65%",
        plugins: { 
          legend: { 
            display: true,
            labels: {
              color: '#ffffff',      
              font: {
                size: 11,
                weight: 'normal'
              }
            }
          },
          tooltip: {
            bodyColor: '#ffffff',    // <- White tooltip
            titleColor: '#c6a87d'    // <- Golden tooltip title
          }
        }
      }
    });
  }
}

    if (branchesInstance.current) {
      branchesInstance.current.destroy();
      branchesInstance.current = null;
    }

    if (stats.allBranches && stats.allBranches.length > 0 && branchesChartRef.current) {
      const ctx = branchesChartRef.current.getContext("2d");
      if (ctx) {
        branchesInstance.current = new Chart(ctx, {
          type: "bar",
          data: {
            labels: stats.allBranches.map((b: any) => b.name),
            datasets: [{
              label: "Ingresos (€)",
              data: stats.allBranches.map((b: any) => b.revenue || b.totalRevenue || 0),
              backgroundColor: stats.allBranches.map((b: any) => b.color || "#c6a87d"),
              borderRadius: 8,
              borderSkipped: false
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              y: { beginAtZero: true, grid: { color: "rgba(255,255,255,0.1)" } },
              x: { ticks: { color: "#e0e0e0" } }
            }
          }
        });
      }
    }

    return () => {
      categoryInstance.current?.destroy();
      branchesInstance.current?.destroy();
    };
  }, [stats]);

  if (!stats) return null;

  console.log("=== DATOS DEL PRODUCTO MEJOR VALORADO ===");
console.log("topRatedProduct completo:", stats.topRatedProduct);
console.log("averageRatingFormatted:", stats.topRatedProduct?.averageRatingFormatted);
console.log("reviewCount:", stats.topRatedProduct?.reviewCount);
console.log("recentReviews:", stats.topRatedProduct?.recentReviews);
console.log("¿recentReviews es array?", Array.isArray(stats.topRatedProduct?.recentReviews));


  const topCategoryName = stats.topCategory?.category;
  const matchedCategory = stats.allCategories?.find((cat: any) => cat.category === topCategoryName);
  const topUnits = matchedCategory?.units ?? 0;
  const topAmountFormatted = matchedCategory?.amountFormatted ?? "0,00";
  const topOrderCount = stats.topCategory?.orderCount ?? 0;

  return (
    <div className="stats-container">
      <h1 className="stats-title">☕ Estadísticas Mokaf</h1>

      <div className="stats-grid">
        {/* ========== CARD 1: Top Rated Product ========== */}
        <div className="stats-card">
          <h2>⭐ Producto Mejor Valorado</h2>

          {stats.topRatedProduct ? (
            <>
              <div className="product-spotlight">
                <div className="product-image-frame">
                  <img
                    src={
                      (() => {
                        const img = stats.topRatedProduct?.imageUrl ||
                          stats.topRatedProduct?.imagePath ||
                          stats.topRatedProduct?.image;
                        if (!img) return "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=500";
                        if (img.startsWith("http")) return img;
                        return `${API_BASE_URL}${img}`;
                      })()
                    }
                    alt={stats.topRatedProduct.name}
                  />
                </div>
                <div className="product-details">
                  <div className="product-name">{stats.topRatedProduct.name}</div>
                  <span className="product-category">{stats.topRatedProduct.category}</span>
                </div>
              </div>

              <div className="kpi-grid">
                <div className="kpi-item">
                  <div className="kpi-label">Valoración Media</div>
                  <div className="kpi-value">
                    {stats.topRatedProduct.averageRatingFormatted || stats.topRatedProduct.averageRating?.toFixed(1) || "—"}
                    <span className="kpi-unit">⭐</span>
                  </div>
                </div>
                <div className="kpi-item">
                  <div className="kpi-label">Nº de Reseñas</div>
                  <div className="kpi-value">{stats.topRatedProduct.reviewCount || 0}</div>
                </div>
              </div>

              {/* Recent reviews */}
              {stats.topRatedProduct.recentReviews && stats.topRatedProduct.recentReviews.length > 0 && (
                <div style={{ marginTop: "1rem", borderTop: "1px solid rgba(198,168,125,0.15)", paddingTop: "0.8rem" }}>
                  <h5 style={{ color: "var(--color-mokaf-gold)", fontSize: "0.85rem", marginBottom: "0.6rem", textTransform: "uppercase", letterSpacing: "0.5px" }}>
                    <i className="fas fa-comment" style={{ marginRight: "0.4rem" }} /> Reseñas recientes
                  </h5>
                  <div style={{ display: "flex", flexDirection: "column", gap: "0.4rem" }}>
                    {stats.topRatedProduct.recentReviews.map((review: any, i: number) => (
                      <div key={i} style={{ background: "rgba(0,0,0,0.15)", borderRadius: "6px", padding: "0.5rem 0.7rem" }}>
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "0.15rem" }}>
                          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                            <span style={{ color: "var(--color-mokaf-gold)", fontSize: "0.75rem", fontWeight: 500 }}>{review.userName}</span>
                            <span style={{ color: "gold", fontSize: "0.65rem" }}>{'⭐'.repeat(review.stars)}</span>
                          </div>
                        </div>
                        <p style={{ color: "var(--beige)", fontSize: "0.75rem", margin: 0, lineHeight: 1.2, opacity: 0.9 }}>"{review.text}"</p>
                        <div style={{ textAlign: "right", color: "#e0e0e0", fontSize: "0.55rem", marginTop: "0.15rem", opacity: 0.6 }}>{review.createdAt}</div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className="no-data">📭 No hay suficientes reseñas este mes</div>
          )}
        </div>

        {/* ========== CARD 2: Sales by Category ========== */}
        <div className="stats-card">
          <h2>📊 Ventas por Categoría</h2>

          {stats.topCategory ? (
            <>
              <div className="kpi-grid">
                <div className="kpi-item">
                  <div className="kpi-label">Categoría Top</div>
                  <div className="kpi-value">{topCategoryName}</div>
                </div>
                <div className="kpi-item">
                  <div className="kpi-label">Unidades</div>
                  <div className="kpi-value">
                    {topUnits}
                    <span className="kpi-unit"> uds</span>
                  </div>
                </div>
              </div>

              <div className="summary-row">
                <span className="summary-label">Ingresos totales</span>
                <span className="summary-value">{topAmountFormatted} €</span>
              </div>

              <div className="summary-row">
                <span className="summary-label">Pedidos</span>
                <span className="summary-value">{topOrderCount}</span>
              </div>

              {stats.allCategories?.length > 0 && (
                <>
                  <h3 style={{ color: "var(--color-mokaf-gold)", fontSize: "1.2rem", fontWeight: 400, margin: "1rem 0 0.8rem" }}>
                    Distribución por categorías
                  </h3>
                  <div className="chart-container">
                    <canvas ref={categoryChartRef}></canvas>
                  </div>
                </>
              )}
            </>
          ) : (
            <div className="no-data">📭 No hay datos de categorías en los últimos 3 meses</div>
          )}
        </div>

        {/* ========== CARD 3: Featured Branch ========== */}
        <div className="stats-card">
          <h2>📍 Sucursal Destacada</h2>

          {stats.topBranch ? (
            <>
              <div style={{ textAlign: "center", marginBottom: "1rem" }}>
                <span style={{ color: "var(--color-mokaf-gold)", fontSize: "1.4rem", fontWeight: 500 }}>
                  {stats.topBranch.name}
                </span>
              </div>

              <div style={{ display: "flex", justifyContent: "space-around", marginBottom: "1rem", background: "rgba(0,0,0,0.15)", borderRadius: "10px", padding: "0.8rem" }}>
                <div style={{ textAlign: "center" }}>
                  <div style={{ color: "#e0e0e0", fontSize: "0.8rem", textTransform: "uppercase" }}>Pedidos</div>
                  <div style={{ color: "var(--color-mokaf-gold)", fontSize: "2rem", fontWeight: 700, lineHeight: 1.2 }}>
                    {stats.topBranch.totalOrders}
                  </div>
                </div>
                <div style={{ textAlign: "center" }}>
                  <div style={{ color: "#e0e0e0", fontSize: "0.8rem", textTransform: "uppercase" }}>Unidades</div>
                  <div style={{ color: "var(--color-mokaf-gold)", fontSize: "2rem", fontWeight: 700, lineHeight: 1.2 }}>
                    {stats.topBranch.totalUnits}
                  </div>
                </div>
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem", marginBottom: "1rem" }}>
                <div style={{ background: "linear-gradient(135deg, var(--cafe-medio), var(--cafe-oscuro))", borderRadius: "12px", padding: "0.8rem", border: "2px dashed var(--color-mokaf-gold)", position: "relative", overflow: "hidden", textAlign: "center" }}>
                  <div style={{ position: "absolute", top: "-15px", left: "-15px", width: "50px", height: "50px", borderRadius: "50%", background: "rgba(198,168,125,0.2)" }} />
                  <div style={{ position: "absolute", bottom: "-15px", right: "-15px", width: "50px", height: "50px", borderRadius: "50%", background: "rgba(198,168,125,0.15)" }} />
                  <div style={{ position: "relative", zIndex: 1 }}>
                    <span style={{ color: "var(--color-mokaf-gold)", fontSize: "0.7rem", textTransform: "uppercase", display: "block", marginBottom: "0.2rem" }}>Cupón</span>
                    <span style={{ color: "#fff", fontSize: "2rem", fontWeight: 700 }}>{stats.topBranch.discountPercent || 0}%</span>
                    <span style={{ background: "var(--color-mokaf-gold)", color: "var(--negro)", padding: "0.15rem 0.5rem", borderRadius: "4px", fontSize: "0.6rem", fontWeight: "bold", display: "inline-block", marginLeft: "0.3rem" }}>EXC</span>
                  </div>
                </div>

                <div style={{ background: "rgba(0,0,0,0.25)", borderRadius: "12px", padding: "0.8rem", textAlign: "center", border: "1px solid rgba(198,168,125,0.3)", display: "flex", flexDirection: "column", justifyContent: "center" }}>
                  <span style={{ color: "#e0e0e0", fontSize: "0.7rem", textTransform: "uppercase", display: "block" }}>Ingresos</span>
                  <span style={{ color: "var(--color-mokaf-gold)", fontSize: "1.6rem", fontWeight: 700 }}>{stats.topBranch.totalRevenueFormatted || stats.topBranch.totalRevenue || "0,00"}€</span>
                  <span style={{ color: "#e0e0e0", fontSize: "0.65rem" }}>{stats.topBranch.avgOrderValue || "—"} €/pedido</span>
                </div>
              </div>

              <div style={{ background: "rgba(0,0,0,0.2)", borderRadius: "10px", padding: "1rem", border: "1px solid rgba(198,168,125,0.2)", flex: 1, minHeight: 0 }}>
                <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "0.5rem" }}>
                  <i className="fas fa-store" style={{ color: "var(--color-mokaf-gold)", fontSize: "1rem" }} />
                  <span style={{ color: "var(--color-mokaf-gold)", fontSize: "0.9rem", fontWeight: 500 }}>Sobre esta sucursal</span>
                </div>
                <div style={{ color: "var(--beige)", fontSize: "0.9rem", lineHeight: 1.5, overflowY: "auto", flex: 1, paddingRight: "0.3rem", scrollbarWidth: "thin", scrollbarColor: "var(--dorado) rgba(0,0,0,0.2)" }}>
                  {stats.topBranch.description || "Sin descripción disponible para esta sucursal."}
                </div>
                <div style={{ textAlign: "right", fontSize: "0.6rem", color: "rgba(198,168,125,0.3)", marginTop: "0.3rem" }}>
                  <i className="fas fa-coffee" /> <i className="fas fa-coffee" /> <i className="fas fa-coffee" />
                </div>
              </div>
            </>
          ) : (
            <div className="no-data">📭 No hay datos de sucursales</div>
          )}
        </div>

        {/* ========== CARD 4: Branches Ranking ========== */}
        <div className="stats-card full-width-card">
          <h2>📈 Ranking de Sucursales</h2>

          {stats.allBranches?.length > 0 ? (
            <>
              <div className="chart-container" style={{ height: "300px", width: "100%" }}>
                <canvas ref={branchesChartRef}></canvas>
              </div>

              <div className="branches-grid" style={{ width: "100%" }}>
                {stats.allBranches.map((branch: any, idx: number) => (
                  <div className="branch-card" key={idx}>
                    <div
                      className="branch-color"
                      style={{ backgroundColor: branch.color || "#c6a87d" }}
                    />
                    <div className="branch-info">
                      <div className="branch-name">{branch.name}</div>
                      <div className="branch-stats">
                        <span>📦 {branch.units || branch.totalUnits || 0} uds</span>
                        <span>💰 {branch.revenueFormatted || branch.totalRevenue || "0,00"}€</span>
                      </div>
                    </div>
                    <span className="branch-badge">{branch.percentage || 0}%</span>
                  </div>
                ))}
              </div>
            </>
          ) : (
            <div className="no-data">📭 No hay datos de sucursales para mostrar</div>
          )}
        </div>
      </div>

      <div className="stats-footer">
        <a href="/" className="back-button">← Volver al Inicio</a>
      </div>
    </div>
  );
}