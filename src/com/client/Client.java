package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.message.Message;
import com.server.ServerThread;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Client Alive");
		try {
			Socket socket = new Socket("localhost",6666);
			
			ObjectOutputStream ous = new ObjectOutputStream(socket.getOutputStream());
			
			
			ous.flush();
			
			ous.close();
			socket.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
