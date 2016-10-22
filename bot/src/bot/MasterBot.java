package bot;
/*		
		// create a dictionary with key = SourcePortNumber and value = SlaveHostName, IPAddress, SourcePortNumber, RegistrationDate
		
		
		//list: print all the values in the dictionary
		
		
		/*connect:
		 * 1. check if the entry exists in the dictionary, if exists error, else continue 
		 * 2. create a slave process and run it 
		 * 3. parameters to pass are master PORT and slave id
		 */
		 
		

/*disconnect:
		 * 1. check if the etry exsits in the disctionary, if not error
		 * 2. get the slave id and issue a kill command to the slave
		 * 3. update the dictionary by deleting this entry
		 */
		 

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
				System.out.println("Input says:" + in.readLine());
			}    

			//DataInputStream from_slave = new6 DataInputStream(serviceSocket.getInoutStream());
		}
		catch (Exception e){
			System.out.println(e);
		}


	}
   
    
    
}
       

