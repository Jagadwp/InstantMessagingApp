package com.server;

import com.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class ServerThread extends Thread {
	 private Hashtable<String, MessageThread> clientList;
	    private ServerSocket server;

	    public ServerThread() {
	        try {
	            this.clientList = new Hashtable<String, MessageThread>();
	            this.server = new ServerSocket(8080);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public void run() {
	    	System.out.println("Server is Running");
	        // listen for a new connection
	        while(true) {
	            try {
	                // accept a new connection
	                Socket socket = this.server.accept();

	                // create a new MessageThread
	                MessageThread mt = new MessageThread(socket, this);

	                // start the new thread
	                mt.start();

	                // store the new thread to the hash table
	                String clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	                
	                System.out.println("Connection established with: " + clientId);
	                
	                this.clientList.put(clientId, mt);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    public void sendToAll(Message message) {
	        // iterate through all clients
	        Enumeration<String> clientKeys = this.clientList.keys();
	        while (clientKeys.hasMoreElements()) {
	            String clientId = clientKeys.nextElement();

	            MessageThread mt = this.clientList.get(clientId);

	            // send the message
	            mt.send(message);
	        }

	    }
	    
	    public void sendPrivately(Message message, String username) {
	    	// iterate through all clients
	    	Enumeration<String> clientKeys = this.clientList.keys();
	        while (clientKeys.hasMoreElements()) {
	            String clientId = clientKeys.nextElement();

	            MessageThread mt = this.clientList.get(clientId);

	            // send the message to specified username
	            if(mt.getUsername().equals(username)) {
	            	mt.send(message);
	            	break;
	            }
	        }
	    }
	    
	    public boolean isExist(String username) {
	    	// iterate through all clients
	    	Enumeration<String> clientKeys = this.clientList.keys();
	        while (clientKeys.hasMoreElements()) {
	            String clientId = clientKeys.nextElement();

	            MessageThread mt = this.clientList.get(clientId);

	            // if found return true
	            if(mt.getUsername().equals(username)) {
	            	return true;
	            }
	        }
	        // Not found
	        return false;
	    }
	    
	    public void removeClient(String username)  {
	    	// iterate through all clients
	    	Enumeration<String> clientKeys = this.clientList.keys();
	        while (clientKeys.hasMoreElements()) {
	            String clientId = clientKeys.nextElement();

	            MessageThread mt = this.clientList.get(clientId);

	            if (mt.getUsername().equals(username)) { // Username found
	            	this.clientList.remove(clientId);
	            	return;
	            }
	        }
	    }

		public String getOnlineUsers() {
	    	String onlineUsers = "\nOnline Users:n";
	    	// iterate through all clients
	    	Enumeration<String> clientKeys = this.clientList.keys();
			int count = 1;
	        while (clientKeys.hasMoreElements()) {
				String clientId = clientKeys.nextElement();
	            MessageThread mt = this.clientList.get(clientId);
				
	            onlineUsers += (count++ + ". " + mt.getUsername() + "\n");
	        }
	        return onlineUsers;
	    }
}
