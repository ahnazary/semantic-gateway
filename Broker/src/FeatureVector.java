import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;

public class FeatureVector {
	
	private String inputAddress;
	private String modelAddress;
	private String SVMMethod;
	private static String input;
	protected final Model model;
	protected static ArrayList<RDFNode> URIs = new ArrayList<RDFNode>();
	protected static ArrayList<HashMap<String, Object>> JSONPairs = new ArrayList<HashMap<String,Object>>();
	
	static final double [][][] TRAINING_DATA = {{{0.5555, 0.04175} , {+1}}, 							
												{{0.4165, 0.06217} , {+1}},
												{{0.4154, 0.05565} , {+1}},
												{{0.5239, 0.06456} , {+1}},
												{{0.4894, 0.04456} , {+1}},
												{{0.3495, 0.04456} , {+1}},
												{{0.4136, 0.03411} , {+1}},
												{{0.7794, 0.05411} , {+1}},
												{{0.4469, 0.07411} , {+1}},
												{{0.2269, 0.09411} , {+1}},
												{{0.2269, 0.05411} , {+1}},
											
												{{0.1465, 0.01789} , {-1}},
												{{0.1469, 0.00568} , {-1}},
												{{0.0462, 0.01567} , {-1}},
												{{0.0465, 0.00176} , {-1}},
												{{0.2654, 0.03519} , {-1}},
												{{0.2465, 0.02299} , {-1}},
												{{0.2796, 0.02274} , {-1}},
												{{0.3945, 0.01274} , {-1}},
												{{0.1954, 0.03238} , {-1}},
												{{0.6954, 0.00828} , {-1}},
												};


	@SuppressWarnings("deprecation")
	FeatureVector(String inputAddress, String modelAddress,String SVMMethod) throws IOException {
		this.inputAddress = inputAddress;
		this.modelAddress = modelAddress;
		this.SVMMethod = SVMMethod;
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		model = FileManager.get().loadModel(modelAddress); // model that query request is sent to

	}
	
	@SuppressWarnings("unused")
	public void start() throws IOException {
		
		ReadJSON rs = new ReadJSON(inputAddress);
		JSONPairs = rs.getJSONPairs();
		final SVM svm = new SVM(TRAINING_DATA);
		final WeightedSVM wsvm = new WeightedSVM(TRAINING_DATA);
		
		
		MorphemesQuery morphemesQuery = new MorphemesQuery(inputAddress, modelAddress, SVMMethod); 
		DateTimeQuery dateTimeQuery = new DateTimeQuery(inputAddress, modelAddress, SVMMethod); 
		morphemesQuery.generateMorphemesResultsArr();
		dateTimeQuery.generatedateTimeResultsArr();
		morphemesQuery.morphemesQuery(SVMMethod);
		dateTimeQuery.dateTimeQuery(SVMMethod);
		
		System.out.println("Total number of URIs is : " + URIs.size());
	}
	
	
	// all of the URIs generated are stored in this ArrayList
	protected ArrayList<RDFNode> resultsArr(String inputQuery, Model model) {
		Query query = QueryFactory.create(inputQuery);

		QueryExecution qExe = QueryExecutionFactory.create(query, model);
		ResultSet resultsOutput = qExe.execSelect();

		for (; resultsOutput.hasNext();) {

			QuerySolution soln = resultsOutput.nextSolution();
			RDFNode subject = soln.get("subject");
			URIs.add(subject);
		}
		return URIs;
	}

	// this Map saves a specific URI with its corresponding FeatureVector
	protected Map<RDFNode, float[]> resultsMap(String inputQuery, Model model, float similarityFeature) {
		Query query = QueryFactory.create(inputQuery);

		QueryExecution qExe = QueryExecutionFactory.create(query, model);
		ResultSet resultsOutput = qExe.execSelect();

		Map<RDFNode, float[]> map = new HashMap<RDFNode, float[]>();

		for (; resultsOutput.hasNext();) {

			QuerySolution soln = resultsOutput.nextSolution();
			RDFNode subject = soln.get("subject");

			float popularity = popularity(subject, URIs);

			float[] floatArray = { similarityFeature, popularity };
			map.put(subject, floatArray);
		}
		return map;
	}

	protected float surfaceSimilarity(String word, String morphemes) {

		float z = ((float) morphemes.length()) / ((float) word.length());
		return z;

	}

	private float popularity(RDFNode URI, ArrayList<RDFNode> URIs) {

		int repetition = 0;
		for (int i = 0; i < URIs.size(); i++) {
			if (URI.equals(URIs.get(i))) {
				repetition++;
			}
		}

		float result;
		result = ((float) repetition) / ((float) URIs.size());
		return result;
	}

	@SuppressWarnings("unused")
	private static void appendStrToFile(String filePath, String str) {
		try (FileWriter fw = new FileWriter("Output", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(str);

		} catch (IOException e) {
			System.out.println("Exception Occurred" + e);
		}
	}

	protected static boolean isValidDate(String inDate) {

		boolean result = false;
		;
		ArrayList<String> validFormats = new ArrayList<String>();
		validFormats.add("HH:mm:ss");
		validFormats.add("dd-MM-yyyy HH:mm:ss:ms");
		validFormats.add("dd-MM-yyyy HH:mm:ss");
		validFormats.add("dd-MM-yyyy HH:mm");
		validFormats.add("dd-MM-yyyy");

		for (int i = 0; i < validFormats.size(); i++) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(validFormats.get(i));
			dateFormat.setLenient(false);
			try {
				dateFormat.parse(inDate.trim());
			} catch (ParseException pe) {

				if (i == validFormats.size())
					result = false;

				else
					continue;
			}
			result = true;
			break;
		}
		return result;
	}
	
	public ArrayList<RDFNode> getURIs() {
		return URIs;	
	}

	public static String getInput() {
		return input;
	}

	public static void setInput(String input) {
		FeatureVector.input = input;
	}
}

