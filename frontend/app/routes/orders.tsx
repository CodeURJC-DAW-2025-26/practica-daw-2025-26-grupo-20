import { useLoaderData, Link, useNavigate } from "react-router";
import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config";

export async function clientLoader({ request }: { request: Request }) {
  const url = new URL(request.url);
  const page = url.searchParams.get("page") || "0";
  
  const response = await fetch(`${API_BASE_URL}/api/v1/orders?page=${page}&size=10`, {
    credentials: "include"
  });
  
  if (response.status === 401) return { isUnauthorized: true };
  if (!response.ok) return { orders: { content: [] } };
  
  const orders = await response.json();
  return { orders };
}

export default function Orders() {
  const { orders, isUnauthorized } = useLoaderData<typeof clientLoader>();
  const { isLogged } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (isUnauthorized || !isLogged) {
      navigate("/login");
    }
  }, [isUnauthorized, isLogged, navigate]);

  if (!orders || orders.content?.length === 0) {
    return (
      <div className="min-h-[70vh] flex flex-col items-center justify-center p-4 bg-stone-50 animate-fade-in">
        <div className="w-24 h-24 bg-stone-100 rounded-[2rem] flex items-center justify-center text-stone-200 text-4xl mb-8">
           <i className="fas fa-box-open"></i>
        </div>
        <h2 className="text-2xl font-black text-stone-800 uppercase tracking-tight mb-2">No se encontraron pedidos</h2>
        <p className="text-stone-400 font-bold text-xs uppercase tracking-widest mb-10 italic">Aún no has realizado ninguna compra en Mokaf</p>
        <Link to="/menu" className="bg-amber-800 text-white px-10 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] hover:bg-amber-900 transition-all shadow-xl shadow-amber-900/20">Empezar a Comprar</Link>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-20 max-w-6xl animate-fade-in">
      <div className="flex flex-col md:flex-row justify-between items-end mb-16 gap-8">
        <div className="space-y-2">
           <h1 className="text-5xl font-black text-stone-800 uppercase tracking-tight">Historial de Pedidos</h1>
           <p className="text-stone-400 font-black text-xs uppercase tracking-[0.3em] flex items-center gap-3">
              <i className="fas fa-history text-amber-800"></i>
              Tus experiencias en Mokaf
           </p>
        </div>
        <div className="bg-stone-100 px-6 py-3 rounded-2xl border border-stone-200">
           <span className="text-[10px] font-black uppercase tracking-widest text-stone-500">Total Pedidos: <span className="text-stone-800">{orders.totalElements}</span></span>
        </div>
      </div>

      <div className="grid gap-8">
        {orders.content.map((order: any) => (
          <div key={order.id} className="group bg-white rounded-[2.5rem] p-8 md:p-10 border border-stone-100 shadow-sm hover:shadow-2xl transition-all duration-500 hover:-translate-y-1">
            <div className="flex flex-col md:flex-row justify-between gap-10">
              {/* Order Info */}
              <div className="space-y-6 flex-grow">
                <div className="flex items-center gap-4">
                   <div className="w-14 h-14 bg-stone-900 rounded-2xl flex items-center justify-center text-white text-xl shadow-lg">
                      <i className="fas fa-receipt"></i>
                   </div>
                   <div>
                      <h3 className="text-xl font-black text-stone-800 uppercase tracking-tighter">Pedido #{order.id}</h3>
                      <p className="text-[10px] font-black uppercase tracking-widest text-stone-400">{order.createdAtFormatted}</p>
                   </div>
                </div>
                
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-6">
                   <div className="space-y-1">
                      <p className="text-[8px] font-black uppercase tracking-widest text-stone-300">Estado</p>
                      <span className={`inline-block px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                         order.status === 'PAID' ? 'bg-green-100 text-green-700' : 
                         order.status === 'CART' ? 'bg-amber-100 text-amber-700' : 'bg-stone-100 text-stone-700'
                      }`}>
                         {order.status}
                      </span>
                   </div>
                   <div className="space-y-1">
                      <p className="text-[8px] font-black uppercase tracking-widest text-stone-300">Sucursal de Recogida</p>
                      <p className="text-xs font-black text-stone-700 uppercase">{order.branch?.name || 'Central'}</p>
                   </div>
                   <div className="space-y-1">
                      <p className="text-[8px] font-black uppercase tracking-widest text-stone-300">Items</p>
                      <p className="text-xs font-black text-stone-700 uppercase">{order.items?.length || 0} Productos</p>
                   </div>
                </div>
              </div>

              {/* Price & Action */}
              <div className="flex flex-col justify-between items-end border-l border-stone-100 pl-10">
                <div className="text-right">
                   <p className="text-[10px] font-black uppercase tracking-[0.3em] text-stone-300 mb-1">Monto Total</p>
                   <p className="text-4xl font-black text-amber-800 italic">{order.totalPrice.toFixed(2)}€</p>
                </div>
                <button className="bg-stone-50 hover:bg-stone-900 border border-stone-200 hover:border-stone-900 text-stone-400 hover:text-white w-14 h-14 rounded-2xl transition-all flex items-center justify-center">
                   <i className="fas fa-chevron-right"></i>
                </button>
              </div>
            </div>
            
            {/* Items Preview */}
            <div className="mt-10 pt-10 border-t border-stone-50 flex flex-wrap gap-4">
               {order.items?.slice(0, 5).map((item: any) => (
                  <div key={item.id} className="relative group/item">
                     <img 
                        src={item.productImageUrl ? `${API_BASE_URL}${item.productImageUrl}` : "https://images.unsplash.com/photo-1511920170033-f8396924c348?w=100"} 
                        alt="Product" 
                        className="w-12 h-12 rounded-xl object-cover border-2 border-white shadow-md group-hover/item:scale-110 transition-transform"
                     />
                     <div className="absolute -top-2 -right-2 bg-amber-800 text-white w-5 h-5 rounded-full flex items-center justify-center text-[8px] font-bold shadow-lg">
                        {item.quantity}
                     </div>
                  </div>
               ))}
               {order.items?.length > 5 && (
                  <div className="w-12 h-12 rounded-xl bg-stone-50 border border-stone-100 flex items-center justify-center text-[10px] font-black text-stone-400 uppercase tracking-tighter">
                     +{order.items.length - 5}
                  </div>
               )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
