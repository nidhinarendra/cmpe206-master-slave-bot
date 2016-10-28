//package bot;


import java.io.*;
import java.net.*;
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
		public Date registerDate;
		public flag status;

		public SlaveData(String ip, String port) {                                                                           
			this.port = port;                                                                                             
			this.ip = ip;    
			this.registerDate = new Date();
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
				Thread.sleep(4000);
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

	public static void storeSlaveData(SlaveData slaveObj){

		String uniqueStr = concatinatedData(slaveObj.ip, slaveObj.port);
		slaveDataMap.put(uniqueStr, slaveObj);
		Set<Entry<String, SlaveData>> set = slaveDataMap.entrySet();
		Iterator<Entry<String, SlaveData>> iterator = set.iterator();		

		while(iterator.hasNext()) {
			Map.Entry slaveData = (Map.Entry)iterator.next();
		}
	}

	public static String concatinatedData(String ip, String port){
		String concatinate = ip + port;
		return concatinate;
	}


	public static void list (Map slaveDataMap){
		Set set = slaveDataMap.entrySet();

		Iterator<Entry<String, SlaveData>> iter = slaveDataMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, SlaveData> entry = iter.next();
			SlaveData localObjSlave = entry.getValue();
			if (localObjSlave.status == flag.registred){
				System.out.print(localObjSlave.ip + "\t" + localObjSlave.port + "\t" + localObjSlave.registerDate);
			}
		}
	}


	public static void connectSlave(String ip, String portNum){
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

	public static void disConnectSlave(String ip, String portNum){
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
		System.out.print("- ");
		String usercommand = scanner.next();	

		//DataInputStream from_slave = new6 DataInputStream(serviceSocket.getInoutStream());
	}

}








