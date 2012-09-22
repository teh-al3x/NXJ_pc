/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.mastermind.NXJ_pc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

/**
 *
 * @author Alexander
 */
public class Connector implements Runnable {
    
    private NXTComm nxtComm = null;
    private NXTInfo[] nxtInfo = null;
    private NXTConnector nxtCon = new NXTConnector();
    private OutputStream nxtOut = null;
    private InputStream nxtIn = null;
    
    public int data = 0;
    
    public Connector() {
        try {
            nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
            nxtInfo = nxtComm.search(null);
        } catch (NXTCommException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public NXTInfo[] getNXTInfo() {
        return nxtInfo;
    }
    
    public boolean tryConnection(int index, int mode) {
        if (nxtCon.connectTo(nxtInfo[index], mode)) {
            nxtOut = nxtCon.getOutputStream();
            nxtIn = nxtCon.getInputStream();
            new Thread(this).start();
            return true;
        } else {
            return false;
        }
    }
    
    public boolean writeData(int data) {
        try {
            if (data >=255 || data < 0) {
                return false;
            }
            nxtOut.write(data);
            nxtOut.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public void killNXT() {
        try {
            nxtOut.write(255);
            nxtOut.flush();
        } catch (IOException ex) {
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                data = nxtIn.read();
                if (data == 255) {
                    System.out.println("Shutdown by NXT");
                    System.exit(0);
                }
            } catch (IOException ex) {
                Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0); // workaround because it doesn't get the data packet for some reason...
            }
        }
    }
}