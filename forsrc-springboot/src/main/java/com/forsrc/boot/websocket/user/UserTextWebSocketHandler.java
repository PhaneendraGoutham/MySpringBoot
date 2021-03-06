package com.forsrc.boot.websocket.user;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class UserTextWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(String.format("--> UserTextWebSocketHandler afterConnectionEstablished() --> %s", session));
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println(String.format("--> UserTextWebSocketHandler handleTextMessage() --> %s", message));
        sessions.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(String.format("--> UserTextWebSocketHandler afterConnectionClosed() --> %s", session));
        sessions.remove(session);
    }

    public void broadcastMessage(TextMessage message) throws IOException {
        System.out.println(String.format("--> UserTextWebSocketHandler broadcastMessage() --> %s", message));
        sessions.sendMessage(message);
    }

    public static class WebSocketSessions {

        Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

        public void add(WebSocketSession session) {
            sessions.put(session.getId(), session);
        }

        public void remove(WebSocketSession session) {
            sessions.remove(session.getId());
        }

        public void sendMessage(TextMessage message) throws IOException {
            for (String s : sessions.keySet()) {
                sessions.get(s).sendMessage(message);
            }
        }
    }
}
