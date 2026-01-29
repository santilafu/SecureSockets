
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * SERVIDOR SSL - BANCA ONLINE
 * Escucha peticiones cifradas y realiza conversiones de divisa.
 */
public class ServidorSSL {

    public static void main(String[] args) throws IOException {

        // CONFIGURACIÓN DE SEGURIDAD (PROPIEDADES DEL SISTEMA)
        // Indicamos dónde está el "Almacén de Llaves" (KeyStore) que hemos creado con keytool.
        // Contiene la Clave Privada del servidor.
        System.setProperty("javax.net.ssl.keyStore", "almacen_banco.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        //  INICIO DEL SERVIDOR SSL
        // Utilizamos la factoría SSL para crear sockets seguros en lugar de normales
        SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        // Abrimos el puerto 5555
        SSLServerSocket servidor = (SSLServerSocket) sfact.createServerSocket(5555);

        System.out.println("=== 🏦 SERVIDOR BANCO ACTIVO (SSL) ===");
        System.out.println("🔒 Esperando clientes en puerto 5555...");

        // Bucle infinito para atender a múltiples clientes secuencialmente
        while (true) {
            SSLSocket clienteConectado = null;
            try {
                //ACEPTAR CONEXIÓN (Handshake SSL ocurre aquí automáticamente)
                clienteConectado = (SSLSocket) servidor.accept();
                System.out.println("\n-> Cliente conectado desde: " + clienteConectado.getInetAddress());

                // CANALES DE COMUNICACIÓN (ENTRADA / SALIDA)
                // Flujo de entrada: Para leer los Euros que manda el cliente
                DataInputStream flujoEntrada = new DataInputStream(clienteConectado.getInputStream());
                // Flujo de salida: Para enviar la respuesta convertida
                DataOutputStream flujoSalida = new DataOutputStream(clienteConectado.getOutputStream());

                // LÓGICA DE NEGOCIO (Conversión)
                // Leemos la cantidad (Double)
                double euros = flujoEntrada.readDouble();
                System.out.println("   Solicitud recibida: " + euros + " €");

                // Tipos de cambio fijos (Simulación)
                double tasaDolar = 1.09; // 1€ = 1.09$
                double tasaLibra = 0.85; // 1€ = 0.85£

                double dolares = euros * tasaDolar;
                double libras = euros * tasaLibra;

                // Preparamos el mensaje de respuesta
                String respuesta = String.format("💰 RESULTADO: %.2f € son %.2f $ (USD) y %.2f £ (GBP)",
                        euros, dolares, libras);

                // Enviamos la respuesta al cliente
                flujoSalida.writeUTF(respuesta);
                System.out.println("   Respuesta enviada. Cerrando conexión.");

                //CERRAR FLUJOS
                flujoEntrada.close();
                flujoSalida.close();
                clienteConectado.close();

            } catch (IOException e) {
                System.err.println("Error con un cliente: " + e.getMessage());
            }
        }
    }
}