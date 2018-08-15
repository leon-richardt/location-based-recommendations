NOTE:

Bitte nutzt die Punkt (.) Notation für Gleitkommazahlen und für Währungen.

Beim erstellen von BSP Daten mit den Excel-Vorlagen ist es nicht Notwenig ID'S bei Event, Genre, Venue, Cocktail zu vergeben. 
Diese werden Automatisch gesetzt noch folgendem Schema:
Genre: StartID 1, +1 für jeden weiteren Eintrag
Cocktail: StartID 1, +1 für jeden weiteren Eintrag
Venue: StartID 100, +1 für jeden weiteren Eintrag
Event: StartID 1000, +1 für jeden weiteren Eintrag.

Eine Anleitung wie man unter Windows JDBC installiert mit Maven findet man hier:
https://www.microsoft.com/en-us/sql-server/developer-get-started/java/windows/

Verbindungseinstellung zur Datenbank:
user: hegerdes
PW in WhatsApp

Für JDBC:
jdbc:sqlserver://uni-os-he.database.windows.net:1433;database=LBR;user=hegerdes@uni-os-he;password={your_password_here};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;

Für NODE.jd:
Driver={ODBC Driver 13 for SQL Server};Server=tcp:uni-os-he.database.windows.net,1433;Database=LBR;Uid=hegerdes@uni-os-he;Pwd={your_password_here};Encrypt=yes;TrustServerCertificate=no;Connection Timeout=30;

Für PHP:
// PHP Data Objects(PDO) Sample Code:
try {
    $conn = new PDO("sqlsrv:server = tcp:uni-os-he.database.windows.net,1433; Database = LBR", "hegerdes", "{your_password_here}");
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
}
catch (PDOException $e) {
    print("Error connecting to SQL Server.");
    die(print_r($e));
}


// SQL Server Extension Sample Code:
$connectionInfo = array("UID" => "hegerdes@uni-os-he", "pwd" => "{your_password_here}", "Database" => "LBR", "LoginTimeout" => 30, "Encrypt" => 1, "TrustServerCertificate" => 0);
$serverName = "tcp:uni-os-he.database.windows.net,1433";
$conn = sqlsrv_connect($serverName, $connectionInfo);



Musterabfrage
Die Query Gibt die 20 nächsten Events mit Name, ID, VenueID und Venue_name aus:
Die Distanz bezieht sich natürlich nur auf Luftlinie

SELECT Top 20
        (
            6371 * acos
            (
                cos(
                    radians(*Dein Breitengrad*)
                ) 
                * cos(
                    radians( lat)
                ) * cos(
                    radians( lng) - radians( *Dein Längengrad* )
                ) + sin(
                    radians( *Dein Breitengrad* )
                ) * sin(
                    radians( lat)
                )
            )
        ) AS distance, event_id, event_name, event_description, Venue.venue_id, Venue.venue_name
FROM Event, Venue
where Event.venue_id = Venue.venue_id
ORDER BY distance ASC;