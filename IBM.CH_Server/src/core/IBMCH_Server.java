package core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import networking.TCPServer;

public class IBMCH_Server {

	public static void main(String[] args) throws IOException, Exception {
		
		InetAddress ip = InetAddress.getByName("127.0.0.1");
		TCPServer Server = new TCPServer(ip);
		Server.AddListener("main", 50000, 999, 1024, 3, 100);
		Server.Start();
	}


}
