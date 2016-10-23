package bot;


import java.io.*;
import java.net.*;
import java.util.*;


public class MasterBot {

	static class SlaveData{                                                                                               
		public int port;                                                                                                  
		public String ip; 
		public Date registerDate;

		public SlaveData(String ip, int port) {                                                                           
			this.port = port;                                                                                             
			this.ip = ip;    
			this.registerDate = new Date();
		}                                                                                                                 
	}


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

				String dataRecived = in.readLine();
				System.out.println("Slave says:" + dataRecived);


				extractSlaveData(dataRecived);
			}    

			//DataInputStream from_slave = new6 DataInputStream(serviceSocket.getInoutStream());
		}
		catch (Exception e){
			System.out.println(e);
		}
	}


	public static String concatinatedData(SlaveData params){
		String concatinate = params.ip + params.port;
		return concatinate;
	}


	public static void storeSlaveData(SlaveData slaveObj){
		Map<String, SlaveData> slaveDataMap = new HashMap <String,SlaveData> ();
		String uniqueStr = concatinatedData(slaveObj);
		slaveDataMap.put(uniqueStr, slaveObj);
		Set set = slaveDataMap.entrySet();
		Iterator iterator = set.iterator();		

		while(iterator.hasNext()) {
			Map.Entry slaveData = (Map.Entry)iterator.next();
			System.out.print(uniqueStr + "\t" + slaveObj.ip + "\t" + slaveObj.port + "\t" + slaveObj.registerDate);
			//System.out.println(slaveData.getValue());
		}

	}

	public static void extractSlaveData(String slaveDataRecieved){
		String delims = ",";
		StringTokenizer st = new StringTokenizer(slaveDataRecieved, delims);
		
		SlaveData slv1 = new SlaveData((String)st.nextElement(), Integer.parseInt((String) st.nextElement()));
		storeSlaveData(slv1);
	}


}


