package bot;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;




public class SlaveBot implements Runnable {

	static ServerSocket slavePort;
	static Integer slaveListenPort;
	static Map<String, targetData> targetDataMap = new HashMap <String,targetData> ();
	static class targetData{  
		public String action;
		public String port;                                                                                                  
		public String targetHost; 
		public String numConnections;

		public targetData(String action, String targetHost, String port, String numConnections) {                                                                           
			this.port = port;                                                                                             
			this.targetHost = targetHost;   
			this.numConnections = numConnections;
			this.action = action;
			
		}                                                                                                                 
	}
	public void run() {
		try{
			while(true)
			{
				System.out.println("enter");
				String dataFromMaster = "connect 54.173.226.204 8080 5";
				extractTargetData(dataFromMaster);
				Socket listener = slavePort.accept();
				PrintWriter out = new PrintWriter(listener.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(listener.getInputStream()));

				//To get the server's response, EchoClient reads from the BufferedReader object stdIn,
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				
			}        
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	public static void extractTargetData(String targetData){
		String delims = " ";
		StringTokenizer st = new StringTokenizer(targetData, delims);
		targetData targetObj = new targetData((String)st.nextElement(), ((String) st.nextElement()), ((String) st.nextElement()), (String) st.nextElement());
		storeTargetData(targetObj);
	}

	public static void storeTargetData(targetData targetObj){

		String uniqueKey = concatinatedData(targetObj.targetHost, targetObj.port);
		targetDataMap.put(uniqueKey, targetObj);
		Set<Entry<String, targetData>> set = targetDataMap.entrySet();
		Iterator<Entry<String, targetData>> iterator = set.iterator();		

		while(iterator.hasNext()) {
			Map.Entry slaveData = (Map.Entry)iterator.next();
		}
		//performAction();
		
	}

	public static String concatinatedData(String ip, String port){
		String concatinate = ip + port;
		return concatinate;
	}
	
	public static Integer registerSlave(String hostname, Integer masterport){
		Socket client_socket;
		try{
			client_socket = new Socket(hostname, masterport);
			PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

			String ip=(((InetSocketAddress) client_socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
			Integer slavePort_int = (Integer) (client_socket.getLocalPort());
			String local_host = client_socket.getInetAddress().getHostAddress();

			System.out.println(ip + slavePort_int + local_host); //print here in slave just to confirm

			out.println(ip +","+ slavePort_int + "," + local_host);
			return slavePort_int;

		}
		catch (Exception e){
			System.out.println(e);
			return -1;
		}

	}

public static void performAction(){
	
	}
	

	


	public static void main(String[] args) throws Exception {
		if(args.length != 4)
		{
			System.out.println("Usage: SlaveBot -h hostname -p portnumber of master");
			System.exit(0);
		}

		String hostname = args[1];
		Integer masterport = Integer.parseInt(args[3]);
		slaveListenPort = registerSlave(hostname, masterport);
		slavePort = new ServerSocket(slaveListenPort);
		new Thread(new SlaveBot()).start();

		for (Map.Entry<String, targetData> entry : targetDataMap.entrySet()) {
			System.out.println(entry.getKey() +" : " +entry.getValue().action + " : "+ entry.getValue().targetHost + " : " + entry.getValue().port + " : " + entry.getValue().numConnections);
		}
		
		

	}



	

}
