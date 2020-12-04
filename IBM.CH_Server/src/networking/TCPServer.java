package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import networking.Extasys.DataFrame;
import networking.Extasys.ExtasysThreadPool;
import networking.Extasys.Network.TCP.Server.Listener.TCPClientConnection;
import networking.Extasys.Network.TCP.Server.Listener.TCPListener;
import networking.Extasys.Network.TCP.Server.Listener.Exceptions.ClientIsDisconnectedException;
import networking.Extasys.Network.TCP.Server.Listener.Exceptions.OutgoingPacketFailedException;

public class TCPServer {
	
	private Hashtable connectedClients = new Hashtable();
	private final ArrayList<TCPListener> serverListeners = new ArrayList<TCPListener>();
    public final ExtasysThreadPool serverThreadPool;
    private boolean isRunning;
    private int corePoolSize;
    private int maximumPoolSize;
    private InetAddress listenerIP;


    public TCPServer(InetAddress listenerIP) {
    	/*TODO
    	 *naèítání / nastavení parametrù serveru pøed spuštìním  
    	 * 
    	 * 
    	 */
    	corePoolSize = 10;
    	maximumPoolSize = 100;
    	this.listenerIP = listenerIP;
    	
    	this.isRunning = false;
		this.serverThreadPool = new ExtasysThreadPool(corePoolSize, maximumPoolSize, 10, TimeUnit.SECONDS);
    }
    
    
    /* 
     * Use multiple listeners to increase number of available connections for the same purpose on different ports
     * To each purpose you should create new server with its own listeners (Client server, Admin server)
     *
     * New listener WITH specified ip addess
    */
	public TCPListener AddListener(String name, InetAddress ipAddress, int port, int maxConnections, int readBufferSize, int connectionTimeOut, int backLog) {
        TCPListener listener = new TCPListener(name, ipAddress, port, maxConnections, readBufferSize, connectionTimeOut, backLog);
        listener.setMyTCPServer(this);
        serverListeners.add(listener);
        return listener;
    }
	
	
    /* 
     * Use multiple listeners to increase number of available connections for the same purpose on different ports
     * To each purpose you should create new server with its own listeners (Client server, Admin server)
     *
     * New listener WITHOUT specified ip addess - default server ip is used
    */
	public TCPListener AddListener(String name, int port, int maxConnections, int readBufferSize, int connectionTimeOut, int backLog) {
        TCPListener listener = new TCPListener(name, listenerIP, port, maxConnections, readBufferSize, connectionTimeOut, backLog);
        listener.setMyTCPServer(this);
        serverListeners.add(listener);
        return listener;
    }
	
	
	public void RemoveListener(String name) {
        for (int i = 0; i < serverListeners.size(); i++) {
            if (((TCPListener) serverListeners.get(i)).getName().equals(name)) {
                ((TCPListener) serverListeners.get(i)).Stop();
                serverListeners.remove(i);
                break;
            }
        }
    }
	
	
	//Start or restart 
	public void Start() throws IOException, Exception {
        Stop(false);
        try {
            //Start all listeners.
            for (int i = 0; i < serverListeners.size(); i++) {
                ((TCPListener) serverListeners.get(i)).Start();
            }
        }
        catch (IOException ex) {
            Stop(false);
            throw ex;
        }
        catch (Exception ex) {
            Stop(false);
            throw ex;
        }
        this.isRunning = true;
    }

	
    public void Stop() {
        Stop(false);
    }

    
    public void ForceStop() {
        Stop(true);
    }
    
    
    //Internal stop method
    private void Stop(boolean forceStop) {
    	this.isRunning = false;
        //Stop all listeners.
        for (int i = 0; i < serverListeners.size(); i++) {
            if (!forceStop) {
                ((TCPListener) serverListeners.get(i)).Stop();
            }else {
                ((TCPListener) serverListeners.get(i)).ForceStop();
            }
        }

        try{
            serverThreadPool.getQueue().clear();
        }
        catch (Exception ex){}
    }
    
    
    //Destroys all server data and shutdowns the server
    public void Dispose() {
        Stop(false);
        serverThreadPool.shutdown();
    }
    
    
	public boolean isRunning() {
		return isRunning;
	}
	
	
	public ArrayList<TCPListener> getListeners() {
		return serverListeners;
	}
    
    
    public void OnDataReceive(TCPClientConnection sender, DataFrame data) {
    	System.out.println(sender.getIPAddress() + ": " + data.toString());
    }


    public void OnClientConnect(TCPClientConnection client) {
    	System.out.println(client.getIPAddress() + ": Connected");
    }

    
    public void OnClientDisconnect(TCPClientConnection client) {
    	System.out.println(client.getIPAddress() + ": Disconnected");
    }
    
    
    public void SendToSender(String data, TCPClientConnection sender) throws ClientIsDisconnectedException, OutgoingPacketFailedException {
        sender.SendData(data);
    }
    
    
    public void SendToSender(byte[] bytes, int offset, int length, TCPClientConnection sender) throws ClientIsDisconnectedException, OutgoingPacketFailedException {
        sender.SendData(bytes, offset, length);
    }
    
    
    public void SendToAll(String data) {
        for (int i = 0; i < serverListeners.size(); i++) {
            try {
                ((TCPListener) serverListeners.get(i)).ReplyToAll(data);
            }
            catch (Exception ex) {}
        }
    }
    
    
    public void SendToAll(byte[] bytes, int offset, int length) {
        for (int i = 0; i < serverListeners.size(); i++) {
            try {
                ((TCPListener) serverListeners.get(i)).ReplyToAll(bytes, offset, length);
            }
            catch (Exception ex) {}
        }
    }
    
    
    public void SendToAllExceptSender(String data, TCPClientConnection sender) {
        for (int i = 0; i < serverListeners.size(); i++) {
            try {
                ((TCPListener) serverListeners.get(i)).ReplyToAllExceptSender(data, sender);
            }
            catch (Exception ex) {}
        }
    }
    
    
    public void SendToAllExceptSender(byte[] bytes, int offset, int length, TCPClientConnection sender) {
        for (int i = 0; i < serverListeners.size(); i++) {
            try {
                ((TCPListener) serverListeners.get(i)).ReplyToAllExceptSender(bytes, offset, length, sender);
            }
            catch (Exception ex) {}
        }
    }
    
    
    //Total amount of Bytes coming to the server
    public long GetTotalIncomingBytes() {
        long bytesIn = 0;
        try {
            for (int i = 0; i < serverListeners.size(); i++) {
                try {
                    bytesIn += ((TCPListener) serverListeners.get(i)).getBytesIn();
                }
                catch (Exception ex) {}
            }
        }
        catch (Exception ex) {}
        return bytesIn;
    }

    
    //Total amount of Bytes outgoing from the server
    public long GetTotalOutgoingBytes() {
        long bytesOut = 0;
        try {
            for (int i = 0; i < serverListeners.size(); i++) {
                try {
                    bytesOut += ((TCPListener) serverListeners.get(i)).getBytesOut();
                }
                catch (Exception ex) {}
            }
        }
        catch (Exception ex) {}
        return bytesOut;
    }
    
    
}
