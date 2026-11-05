package co.vinni.dmos.recursos;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/network-monitor")
public class ServerWS {
    // Almacena las sesiones activas de forma segura para hilos
    private static final Set<Session> clienSessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        clienSessions.add(session);
        System.out.println("🔌 [RED] Nuevo nodo de monitoreo conectado: " + session.getId());
        try {
            session.getBasicRemote().sendText("CONEXIÓN ESTABLECIDA: Escuchando tráfico de red...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("📥 [Paquete Recibido] de " + session.getId() + ": " + message);

        // Responder al cliente de forma directa (Echo / Ping-Pong)
        if ("PING".equalsIgnoreCase(message)) {
            try {
                session.getBasicRemote().sendText("PONG (Latencia: 1ms)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Difundir una alerta de red a todos los nodos conectados (Broadcast)
        synchronized (clienSessions) {
            for (Session client : clienSessions) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendText("📢 [ALERTA DE TRÁFICO]: " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        clienSessions.remove(session);
        System.out.println("❌ [RED] Nodo desconectado: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("⚠️ Error en el nodo " + session.getId() + ": " + throwable.getMessage());
    }
}
