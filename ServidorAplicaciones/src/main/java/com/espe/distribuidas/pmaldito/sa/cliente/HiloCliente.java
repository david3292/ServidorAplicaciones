/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.espe.distribuidas.pmaldito.sa.cliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class HiloCliente extends Thread{
    DataInputStream entrada;

    public HiloCliente(DataInputStream entrada) {
        this.entrada = entrada;
    }
    
    public void run(){
        try {
            String trama = this.entrada.readUTF();
            System.out.println("esta es la trama: "+trama);
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}
