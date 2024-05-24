package chatbidireccional;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

//Servidor para un chat entre múltiples usuarios
public class Server {
	
	 //Puerto donde escucha el servidor
  private static final int PUERTO = 6262;
  
  //Mapa que almacena los usuarios conectados y sus canales de escritura
  private static Map<String, PrintWriter> usuarios = new HashMap<>();

  public static void main(String[] args) {
  	 try {
           // Inicia el servidor en el puerto especificado
           ServerSocket servidor = new ServerSocket(PUERTO);
           System.out.println("Servidor iniciado y contestando OK");

           // Bucle infinito para aceptar conexiones de clientes
           while (true) {
               // Acepta una conexión de un cliente
               Socket usuario = servidor.accept();
               System.out.println("Cliente con la IP " + usuario.getInetAddress().getHostName() + " conectado.");

               // Crea los canales de entrada y salida para el cliente
               BufferedReader entrada = new BufferedReader(new InputStreamReader(usuario.getInputStream()));
               PrintWriter salida = new PrintWriter(usuario.getOutputStream(), true);

               // Lee el nombre de usuario del cliente
               String nombreUsuario = entrada.readLine();
               // Comprueba si el nombre de usuario es válido
               if (nombreUsuario == null || nombreUsuario.isEmpty() || usuarios.containsKey(nombreUsuario)) {
                   salida.println("Server: Nombre de usuario no válido. Debes proporcionar un nombre único.");
                   usuario.close();
                   continue;
               }

               // Añade el usuario a la lista de usuarios conectados
               usuarios.put(nombreUsuario, salida);
               System.out.println("Usuario " + nombreUsuario + " conectado");

               // Informa a los demás usuarios de la conexión del nuevo usuario
               for (PrintWriter s : usuarios.values()) {
                   if (s != salida) {
                       s.println("Server: " + nombreUsuario + " se ha conectado");
                   }
               }

               // Crea un nuevo hilo para manejar la comunicación con este cliente
               Thread thread = new Thread(new ManejadorCliente(usuario, entrada, salida, nombreUsuario));
               thread.start();
           }
       } catch (IOException e) {
           System.out.println("Error al iniciar el servidor: " + e.getMessage());
       }
   }

   // Clase interna para manejar la comunicación con un cliente específico
   static class ManejadorCliente implements Runnable {
       private Socket socket;
       private BufferedReader entrada;
       private PrintWriter salida;
       private String nombreUsuario;

       // Constructor de la clase ManejadorCliente
       public ManejadorCliente(Socket socket, BufferedReader entrada, PrintWriter salida, String nombreUsuario) {
           this.socket = socket;
           this.entrada = entrada;
           this.salida = salida;
           this.nombreUsuario = nombreUsuario;
       }

       @Override
       public void run() {
           try {
               // Crea una lista de usuarios disponibles
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

               // Solicita al cliente que elija con quién desea chatear
               salida.println("Server: Elige con quién deseas chatear. \nEscribe el nombre de usuario de tu destinatario:\n" + listaUsuarios);
               String destinatario = entrada.readLine();

               // Comprueba si el destinatario existe
               if (!usuarios.containsKey(destinatario)) {
                   // Si el destinatario no existe, cierra la conexión
                   salida.println("Server: El usuario " + destinatario + " no existe o no está conectado. Chat cerrado.");
                   socket.close();
                   System.out.println("Usuario " + nombreUsuario + " desconectado");
                   return;
               }

               // Confirma la conexión con el destinatario
               salida.println("Server: Conectado con " + destinatario + ". Puedes empezar a chatear.");

               String mensaje;
               while (true) {
                   // Lee los mensajes del cliente
                   if ((mensaje = entrada.readLine()) != null) {
                       // Si el cliente envía "Chao", cierra la conexión
                       if ("Chao".equals(mensaje) || "chao".equals(mensaje)) {
                           // Notifica al destinatario que el usuario se ha desconectado
                           usuarios.get(destinatario).println("Server: " + nombreUsuario + " se ha desconectado");
                           usuarios.remove(nombreUsuario);
                           socket.close();
                           System.out.println("Usuario " + nombreUsuario + " desconectado");
                           break;
                       } else {
                           // Reenvía el mensaje al destinatario
                           usuarios.get(destinatario).println(nombreUsuario + ": " + mensaje);
                       }
                   }
               }
           } catch (IOException e) {
               // Maneja cualquier error de E/S que pueda ocurrir
               System.out.println("Error de E/S con el cliente: " + e.getMessage());
           }
       }
       
   }
   
}