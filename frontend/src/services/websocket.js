import SockJS from 'sockjs-client/dist/sockjs';
import { Client } from '@stomp/stompjs';
import { useNotificationStore } from '../store/notificationStore';
import { useAuthStore } from '../store/authStore';

let stompClient = null;

export const getWebSocketUrl = () => {
  // In development, Vite proxy doesn't work for WebSocket, so use explicit backend URL
  // In production (Docker), use the environment variable or same origin
  const wsBaseUrl = import.meta.env.VITE_WS_URL || 
    (import.meta.env.DEV ? 'http://localhost:8082' : window.location.origin);
  return wsBaseUrl + '/ws';
};

export const connectWebSocket = () => {
  const { token, user } = useAuthStore.getState();
  
  if (!token || !user) {
    console.log('WebSocket: No token or user, skipping connection');
    return;
  }

  if (stompClient && stompClient.connected) {
    console.log('WebSocket: Already connected');
    return;
  }

  const wsUrl = getWebSocketUrl();
  console.log('WebSocket: Connecting to', wsUrl);

  stompClient = new Client({
    webSocketFactory: () => new SockJS(wsUrl),
    debug: (str) => {
      if (import.meta.env.DEV) {
        console.log('STOMP:', str);
      }
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  stompClient.onConnect = () => {
    console.log('WebSocket: Connected');
    useNotificationStore.getState().setConnected(true);
    
    // Subscribe to user-specific notifications
    stompClient.subscribe(`/user/${user.id}/queue/notifications`, (message) => {
      try {
        const notification = JSON.parse(message.body);
        console.log('WebSocket: Received notification', notification);
        useNotificationStore.getState().addNotification(notification);
      } catch (error) {
        console.error('WebSocket: Error parsing notification', error);
      }
    });
  };

  stompClient.onDisconnect = () => {
    console.log('WebSocket: Disconnected');
    useNotificationStore.getState().setConnected(false);
  };

  stompClient.onStompError = (frame) => {
    console.error('WebSocket: STOMP error', frame.headers['message'], frame.body);
  };

  stompClient.activate();
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
    useNotificationStore.getState().setConnected(false);
    console.log('WebSocket: Disconnected manually');
  }
};

export const isWebSocketConnected = () => {
  return stompClient && stompClient.connected;
};
