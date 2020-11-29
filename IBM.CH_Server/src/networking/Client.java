package networking;

import networking.Extasys.Network.TCP.Server.Listener.Exceptions.*;
import networking.Extasys.Network.TCP.Server.Listener.TCPClientConnection;

public class Client
{
	
	//TODO kompletnì pøedìlat pro potøeby IBM.CH_Serveru

    private String fUsername;
    private TCPClientConnection fConnection;

    public Client(String username, TCPClientConnection connection)
    {
        fUsername = username;
        fConnection = connection;
    }

    public String getUsername()
    {
        return fUsername;
    }

    public TCPClientConnection getConnection()
    {
        return fConnection;
    }

    public void SendData(String data) throws ClientIsDisconnectedException, OutgoingPacketFailedException
    {
        fConnection.SendData(data);
    }
}