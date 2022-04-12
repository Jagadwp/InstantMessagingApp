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
    private ServerThread st;
    private String username;

    public MessageThread(Socket socket, ServerThread st) {
        try {
            this.socket = socket;
            this.ous = new ObjectOutputStream(this.socket.getOutputStream());
            this.ois = new ObjectInputStream(this.socket.getInputStream());
            this.st = st;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
    	try {
			Message initialMsg = (Message) this.ois.readObject();
			this.setUsername(initialMsg.getText());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        while(true) {
            try {
                Message message = (Message) this.ois.readObject();
                //Private
                if (message.getRequest().equals("1")) { 
                	System.out.println("Receive private message (" + message.getSender() + " --> " + message.getReceiver() + ")");
                	System.out.println("Message: "+ message.getText() + "\n---");

                	if (this.st.isExist(message.getReceiver())) { 
                		this.st.sendPrivately(message, message.getReceiver());                		
                	}
                	else { 
                		String response = "User " + message.getReceiver() + " not found";
                		this.returnMessageToSender(message.getSender(), response, "Private");
                	}
                }
                // Broadcast
                else if (message.getRequest().equals("2")) { 
                	System.out.println("Receive broadcasted message");
                	System.out.println(message.getSender() + ": "+ message.getText() + "\n---");
                	this.st.sendToAll(message);
                }
                // Online Users
                else if (message.getRequest().equals("3")) { 
                	System.out.println("Receive Online Users Request from " + message.getSender());
                	String onlineUserList = this.st.getOnlineUsers();
                	this.returnMessageToSender(message.getSender(), onlineUserList, "Private");
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
            this.st.removeClient(message.getReceiver());
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
        System.out.println("masuk return: " + sender + "-" + text + "-" + request);
    	Message message = new Message(request);
    	message.setSender("Remote Server");
    	message.setReceiver(sender);
    	message.setText(text);
        System.out.println(message.getText());
    	this.st.sendPrivately(message, sender);
    }
}
