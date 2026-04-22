import { useLoaderData, Link, Form } from "react-router";
import { useState } from "react";
import { API_BASE_URL } from "../config";

export async function clientLoader({ params }: { params: { id: string } }) {
  const [productRes, reviewRes] = await Promise.all([
    fetch(`${API_BASE_URL}/api/v1/products/${params.id}`, { credentials: "include" }),
    fetch(`${API_BASE_URL}/api/v1/products/${params.id}/reviews`, { credentials: "include" }),
  ]);

  if (!productRes.ok) throw new Response("Producto no encontrado", { status: 404 });

  const product = await productRes.json();
  const reviewsData = reviewRes.ok ? await reviewRes.json() : { content: [] };

  return { product, reviews: reviewsData.content };
}

export async function clientAction({ request, params }: { request: Request; params: { id: string } }) {
  const formData = await request.formData();
  const intent = formData.get("intent");

  if (intent === "review") {
    const data = Object.fromEntries(formData);
    const response = await fetch(`/api/v1/products/${params.id}/reviews`, {
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
  const { product, reviews } = useLoaderData<typeof clientLoader>();
  const [qty, setQty] = useState(1);

  return (
    <div className="container mx-auto px-4 py-12 animate-fade-in max-w-6xl">
      <nav className="mb-12 text-[10px] font-black uppercase tracking-[0.2em] text-stone-400">
        <Link to="/" className="hover:text-amber-800 transition-colors">Inicio</Link>
        <span className="mx-3 opacity-30">/</span>
        <Link to="/menu" className="hover:text-amber-800 transition-colors">Menú</Link>
        <span className="mx-3 opacity-30">/</span>
        <span className="text-stone-800 font-black">{product.name}</span>
      </nav>

      <div className="grid lg:grid-cols-2 gap-20 mb-32">
        <div className="relative group">
          <div className="absolute -inset-4 bg-stone-100 rounded-[3rem] -z-10 group-hover:rotate-2 group-hover:scale-105 transition-transform duration-700"></div>
          <div className="relative aspect-square overflow-hidden rounded-[2.5rem] shadow-2xl">
            <img src={product.imageUrl ? `${API_BASE_URL}${product.imageUrl}` : `https://images.unsplash.com/photo-1485808191679-5f86510681a2?w=800`} alt={product.name} className="w-full h-full object-cover transform group-hover:scale-110 transition-transform duration-1000" />
          </div>
        </div>

        <div className="flex flex-col">
          <div className="flex flex-wrap items-center gap-3 mb-8">
            <span className="bg-stone-900 text-white px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-widest">{product.category || 'Specialty'}</span>
            {product.allergens?.map((a: any) => (
              <span key={a.id} className="bg-white text-stone-400 px-4 py-1.5 rounded-full text-[10px] font-black tracking-widest border-2 border-stone-100 uppercase">
                <i className="fas fa-info-circle mr-2 text-amber-600 opacity-50"></i>{a.name}
              </span>
            ))}
          </div>
          <h1 className="text-6xl font-black text-stone-800 mb-6 leading-tight uppercase tracking-tight italic">{product.name}</h1>
          <div className="flex items-center gap-4 mb-8">
            <div className="flex text-amber-500 gap-1 pb-1">
              {[1,2,3,4,5].map(i => <i key={i} className="fas fa-star text-sm"></i>)}
            </div>
            <span className="text-stone-400 font-bold text-xs uppercase tracking-widest">{reviews.length} Reseñas</span>
          </div>
          <p className="text-xl text-stone-500 leading-relaxed mb-12 font-medium italic border-l-4 border-amber-800/20 pl-8">"{product.description}"</p>
          <div className="text-6xl font-black text-amber-800 mb-12 flex items-baseline gap-1">
            {product.priceBase.toFixed(2)}<span className="text-xl tracking-tighter">€</span>
          </div>
          <div className="space-y-8 mt-auto">
            <div className="flex items-center gap-8 border-t border-stone-100 pt-12">
              <div className="flex items-center bg-stone-50 rounded-3xl p-2 border-2 border-stone-100 shadow-inner">
                <button onClick={() => setQty(Math.max(1, qty - 1))} className="w-14 h-14 flex items-center justify-center text-stone-400 hover:text-amber-800 hover:bg-white rounded-2xl transition-all text-xl"><i className="fas fa-minus"></i></button>
                <div className="w-20 text-center flex flex-col">
                  <span className="text-2xl font-black text-stone-800 leading-none">{qty}</span>
                  <span className="text-[8px] font-black uppercase text-stone-400 tracking-widest mt-1">UDS</span>
                </div>
                <button onClick={() => setQty(qty + 1)} className="w-14 h-14 flex items-center justify-center text-stone-400 hover:text-amber-800 hover:bg-white rounded-2xl transition-all text-xl"><i className="fas fa-plus"></i></button>
              </div>
              <button className="flex-grow group/btn relative h-16 bg-amber-800 rounded-3xl overflow-hidden shadow-2xl active:scale-95 transition-all">
                <div className="absolute inset-0 bg-amber-900 transform -translate-x-full group-hover/btn:translate-x-0 transition-transform duration-500"></div>
                <div className="relative z-10 flex items-center justify-center gap-4 text-white">
                  <i className="fas fa-cart-plus text-2xl"></i>
                  <span className="font-black uppercase tracking-[0.2em] text-sm">Añadir al Carrito</span>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>

      <section className="bg-stone-50 rounded-[3rem] p-12 md:p-20 border border-stone-100 shadow-inner">
        <h2 className="text-4xl font-black text-stone-800 uppercase tracking-tight mb-16">Experiencias Mokaf</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {reviews.map((review: any) => (
            <div key={review.id} className="bg-white p-10 rounded-[2.5rem] border border-stone-100 shadow-sm hover:shadow-2xl transition-all duration-500 group">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h4 className="font-black text-stone-800 uppercase text-sm tracking-tight mb-2">{review.user?.name || 'Cliente Apasionado'}</h4>
                  <div className="flex text-amber-500 text-[10px] gap-0.5">
                    {[...Array(5)].map((_, i) => <i key={i} className={`fas fa-star ${i < review.stars ? "" : "text-stone-100"}`}></i>)}
                  </div>
                </div>
              </div>
              <p className="text-stone-500 italic leading-relaxed font-medium line-clamp-4">"{review.text}"</p>
            </div>
          ))}
          {reviews.length === 0 && (
            <div className="col-span-full text-center py-20 bg-stone-100/50 rounded-3xl border-2 border-dashed border-stone-200">
              <i className="fas fa-comment-dots text-4xl text-stone-300 mb-4 block"></i>
              <p className="text-stone-400 font-black uppercase tracking-widest text-xs">Sé el primero en compartir tu experiencia</p>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
