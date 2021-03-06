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
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

				String dataRecived = in.readLine();
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
		}
	}

	public static String concatinatedData(String ip, String port){
		String concatinate = ip+port;
		return concatinate;
	}




	public static void listSlaves(){
		Iterator<Entry<String, SlaveData>> iter = slaveDataMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, SlaveData> entry = iter.next();
			SlaveData localObjSlave = entry.getValue();
			Date date = new Date();
			if (localObjSlave.status == flag.registred){
				System.out.print(localObjSlave.ip + "\t" + localObjSlave.port + "\t" + localObjSlave.registerDate.format(date) + "\n");
			}
		}
	}

	public static void checkGivenCommand(String command){
		String delims = " ";
		StringTokenizer st = new StringTokenizer(command, delims);
		Integer numElements = st.countTokens();
		String givenCommand = st.nextElement().toString();
		String slaveIp = st.nextElement().toString();
		String targetIP =  st.nextElement().toString();
		String targetPort = st.nextElement().toString();
		String numConnect;
		String keepAliveOrUrl;

		if (givenCommand.contentEquals("connect")){

			if(numElements == 4){
				if(slaveIp.contentEquals("all"))
				{
					for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){

						SlaveData newObj1 = entry.getValue();
						String ip = newObj1.ip;
						String port = newObj1.port;
						Integer portInt = Integer.parseInt(port);
						try{
							Socket sendData = new Socket(ip, portInt);
							PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
							//command.replaceAll(slaveIp, "");
							out.println(givenCommand + " " + targetIP + " " + targetPort);
							out.println();
							//System.out.println("Sending data to all the slaves");
						}
						catch(Exception e){
							System.out.println(e);
						}
					}
				}
				else{
					for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){
						if (entry.getKey().startsWith(slaveIp)){
							SlaveData newObj1 = entry.getValue();
							String ip = newObj1.ip;
							String port = newObj1.port;
							Integer portInt = Integer.parseInt(port);
							try{
								Socket sendData = new Socket(ip, portInt);
								PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
								command.replaceAll(slaveIp, "");
								out.println(givenCommand + " " + targetIP + " " + targetPort);
								out.println();
								//System.out.println("Sending data to one slave");
							}
							catch(Exception e){
								System.out.println(e);
							}
						}
						else{
							System.out.println("The given slave does not exist");
						}
					}
				}
			}

			else if (numElements == 5){
				String toCheck = st.nextElement().toString();
				if(toCheck.equals("keepalive") || toCheck.startsWith("url=")){
					numConnect = "1";
					keepAliveOrUrl = toCheck;
					if(slaveIp.contentEquals("all"))
					{
						for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){

							SlaveData newObj1 = entry.getValue();
							String ip = newObj1.ip;
							String port = newObj1.port;
							Integer portInt = Integer.parseInt(port);
							try{
								Socket sendData = new Socket(ip, portInt);
								PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
								command.replaceAll(slaveIp, "");
								out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect + " " + keepAliveOrUrl);
								out.println();
								//System.out.println("Sending data to all the slaves");
							}
							catch(Exception e){
								System.out.println(e);
							}
						}
					}

					else{
						for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){
							if (entry.getKey().startsWith(slaveIp)){
								SlaveData newObj1 = entry.getValue();
								String ip = newObj1.ip;
								String port = newObj1.port;
								Integer portInt = Integer.parseInt(port);
								Socket sendData = null;
								try{
									sendData = new Socket(ip, portInt);
									PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
									command.replaceAll(slaveIp, "");
									out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect + " " + keepAliveOrUrl);
									out.println();
									//System.out.println("Sending data to one slave");
								}
								catch(Exception e){
									System.out.println(e);
								}
							}
							else{
								System.out.println("The given slave does not exist");
							}
						}
					}
				}

				else{
					numConnect = toCheck;
					keepAliveOrUrl = null;
					if(slaveIp.contentEquals("all"))
					{
						for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){

							SlaveData newObj1 = entry.getValue();
							String ip = newObj1.ip;
							String port = newObj1.port;
							Integer portInt = Integer.parseInt(port);
							try{
								Socket sendData = new Socket(ip, portInt);
								PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
								command.replaceAll(slaveIp, "");
								out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect + " " + keepAliveOrUrl );
								out.println();
								//System.out.println("Sending data to all the slaves");
							}
							catch(Exception e){
								System.out.println(e);
							}
						}
					}
					else{
						for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){
							if (entry.getKey().startsWith(slaveIp)){
								SlaveData newObj1 = entry.getValue();
								String ip = newObj1.ip;
								String port = newObj1.port;
								Integer portInt = Integer.parseInt(port);
								Socket sendData = null;
								try{
									sendData = new Socket(ip, portInt);
									PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
									command.replaceAll(slaveIp, "");
									out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect + " " + keepAliveOrUrl);
									out.println();
									//System.out.println("Sending data to one slave");
								}
								catch(Exception e){
									System.out.println(e);
								}
							}
							else{
								System.out.println("The given slave does not exist");
							}
						}
					}
				}
			}

			else if(numElements == 6){
				numConnect = (String) st.nextElement();
				keepAliveOrUrl = (String) st.nextElement();
				if(slaveIp.contentEquals("all"))
				{
					for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){

						SlaveData newObj1 = entry.getValue();
						String ip = newObj1.ip;
						String port = newObj1.port;
						Integer portInt = Integer.parseInt(port);
						try{
							Socket sendData = new Socket(ip, portInt);
							PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
							command.replaceAll(slaveIp, "");
							out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect + " " + keepAliveOrUrl);
							out.println();
							//System.out.println("Sending data to all the slaves");
						}
						catch(Exception e){
							System.out.println(e);
						}
					}
				}
				else{
					for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){
						if (entry.getKey().startsWith(slaveIp)){
							SlaveData newObj1 = entry.getValue();
							String ip = newObj1.ip;
							String port = newObj1.port;
							Integer portInt = Integer.parseInt(port);
							try{
								Socket sendData = new Socket(ip, portInt);
								PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
								command.replaceAll(slaveIp, "");
								out.println(givenCommand + " " + targetIP + " " + targetPort + " " + numConnect + " " + keepAliveOrUrl);
								out.println();
								//System.out.println("Sending data to one slave");
							}
							catch(Exception e){
								System.out.println(e);
							}
						}
						else{
							System.out.println("The given slave does not exist");
						}
					}
				}
			}
			else {
				System.out.println("Invalid arguments");
			}

		}

		else if(givenCommand.contentEquals("disconnect")){
			if(slaveIp.contentEquals("all"))
			{
				for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){
					SlaveData newObj1 = entry.getValue();
					String ip = newObj1.ip;
					String port = newObj1.port;
					Integer portInt = Integer.parseInt(port);
					try{
						Socket sendData = new Socket(ip, portInt);
						PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
						out.println(givenCommand + " " + targetIP + " " + targetPort);
						out.println();
						//System.out.println("Sending data to all the slaves");
					}
					catch(Exception e){
						System.out.println(e);
					}
				}
			}
			else{
				for (Entry<String, SlaveData> entry : slaveDataMap.entrySet()){
					if (entry.getKey().startsWith(slaveIp)){
						SlaveData newObj1 = entry.getValue();
						String ip = newObj1.ip;
						String port = newObj1.port;
						Integer portInt = Integer.parseInt(port);
						try{
							Socket sendData = new Socket(ip, portInt);
							PrintWriter out = new PrintWriter(sendData.getOutputStream(), true);
							out.println(givenCommand + " " + targetIP + " " + targetPort);
							out.println();
							//System.out.println("Sending data to one slave");
						}
						catch(Exception e){
							System.out.println(e);
						}
					}
					else{
						System.out.println("The given slave does not exist");
					}
				}
			}
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
			String command = scanner.nextLine();
			if (command.startsWith("list")){
				listSlaves();
			}
			else if (command.startsWith("connect") || command.startsWith("disconnect")){
				//System.out.println("going to connect/disconnect  the slave");
				checkGivenCommand(command);
			}
			else {
				System.out.println("Invalid command");
			}
			System.out.print(">");
		}
		scanner.close();
	}
}
