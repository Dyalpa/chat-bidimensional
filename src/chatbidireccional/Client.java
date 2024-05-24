package chatbidireccional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends javax.swing.JFrame {

    public Client() {
        initComponents(); // Inicializa los componentes de la interfaz gráfica
    }

    private static Socket socket; // Socket para la conexión con el servidor
    private static BufferedReader entrada; // Canal de entrada de datos
    private static PrintWriter salida; // Canal de salida de datos
    private static String nombreUsuario; // Nombre del usuario

    private static void ConnectSocket(String ip, Integer port) {
        try {
            // Establece una conexión con el servidor en la dirección IP y el puerto especificados
            socket = new Socket(ip, port);
            System.out.println("Conectado al servidor");

            // Crea los canales de entrada y salida
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            // Inicia un hilo para escuchar los mensajes del servidor
            startServerListenerThread();
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private static void startServerListenerThread() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mensajeServidor;
                    // Bucle que escucha los mensajes del servidor
                    while ((mensajeServidor = entrada.readLine()) != null) {
                        System.out.println(mensajeServidor);
                        // Añade el mensaje del servidor al área de texto del chat
                        chat_textarea.append(mensajeServidor + "\n");
                        // Si el servidor envía "Chat cerrado", rompe el bucle
                        if ("Chat cerrado".equals(mensajeServidor)) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error en la conexión: " + e.getMessage());
                }
            }
        });

        hilo.start(); // Inicia el hilo
    }

    private static void SendName(String name) {
        salida.println(name); // Envía el nombre del usuario al servidor
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Aquí se crean todos los componentes de la interfaz gráfica (etiquetas, campos de texto, botones, etc.)
        // y se configuran sus propiedades y comportamientos.
        // También se define el layout de la interfaz gráfica.

        ip_label = new javax.swing.JLabel();
        port_label = new javax.swing.JLabel();
        name_label = new javax.swing.JLabel();
        ip_textfield = new javax.swing.JTextField();
        port_textfield = new javax.swing.JTextField();
        name_textfield = new javax.swing.JTextField();
        cancel_button = new javax.swing.JButton();
        accept_button = new javax.swing.JButton();
        chat_scrollpane = new javax.swing.JScrollPane();
        chat_textarea = new javax.swing.JTextArea();
        message_textfield = new javax.swing.JTextField();
        send_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ip_label.setText("IP del servidor:");

        port_label.setText("Puerto de la conexión:");

        name_label.setText("Escriba su nombre:");

        port_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                port_textfieldActionPerformed(evt);
            }
        });

        cancel_button.setText("Cancelar");
        cancel_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_buttonActionPerformed(evt);
            }
        });

        accept_button.setText("Aceptar");
        accept_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accept_buttonActionPerformed(evt);
            }
        });

        chat_textarea.setEditable(false);
        chat_textarea.setColumns(20);
        chat_textarea.setRows(5);
        chat_scrollpane.setViewportView(chat_textarea);

        send_button.setText("Enviar");
        send_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chat_scrollpane)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(name_label)
                            .addComponent(ip_label)
                            .addComponent(port_label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(port_textfield, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(cancel_button)
                                .addGap(18, 18, 18)
                                .addComponent(accept_button))
                            .addComponent(ip_textfield, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(name_textfield)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(message_textfield)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(send_button)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ip_label)
                    .addComponent(ip_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(port_label)
                    .addComponent(port_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name_label)
                    .addComponent(name_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancel_button)
                    .addComponent(accept_button))
                .addGap(18, 18, 18)
                .addComponent(chat_scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(message_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(send_button))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }

    private void port_textfieldActionPerformed(java.awt.event.ActionEvent evt) {
        // Este método se activa cuando se realiza una acción en el campo de texto del puerto.
        // Actualmente está vacío, pero puedes añadir código aquí si necesitas realizar alguna acción cuando esto ocurra.
    }

    private void accept_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        // Este método se activa cuando se presiona el botón Aceptar.
        // Recoge la dirección IP, el puerto y el nombre de usuario de los campos de texto correspondientes,
        // luego llama a los métodos ConnectSocket y SendName para establecer la conexión con el servidor y enviar el nombre de usuario.
        String ip = ip_textfield.getText();
        Integer port = Integer.parseInt(port_textfield.getText());
        nombreUsuario = name_textfield.getText();

        ConnectSocket(ip, port);
        SendName(nombreUsuario);
    }

    private void send_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        // Este método se activa cuando se presiona el botón Enviar.
        // Recoge el mensaje del campo de texto del mensaje y lo envía al servidor.
        // Si el mensaje es "chao" o "Chao", cierra la conexión con el servidor y cierra la ventana del chat.
        String mensaje = message_textfield.getText();

        if ("chao".equals(mensaje) || "Chao".equals(mensaje)) {
            try {
                salida.println(mensaje);
                socket.close();

                dispose();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        salida.println(mensaje);
        chat_textarea.append(nombreUsuario + ": " + mensaje + "\n");
        message_textfield.setText("");
    }

    private void cancel_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        // Este método se activa cuando se presiona el botón Cancelar.
        // Cierra la conexión con el servidor y cierra la ventana del chat.
        try {
            socket.close();

            dispose();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        // Este es el punto de entrada de la aplicación.
        // Configura el aspecto de la interfaz gráfica de usuario y muestra la ventana del chat.
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

 // Declaración de los componentes de la interfaz gráfica de usuario (GUI)
    private javax.swing.JButton accept_button; // Botón "Aceptar"
    private javax.swing.JButton cancel_button; // Botón "Cancelar"
    private javax.swing.JScrollPane chat_scrollpane; // Panel de desplazamiento para el área de texto del chat
    private static javax.swing.JTextArea chat_textarea; // Área de texto para los mensajes del chat
    private javax.swing.JLabel ip_label; // Etiqueta para la dirección IP del servidor
    private javax.swing.JTextField ip_textfield; // Campo de texto para la dirección IP del servidor
    private javax.swing.JTextField message_textfield; // Campo de texto para los mensajes del usuario
    private javax.swing.JLabel name_label; // Etiqueta para el nombre de usuario
    private static javax.swing.JTextField name_textfield; // Campo de texto para el nombre de usuario
    private javax.swing.JLabel port_label; // Etiqueta para el puerto del servidor
    private javax.swing.JTextField port_textfield; // Campo de texto para el puerto del servidor
    private javax.swing.JButton send_button; // Botón "Enviar"
    
    }
