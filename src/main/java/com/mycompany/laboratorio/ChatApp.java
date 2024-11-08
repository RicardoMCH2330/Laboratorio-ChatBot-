/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.laboratorio;

import com.sun.source.tree.BreakTree;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 *
 * @author Juan Sebastian
 */
public class ChatApp extends javax.swing.JFrame {

    /**
     * Creates new form ChatApp
     */
    
    private void guardarEnHistorial() {
    
    
     if (ListaChat.getModel().getSize() > 0) {
            ListaChat.setListData(Vacio);
            Promt2 = Promt;

            // Verificar si ya existe un título en el historial
            boolean tituloOcupado = false;
            for (int i = 0; i < n; i++) {
                if (Titulo[i] != null && Titulo[i].equals(K)) {
                    tituloOcupado = true;  // Si el título ya está asignado, no se puede añadir un nuevo chat
                    break;
                }
            }

            if (!tituloOcupado) {  // Solo añadir un nuevo chat si no está ocupado
                Conver.add(Promt.clone());

                // Asignar el título a la primera posición disponible
                for (int i = 0; i < n; i++) {
                    if (Titulo[i] == null) {
                        Titulo[i] = K;
                        break;
                    }
                }

                // Actualizar el historial
                Historial.setListData(Titulo);

                // Limpiar el arreglo Promt
                for (int i = 0; i < n; i++) {
                    Promt[i] = null;
                }
            } else {
                if (G != 1 & G != 2) {
                    JOptionPane.showMessageDialog(rootPane, "El chat ya existe en el historial.", "Error", HEIGHT);
                } else {
                    Conver.add(Promt.clone());
                    for (int i = 0; i < n; i++) {
                        Promt[i] = null;
                    }
                    G=1;
                }
            }
        }
    
    }
    
