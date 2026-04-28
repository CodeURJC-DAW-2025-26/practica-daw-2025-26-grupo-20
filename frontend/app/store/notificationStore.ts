import { create } from 'zustand';

interface Notification {
  message: string;
  type: 'success' | 'error';
  visible: boolean;
}

interface NotificationState {
  notification: Notification;
  showNotification: (message: string, type?: 'success' | 'error') => void;
  hideNotification: () => void;
}

export const useNotificationStore = create<NotificationState>((set) => ({
  notification: { message: '', type: 'success', visible: false },
  showNotification: (message, type = 'success') => {
    set({ notification: { message, type, visible: true } });
    // Hide after 3 seconds
    setTimeout(() => {
      set((state) => ({ 
        notification: { ...state.notification, visible: false } 
      }));
    }, 3000);
  },
  hideNotification: () => set((state) => ({ 
    notification: { ...state.notification, visible: false } 
  })),
}));
