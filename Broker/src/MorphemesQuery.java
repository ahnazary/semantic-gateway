import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.jena.rdf.model.RDFNode;

public class MorphemesQuery extends FeatureVector{

	public MorphemesQuery(String inputAddress, String modelAddress,String SVMMethod) throws IOException {
		super(inputAddress, modelAddress, SVMMethod);
		
	}

	protected void morphemesQuery(String method) throws FileNotFoundException {
			
		for (int i = 0; i < JSONPairs.size(); i++) {
			String word = (String) JSONPairs.get(i).keySet().toArray()[0];
			//String word = keyList.get(i);
			//System.out.println(word);
			System.out.println(JSONPairs.get(i));
			char wordArr[] = new char[word.length()];

			for (int j = 0; j < word.length(); j++) {
				wordArr[j] = word.charAt(j);
			}

			for (int j = 0; j < word.length(); j++) {
				for (int k = j + 3; k < word.length(); k++) {
					String morphemes = "";

					for (int z = j; z <= k; z++) {
						morphemes += wordArr[z];
					}

					String sarefQueryFileExact = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
							+ "PREFIX om: <http://www.wurvoc.org/vocabularies/om-1.8/> "
							+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
							+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
							+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
							+ "PREFIX time: <http://www.w3.org/2006/time#> "
							+ "PREFIX saref: <https://w3id.org/saref#>  " + "PREFIX schema: <http://schema.org/>  "
							+ "PREFIX dcterms: <http://purl.org/dc/terms/>  "

							+ "SELECT ?subject \n" + "WHERE\n" + "{\n" + "{?subject ?predicate ?object}"
							// +"filter (contains(str(?object), \""+word+"\") || contains(str(?subject),
							// \""+word+"\") || contains(str(?predicate), \""+word+"\"))"
							// +"FILTER (regex(?object, \""+word+"\", \"i\" ) || regex(?predicate,
							// \""+word+"\", \"i\" ) || regex(?subject, \""+word+"\", \"i\" )) "
							// +"filter (contains(str(?object), \""+word+"\"))"
							+ "FILTER regex(?object, \"" + morphemes + "\", \"i\" ) " + "}";
					String sarefQueryFile = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
							+ "PREFIX om: <http://www.wurvoc.org/vocabularies/om-1.8/> "
							+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
							+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
							+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
							+ "PREFIX time: <http://www.w3.org/2006/time#> "
							+ "PREFIX saref: <https://w3id.org/saref#>  " + "PREFIX schema: <http://schema.org/>  "
							+ "PREFIX dcterms: <http://purl.org/dc/terms/>  "

							+ "SELECT ?subject \n" + "WHERE\n" + "{\n" + "{?subject ?predicate ?object}"

							// +"filter (contains(str(?object), \""+word+"\") || contains(str(?subject),
							// \""+word+"\") || contains(str(?predicate), \""+word+"\"))"
							// +"FILTER (regex(?object, \""+word+"\", \"i\" ) || regex(?predicate,
							// \""+word+"\", \"i\" ) || regex(?subject, \""+word+"\", \"i\" )) "
							// +"filter (contains(str(?object), '"+morphemes+"'))"
							// +"FILTER regex(?object, \" "+morphemes+" \", \"i\" ) "
							+ "FILTER regex(?object, \" " + morphemes + " \", \"i\" ) "
							// +"FILTER regex(?object, \"\\b"+morphemes+"\\b\" ) "
							// +"FILTER regex(?object,'"+morphemes+"') "
							+ "}";

					Map<RDFNode, float[]> map = new HashMap<RDFNode, float[]>();
					if (j == 0 && k == word.length() - 1) {

						map = resultsMap(sarefQueryFileExact, model, 1f);
						System.out.println(morphemes);

						for (Entry<RDFNode, float[]> pair : map.entrySet()) {
							
							if(method == "SVM" && SVM.classificationResult(MatrixUtils.createRealMatrix(new double[][] {{pair.getValue()[0] , pair.getValue()[1]}})) == 1) {
							System.out.println("    Approved by SVM");
							System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
											pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));					
							}
							
							else if (method == "SVM"){
								System.out.println("    Not Approved by SVM");
								System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
												pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));
							}
							else if(method == "WSVM" && WeightedSVM.classificationResult(MatrixUtils.createRealMatrix(new double[][] {{pair.getValue()[0] , pair.getValue()[1]*WeightedSVM.multiplier(TRAINING_DATA)}})) == 1) {
								System.out.println("    Approved by Weighted SVM");
								System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
												pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));					
							}
							
