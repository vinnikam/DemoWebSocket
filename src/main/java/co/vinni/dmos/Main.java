package co.vinni.dmos;

import co.vinni.dmos.recursos.ServerWS;
import org.glassfish.tyrus.server.Server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        // Configuramos el servidor: Host, Puerto, ContextPath y la clase Endpoint
        Server server = new Server("localhost", 8021, "/ws", null, ServerWS.class);

        try {
            server.start();
            System.out.println("\n🚀 Servidor WebSocket de Redes corriendo en: ws://localhost:8080/ws/network-monitor");
            System.out.println("Presiona ENTER para detener el servidor...\n");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
            System.out.println("🛑 Servidor detenido.");
        }
    }
}
