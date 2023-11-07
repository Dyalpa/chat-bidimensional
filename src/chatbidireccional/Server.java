package chatbidireccional;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int PUERTO = 6764;
    private static Map<String, PrintWriter> usuarios = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado y contestando OK");

            while (true) {
                Socket usuario = servidor.accept();

                BufferedReader entrada = new BufferedReader(new InputStreamReader(usuario.getInputStream()));
                PrintWriter salida = new PrintWriter(usuario.getOutputStream(), true);

                // Pedir al usuario su nombre de usuario
                String nombreUsuario = entrada.readLine();
                if (nombreUsuario == null || nombreUsuario.isEmpty() || usuarios.containsKey(nombreUsuario)) {
                    salida.println("Server: Nombre de usuario no válido. Debes proporcionar un nombre único.");
                    usuario.close();
                    continue;
                }

                usuarios.put(nombreUsuario, salida);
                System.out.println("Usuario " + nombreUsuario + " conectado");

                // Notificar a los otros usuarios que alguien se ha conectado
                for (PrintWriter s : usuarios.values()) {
                    if (s != salida) {
                        s.println("Server: " + nombreUsuario + " se ha conectado");
                    }
                }

                // Iniciar un hilo para escuchar y reenviar mensajes para este usuario
                Thread thread = new Thread(new ManejadorCliente(usuario, entrada, salida, nombreUsuario));
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    static class ManejadorCliente implements Runnable {

        private Socket socket;
        private BufferedReader entrada;
        private PrintWriter salida;
        private String nombreUsuario;

        public ManejadorCliente(Socket socket, BufferedReader entrada, PrintWriter salida, String nombreUsuario) {
            this.socket = socket;
            this.entrada = entrada;
            this.salida = salida;
            this.nombreUsuario = nombreUsuario;
        }

        @Override
        public void run() {
            try {
                String listaUsuarios = "Usuarios disponibles:\n";

                if (usuarios.size() < 2) {
                    listaUsuarios = "- No hay nadie conectado.\n";
                } else {
                    for (String usuarioDisponible : usuarios.keySet()) {
                        if (!usuarioDisponible.equals(nombreUsuario)) {
                            listaUsuarios += "- " + usuarioDisponible + "\n";
                        }
                    }
                }
                salida.println("Server: Elige con quién deseas chatear. \nEscribe el nombre de usuario de tu destinatario:\n" + listaUsuarios);
                String destinatario = entrada.readLine();

                if (!usuarios.containsKey(destinatario)) {
                    salida.println("Server: El usuario " + destinatario + " no existe o no está conectado. Chat cerrado.");
                    socket.close();
                    System.out.println("Usuario " + nombreUsuario + " desconectado");
                    return;
                }

                salida.println("Server: Conectado con " + destinatario + ". Puedes empezar a chatear.");

                String mensaje;
                while (true) {
                    if ((mensaje = entrada.readLine()) != null) {
                        if ("Chao".equals(mensaje) || "chao".equals(mensaje)) {
                            usuarios.get(destinatario).println("Server: " + nombreUsuario + " se ha desconectado");
                            usuarios.remove(nombreUsuario);
                            socket.close();
                            System.out.println("Usuario " + nombreUsuario + " desconectado");
                            break;
                        } else {
                            usuarios.get(destinatario).println(nombreUsuario + ": " + mensaje);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error de E/S con el cliente: " + e.getMessage());
            }
        }
    }
}
