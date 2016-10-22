//package project1;

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
    	Integer masterport;
    	masterport = Integer.parseInt(args[3]);
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
    	}
