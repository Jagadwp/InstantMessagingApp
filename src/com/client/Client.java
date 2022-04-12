package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.message.Message;
import com.server.ServerThread;

public class Client {

	public static void main(String[] args) {
		System.out.println("Client is Running");
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
 
            ObjectOutputStream ous = new ObjectOutputStream(socket.getOutputStream());
            ThreadClient tc = new ThreadClient(new ObjectInputStream(socket.getInputStream()));
        	tc.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 
 
            System.out.println("Enter your username: ");
            String username = reader.readLine();
            
            Message initialMsg = new Message();
            initialMsg.setText(username);

            ous.writeObject(initialMsg);
            ous.flush();
            
            while(true) {
                System.out.println("\nRequest Features:");
                System.out.println("1. Private Message");
                System.out.println("2. Broadcast Message");	
                System.out.println("3. Show Online Users");
                System.out.println("Enter request number:");
                String req = reader.readLine();
                
                Message message = new Message();
                message.setSender(username);
                message.setRequest(req);
                
                if (req.equals("1")) {
                    System.out.println("Enter message receiver name:");
                    String receiver = reader.readLine();
                    message.setReceiver(receiver);
                }
                
                if (req.equals("1") || req.equals("2")) {                 
                    System.out.println("Type your message:");
                    String text = reader.readLine();
                    message.setText(text);
                }
 
                ous.writeObject(message);
                ous.flush();
            }
 
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

}
