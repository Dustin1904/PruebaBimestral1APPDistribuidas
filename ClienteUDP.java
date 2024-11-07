import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClienteUDP {

    public static void main(String[] args) {

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress IP_Servidor = InetAddress.getByName("localhost");
            Scanner scanner = new Scanner(System.in);

            // Enviar un mensaje inicial al servidor para establecer la conexión
            String mensajeInicial = "Conexión inicial";
            byte[] bufferInicial = mensajeInicial.getBytes();
            DatagramPacket paqueteInicial = new DatagramPacket(bufferInicial, bufferInicial.length, IP_Servidor, 1453);
            socket.send(paqueteInicial);
            System.out.println("Mensaje inicial enviado al servidor");

            while (true) {
                System.out.println("Esperando pregunta del servidor...");
                byte[] bufferEntrada = new byte[1024];
                DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteEntrada);

                String mensajeRecibido = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
                System.out.println("Mensaje recibido: " + mensajeRecibido);

                System.out.println("Ingrese la respuesta: ");
                String mensaje = scanner.nextLine();

                byte[] bufferSalida = mensaje.getBytes();
                DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, IP_Servidor, 1453);
                socket.send(paqueteSalida);

                // Esperar la confirmación del servidor
                socket.receive(paqueteEntrada);
                String confirmacion = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
                System.out.println("Confirmación del servidor: " + confirmacion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}