import { useLoaderData } from "react-router";
import { API_BASE_URL } from "../config";

interface Branch {
  id: number;
  name: string;
  description: string;
  purchaseDiscountPercent?: number;
}

export async function clientLoader({ request }: { request: Request }) {
  try {
    const res = await fetch(`${API_BASE_URL}/api/v1/branches`, { credentials: "include" });
    if (!res.ok) return { branches: [] };
    const data = await res.json();
    const branches = Array.isArray(data) ? data : (Array.isArray(data?.content) ? data.content : []);
    return { branches };
  } catch (error) {
    console.error("Error fetching branches:", error);
    return { branches: [] };
  }
}

export default function Branches() {
  const data = useLoaderData<typeof clientLoader>();
  const branches: Branch[] = data?.branches || [];

  return (
    <main className="container my-5 pb-5">
      <div 
        className="p-4 p-md-5 rounded-4 shadow-lg mx-auto" 
        style={{ 
          backgroundColor: "transparent", 
          border: "1px solid rgba(198, 168, 125, 0.3)",
          maxWidth: "1100px" 
        }}
      >
        <div className="text-center mb-5">
          <h1 style={{ color: "var(--dorado)", fontWeight: "500", letterSpacing: "0.5px" }}>
            <i className="fas fa-store me-2" style={{ color: "var(--dorado)" }}></i>
            Nuestras Sucursales
          </h1>
          <div className="mx-auto mt-3" style={{ width: "150px", height: "1px", background: "linear-gradient(90deg, transparent, var(--dorado), transparent)" }}></div>
        </div>

        <div className="row g-4">
          {branches.map((branch) => (
            <div key={branch.id} className="col-md-6">
              <div 
                className="card h-100 p-4" 
                style={{ 
                  backgroundColor: "var(--cafe-oscuro)", 
                  border: "1px solid rgba(198, 168, 125, 0.15)",
                  borderRadius: "8px",
                  boxShadow: "0 10px 20px rgba(0,0,0,0.2)",
                  transition: "transform 0.3s ease",
                }}
                onMouseEnter={(e) => e.currentTarget.style.transform = "translateY(-5px)"}
                onMouseLeave={(e) => e.currentTarget.style.transform = "translateY(0)"}
              >
                <div className="card-body p-0 d-flex flex-column">
                  <h5 className="mb-4" style={{ color: "var(--beige)", fontWeight: "500", fontSize: "1.1rem" }}>
                    <i className="fas fa-map-marker-alt me-2" style={{ color: "#e74c3c" }}></i> 
                    {branch.name}
                  </h5>
                  
                  <div className="flex-grow-1" style={{ position: "relative" }}>
                    <p 
                      style={{ 
                        color: "var(--beige)", 
                        opacity: "0.8", 
                        fontSize: "0.9rem",
                        lineHeight: "1.6",
                        borderRight: "3px solid var(--dorado)",
                        paddingRight: "1rem",
                        margin: 0
                      }}
                    >
                      {branch.description}
                    </p>
                  </div>

                  <hr style={{ borderColor: "rgba(255,255,255,0.05)", margin: "1.5rem 0 1rem 0" }} />

                  <div className="d-flex justify-content-between align-items-center">
                    {branch.purchaseDiscountPercent ? (
                      <span 
                        className="badge rounded-pill px-3 py-2" 
                        style={{ 
                          border: "1px solid var(--dorado)", 
                          color: "var(--beige)",
                          backgroundColor: "transparent",
                          fontWeight: "normal",
                          fontSize: "0.8rem",
                          opacity: 0.9
                        }}
                      >
                        <i className="fas fa-tag me-1"></i> {branch.purchaseDiscountPercent.toFixed(2)}%
                      </span>
                    ) : (
                      <span className="badge rounded-pill px-3 py-2" style={{ visibility: "hidden" }}>hidden</span>
                    )}
                    <i className="fas fa-coffee" style={{ color: "rgba(198, 168, 125, 0.6)", fontSize: "1rem" }}></i>
                  </div>
                </div>
              </div>
            </div>
          ))}

          {branches.length === 0 && (
            <div className="col-12 text-center py-5" style={{ color: "var(--beige)", opacity: 0.7 }}>
              <i className="fas fa-store-slash fa-3x mb-3"></i>
              <p>No hay sucursales disponibles en este momento.</p>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
