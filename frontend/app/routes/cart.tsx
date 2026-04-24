import { useLoaderData, Link, useActionData, Form, useNavigate, useSubmit } from "react-router";
import { useEffect, useState } from "react";
import { useAuthStore } from "../store/authStore";
import { useCartStore } from "../store/cartStore";
import { API_BASE_URL } from "../config";

export async function addToCart(productId: number | string, quantity: number = 1) {
    const formData = new FormData();
    formData.append("productId", productId.toString());
    formData.append("quantity", quantity.toString());

    const response = await fetch(`${API_BASE_URL}/api/v1/cart/items`, {
        method: "POST",
        credentials: "include",
        body: formData,
    });

    if (!response.ok) {
        throw new Error("Error al añadir al carrito");
    }

    return await response.json();
}

interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
  totalPrice: number;
  productImageUrl?: string;
}

interface CartSummary {
  items: CartItem[];
  subtotal: number;
  tax: number;
  total: number;
  itemCount: number;
  totalUnits: number;
}

//For making petitions at backend

export async function clientLoader({ request }: { request: Request }) {
  // In a real app, we'd handle cookies for SSR auth.
  // For now, we'll try to fetch, expecting the browser to send HttpOnly cookies.
  const response = await fetch("/api/v1/cart", {
    headers: { "Content-Type": "application/json" },
    credentials: "include"
  });

  if (response.status === 401) return { isUnauthorized: true };
  if (!response.ok) return { cart: null };

  const apiCart = await response.json();

  const parsePrice = (priceStr: any): number => {
    if (typeof priceStr === 'number') return priceStr;
    return Number(priceStr?.toString().replace('€', '').trim()) || 0;
  };

  const cart: CartSummary = {
    items: apiCart.items.map((item: any) => ({
      id: item.id,
      productId: item.productId,
      productName: item.name,
      productPrice: typeof item.unitPrice === 'string' ? parsePrice(item.unitPrice) : Number(item.unitPrice),
      quantity: item.quantity,
      totalPrice: typeof item.lineTotal === 'string' ? parsePrice(item.lineTotal) : Number(item.lineTotal),
      productImageUrl: item.imageUrl,
    })),
    subtotal: parsePrice(apiCart.subtotal),
    tax: parsePrice(apiCart.tax),
    total: parsePrice(apiCart.total),
    itemCount: apiCart.itemCount,
    totalUnits: apiCart.totalUnits || 0,
  };

  return { cart };
}

export async function clientAction({ request }: { request: Request }) {
  const formData = await request.formData();
  const intent = formData.get("intent");
  const itemId = formData.get("itemId");
  const quantity = formData.get("quantity");

  if (intent === "update") {
    const res = await fetch(`/api/v1/cart/items/${itemId}?quantity=${quantity}`, { method: "PUT", credentials: "include" });
    return { success: res.ok };
  }
  if (intent === "delete") {
    const res = await fetch(`/api/v1/cart/items/${itemId}`, { method: "DELETE", credentials: "include" });
    return { success: res.ok };
  }
  if (intent === "checkout") {
    const res = await fetch("/api/v1/cart/payments?paymentMethod=CARD", { method: "POST", credentials: "include" });
    return { success: res.ok, checkout: true };
  }
  return null;
}

