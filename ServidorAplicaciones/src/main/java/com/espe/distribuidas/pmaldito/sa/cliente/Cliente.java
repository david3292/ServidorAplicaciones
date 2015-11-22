/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.espe.distribuidas.pmaldito.sa.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class Cliente {
    private static final  String IP = "127.0.0.1";
    DataInputStream input = null;
    DataOutputStream output = null;
    Socket comunicacion = null;
    ventana v;
    
    public void Cliente(ventana v){
        this.v = v;
    }
    
    public void conexio() throws IOException{
        comunicacion = new Socket(Cliente.IP,4228);
        input = new DataInputStream(comunicacion.getInputStream());
        output = new DataOutputStream(comunicacion.getOutputStream());
        //String trama = input.readUTF();
    }
    
    public void flujo(String trama){
        try {
            System.out.println("Mensaje desde Cliente: "+trama);
            output.writeUTF(trama);
        } catch (IOException ex) {
            System.out.println("Error...." + ex);
        }
    }
    
}
