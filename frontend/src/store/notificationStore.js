import { create } from 'zustand';
import { notificationService } from '../services/api';

export const useNotificationStore = create((set, get) => ({
  notifications: [],
  unreadCount: 0,
  isLoading: false,
  error: null,
  isConnected: false,

  // Fetch all notifications
  fetchNotifications: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await notificationService.getAll();
      set({ notifications: response.data, isLoading: false });
    } catch (error) {
      set({ error: error.message, isLoading: false });
    }
  },

  // Fetch unread count
  fetchUnreadCount: async () => {
    try {
      const response = await notificationService.getUnreadCount();
      set({ unreadCount: response.data.count });
    } catch (error) {
      console.error('Error fetching unread count:', error);
    }
  },

  // Mark single notification as read
  markAsRead: async (id) => {
    try {
      await notificationService.markAsRead(id);
      const { notifications, unreadCount } = get();
      set({
        notifications: notifications.map(n =>
          n.id === id ? { ...n, isRead: true, readAt: new Date().toISOString() } : n
        ),
        unreadCount: Math.max(0, unreadCount - 1),
      });
    } catch (error) {
      console.error('Error marking as read:', error);
    }
  },

  // Mark all as read
  markAllAsRead: async () => {
    try {
      await notificationService.markAllAsRead();
      const { notifications } = get();
      set({
        notifications: notifications.map(n => ({ ...n, isRead: true, readAt: new Date().toISOString() })),
        unreadCount: 0,
      });
    } catch (error) {
      console.error('Error marking all as read:', error);
    }
  },

  // Delete notification
  deleteNotification: async (id) => {
    try {
      await notificationService.delete(id);
      const { notifications, unreadCount } = get();
      const notification = notifications.find(n => n.id === id);
      const newUnreadCount = notification && !notification.isRead ? unreadCount - 1 : unreadCount;
      set({
        notifications: notifications.filter(n => n.id !== id),
        unreadCount: Math.max(0, newUnreadCount),
      });
    } catch (error) {
      console.error('Error deleting notification:', error);
    }
  },

  // Add notification (from WebSocket)
  addNotification: (notification) => {
    const { notifications, unreadCount } = get();
    // Avoid duplicates
    if (!notifications.find(n => n.id === notification.id)) {
      set({
        notifications: [notification, ...notifications],
        unreadCount: notification.isRead ? unreadCount : unreadCount + 1,
      });
    }
  },

  // Set WebSocket connection status
  setConnected: (status) => set({ isConnected: status }),

  // Clear notifications on logout
  clearNotifications: () => set({ notifications: [], unreadCount: 0, isConnected: false }),
}));
