/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.espe.distribuidas.pmaldito.sa.servidoraplicaciones;

import com.espe.distribuidas.pmaldito.cliente.InformacionClienteRS;
import com.espe.distribuidas.pmaldito.cliente.IngresarClienteRS;
import com.espe.distribuidas.pmaldito.factura.IngresarFacturaRQ;
import com.espe.distribuidas.pmaldito.factura.IngresarFacturaRS;
import com.espe.distribuidas.pmaldito.originador.Originador;
import com.espe.distribuidas.pmaldito.pcs.Mensaje;
import com.espe.distribuidas.pmaldito.pcs.MensajeRS;
import com.espe.distribuidas.pmaldito.producto.InformacionProductoRS;
import com.espe.distribuidas.pmaldito.protocolobdd.mensajesBDD.MensajeBDD;
import com.espe.distribuidas.pmaldito.protocolobdd.mensajesBDD.MensajeRQ;
import com.espe.distribuidas.pmaldito.protocolobdd.operaciones.ConsultarRQ;
import com.espe.distribuidas.pmaldito.protocolobdd.operaciones.InsertarRQ;
import com.espe.distribuidas.pmaldito.protocolobdd.operaciones.InsertarRS;
import com.espe.distribuidas.pmaldito.protocolobdd.operaciones.VocabularioBDD;
import com.espe.distribuidas.pmaldito.protocolobdd.seguridad.AutenticacionRQ;
import com.espe.distribuidas.pmaldito.seguridad.AutenticacionRS;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

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
                System.out.println("Esperando datos.....");
                String trama = input.readUTF();
                System.out.println("Datos recibidos.....");
                System.out.println("trama:"+trama);
                if (trama.equals("FIN")) {
                    break;
                }
                String idMensaje = trama.substring(39,49);
                System.out.println(idMensaje);
                
                switch(idMensaje){
                    case Mensaje.AUTENTIC_USER:
                        if(trama.length() == 105 && Mensaje.validaHash(trama)){
                            String usuario = trama.substring(85,95);
                            String clave = trama.substring(95,105);
                            usuario = StringUtils.stripEnd(usuario, " ");
                            clave = StringUtils.stripEnd(clave, " ");
                            AutenticacionRQ auRQ= new AutenticacionRQ();
                            auRQ.setUsuario(usuario);
                            auRQ.setClave(clave);
                            MensajeRQ mauRQ = new MensajeRQ(Originador.getOriginador(Originador.SRV_APLICACION),MensajeBDD.idMensajeAutenticacion);
                            mauRQ.setCuerpo(auRQ);
                            System.out.println("TramaAutenticacion "+mauRQ.asTexto());
                            
                            ServBase comunicacion = new ServBase();
                            comunicacion.conexion();
                            comunicacion.flujo(mauRQ.asTexto());
                            
                            String respuesta = comunicacion.flujoRS();
                            AutenticacionRS aurs = new AutenticacionRS();
                            aurs.build(respuesta);
                            MensajeRS maurs = new MensajeRS(Originador.getOriginador(Originador.SRV_APLICACION), Mensaje.AUTENTIC_USER);
                            maurs.setCuerpo(aurs);
                            output.writeUTF(maurs.asTexto());
                            System.out.println("Respuesta: " + maurs.asTexto());
                        }
                        break;
                    case Mensaje.INFO_CLIENT:
                        if(Mensaje.validaHash(trama)){
                            String idCliente = trama.substring(85);
                            idCliente = StringUtils.stripStart(idCliente, "0");
                            System.out.println("Id_Cliente:"+idCliente);
                            ConsultarRQ coninfCli = new ConsultarRQ();
                            coninfCli.setNombreTabla(Mensaje.nombreTablaCliente);
                            coninfCli.setCamposTabla("/");
                            coninfCli.setCodigoIdentificadorColumna("1");
                            coninfCli.setValorCodigoidentificadorColumna(idCliente);
                            MensajeRQ mconinfCli = new MensajeRQ(Originador.getOriginador(Originador.SRV_APLICACION), MensajeBDD.idMensajeConsultar);
                            mconinfCli.setCuerpo(coninfCli);
                            System.out.println("Trama Info CLiente "+mconinfCli.asTexto());
                            
                            ServBase comunicacion = new ServBase();
                            comunicacion.conexion();
                            comunicacion.flujo(mconinfCli.asTexto());
                            
                            String respuesta = comunicacion.flujoRS();
                            InformacionClienteRS infclRS = new InformacionClienteRS();
                            infclRS.build(respuesta);
                            MensajeRS minfclRS = new MensajeRS(Originador.getOriginador(Originador.SRV_APLICACION), Mensaje.INFO_CLIENT);
                            minfclRS.setCuerpo(infclRS);
                                                       
                            output.writeUTF(minfclRS.asTexto());
                            System.out.println("RespuestaInfCliente: " + minfclRS.asTexto());
                        }
                        break;
                    case Mensaje.INFO_FACT:
                        
                        break;
                    case Mensaje.INFO_PRODUCT:
                        if(Mensaje.validaHash(trama)){
                            String idProducto = trama.substring(85);
                            idProducto = StringUtils.stripEnd(idProducto, " ");
                            System.out.println("Id_Producto:"+idProducto);
                            ConsultarRQ infoPro = new ConsultarRQ();
                            infoPro.setNombreTabla(Mensaje.nombreTablaProducto);
                            infoPro.setCamposTabla("/");
                            infoPro.setCodigoIdentificadorColumna("0");
                            infoPro.setValorCodigoidentificadorColumna(idProducto);
                            MensajeRQ mconinfCli = new MensajeRQ(Originador.getOriginador(Originador.SRV_APLICACION), MensajeBDD.idMensajeConsultar);
                            mconinfCli.setCuerpo(infoPro);
                            System.out.println("Trama Info Producto "+mconinfCli.asTexto());
                            
                            ServBase comunicacion = new ServBase();
                            comunicacion.conexion();
                            comunicacion.flujo(mconinfCli.asTexto());
                            
                            String respuesta = comunicacion.flujoRS();
                            InformacionProductoRS infoProRS = new InformacionProductoRS();
                            infoProRS.build(respuesta);
                            MensajeRS minfoProRS = new MensajeRS(Originador.getOriginador(Originador.SRV_APLICACION), Mensaje.INFO_PRODUCT);
                            minfoProRS.setCuerpo(infoProRS);
                                                       
                            output.writeUTF(minfoProRS.asTexto());
                            System.out.println("RespuestaInfCliente: " + minfoProRS.asTexto());
                        }
                        break;
                    case Mensaje.INSERT_CLIENT:
                        if(Mensaje.validaHash(trama)){
                            String cuerpo = trama.substring(85);
                            InsertarRQ inserRQ = new InsertarRQ();
                            inserRQ.setNombreTabla(Mensaje.nombreTablaCliente);
                            inserRQ.setValorCamposTabla(cuerpo);
                            MensajeRQ minserRQ = new MensajeRQ(Originador.getOriginador(Originador.SRV_APLICACION), MensajeBDD.idMensajeInsertar);
                            minserRQ.setCuerpo(inserRQ);
                            System.out.println("TramaInsertarFactura "+minserRQ.asTexto());
                            
                            ServBase comunicacion = new ServBase();
                            comunicacion.conexion();
                            comunicacion.flujo(minserRQ.asTexto());
                            
                            String respuesta = comunicacion.flujoRS();
                            IngresarClienteRS incRS = new IngresarClienteRS();
                            incRS.build(respuesta);
                            MensajeRS  mincRS = new MensajeRS(Originador.getOriginador(Originador.SRV_APLICACION), Mensaje.INSERT_CLIENT);
                            mincRS.setCuerpo(incRS);
                            output.writeUTF(mincRS.asTexto());
                            System.out.println("Respuesta: " + mincRS.asTexto());
                            
                        }
                        break;
                    case Mensaje.INSERT_FACT:
                        if(Mensaje.validaHash(trama)){
                            String cuerpo = trama.substring(85);
                            InsertarRQ inserRQ = new InsertarRQ();
                            inserRQ.setNombreTabla(Mensaje.nombreTablaFactura);
                            inserRQ.setValorCamposTabla(cuerpo);
                            MensajeRQ minserRQ = new MensajeRQ(Originador.getOriginador(Originador.SRV_APLICACION), MensajeBDD.idMensajeInsertar);
                            minserRQ.setCuerpo(inserRQ);
                            System.out.println("TramaInsertarCliente "+minserRQ.asTexto());
                            
                            ServBase comunicacion = new ServBase();
                            comunicacion.conexion();
                            comunicacion.flujo(minserRQ.asTexto());
                            String respuesta = comunicacion.flujoRS();
                            IngresarFacturaRS incRS = new IngresarFacturaRS();
                            incRS.build(respuesta);
                            MensajeRS  mincRS = new MensajeRS(Originador.getOriginador(Originador.SRV_APLICACION), Mensaje.INSERT_FACT);
                            mincRS.setCuerpo(incRS);
                            output.writeUTF(mincRS.asTexto());
                            System.out.println("Respuesta: " + mincRS.asTexto());
                        }
                        break;
                    
                }

            } catch (IOException ex) {
                Logger.getLogger(HiloServer.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("no se pudo recibir la trama");
                break;
            }

        }
    }
    
    public void enviaTrama(String trama){
        
    }
}
