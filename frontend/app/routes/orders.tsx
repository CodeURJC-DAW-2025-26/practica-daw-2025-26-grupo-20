import { useLoaderData, Link, useNavigate } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

export async function clientLoader({ request }: { request: Request }) {
  const url = new URL(request.url);
  const page = url.searchParams.get("page") || "0";

  const response = await fetch(
    `${API_BASE_URL}/api/v1/orders?page=${page}&size=10`,
    { credentials: "include" }
  );

  if (response.status === 401) return { isUnauthorized: true };
  if (!response.ok) return { orders: { content: [] } };

  const orders = await response.json();
  return { orders };
}

export default function Orders() {
  const { orders, isUnauthorized } = useLoaderData<typeof clientLoader>();
  const { isLogged, user } = useAuthStore();
  const isAdmin = user?.role === "ADMIN";
  const navigate = useNavigate();

  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
  }, [isUnauthorized, isLogged, navigate]);

  const toEuro = (val: any): string => {
    const n = parseFloat(val);
    return isNaN(n) ? "0.00" : n.toFixed(2);
  };

  const formatDate = (dateStr: string | undefined) => {
    if (!dateStr) return "";
    try {
      const clean = dateStr.split(".")[0];
      return new Date(clean).toLocaleDateString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    } catch {
      return dateStr;
    }
  };

  const formatTime = (dateStr: string | undefined) => {
    if (!dateStr) return "";
    try {
      const clean = dateStr.split(".")[0];
      return new Date(clean).toLocaleTimeString("es-ES", {
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch {
      return "";
    }
  };

  if (!orders || !orders.content || orders.content.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center text-center px-4">
        <i className="fas fa-shopping-bag text-4xl text-stone-500 mb-4"></i>
        <h3 className="text-xl font-semibold text-stone-400 mb-2">No tienes pedidos aún</h3>
        <p className="text-stone-500 mb-6">Cuando realices tu primer pedido, aparecerá aquí</p>
        <Link
          to="/menu"
          className="px-6 py-2 bg-[#d4b88d] text-[#050404] font-semibold rounded hover:bg-[#c4a87d] transition-colors"
        >
          Ver Menú
        </Link>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-12 max-w-5xl">
      {/* Title */}
      <h2 className="text-3xl font-serif italic text-[#d4b88d] text-center mb-10">
        Historial de Pedidos
      </h2>

      {/* Orders list */}
      <div className="flex flex-col gap-6">
        {orders.content.map((order: any) => (
          <div
            key={order.id}
            className="bg-white rounded-2xl shadow-sm overflow-hidden border border-stone-200"
          >
            {/* Card Header */}
            <div className="bg-white border-b border-stone-200 px-6 py-4">
              <div className="flex flex-wrap items-center gap-4">
                <div className="flex-1 min-w-[150px]">
                  <h5 className="font-bold text-stone-800 flex items-center gap-2">
                    <i className="fas fa-receipt text-blue-500"></i>
                    Pedido #{order.id}
                  </h5>
                </div>

                <div className="flex-1 min-w-[150px]">
                  <p className="text-stone-500 text-sm flex items-center gap-2">
                    <i className="fas fa-calendar"></i>
                    <strong className="text-stone-700">Fecha:</strong> {formatDate(order.createdAt)}
                  </p>
                </div>

                <div className="flex-1 min-w-[150px]">
                  {isAdmin ? (
                    <p className="text-stone-500 text-sm flex items-center gap-2">
                      <i className="fas fa-user"></i>
                      <strong className="text-stone-700">Cliente:</strong> {order.userEmail}
                    </p>
                  ) : (
                    <p className="text-stone-500 text-sm flex items-center gap-2">
                      <i className="fas fa-clock"></i>
                      <strong className="text-stone-700">Hora:</strong> {formatTime(order.createdAt)}
                    </p>
                  )}
                </div>

                <div>
                  <span className={`inline-block text-sm font-semibold px-3 py-1 rounded-full ${
                    order.status === "PAID"
                      ? "bg-green-100 text-green-700"
                      : order.status === "CART"
                      ? "bg-yellow-100 text-yellow-700"
                      : order.status === "CANCELLED"
                      ? "bg-red-100 text-red-700"
                      : "bg-stone-100 text-stone-600"
                  }`}>
                    {order.status === "PAID"
                      ? "Completado"
                      : order.status === "CART"
                      ? "Carrito"
                      : order.status === "CANCELLED"
                      ? "Cancelado"
                      : order.status}
                  </span>
                </div>
              </div>
            </div>

            {/* Card Body */}
            <div className="px-6 py-5">
              {/* Branch */}
              <div className="mb-4">
                <h6 className="font-bold text-stone-800 mb-1 flex items-center gap-2">
                  <i className="fas fa-map-marker-alt text-red-500"></i>Sucursal
                </h6>
                <p className="text-stone-500 text-sm ml-6">{order.branchName}</p>
              </div>

              {/* Products */}
              <div className="mb-4">
                <h6 className="font-bold text-stone-800 mb-3 flex items-center gap-2">
                  <i className="fas fa-shopping-bag"></i>Productos
                </h6>
                <div className="overflow-x-auto ml-6">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="bg-stone-50 text-stone-600">
                        <th className="text-left py-2 px-3 rounded-tl font-semibold">Producto</th>
                        <th className="text-left py-2 px-3 font-semibold">Cantidad</th>
                        <th className="text-right py-2 px-3 font-semibold">Precio Unit.</th>
                        <th className="text-right py-2 px-3 rounded-tr font-semibold">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(order.items ?? []).map((item: any) => (
                        <tr key={item.id} className="border-b border-stone-100 last:border-0">
                          <td className="py-2 px-3 text-stone-700">{item.productName}</td>
                          <td className="py-2 px-3 text-stone-700">{item.quantity}</td>
                          <td className="py-2 px-3 text-right text-stone-700">{toEuro(item.finalUnitPrice)}€</td>
                          <td className="py-2 px-3 text-right text-stone-700">{toEuro(item.lineTotal)}€</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Total */}
              <div className="flex justify-end">
                <div className="bg-blue-50 rounded-xl px-6 py-3 text-right">
                  <p className="font-bold text-stone-800 text-lg">
                    <i className="fas fa-euro-sign mr-2"></i>
                    Total: {toEuro(order.totalAmount)}€
                  </p>
                </div>
              </div>

            </div>
          </div>
        ))}
      </div>
    </div>
  );
}