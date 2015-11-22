/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.espe.distribuidas.pmaldito.sa.servidoraplicaciones;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class Servidor {

    public static void main(String args[]) {
        try {
            System.out.println("Servidor de sockets");
            ServerSocket Server = new ServerSocket(4228);
            while (true) {
                System.out.println("aki");
                Socket client = Server.accept();
                new HiloServer(client).start();
                System.out.println("Se ha recibido una conexion");
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
