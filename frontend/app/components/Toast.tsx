import { useNotificationStore } from "../store/notificationStore";
import { useEffect, useState } from "react";

export default function Toast() {
  const { notification, hideNotification } = useNotificationStore();
  const [shouldRender, setShouldRender] = useState(false);

  useEffect(() => {
    if (notification.visible) {
      setShouldRender(true);
    } else {
      const timer = setTimeout(() => setShouldRender(false), 500);
      return () => clearTimeout(timer);
    }
  }, [notification.visible]);

  if (!shouldRender) return null;

  return (
    <div 
      className={`fixed top-24 right-8 z-[9999] flex items-center gap-3 px-8 py-4 rounded-full shadow-[0_10px_40px_rgba(0,0,0,0.5)] transition-all duration-500 ease-out transform ${
        notification.visible ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'
      } ${
        notification.type === 'success' ? 'bg-[#43a047]' : 'bg-[#d32f2f]'
      }`}
    >
      <div className="flex items-center gap-3">
        {notification.type === 'success' ? (
          <i className="fas fa-check text-white text-lg"></i>
        ) : (
          <i className="fas fa-exclamation-triangle text-white text-lg"></i>
        )}
        <span className="text-white font-bold tracking-tight">
          {notification.message}
        </span>
      </div>
      <button 
        onClick={hideNotification}
        className="ml-4 text-white/60 hover:text-white transition-colors"
      >
        <i className="fas fa-times"></i>
      </button>
    </div>
  );
}
