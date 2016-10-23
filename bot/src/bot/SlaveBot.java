//package bot;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Trivial client for the date server.
 */
public class SlaveBot {
	public static void main(String[] args) throws Exception {
		if(args.length != 4)
		{
			System.out.println("Usage: SlaveBot -h hostname -p portnumber of master");
			System.exit(0);
		}

		String hostname = args[1];
		Integer masterport = Integer.parseInt(args[3]);
		registerSlave(hostname, masterport);
		//slaveListen();
	}

	public static void registerSlave(String hostname, int masterport){
		Socket client_socket;
		try{
			client_socket = new Socket(hostname, masterport);
			PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(client_socket.getInputStream()));
			out.println("Hi from nidhi as a slave");
		}
		catch (Exception e){
			System.out.println(e);
		}
	}


	public static void slaveListen(){
			try{
				ServerSocket slaveListener = new ServerSocket(9001);
				while (true)
				{
					Socket master_socket = slaveListener.accept();
					PrintWriter out = new PrintWriter(master_socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(master_socket.getInputStream()));
				}    
			}
			catch (Exception e){
				System.out.println(e);

			}
	}
}
