import { Form } from "react-router";

interface ConfirmDeleteModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  message?: string;
  intent?: string;
}

export function ConfirmDeleteModal({
  isOpen,
  onClose,
  title = "Eliminar cuenta",
  message = "¿Estás seguro de que deseas eliminar definitivamente tu cuenta? Esta acción no se puede deshacer.",
  intent = "delete"
}: ConfirmDeleteModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 px-4">
      <div className="bg-[#111] border border-[var(--dorado)]/40 rounded-2xl p-10 max-w-md w-full shadow-2xl">
        <h5 className="text-xl font-bold text-[var(--dorado)] uppercase tracking-tight mb-4">{title}</h5>
        <p className="text-stone-400 text-sm leading-relaxed mb-10">
          {message}
        </p>
        <div className="flex gap-4">
          <button
            onClick={onClose}
            className="flex-1 px-6 py-3 border border-white/10 text-stone-400 hover:text-white rounded-xl text-sm font-bold transition-all"
          >
            Cancelar
          </button>
          <Form method="post" className="flex-1">
            <input type="hidden" name="intent" value={intent} />
            <button type="submit" className="w-full px-6 py-3 bg-red-500 hover:bg-red-600 text-white rounded-xl text-sm font-bold transition-all shadow-lg">
              Sí, eliminar
            </button>
          </Form>
        </div>
      </div>
    </div>
  );
}
