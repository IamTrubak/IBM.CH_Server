# IBM.CH_Server
End Of Transmission (0x04) na konci každý zprávy (musí se kontrolovat, jestli uživatel nezadal do zprávy EOT character) <br>
Zprávy se posílají v JSON formátu převedený na string v utf-8 bytekódu.<br>
message example: {"headers": ["type", responses], "data": b'message data'}<br>

Funkce a proměnné začínající _ jsou private (např.: _foo() je private, ale foo() je public) <br>
GDScript nepodporuje private a public prvky, tak se budou rozlišovat pomocí _<br>

Response kódy od serveru jsou jako int v headers<br>
Server kódy:<br>

0xx Utility<br>
000-Ping<br>

1xx Informational<br>
100-Continue<br>
110-Server Shutting Down - Shut Down<br>
111-Server Shutting Down - Restarting<br>
112-Server Shutting Down - Rebooting<br>
120-No More allowed Connections<br>


2xx Success<br>
200-OK<br>
210-Accepted<br>
220-Authorised<br>

3xx Client Error<br>
300-Bad Request<br>
310-Request Timeout<br>
320-Too Many Requests<br>
330-Unauthorised - Possible Authorisation<br>
331-Unauthorised - Impossible Authorisation<br>
340-Not Found<br>

4xx Server Error<br>
400-Internal Server Error<br>
410-Unavailable<br>
420-Not Inmplemented<br>
430-Rejected<br>
