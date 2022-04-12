package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.message.Message;

public class ThreadClient extends Thread{
	private ObjectInputStream ois;
	 
    public ThreadClient(ObjectInputStream ois) {
        this.ois = ois;
    }
 
    public void run() {
        while(true) {
            try {
                Message message = (Message) ois.readObject();
 
                if (message.getRequest().equals("1")) {
                    System.out.println("Incoming message from "+  "\"" + message.getSender() + "\"");
                    System.out.println(message.getSender() + " : " + message.getText());
                    System.out.println("---");
                }
                else if (message.getRequest().equals("2")) {
                    System.out.println("Broadcasted message from " + "\"" + message.getSender() + "\"");
                    System.out.println(message.getSender() + " : " + message.getText());
                    System.out.println("---");
                }
                else if (message.getRequest().equals("3")) {
                    System.out.println(message.getText());
                }
                
            } catch (IOException e) {
                System.out.println("Server Disconnected");
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
 
        }
    }

}
