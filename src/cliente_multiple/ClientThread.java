/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_multiple;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 *
 * @author alexande chavez
 */
public class ClientThread extends Thread {
    private Socket socket;
    private BufferedReader input;
    private String args[];
    public ClientThread(Socket socket,String args[]) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader ( 
                new InputStreamReader(socket.getInputStream()));
        this.args=args;
    }
    
    @Override
    public void run() {
        try {
             
            while(true) {
                String response = input.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("ClientThread error:"+e.getMessage());
        } finally {
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
