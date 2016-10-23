//package bot;


import java.io.*;
import java.net.*;
import java.util.*;

public class MasterBot {

		
	public static void main(String[] args) throws Exception {
		if (args.length != 2)
		{
			System.out.println("Usage: MasterBot -p portnumber");
			System.exit(0);
		}

		Integer masterport;
		masterport = Integer.parseInt(args[1]);
		
		try{
			ServerSocket listener = new ServerSocket(masterport);
			while (true)
			{
				Socket client_socket = listener.accept();
				PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
				//To get the server's response, EchoClient reads from the BufferedReader object stdIn,
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Slave says:" + in.readLine());
				
				// extract the slave_ip and slave_port
				storeSlaveData();
			}    

			//DataInputStream from_slave = new6 DataInputStream(serviceSocket.getInoutStream());
		}
		catch (Exception e){
			System.out.println(e);
		}
	}
	
	public static void storeSlaveData(){
		Map<String, String> slaveDataMap = new HashMap <String,String> ();
		String str;
		slaveDataMap.put("Nidhi", "Slave1");
		slaveDataMap.put("Darshan", "Slave2");
		Set set = slaveDataMap.entrySet();
	    Iterator iterator = set.iterator();		

	    while(iterator.hasNext()) {
	         Map.Entry slaveData = (Map.Entry)iterator.next();
	         System.out.print("key is: "+ slaveData.getKey() + " & Value is: ");
	         System.out.println(slaveData.getValue());
	      }
		
	}
	
}
       

