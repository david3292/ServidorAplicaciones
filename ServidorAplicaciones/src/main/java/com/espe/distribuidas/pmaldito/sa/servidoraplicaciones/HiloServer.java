/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.espe.distribuidas.pmaldito.sa.servidoraplicaciones;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class HiloServer extends Thread {

    private static Integer idGlobal = 0;

    private DataOutputStream output;
    private DataInputStream input;
    private Socket socket;
    private Integer id;

    public HiloServer(Socket socket) throws IOException {
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            this.id = HiloServer.idGlobal++;
            System.out.println("Conexion Establecida: "+this.idGlobal);
        } catch (IOException ex) {
            Logger.getLogger(HiloServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {
                String trama = input.readUTF();
                System.out.println("trama:"+trama);
                //String idMensaje

            } catch (IOException ex) {
                Logger.getLogger(HiloServer.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("no se pudo recibir la trama");
            }

        }
    }
    
    public void enviaTrama(String trama){
        
    }
}
