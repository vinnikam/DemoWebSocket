package co.vinni.dmos.cliente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;

/**
 * @author Vinni
 * Cliente para web socket usando Jakarta
 */
public class AppCliente {
    public static void main(String[] args) {
        String serverUri = "ws://localhost:8021/ws/network-monitor";
        System.out.println("🔄 Conectando al servidor de monitoreo en: " + serverUri);

        HttpClient client = HttpClient.newHttpClient();

        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(serverUri), new WebSocketListener())
                .join();

        System.out.println("✅ Conexión establecida con éxito.");
        System.out.println("Escribe mensajes para enviar a la red (escribe 'salir' para terminar):");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if ("salir".equalsIgnoreCase(input)) {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Cliente cerró sesión de forma voluntaria").join();
                break;
            }

            webSocket.sendText(input, true);
        }

        System.out.println("🛑 Cliente desconectado.");
        scanner.close();
    }

    private static class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("📡 [CLIENTE] Canal de WebSocket abierto.");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("\n📥 [Mensaje del Servidor]: " + data);

            // Importante: solicita el siguiente mensaje para mantener la escucha activa
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("❌ [CLIENTE] El servidor cerró la conexión. Código: " + statusCode + " | Razón: " + reason);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("⚠️ [CLIENTE] Error detectado en el canal: " + error.getMessage());
        }
    }
}
