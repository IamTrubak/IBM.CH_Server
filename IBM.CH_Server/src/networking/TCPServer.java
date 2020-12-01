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

public class TCPServer {
	
	/*TODO kompletnì pøedìlat pro potøeby IBM.CH_Serveru
	 *	   na vzor ExtasysTCPServeru napsat nový easy to use server
	 *	   Zjednodušit Extasys codebase a zbavit je zbyteènýho kódu
	 */
	private Hashtable connectedClients = new Hashtable();
	private final ArrayList<TCPListener> listeners = new ArrayList<TCPListener>();
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
    
    
	public TCPListener AddListener(String name, InetAddress ipAddress, int port, int maxConnections, int readBufferSize, int connectionTimeOut, int backLog) {
        TCPListener listener = new TCPListener(name, ipAddress, port, maxConnections, readBufferSize, connectionTimeOut, backLog);
        listener.setMyTCPServer(this);
        listeners.add(listener);
        return listener;
    }
	
	public TCPListener AddListener(String name, int port, int maxConnections, int readBufferSize, int connectionTimeOut, int backLog) {
        TCPListener listener = new TCPListener(name, listenerIP, port, maxConnections, readBufferSize, connectionTimeOut, backLog);
        listener.setMyTCPServer(this);
        listeners.add(listener);
        return listener;
    }
	
	
	public void RemoveListener(String name) {
        for (int i = 0; i < listeners.size(); i++) {
            if (((TCPListener) listeners.get(i)).getName().equals(name)) {
                ((TCPListener) listeners.get(i)).Stop();
                listeners.remove(i);
                break;
            }
        }
    }
	
	
	//Start or restart 
	public void Start() throws IOException, Exception {
        Stop(false);
        try {
            //Start all listeners.
            for (int i = 0; i < listeners.size(); i++) {
                ((TCPListener) listeners.get(i)).Start();
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
    
    
    //internal stop method
    private void Stop(boolean forceStop) {
    	this.isRunning = false;
        //Stop all listeners.
        for (int i = 0; i < listeners.size(); i++) {
            if (!forceStop) {
                ((TCPListener) listeners.get(i)).Stop();
            }else {
                ((TCPListener) listeners.get(i)).ForceStop();
            }
        }

        try{
            serverThreadPool.getQueue().clear();
        }
        catch (Exception ex){}
    }
    
    
    public void Dispose() {
        Stop(false);
        serverThreadPool.shutdown();
    }
    
    
	public boolean isRunning() {
		return isRunning;
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
    
}
