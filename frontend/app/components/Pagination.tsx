interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (newPage: number) => void;
}

export default function Pagination({ currentPage, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null;

  return (
    <div className="mt-32 flex justify-between items-center border-t border-white/10 pt-20">
      <button 
        disabled={currentPage === 0}
        onClick={() => onPageChange(currentPage - 1)}
        className="px-12 py-4 rounded-full border-2 border-[#d4b88d]/20 text-[12px] font-bold uppercase tracking-[0.3em] text-[#d4b88d] hover:bg-[#d4b88d] hover:text-black disabled:opacity-10 disabled:cursor-not-allowed transition-all duration-700"
      >
        Anterior
      </button>
      
      <div className="flex flex-col items-center gap-4">
        <span className="text-stone-400 text-[11px] font-bold uppercase tracking-[0.5em]">Vista</span>
        <span className="text-white text-2xl font-bold">Página <span className="text-[#d4b88d]">{currentPage + 1}</span> de {totalPages}</span>
      </div>

      <button 
        disabled={currentPage >= totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
        className="px-12 py-4 rounded-full border-2 border-[#d4b88d]/20 text-[12px] font-bold uppercase tracking-[0.3em] text-[#d4b88d] hover:bg-[#d4b88d] hover:text-black disabled:opacity-10 disabled:cursor-not-allowed transition-all duration-700"
      >
        Siguiente
      </button>
    </div>
  );
}
