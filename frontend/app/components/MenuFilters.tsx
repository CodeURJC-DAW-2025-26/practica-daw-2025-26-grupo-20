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
    <section className="menu-filters">
      {/* Category Selection */}
      <div className="category-nav">
        {categories.map((cat) => (
          <button
            key={cat.id}
            onClick={() => onCategoryChange(cat.id)}
            className={`btn-category ${activeCategory === cat.id ? 'active' : ''}`}
          >
            {cat.label}
          </button>
        ))}
      </div>

      {/* Allergen Filters */}
      <div className="allergen-filter">
        <h6 className="allergen-filter-title">
          Ocultar productos que contengan
        </h6>
        <div className="allergen-buttons">
          {allergens.map((allergen) => {
            const allergenKey = allergen.name
              .toLowerCase()
              .normalize("NFD")
              .replace(/[\u0300-\u036f]/g, "")
              .replace(/\s+/g, "-")
              .trim();
              
            const allergenColor = `var(--color-allergen-${allergenKey})`;
            const isHidden = hiddenAllergens.includes(allergen.name);

            return (
              <button 
                key={allergen.name} 
                onClick={() => onToggleAllergen(allergen.name)}
                style={{ backgroundColor: allergenColor }}
                className={`allergen-filter-btn ${isHidden ? 'active' : ''}`}
                title={`Click para excluir productos con ${allergen.name}`}
              >
                {isHidden && <i className="fas fa-ban me-1"></i>}
                {allergen.name}
              </button>
            );
          })}
        </div>
      </div>
    </section>
  );
}
