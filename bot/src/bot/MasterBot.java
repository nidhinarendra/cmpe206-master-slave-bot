//package bot;


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


public class MasterBot implements Runnable {

	static ServerSocket listener;
	static Integer masterPort;
	static Map<String, SlaveData> slaveDataMap = new HashMap <String,SlaveData> ();
	static class SlaveData{                                                                                               
		public String port;                                                                                                  
		public String ip; 
		public String slavehost;
		public SimpleDateFormat registerDate;
		public flag status;

		public SlaveData(String ip, String port) {                                                                           
			this.port = port;                                                                                             
			this.ip = ip;    
			this.registerDate = new SimpleDateFormat("yyyy/MM/dd");
			this.status = flag.registred;
		}                                                                                                                 
	}


	public enum flag{
		registred, connected, disconnected
	}


	public void run() {
		try{

			while (true)
			{
				Socket client_socket = listener.accept();
				PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

				//To get the server's response, EchoClient reads from the BufferedReader object stdIn,
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

				String dataRecived = in.readLine();
				System.out.println("Slave says:" + dataRecived);
				//out.println("google.com 8080");
				extractSlaveData(dataRecived);
			}  
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	public static void extractSlaveData(String slaveDataRecieved){
		String delims = ",";
		StringTokenizer st = new StringTokenizer(slaveDataRecieved, delims);

		SlaveData slv1 = new SlaveData((String)st.nextElement(), ((String) st.nextElement()));
		storeSlaveData(slv1);
	}

	// slave registration here 
	public static void storeSlaveData(SlaveData slaveObj){

		String uniqueStr = concatinatedData(slaveObj.ip, slaveObj.port);
		slaveDataMap.put(uniqueStr, slaveObj);
		slaveObj.status = flag.registred;
		Set<Entry<String, SlaveData>> set = slaveDataMap.entrySet();
		Iterator<Entry<String, SlaveData>> iterator = set.iterator();		

		while(iterator.hasNext()) {
			Map.Entry slaveData = (Map.Entry)iterator.next();
			System.out.println("storing the slave ip: " + slaveObj.ip + " port: " + slaveObj.port);
		}
	}

	public static String concatinatedData(String ip, String port){
		String concatinate = ip+port;
		return concatinate;
	}


	public static void listSlaves (){
	//	Set set = slaveDataMap.entrySet();
		Iterator<Entry<String, SlaveData>> iter = slaveDataMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, SlaveData> entry = iter.next();
			SlaveData localObjSlave = entry.getValue();
			Date date = new Date();
			if (localObjSlave.status == flag.registred){
				System.out.println(localObjSlave.ip + "\t" + localObjSlave.port + "\t" + localObjSlave.registerDate.format(date));
			}
		}
	}


	public static void slaveConnect(String ip, String portNum){
		String uniqueKey = concatinatedData(ip, portNum);
		Iterator<Entry<String, SlaveData>> iter = slaveDataMap.entrySet().iterator();
		Entry<String, SlaveData> entry = iter.next();
		SlaveData localObjSlaveData = entry.getValue();
		if(slaveDataMap.containsKey(uniqueKey)){
			if(localObjSlaveData.status == flag.registred){
				localObjSlaveData.status = flag.connected;
			}
			else{
				System.err.println("Slave is not registered!!");
			}
		}
		else{
			System.err.println("Slave is not registered!!");
		}
	}

	public static void slaveDisconnect(String ip, String portNum){
		String uniqueKey = concatinatedData(ip, portNum);
		Iterator<Entry<String, SlaveData>> iter = slaveDataMap.entrySet().iterator();
		Entry<String, SlaveData> entry = iter.next();
		SlaveData localObjSlaveData = entry.getValue();
		if(slaveDataMap.containsKey(uniqueKey)){
			if(localObjSlaveData.status == flag.connected){
				localObjSlaveData.status = flag.disconnected;
			}
			if(localObjSlaveData.status == flag.registred){
				System.out.println("The slave is not connected");
			}
			else{
				System.err.println("Slave is not registered!!");
			}
		}
		else{
			System.err.println("Slave is not registered!!");
		}
	}


	public static void main(String[] args) throws Exception {

		if (args.length != 2)
		{
			System.out.println("Usage: MasterBot -p portnumber");
			System.exit(0);
		}

		masterPort = Integer.parseInt(args[1]);
		listener = new ServerSocket(masterPort);
		new Thread(new MasterBot()).start();

		Scanner scanner = new Scanner(System.in);
		System.out.print(">");
		while (!scanner.hasNext("quit")){
			System.out.print(">");
			String command = scanner.nextLine();
			if (command.startsWith("list")){
				listSlaves();
			}
			else if (command.startsWith("connect") || command.startsWith("disconnect")){
				//System.out.println("going to connect/disconnect  the slave");
				sendToSlave(command);
			}
		
		}
		scanner.close();

	}

	public static void sendToSlave(String command){
		String delims = " ";
		StringTokenizer st = new StringTokenizer(command, delims);
		String givenCommand = (String)st.nextElement();
		String slaveIp = (String) st.nextElement();
		String targetIP = (String) st.nextElement();
		String targetPort = (String) st.nextElement();
		String numConnect = (String) st.nextElement();
		//SlaveData newObj = new SlaveData(givenCommand, slaveIp);
		
		if(slaveDataMap.containsKey(slaveIp)){
			SlaveData newObj1 = slaveDataMap.get(slaveIp);
			String port1 = newObj1.port;
			String ip = newObj1.ip;
			Integer port1Int = Integer.parseInt(port1);
			try{
			Socket sendData = new Socket(ip, port1Int);
			PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
			command.replaceAll(slaveIp, "");
			out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect);
			
			out.println();
			}
			catch(Exception e){
				System.out.println("Socket could not be created");
			}
		}
		
	}

	
}








