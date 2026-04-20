import { useState, useEffect, useRef } from "react";
import { API_BASE_URL } from "../config";
import { ProductService, Product, Allergen } from "../services/gestionMenu.service";

export default function GestionMenu() {
  const [products, setProducts] = useState<Product[]>([]);
  const [allAllergens, setAllAllergens] = useState<Allergen[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [filter, setFilter] = useState("ALL");
  const [errorMsg, setErrorMsg] = useState("");
  const [selectedAllergens, setSelectedAllergens] = useState<number[]>([]);
  
  const formRef = useRef<HTMLFormElement>(null);

  const fetchData = async () => {
    try {
      const [prods, algns] = await Promise.all([
        ProductService.getProducts(),
        ProductService.getAllergens()
      ]);
      setProducts(prods);
      setAllAllergens(algns);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchOnlyProducts = async () => {
    try {
      const prods = await ProductService.getProducts();
      setProducts(prods);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg("");
    if (!formRef.current) return;
    
    const formData = new FormData(formRef.current);
    
    // Append allergens
    selectedAllergens.forEach((aId) => {
      formData.append("allergenIds", aId.toString());
    });
    
    // Check if image is provided when adding
    const imageFile = formData.get("imageFile") as File;
    if (!editingId && (!imageFile || imageFile.size === 0)) {
       setErrorMsg("La imagen es requerida para un producto nuevo.");
       return;
    }

    try {
      if (editingId) {
        await ProductService.updateProduct(editingId, formData);
      } else {
        await ProductService.createProduct(formData);
      }
      setShowForm(false);
      setEditingId(null);
      setSelectedAllergens([]);
      formRef.current.reset();
      fetchOnlyProducts();
    } catch (err: any) {
      setErrorMsg(err.message || "Error de red al intentar guardar.");
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas eliminar este producto?")) return;
    try {
      await ProductService.deleteProduct(id);
      fetchOnlyProducts();
    } catch (err: any) {
      alert(err.message);
      console.error(err);
    }
  };

  const handleEdit = (product: Product) => {
    setShowForm(true);
    setEditingId(product.id);
    setSelectedAllergens(product.allergens?.map(a => a.id) || []);
    setErrorMsg("");
    setTimeout(() => {
      if (!formRef.current) return;
      const elements = formRef.current.elements as any;
      elements.name.value = product.name;
      elements.category.value = product.category;
      elements.priceBase.value = product.priceBase;
      elements.description.value = product.description || "";
      elements.imageFile.value = ""; // Clear file input
    }, 0);
  };

  const handleAddNew = () => {
    setEditingId(null);
    setShowForm(!showForm);
    setSelectedAllergens([]);
    setErrorMsg("");
    if (formRef.current) formRef.current.reset();
  };

  const handleCancel = () => {
    setShowForm(false);
    setSelectedAllergens([]);
  };

  const toggleAllergen = (id: number) => {
    setSelectedAllergens(prev => 
      prev.includes(id) ? prev.filter(aId => aId !== id) : [...prev, id]
    );
  };

  const getColorForAllergen = (allergenName: string) => {
    const colors = ["#e63946", "#f4a261", "#2a9d8f", "#264653", "#e76f51", "#8ab17d"];
    let hash = 0;
    for (let i = 0; i < allergenName.length; i++) {
        hash = allergenName.charCodeAt(i) + ((hash << 5) - hash);
    }
    const colorIndex = Math.abs(hash) % colors.length;
    return colors[colorIndex];
  };

  const filteredProducts = filter === "ALL" ? products : products.filter(p => p.category === filter);

  const getCategoryTheme = (cat: string) => {
    const themes: Record<string, string> = {
      HOT: "bg-red-500/20 text-red-400 border-red-500/30",
      COLD: "bg-blue-500/20 text-blue-400 border-blue-500/30",
      BLENDED: "bg-orange-500/20 text-orange-400 border-orange-500/30",
      DESSERTS: "bg-purple-500/20 text-purple-400 border-purple-500/30",
      NON_COFFEE: "bg-green-500/20 text-green-400 border-green-500/30",
    };
    return themes[cat] || "bg-[#d4b88d]/20 text-[#d4b88d] border-[#d4b88d]/30";
  };

  const categories = [
    { id: "ALL", label: "Todos" },
    { id: "HOT", label: "Calientes" },
    { id: "COLD", label: "Fríos" },
    { id: "BLENDED", label: "Frappés" },
    { id: "DESSERTS", label: "Postres" },
    { id: "NON_COFFEE", label: "Sin Café" }
  ];

  return (
    <div className="bg-[#050404] min-h-screen text-white pb-32 pt-10 font-sans">
      <div className="container mx-auto px-4 max-w-7xl">
        
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-medium tracking-tight text-white mb-2">Gestión de Productos</h1>
            <p className="text-[#d4b88d]/60 text-sm">Administra el inventario del menú</p>
          </div>
          <button 
            onClick={handleAddNew}
            className="px-6 py-2.5 bg-[#d4b88d] text-[#050404] font-bold text-sm rounded-lg hover:bg-white transition-all flex items-center gap-2"
          >
            <i className={`fas ${showForm && !editingId ? 'fa-minus' : 'fa-plus'}`}></i>
            {showForm && !editingId ? "Ocultar Formulario" : "Nuevo Producto"}
          </button>
        </div>

        {errorMsg && (
          <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-4 rounded-xl mb-8 flex items-center gap-4 text-sm">
            <i className="fas fa-exclamation-triangle text-lg"></i>
            <span>{errorMsg}</span>
          </div>
        )}

        {/* Product Form */}
        {showForm && (
          <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl p-6 md:p-8 mb-10 transition-all">
            <h3 className="text-xl font-medium text-white mb-6">
              {editingId ? "Editar Producto" : "Agregar Nuevo Producto"}
            </h3>
            <form ref={formRef} onSubmit={handleSubmit} className="space-y-6">
              <div className="grid md:grid-cols-2 gap-6">
                
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Nombre *</label>
                  <input name="name" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="Ej: Café Mokaf Especial" />
                </div>
                
                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Categoría *</label>
                  <div className="relative">
                    <select name="category" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm appearance-none">
                      <option value="" className="bg-[#050404]" disabled selected>Seleccionar...</option>
                      <option value="HOT" className="bg-[#050404]">Caliente</option>
                      <option value="COLD" className="bg-[#050404]">Frío</option>
                      <option value="BLENDED" className="bg-[#050404]">Frappé</option>
                      <option value="DESSERTS" className="bg-[#050404]">Postres</option>
                      <option value="NON_COFFEE" className="bg-[#050404]">Sin Café</option>
                    </select>
                    <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-[#d4b88d]/60">
                      <i className="fas fa-chevron-down text-xs"></i>
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">Precio (€) *</label>
                  <input name="priceBase" type="number" step="0.01" min="0" required className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm" placeholder="0.00" />
                </div>

                <div className="space-y-2">
                  <label className="text-white text-xs font-medium ml-1">
                    Imagen {editingId ? "(Opcional: cambiar)" : "*"}
                  </label>
                  <input name="imageFile" type="file" accept="image/*" className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-[0.55rem] focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm file:mr-4 file:py-1 file:px-4 file:rounded-full file:border-0 file:text-xs file:font-semibold file:bg-[#d4b88d]/20 file:text-[#d4b88d] hover:file:bg-[#d4b88d]/30" />
                </div>
              </div>

              <div className="space-y-2">
                <label className="text-white text-xs font-medium ml-1">Descripción *</label>
                <textarea name="description" required rows={3} className="w-full bg-transparent border border-[#d4b88d]/20 rounded-lg px-4 py-3 focus:border-[#d4b88d] focus:bg-white/5 outline-none transition-all text-white text-sm resize-none" placeholder="Descripción breve..."></textarea>
              </div>

              {/* Allergens Picker */}
              <div className="space-y-2 mt-4">
                  <label className="text-white text-xs font-medium ml-1">Alérgenos (Selección múltiple)</label>
                  <div className="flex flex-wrap gap-2 p-4 border border-[#d4b88d]/20 rounded-lg bg-white/5">
                    {allAllergens.length > 0 ? (
                      allAllergens.map(a => {
                         const isSelected = selectedAllergens.includes(a.id);
                         return (
                            <span 
                               key={a.id}
                               onClick={() => toggleAllergen(a.id)}
                               style={{ 
                                 backgroundColor: getColorForAllergen(a.name),
                                 opacity: isSelected ? 1 : 0.5,
                                 transform: isSelected ? 'scale(1.05)' : 'scale(1)'
                               }}
                               className="px-4 py-1.5 rounded-full text-xs font-bold text-white cursor-pointer transition-all flex items-center"
                            >
                               {isSelected && <i className="fas fa-check mr-1.5"></i>}
                               {a.name}
                            </span>
                         )
                      })
                    ) : (
                      <p className="text-[#d4b88d]/50 text-xs py-1">No hay alérgenos registrados en el sistema.</p>
                    )}
                  </div>
                  <p className="text-[#d4b88d]/60 text-[10px] ml-1">Haz clic en los alérgenos para añadirlos o quitarlos del producto.</p>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-[#d4b88d]/10">
                <button type="button" onClick={handleCancel} className="px-6 py-2.5 bg-transparent border border-[#d4b88d]/30 text-[#d4b88d] font-bold text-sm rounded-lg hover:bg-[#d4b88d]/10 transition-all">
                  Cancelar
                </button>
                <button type="submit" className="px-6 py-2.5 bg-[#d4b88d] text-[#050404] font-bold text-sm rounded-lg hover:bg-white transition-all">
                  {editingId ? "Actualizar Producto" : "Guardar Producto"}
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Categories Bar */}
        <div className="flex flex-wrap gap-2 mb-8">
          {categories.map(cat => (
            <button
              key={cat.id}
              onClick={() => setFilter(cat.id)}
              className={`px-4 py-1.5 rounded-full text-xs font-bold transition-all ${filter === cat.id ? 'bg-[#d4b88d] text-[#050404]' : 'bg-transparent border border-[#d4b88d]/30 text-[#d4b88d] hover:bg-[#d4b88d]/10'}`}
            >
              {cat.label}
            </button>
          ))}
        </div>

        {/* Product List */}
        <div className="bg-[rgba(45,35,25,0.9)] border border-[#d4b88d]/30 rounded-2xl overflow-hidden">
          {loading ? (
             <div className="p-10 text-center text-[#d4b88d]/60"><i className="fas fa-spinner fa-spin text-2xl"></i></div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-stone-300">
                <thead className="bg-[#050404]/50 text-xs uppercase font-medium text-[#d4b88d]/80 border-b border-[#d4b88d]/20">
                  <tr>
                    <th className="px-6 py-4 rounded-tl-xl w-24">Imagen</th>
                    <th className="px-6 py-4">Nombre</th>
                    <th className="px-6 py-4">Categoría</th>
                    <th className="px-6 py-4">Precio</th>
                    <th className="px-6 py-4">Alérgenos</th>
                    <th className="px-6 py-4 text-right rounded-tr-xl">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#d4b88d]/10">
                  {filteredProducts.map((p) => (
                    <tr key={p.id} className="hover:bg-white/[0.02] transition-colors">
                      <td className="px-6 py-3">
                        <img 
                          src={p.imageId ? `${API_BASE_URL}/images/${p.imageId}` : "https://via.placeholder.com/150?text=No+Image"} 
                          alt={p.name} 
                          className="w-12 h-12 rounded-lg object-cover border border-[#d4b88d]/20"
                        />
                      </td>
                      <td className="px-6 py-4 font-medium text-white">{p.name}</td>
                      <td className="px-6 py-4">
                        <span className={`px-3 py-1 rounded-full text-[10px] uppercase font-bold border ${getCategoryTheme(p.category)}`}>
                          {p.category}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-[#d4b88d] font-medium">{p.priceBase}€</td>
                      <td className="px-6 py-4">
                        <div className="flex flex-wrap gap-1">
                          {p.allergens?.map(a => (
                            <span 
                               key={a.id} 
                               style={{ backgroundColor: getColorForAllergen(a.name) }}
                               className="px-2 py-0.5 rounded-full text-[10px] uppercase font-bold text-white border border-[#050404]"
                               title={a.name}
                            >
                              {a.name}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <button onClick={() => handleEdit(p)} className="text-[#d4b88d]/60 hover:text-[#d4b88d] transition-colors mr-4" title="Editar">
                          <i className="fas fa-edit text-lg"></i>
                        </button>
                        <button onClick={() => handleDelete(p.id)} className="text-red-400/60 hover:text-red-400 transition-colors" title="Borrar">
                          <i className="fas fa-trash text-lg"></i>
                        </button>
                      </td>
                    </tr>
                  ))}
                  {filteredProducts.length === 0 && (
                     <tr>
                       <td colSpan={6} className="px-6 py-10 text-center text-[#d4b88d]/50">
                         No hay productos que mostrar.
                       </td>
                     </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

      </div>
    </div>
  );
}
