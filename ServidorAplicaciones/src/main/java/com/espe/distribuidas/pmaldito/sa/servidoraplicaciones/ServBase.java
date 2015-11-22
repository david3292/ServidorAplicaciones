/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.espe.distribuidas.pmaldito.sa.servidoraplicaciones;

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
public class ServBase {

    private static final String IPBD = "192.168.1.103";
    DataInputStream input = null;
    DataOutputStream output = null;
    Socket comunicacion = null;

    public void conexion() {
        try {
            comunicacion = new Socket(ServBase.IPBD, 4420);
            input = new DataInputStream(comunicacion.getInputStream());
            output = new DataOutputStream(comunicacion.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServBase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error de comunicacion con la base...");
        }
    }
    
    public void flujo(String trama){
        try {
            System.out.println("Mensaje desde Cliente: "+trama);
            output.writeUTF(trama);
        } catch (IOException ex) {
            System.out.println("Error...." + ex);
        }
    }
    
    public String flujoRS(){
        String rs="";
        try {            
            rs = input.readUTF();
            System.out.println("Mensaje desde Base de datos: "+rs);
        } catch (IOException ex) {
            System.out.println("Error...." + ex);
        }
        return rs;
    }

}
