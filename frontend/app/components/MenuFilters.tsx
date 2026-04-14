interface Category {
  id: string;
  label: string;
}

interface Allergen {
  name: string;
}

interface MenuFiltersProps {
  categories: Category[];
  activeCategory: string;
  onCategoryChange: (id: string) => void;
  allergens: Allergen[];
  hiddenAllergens: string[];
  onToggleAllergen: (name: string) => void;
}

export default function MenuFilters({ 
  categories, 
  activeCategory, 
  onCategoryChange,
  allergens,
  hiddenAllergens,
  onToggleAllergen
}: MenuFiltersProps) {
  return (
    <section className="mb-20 bg-white/[0.02] p-10 rounded-3xl border border-white/5">
      {/* Category Selection */}
      <div className="flex flex-wrap justify-center gap-5 mb-20">
        {categories.map((cat) => (
          <button
            key={cat.id}
            onClick={() => onCategoryChange(cat.id)}
            className={`px-12 py-4 rounded-xl text-[12px] font-bold uppercase tracking-[0.25em] transition-all duration-700 ${activeCategory === cat.id ? 'bg-[#d4b88d] text-black shadow-[0_20px_50px_rgba(212,184,141,0.4)] scale-105' : 'bg-transparent border border-[#d4b88d]/30 text-stone-200 hover:text-[#d4b88d] hover:border-[#d4b88d]/60 hover:bg-white/5'}`}
          >
            {cat.label}
          </button>
        ))}
      </div>

      {/* Allergen Filters */}
      <div className="flex flex-col items-center gap-8">
        <div className="flex items-center gap-4">
          <div className="h-[1px] w-12 bg-[#d4b88d]/30"></div>
          <span className="text-[12px] text-stone-200 font-black uppercase tracking-[0.25em]">Ocultar productos que contengan</span>
          <div className="h-[1px] w-12 bg-[#d4b88d]/30"></div>
        </div>
        <div className="flex flex-wrap justify-center gap-3 max-w-5xl">
          {allergens.map((allergen) => {
            const allergenKey = allergen.name
              .toLowerCase()
              .normalize("NFD")
              .replace(/[\u0300-\u036f]/g, "")
              .replace(/\s+/g, "-")
              .trim();
              
            const allergenColor = `var(--color-allergen-${allergenKey})`;
            return (
              <button 
                key={allergen.name} 
                onClick={() => onToggleAllergen(allergen.name)}
                style={{ backgroundColor: allergenColor }}
                className={`flex items-center gap-2.5 px-7 py-3 rounded-full text-[12px] font-bold text-white transition-all duration-500 ${hiddenAllergens.includes(allergen.name) ? 'opacity-100 scale-110 shadow-[0_0_30px_rgba(255,255,255,0.1)] ring-2 ring-white/70' : 'opacity-80 hover:opacity-100 grayscale-[0.2] hover:grayscale-0'}`}
              >
                {hiddenAllergens.includes(allergen.name) && <i className="fas fa-ban text-[11px] animate-pulse"></i>}
                {allergen.name}
              </button>
            );
          })}
        </div>
      </div>
    </section>
  );
}
