import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Main {
	
	public static void main(String[] args) throws IOException, ParseException {
	
		FeatureVector FV = new FeatureVector("Floor-example.json","Sargon.ttl", "SVM"); // 3rd field should be either "WSVM" or "SVM"	
		FV.start();
		System.out.println(FV.getURIs().size());
		
		
		JSONLDGenerator Test = new JSONLDGenerator(FV.getInputAddress(), "JSON-LD", FV.getApprovedURIs());
		File file = new File(Test.getJSONLDFilePath()); 
		file.delete();
		Test.Start();
	}			
}
