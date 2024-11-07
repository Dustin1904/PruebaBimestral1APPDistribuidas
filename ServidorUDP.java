import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServidorUDP {
    private static String[] preguntas = {
            "¿En qué año el hombre llegó a la luna?",
            "¿Cuál es el rio mas largo del mundo?",
            "¿Cuantos estados tiene la materia?",
            "¿En qué país se orginó el futbol?",
            "¿En qué año inicio la primera guerra mundial?"
    };
    private static String[] respuestas = {
            "1969",
            "nilo",
            "3",
            "inglaterra",
            "1914"
    };

    private static int mensajeNumero = 1;

    public static void main(String[] args) {
        int puerto = 1453; // Usar el puerto 1453

        try {
            DatagramSocket socket = new DatagramSocket(puerto);
            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {
                System.out.println("Esperando conexiones............");
                byte[] bufferEntrada = new byte[1024];
                DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteEntrada);

                InetAddress IP_Cliente = paqueteEntrada.getAddress();
                int puerto_Cliente = paqueteEntrada.getPort();
                System.out.println("Conexión recibida de " + IP_Cliente.getHostAddress() + ":" + puerto_Cliente);

                int contador = 0;

                for (int i = 0; i < preguntas.length; i++) {
                    byte[] bufferSalida = preguntas[i].getBytes();
                    DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, IP_Cliente, puerto_Cliente);
                    socket.send(paqueteSalida);
                    System.out.println("Pregunta enviada: " + preguntas[i]);

                    socket.receive(paqueteEntrada);
                    String respuesta = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
                    System.out.println("Mensaje recibido: " + respuesta);

                    // Guardar la respuesta en un archivo de texto
                    guardarRespuestaEnArchivo(IP_Cliente, respuesta);

                    String resultado;
                    if (respuesta.equalsIgnoreCase(respuestas[i])) {
                        resultado = "Respuesta correcta";
                        contador++;
                    } else {
                        resultado = "Respuesta incorrecta, la respuesta correcta es: " + respuestas[i];
                    }

                    bufferSalida = resultado.getBytes();
                    paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, IP_Cliente, puerto_Cliente);
                    socket.send(paqueteSalida);
                    System.out.println("Confirmación enviada: " + resultado);
                }

                String resultadoFinal = "Tu puntaje es " + (contador * 4) + " de " + (preguntas.length * 4);
                byte[] bufferSalida = resultadoFinal.getBytes();
                DatagramPacket respuestaCliente = new DatagramPacket(bufferSalida, bufferSalida.length, IP_Cliente, puerto_Cliente);
                socket.send(respuestaCliente);
                System.out.println("Resultado final enviado: " + resultadoFinal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void guardarRespuestaEnArchivo(InetAddress IP_Cliente, String respuesta) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("respuestasPreguntas.txt", true))) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String fechaHora = formatter.format(date);
            writer.write("Mensaje " + mensajeNumero + ": ");
            writer.write("Fecha y hora: " + fechaHora + ", ");
            writer.write("IP Origen: " + IP_Cliente.getHostAddress() + ", ");
            writer.write("Respuesta: " + respuesta);
            writer.newLine();
            mensajeNumero++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}