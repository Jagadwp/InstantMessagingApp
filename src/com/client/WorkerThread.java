package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.message.Message;

public class WorkerThread extends Thread{
	private ObjectInputStream ois;
	 
    public WorkerThread(ObjectInputStream ois) {
        this.ois = ois;
    }
 
    public void run() {
        while(true) {
            try {
                Message message = (Message) ois.readObject();
 
                if (message.getRequest().equals("Private")) {
                    System.out.println("You have new message from \"" + message.getSender() + "\"");
                    System.out.println(message.getSender() + " : " + message.getText());
                    System.out.println("---End of Message---");
                }
                else if (message.getRequest().equals("Online Users")) {
                    System.out.println(message.getText());
                }
                else if (message.getRequest().equals("Broadcast")) {
                    System.out.println( "\"" + message.getSender() + "\" broadcasted a message");
                    System.out.println(message.getSender() + " : " + message.getText());
                    System.out.println("---End of Broadcast---");
                }
                
            } catch (IOException e) {
                System.out.println("Connection Lost: Server Disconnected");
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
 
        }
    }

}