   private void enviarPregunta() {
    // Verificar si el campo de texto está vacío
    if (Pregunta.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, ingrese una pregunta antes de enviar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Guardar la pregunta en el array Promt
    boolean s = false;
    int p = 0;
    for (int i = 0; i < n; i++) {
        if (Promt[i] == null) {
            if (i == 0) {
                K = Pregunta.getText();
            }
            Promt[i] = "Tu: " + Pregunta.getText();  // Guardar la pregunta en el array
            s = true;
            p = i;
            Pregunta.setText("");  // Limpiar el campo de texto después de enviar
            break;
        }
    }

    if (s) {
        // Actualizar la lista de chats (historial) para mostrar la pregunta
        ListaChat.setListData(Promt);
        
        // Procesar la respuesta de la IA
        procesarRespuesta(p);  // Llama a procesarRespuesta y pasa la posición de la pregunta
    }
}

// Método para procesar la respuesta de la IA
private void procesarRespuesta(int posicionPregunta) {
    String pregunta = Promt[posicionPregunta];  // La pregunta que se acaba de enviar
    try {
        // Enviar la pregunta a Ollama usando la clase Laboratorio
        String respuesta = Laboratorio.Chat(pregunta);  // Llamada al método Chat de Laboratorio

        // Guardar la respuesta en el array Promt, justo después de la pregunta
        for (int i = posicionPregunta + 1; i < n; i++) {  // El siguiente índice después de la pregunta
            if (Promt[i] == null) {
                Promt[i] = "IA: " + respuesta;  // Guardar la respuesta de Ollama
                break;
            }
        }

        // Actualizar la lista de chats (historial) para mostrar la pregunta y la respuesta
        ListaChat.setListData(Promt);

    } catch (IOException ex) {
        Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Ocurrió un error al conectar con Ollama.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    String[] Promt;
    public int n = 999;
    String K;
    String Titulo[] = new String[999];
    String Vacio[] = new String[0];
    String Vacio2[] = new String[999];
    ArrayList<String[]> Conver = new ArrayList<>();
    int C = 0;
    int D = 0;
    String Promt2[] = new String[999];
    String Promt3[] = new String[999];
    int H = 0;
    int G = 1;
    int y = 0;

    public static String Chat(String a) throws MalformedURLException, IOException {

        String modelName = "llama3.2:1b";
        String promptText = a;

        //url
        URL url = new URL("http://localhost:11434/api/chat");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8;");
        conn.setRequestProperty("accept", "application/json");
        conn.setDoOutput(true);

        //Mandar vainoso
        String JsonInputString = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}", modelName, promptText);
        // a a a 
        try ( OutputStream os = conn.getOutputStream()) {
            byte[] input = JsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        // e e e 
        // Verificar si la respuesta es exitosa
        int code = conn.getResponseCode();
        System.out.println("Response code: " + code);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

// Mostrar la respuesta en la consola
        System.out.println("Respuesta de Ollama: " + response.toString());

//eaeae
        JSONObject jsonresponse = new JSONObject(response.toString());
        String responseText = jsonresponse.getString("response");
        System.out.println("Respuesta: " + responseText);
        // Cerrar la conexión
        conn.disconnect();

        return responseText;

    }

    public ChatApp() {
        Promt = new String[n];
        Laboratorio Lab = new Laboratorio();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        Pregunta = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        Historial = new javax.swing.JList<>();
        Enviar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListaChat = new javax.swing.JList<>();
        NChat = new javax.swing.JButton();
        CargarChat = new javax.swing.JButton();
        GuardarChat = new javax.swing.JButton();
        Regresar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DuckAI");
        setBackground(new java.awt.Color(51, 51, 51));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\Juan Sebastian\\Documents\\NetBeansProjects\\Laboratorio\\src\\main\\java\\com\\mycompany\\laboratorio\\logo pequeño.png")); // NOI18N
        jLabel1.setText("DuckAI");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, -1));

        Pregunta.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 102)));
        Pregunta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PreguntaMouseClicked(evt);
            }
        });
        Pregunta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreguntaActionPerformed(evt);
            }
        });
        getContentPane().add(Pregunta, new org.netbeans.lib.awtextra.AbsoluteConstraints(448, 386, 427, -1));

        Historial.setBackground(new java.awt.Color(0, 0, 51));
        Historial.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " HISTORIAL ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        Historial.setForeground(new java.awt.Color(255, 255, 255));
        Historial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HistorialMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Historial);

        jScrollPane4.setViewportView(jScrollPane3);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 310, 462));

        Enviar.setBackground(new java.awt.Color(0, 0, 102));
        Enviar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Enviar.setForeground(new java.awt.Color(255, 255, 255));
        Enviar.setText("Enviar");
        Enviar.setToolTipText("");
        Enviar.setBorderPainted(false);
        Enviar.setFocusPainted(false);
        Enviar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnviarMouseClicked(evt);
            }
        });
        getContentPane().add(Enviar, new org.netbeans.lib.awtextra.AbsoluteConstraints(881, 385, -1, -1));

        ListaChat.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 153)));
        jScrollPane2.setViewportView(ListaChat);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(448, 112, 505, 261));

        NChat.setBackground(new java.awt.Color(0, 0, 102));
        NChat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        NChat.setForeground(new java.awt.Color(255, 255, 255));
        NChat.setText("Nuevo chat");
        NChat.setFocusPainted(false);
        NChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NChatMouseClicked(evt);
            }
        });
        getContentPane().add(NChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(859, 83, -1, -1));

        CargarChat.setBackground(new java.awt.Color(0, 0, 102));
        CargarChat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        CargarChat.setForeground(new java.awt.Color(255, 255, 255));
        CargarChat.setText("Cargar Chat Antiguo");
        CargarChat.setFocusPainted(false);
        CargarChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CargarChatMouseClicked(evt);
            }
        });
        getContentPane().add(CargarChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(489, 83, -1, -1));

        GuardarChat.setBackground(new java.awt.Color(0, 0, 102));
        GuardarChat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        GuardarChat.setForeground(new java.awt.Color(255, 255, 255));
        GuardarChat.setText("Guardar Chat");
        GuardarChat.setFocusPainted(false);
        GuardarChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GuardarChatMouseClicked(evt);
            }
        });
        GuardarChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarChatActionPerformed(evt);
            }
        });
        getContentPane().add(GuardarChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(737, 83, -1, -1));

        Regresar.setBackground(new java.awt.Color(0, 0, 102));
        Regresar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Regresar.setForeground(new java.awt.Color(255, 255, 255));
        Regresar.setText("Regresar");
        Regresar.setFocusPainted(false);
        Regresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RegresarMouseClicked(evt);
            }
        });
        getContentPane().add(Regresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(639, 83, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon("C:\\Users\\Juan Sebastian\\Documents\\NetBeansProjects\\Laboratorio\\src\\main\\java\\com\\mycompany\\laboratorio\\wp9365373-technical-4k-wallpapers.jpg")); // NOI18N
        jLabel2.setText("jLabel2");
        jLabel2.setAutoscrolls(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1030, 460));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        JOptionPane.showMessageDialog(rootPane, "Necesitas tener instalado Oallama llama3.2:1b", "Atencion", HEIGHT);
    }//GEN-LAST:event_formComponentShown

    private void PreguntaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreguntaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PreguntaActionPerformed

    private void EnviarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnviarMouseClicked
    enviarPregunta();
    }//GEN-LAST:event_EnviarMouseClicked

    private void NChatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NChatMouseClicked
       guardarEnHistorial();
    }//GEN-LAST:event_NChatMouseClicked

    private void HistorialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HistorialMouseClicked
        int I = Historial.getSelectedIndex();
        ListaChat.setListData(Conver.get(I));
        H = 1;
    }//GEN-LAST:event_HistorialMouseClicked

    private void CargarChatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CargarChatMouseClicked
        int I = Historial.getSelectedIndex();
        Promt3 = Promt;
        Promt = Conver.get(I);
        G=2;
        y=I;
    }//GEN-LAST:event_CargarChatMouseClicked

    private void RegresarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RegresarMouseClicked
        Promt = Promt3;
        ListaChat.setListData(Promt);
    }//GEN-LAST:event_RegresarMouseClicked

    private void PreguntaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PreguntaMouseClicked
        if (H == 1) {
            ListaChat.setListData(Promt);
            H = 0;
        }
    }//GEN-LAST:event_PreguntaMouseClicked

    private void GuardarChatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_GuardarChatMouseClicked
        if (ListaChat.getModel().getSize() > 0) {
            Promt2 = Promt;
            if (G == 1) {
                for (int i = 0; i < n; i++) {
                    if (Titulo[i] == null) {
                        Titulo[i] = K;
                        G = 2;
                        y = i;
                        break;
                    }
                }
            }
            if (G == 2) {
                Titulo[y] = K;
                G = 2;
            }
            Historial.setListData(Titulo);
        }
    }//GEN-LAST:event_GuardarChatMouseClicked

    private void GuardarChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarChatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GuardarChatActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CargarChat;
    private javax.swing.JButton Enviar;
    private javax.swing.JButton GuardarChat;
    private javax.swing.JList<String> Historial;
    private javax.swing.JList<String> ListaChat;
    private javax.swing.JButton NChat;
    private javax.swing.JTextField Pregunta;
    private javax.swing.JButton Regresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    // End of variables declaration//GEN-END:variables
}
