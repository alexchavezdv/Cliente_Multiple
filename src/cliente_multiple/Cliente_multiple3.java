/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_multiple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author DELL
 */
public class Cliente_multiple3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost",5000)){
            BufferedReader input = new BufferedReader( new 
                InputStreamReader(socket.getInputStream()));
            PrintWriter output = 
                    new PrintWriter(socket.getOutputStream(),true);
            Scanner scanner = new Scanner(System.in);
            String userInput;
            String response;
            String clientName = "anonymous";
            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();
            do {
                if (clientName.equals("anonymous")) {
                    System.out.print("Escribe tu nombre>:");
                    userInput = scanner.nextLine();
                    clientName = userInput;
                    output.println(userInput);
                    if (userInput.equals("salir")) {
                        break;
                    }
               } else {
                    String message = ( "("+clientName+")"+" mensaje: " );
                    System.out.println(message);
                    userInput = scanner.nextLine();
                    // output.println(message + " " + userInput);
                    output.println(userInput);
                    if (userInput.equals("salir")) {
                        break;
                    }
                }
            } while (!userInput.equals("salir"));
        } catch (Exception e) {
            System.out.println("Main client. Error:"+e.getMessage());
        }
    }
    
}
