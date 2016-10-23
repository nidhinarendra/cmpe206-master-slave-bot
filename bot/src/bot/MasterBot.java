package bot;


import java.io.*;
import java.net.*;
import java.util.*;


public class MasterBot {

	static class SlaveData{                                                                                               
        public int port;                                                                                                  
        public String ip;                                                                                                 
                                                                                                                          
        public SlaveData(String ip, int port) {                                                                           
            this.port = port;                                                                                             
            this.ip = ip;                                                                                                 
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
	
	public static void extractSlaveData(String slaveDataRecieved){
		String delims = ",";
		StringTokenizer st = new StringTokenizer(slaveDataRecieved, delims);
		while (st.hasMoreElements()) {
			 SlaveData slv1 = new SlaveData((String)st.nextElement(), Integer.parseInt((String) st.nextElement()));
			System.out.println("StringTokenizer Output: " + st.nextElement());
		}
	}
	
	/*
	 *         SlaveData slv1 = new SlaveData(port_int, ip);
	        
	        System.out.println("ip is:" + ip + "port is" + port_int + "slvdata object is" + slv1.toString());

	 */
	
}
       