export default function Cart() {
  const { cart, isUnauthorized } = useLoaderData<typeof clientLoader>();
  const navigate = useNavigate();
  const isLogged = useAuthStore(state => state.isLogged);
  const setItemCount = useCartStore(state => state.setItemCount);
  const submit = useSubmit();
  const [paymentMethod, setPaymentMethod] = useState("PAYPAL");
  const [isProcessing, setIsProcessing] = useState(false);
  const [checkoutMessage, setCheckoutMessage] = useState("");

  // Sincronizar el contador global con los datos que acabamos de cargar en esta página
  useEffect(() => {
    if (cart) {
      setItemCount(cart.totalUnits);
    }
  }, [cart, setItemCount]);

  useEffect(() => {
    if (isUnauthorized || !isLogged) navigate("/login");
  }, [isUnauthorized, isLogged, navigate]);

  const handleCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsProcessing(true);
    setCheckoutMessage("");

    try {
      const response = await fetch("/api/v1/cart/payments?paymentMethod=" + paymentMethod, {
        method: "POST",
        credentials: "include",
      });

      if (response.ok) {
        const data = await response.json();
        setCheckoutMessage(data.message || "¡Pedido realizado con éxito!");
        // Recargar la página después de un momento
        setTimeout(() => {
          window.location.href = "/orders";
        }, 2000);
      } else {
        const errorData = await response.json();
        setCheckoutMessage(errorData.message || "Error al procesar el pago");
      }
    } catch (error) {
      setCheckoutMessage("Error al procesar el pago");
    } finally {
      setIsProcessing(false);
    }
  };

  if (!cart || cart.items?.length === 0) {
    return (
      <div className="min-h-[80vh] flex flex-col items-center justify-center p-4 bg-stone-50 animate-fade-in">
        <div className="relative group mb-10">
          <i className="fas fa-shopping-cart text-8xl text-stone-200 relative z-10"></i>
        </div>
        <h2 className="text-3xl font-black text-stone-800 uppercase tracking-tight mb-4">Tu carrito está vacío</h2>
        <p className="text-stone-400 font-bold text-xs uppercase tracking-widest mb-12 italic">Parece que aún no has descubierto tu café favorito</p>
        <Link to="/menu" className="group relative h-16 px-12 bg-amber-800 rounded-3xl overflow-hidden shadow-2xl active:scale-95 transition-all flex items-center justify-center">
          <span className="relative z-10 text-white font-black uppercase tracking-[0.2em] text-xs">Ir al Menú</span>
        </Link>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-20 animate-fade-in max-w-7xl">
      <div className="flex flex-col lg:flex-row gap-16">
        <div className="flex-grow space-y-8 lg:w-2/3">
          <div className="flex items-center justify-between mb-4 px-4">
            <h1 className="text-4xl font-black text-stone-800 uppercase tracking-tight">Tu Carrito</h1>
            <span className="text-[10px] font-black uppercase tracking-widest text-stone-400 border border-stone-200 px-4 py-1.5 rounded-full bg-stone-50">{cart.itemCount} Productos</span>
          </div>
          <div className="space-y-6">
            {cart.items.map((item: CartItem) => (
              <div key={item.id} className="group bg-white rounded-[3rem] p-8 flex flex-col md:flex-row items-center gap-8 border border-stone-100 shadow-sm hover:shadow-2xl transition-all duration-500">
                <Link to={`/product/${item.productId}`} className="w-40 h-40 rounded-[2rem] overflow-hidden flex-shrink-0 shadow-lg group-hover:scale-105 transition-transform">
                  <img src={item.productImageUrl ? `${API_BASE_URL}${item.productImageUrl}` : `https://images.unsplash.com/photo-1511920170033-f8396924c348?w=500`} alt={item.productName} className="w-full h-full object-cover" />
                </Link>
                <div className="flex-grow space-y-2 text-center md:text-left">
                  <h3 className="text-2xl font-black text-stone-800 group-hover:text-amber-800 transition-colors uppercase tracking-tight">{item.productName}</h3>
                  <p className="text-stone-400 font-bold text-[10px] uppercase tracking-widest">Mokaf Specialty Coffee</p>
                  <div className="text-2xl font-black text-amber-800 pt-2 italic">{item.productPrice.toFixed(2)}€</div>
                </div>
                <div className="flex items-center gap-6">
                  <Form method="post" className="flex items-center bg-stone-50 rounded-2xl p-1.5 border border-stone-100 shadow-inner">
                    <input type="hidden" name="itemId" value={item.id} />
                    <input type="hidden" name="intent" value="update" />
                    <button name="quantity" value={Math.max(1, item.quantity - 1)} disabled={item.quantity <= 1} className="w-10 h-10 flex items-center justify-center text-stone-300 hover:text-amber-800 hover:bg-white rounded-xl transition-all disabled:opacity-30">
                      <i className="fas fa-minus text-xs"></i>
                    </button>
                    <span className="w-10 text-center font-black text-stone-800">{item.quantity}</span>
                    <button name="quantity" value={item.quantity + 1} className="w-10 h-10 flex items-center justify-center text-stone-300 hover:text-amber-800 hover:bg-white rounded-xl transition-all">
                      <i className="fas fa-plus text-xs"></i>
                    </button>
                  </Form>
                  <Form method="post">
                    <input type="hidden" name="itemId" value={item.id} />
                    <input type="hidden" name="intent" value="delete" />
                    <button className="w-14 h-14 rounded-2xl bg-stone-50 border border-stone-100 text-stone-300 hover:bg-red-50 hover:text-red-500 hover:border-red-100 transition-all shadow-sm">
                      <i className="fas fa-trash-alt"></i>
                    </button>
                  </Form>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="lg:w-1/3 lg:sticky lg:top-24 h-fit">
          <div className="bg-stone-900 rounded-[3rem] p-10 md:p-14 text-white shadow-2xl space-y-10 relative overflow-hidden">
            <div className="absolute -top-10 -right-10 w-40 h-40 bg-amber-800 rounded-full blur-3xl opacity-20"></div>
            <h2 className="text-3xl font-black uppercase tracking-tight relative z-10 border-b border-stone-800 pb-8">Resumen de Pago</h2>
            <div className="space-y-6 relative z-10">
              <div className="flex justify-between items-center text-stone-400 font-bold uppercase text-[10px] tracking-widest">
                <span>Subtotal</span><span className="text-white text-lg font-black italic">{cart.subtotal.toFixed(2)}€</span>
              </div>
              <div className="flex justify-between items-center text-stone-400 font-bold uppercase text-[10px] tracking-widest">
                <span>Envío / Comisión</span><span className="text-white text-lg font-black italic">0.00€</span>
              </div>
              <div className="flex justify-between items-center text-stone-400 font-bold uppercase text-[10px] tracking-widest">
                <span>Impuestos (IVA)</span><span className="text-white text-lg font-black italic">{cart.tax.toFixed(2)}€</span>
              </div>
              <div className="pt-8 border-t border-stone-800 mt-4 flex justify-between items-end">
                <div>
                  <p className="text-stone-400 text-[10px] font-black uppercase tracking-[0.3em] mb-1">Total a Pagar</p>
                  <p className="text-5xl font-black text-amber-500 tracking-tighter italic">{cart.total.toFixed(2)}€</p>
                </div>
              </div>
            </div>

            {/* Payment Methods */}
            <div className="relative z-10 pt-4">
              <h3 className="text-sm font-black uppercase tracking-widest text-stone-400 mb-4">Método de pago</h3>
              <div className="space-y-3">
                <label className="flex items-center gap-4 p-4 rounded-2xl bg-stone-800 cursor-pointer hover:bg-stone-700 transition-all border-2 border-transparent has-[:checked]:border-amber-500">
                  <input 
                    type="radio" 
                    name="paymentMethod" 
                    value="PAYPAL" 
                    checked={paymentMethod === "PAYPAL"}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    className="w-5 h-5 accent-amber-500"
                  />
                  <i className="fab fa-paypal text-2xl text-blue-400"></i>
                  <span className="font-black uppercase text-sm tracking-wider">PayPal</span>
                </label>
                <label className="flex items-center gap-4 p-4 rounded-2xl bg-stone-800 cursor-pointer hover:bg-stone-700 transition-all border-2 border-transparent has-[:checked]:border-amber-500">
                  <input 
                    type="radio" 
                    name="paymentMethod" 
                    value="CARD" 
                    checked={paymentMethod === "CARD"}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    className="w-5 h-5 accent-amber-500"
                  />
                  <i className="fas fa-credit-card text-2xl text-amber-400"></i>
                  <span className="font-black uppercase text-sm tracking-wider">Tarjeta de Crédito</span>
                </label>
                <label className="flex items-center gap-4 p-4 rounded-2xl bg-stone-800 cursor-pointer hover:bg-stone-700 transition-all border-2 border-transparent has-[:checked]:border-amber-500">
                  <input 
                    type="radio" 
                    name="paymentMethod" 
                    value="CASH" 
                    checked={paymentMethod === "CASH"}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    className="w-5 h-5 accent-amber-500"
                  />
                  <i className="fas fa-money-bill-wave text-2xl text-green-400"></i>
                  <span className="font-black uppercase text-sm tracking-wider">Efectivo en Tienda</span>
                </label>
              </div>
            </div>

            {/* Checkout Button */}
            <button 
              onClick={handleCheckout}
              disabled={isProcessing}
              className="w-full bg-amber-800 hover:bg-amber-700 disabled:bg-stone-600 text-white py-6 rounded-3xl font-black uppercase tracking-[0.2em] text-sm shadow-xl transition-all active:scale-95 group relative z-10"
            >
              {isProcessing ? (
                <span className="flex items-center justify-center gap-3">
                  <i className="fas fa-spinner fa-spin"></i>
                  Procesando...
                </span>
              ) : (
                <>
                  <i className="fas fa-lock mr-3"></i>
                  Proceder al Pago
                </>
              )}
            </button>

            {/* Checkout Message */}
            {checkoutMessage && (
              <div className={`p-4 rounded-2xl text-center font-black text-sm uppercase tracking-wider ${checkoutMessage.includes("éxito") ? "bg-green-800" : "bg-red-800"}`}>
                {checkoutMessage}
              </div>
            )}

            {/* Security Info */}
            <div className="text-center relative z-10">
              <small className="text-stone-500">
                <i className="fas fa-shield-alt mr-2"></i>
                Pago 100% seguro - Tus datos están protegidos
              </small>
            </div>
          </div>

          {/* Delivery Info */}
          <div className="bg-white rounded-[2rem] p-6 mt-4 shadow-lg border border-stone-100">
            <h4 className="text-sm font-black uppercase tracking-widest text-stone-800 mb-4">
              <i className="fas fa-shipping-fast mr-2 text-amber-800"></i>
              Entrega Estimada
            </h4>
            <p className="text-stone-500 text-sm mb-2">
              <i className="fas fa-clock mr-2"></i>
              25-35 minutos
            </p>
            <p className="text-stone-400 text-xs">
              <i className="fas fa-info-circle mr-1"></i>
              Envío gratuito en pedidos superiores a 15€
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
