package com.server;

import com.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageThread extends Thread {
	private Socket socket;
    private ObjectOutputStream ous;
    private ObjectInputStream ois;
    private ServerThread serverThread;
    private String username;

    public MessageThread(Socket socket, ServerThread serverThread) {
        try {
            this.socket = socket;
            this.ous = new ObjectOutputStream(this.socket.getOutputStream());
            this.ois = new ObjectInputStream(this.socket.getInputStream());
            this.serverThread = serverThread;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
    	try {
			Message firstMessage = (Message) this.ois.readObject();
			this.setUsername(firstMessage.getText());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        while(true) {
            try {
                Message message = (Message) this.ois.readObject();
                if (message.getRequest().equals("Broadcast")) { 
                	System.out.println("Got Broadcast from " + message.getSender() + " with message:\n"+message.getText());
                	this.serverThread.sendToAll(message);
                }
                else if (message.getRequest().equals("Online Users")) { 
                	System.out.println("Got Online Users Request from " + message.getSender());
                	String onlineUserList = this.serverThread.getOnlineUsers();
                	this.returnMessageToSender(message.getSender(), onlineUserList, "Private");
                }
                else if (message.getRequest().equals("Private")) { 
                	System.out.println("Got private message from " + message.getSender() + " to " + message.getReceiver() + " with message:\n" + message.getText());
                	if (this.serverThread.isExist(message.getReceiver())) { 
                		this.serverThread.sendPrivately(message, message.getReceiver());                		
                	}
                	else { 
                		String response = "User " + message.getReceiver() + " not found";
                		this.returnMessageToSender(message.getSender(), response, "Private");
                	}
                }
            } catch (IOException e) {
                System.out.println("Connection Lost with " + this.username);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Message message) {
        try {
            this.ous.writeObject(message);
            this.ous.flush();
        } catch (IOException e) {
            this.serverThread.removeClient(message.getReceiver());
            String response = "User " + message.getReceiver() + " is Disconnected";
            this.returnMessageToSender(message.getSender(), response, "Private");
        }
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public Socket getSocket() {
    	return this.socket;
    }
    
    public void returnMessageToSender(String sender, String text, String request) {
    	Message message = new Message(request);
    	message.setSender("Remote Server");
    	message.setReceiver(sender);
    	message.setText(text);
    	this.serverThread.sendPrivately(message, sender);
    }
}