							else if (method == "WSVM"){
								System.out.println("    Not Approved by Weighted SVM");
								System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
												pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));
							}
						}
						
					} 
					else {

						map = resultsMap(sarefQueryFile, model, surfaceSimilarity(word, morphemes));
						System.out.println(morphemes);
						for (Entry<RDFNode, float[]> pair : map.entrySet()) {
							
							if(method == "SVM" && SVM.classificationResult(MatrixUtils.createRealMatrix(new double[][] {{pair.getValue()[0] , pair.getValue()[1]}})) == 1) {
							System.out.println("    Approved by SVM");
							System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
											pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));
							}
							else if(method == "SVM"){
								System.out.println("    Not Approved by SVM");
								System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
												pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));
							}
							else if(method == "WSVM" && WeightedSVM.classificationResult(MatrixUtils.createRealMatrix(new double[][] {{pair.getValue()[0] , pair.getValue()[1]*WeightedSVM.multiplier(TRAINING_DATA)}})) == 1) {
								System.out.println("    Approved by Weighted SVM" );
								System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
												pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));
							}
							else if(method == "WSVM"){
								System.out.println("    Not Approved by weighted SVM");
								System.out.println(String.format("     Key (URI) is: %s     Value (features Vector) is : %s",
												pair.getKey() + "\n", Arrays.toString(pair.getValue()) + "\n"));
							}
						}
					}
				}
			}
		}
	}
	
	//this method queries over the model and generates the result array which stores all the URIs and is used to calculate popularity feature
	protected void generateMorphemesResultsArr() throws FileNotFoundException {
	
		for (int i = 0; i < JSONPairs.size(); i++) {
			String word = (String) JSONPairs.get(i).keySet().toArray()[0];
			char wordArr[] = new char[word.length()];
	
			for (int j = 0; j < word.length(); j++) {
				wordArr[j] = word.charAt(j);
			}
	
			for (int j = 0; j < word.length(); j++) {
				for (int k = j + 3; k < word.length(); k++) {
					String morphemes = "";
	
					for (int z = j; z <= k; z++) {
						morphemes += wordArr[z];
					}
	
					String sarefQueryFileExact = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
							+ "PREFIX om: <http://www.wurvoc.org/vocabularies/om-1.8/> "
							+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
							+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
							+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
							+ "PREFIX time: <http://www.w3.org/2006/time#> "
							+ "PREFIX saref: <https://w3id.org/saref#>  " + "PREFIX schema: <http://schema.org/>  "
							+ "PREFIX dcterms: <http://purl.org/dc/terms/>  "
	
							+ "SELECT ?subject \n" + "WHERE\n" + "{\n" + "{?subject ?predicate ?object}"
							// +"filter (contains(str(?object), \""+word+"\") || contains(str(?subject),
							// \""+word+"\") || contains(str(?predicate), \""+word+"\"))"
							// +"FILTER (regex(?object, \""+word+"\", \"i\" ) || regex(?predicate,
							// \""+word+"\", \"i\" ) || regex(?subject, \""+word+"\", \"i\" )) "
							// +"filter (contains(str(?object), \""+word+"\"))"
							+ "FILTER regex(?object, \"" + morphemes + "\", \"i\" ) " + "}";
					String sarefQueryFile = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
							+ "PREFIX om: <http://www.wurvoc.org/vocabularies/om-1.8/> "
							+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
							+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
							+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
							+ "PREFIX time: <http://www.w3.org/2006/time#> "
							+ "PREFIX saref: <https://w3id.org/saref#>  " + "PREFIX schema: <http://schema.org/>  "
							+ "PREFIX dcterms: <http://purl.org/dc/terms/>  "
	
							+ "SELECT ?subject \n" + "WHERE\n" + "{\n" + "{?subject ?predicate ?object}"
	
							// +"filter (contains(str(?object), \""+word+"\") || contains(str(?subject),
							// \""+word+"\") || contains(str(?predicate), \""+word+"\"))"
							// +"FILTER (regex(?object, \""+word+"\", \"i\" ) || regex(?predicate,
							// \""+word+"\", \"i\" ) || regex(?subject, \""+word+"\", \"i\" )) "
							// +"filter (contains(str(?object), '"+morphemes+"'))"
							+ "FILTER regex(?object, \" " + morphemes + " \", \"i\" ) "
							// +"FILTER regex(?object, \"\\b"+morphemes+"\\b\" ) "
							// +"FILTER regex(?object,'"+morphemes+"') "
							+ "}";
	
					if (j == 0 && k == word.length() - 1) {
						URIs = resultsArr(sarefQueryFileExact, model);
					} else {
						URIs = resultsArr(sarefQueryFile, model);
					}
				}
			}
		}
	}

}
