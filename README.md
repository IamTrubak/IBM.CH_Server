# IBM.CH_Server
End Of Transmission (0x04) na konci každý zprávy (musí se kontrolovat, jestli uživatel nezadal do zprávy EOT character)
Zprávy se posílají v JSON formátu převedený na string v utf-8 bytekódu.
message example: {"headers": ["type", responses], "data": b'message data'}

Funkce a proměnné začínající _ jsou private (např.: _foo() je private, ale foo() je public) 
GDScript nepodporuje private a public prvky, tak se budou rozlišovat pomocí "_"

Response kódy od serveru jsou jako int v headers
Server kódy:

0xx Utility
000-Ping

1xx Informational
100-Continue
110-Server Shutting Down - Shut Down
111-Server Shutting Down - Restarting
112-Server Shutting Down - Rebooting
120-No More allowed Connections


2xx Success
200-OK
210-Accepted
220-Authorised

3xx Client Error
300-Bad Request
310-Request Timeout
320-Too Many Requests
330-Unauthorised - Possible Authorisation
331-Unauthorised - Impossible Authorisation
340-Not Found

4xx Server Error
400-Internal Server Error
410-Unavailable
420-Not Inmplemented
430-Rejected
