
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

/**
 * CLIENTE SSL - USUARIO
 * Se conecta al servidor bancario para solicitar un cambio de divisas.
 */
public class ClienteSSL {

    public static void main(String[] args) {
        try {
            // CONFIGURACIÓN DE CONFIANZA (TRUSTSTORE)
            // Como el certificado es autofirmado (lo hemos hecho nosotros), el cliente
            // no confiaría en él por defecto.
            // Le decimos que confíe en las claves guardadas en "almacen_banco.jks".
            System.setProperty("javax.net.ssl.trustStore", "almacen_banco.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");


            // CONEXIÓN AL SERVIDOR
            System.out.println("=== 👤 CLIENTE BANCARIO SSL ===");
            System.out.println("Conectando al servidor seguro...");

            // Factoría SSL para crear el socket cliente
            SSLSocketFactory cfact = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // Conectamos a localhost (nuestra máquina) puerto 5555
            SSLSocket cliente = (SSLSocket) cfact.createSocket("localhost", 5555);

            //  INTERACCIÓN CON EL SERVIDOR
            DataOutputStream flujoSalida = new DataOutputStream(cliente.getOutputStream());
            DataInputStream flujoEntrada = new DataInputStream(cliente.getInputStream());
            Scanner scanner = new Scanner(System.in);

            // Pedimos al usuario la cantidad
            System.out.print("Introduce la cantidad en EUROS (€) a convertir: ");
            double cantidadEuros = scanner.nextDouble();

            // Enviamos el dato cifrado
            flujoSalida.writeDouble(cantidadEuros);

            // Esperamos la respuesta del servidor
            String respuestaServidor = flujoEntrada.readUTF();

            // MOSTRAR RESULTADO
            System.out.println("\n📩 RESPUESTA DEL BANCO:");
            System.out.println(respuestaServidor);

            // Cerramos conexiones
            flujoSalida.close();
            flujoEntrada.close();
            cliente.close();
            scanner.close();

        } catch (Exception e) {
            System.err.println("❌ Error de conexión SSL: " + e.getMessage());
            System.err.println("Asegúrate de que el servidor está encendido y el archivo .jks existe.");
        }
    }
}