import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) throws IOException, ParseException{
		
		SurfaceForm Test = new SurfaceForm("/home/amirhossein/Documents/GitHub/semantic-broker/Broker/input","/home/amirhossein/Documents/GitHub/semantic-broker/Broker/saref.ttl");
		
		//WriteJSON Sensors = new WriteJSON();
		//Sensors.writeJSONFile();
		
		ReadJSON rs = new ReadJSON("/home/amirhossein/Documents/GitHub/semantic-broker/Broker/Sensors.json");
		rs.returnKeys();
		
		//Test.exactQuery();
		//Test.morphemesQuery();
		
	}	
}
