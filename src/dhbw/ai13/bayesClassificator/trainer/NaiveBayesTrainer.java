package dhbw.ai13.bayesClassificator.trainer;



import java.util.ArrayList;


import dhbw.ai13.bayesClassificator.naiveBayes.*;;

/**
 * Der Trainer ist das Zentrale Objekt, hier sind alle bisher zum Training genutzten Objekte, sowie die Matrix
 * gespeichert.
 * @author Tim Tiede
 */

public class NaiveBayesTrainer {
	private Matrix database;
	private ArrayList<TrainingsSet> trainingsSets;
	private int numberOfIntensity;
	private int numberOfFrequences;
	private int timeSteps;
	private int numberOfPhonems;
	
	
	//constructor
	public NaiveBayesTrainer(int numberOfIntensity, int numberOfFrequences, int timeSteps, int numberOfPhonems){
		this.numberOfIntensity = numberOfIntensity; //notice it will devided /10
		this.timeSteps = timeSteps;
		this.numberOfPhonems = numberOfPhonems;
		database = new Matrix(numberOfIntensity, numberOfFrequences, timeSteps, numberOfPhonems);
		this.numberOfFrequences = numberOfFrequences/8;
		trainingsSets = new ArrayList<TrainingsSet>();
	}
	
	
	//train the Database with a new stream
	public void trainDatabase(double[][] stream, String name){
		if(check(stream)){
			addToTrainingsSet(stream, name);
		}
	}
	
	//check stream
	private boolean check(double[][] stream){
		if(stream.length == timeSteps){
			if(stream[0].length == numberOfFrequences){
				//System.out.println("stream gecheckt");
				return true;
			}else{
				System.out.println("Trainingsstream hat die falsche Anzahl von Reihen!!");
				return false;
			}
		}else{
			System.out.println("Trainingsstream hat die falsche LÃ¤nge!!");
			return false;
		}
	}
	
	//Adds to the right TrainingsSet
	private void addToTrainingsSet(double[][] stream, String name){
		int index = Integer.MAX_VALUE;
		for(int i=0;i<trainingsSets.size();i++){
			if(trainingsSets.get(i).getName().equals(name))index = i;
		}
		if(index<trainingsSets.size()){
			trainingsSets.get(index).addTrainingValue(stream);
			//System.out.println("added to index: " + 0);
		}else{
			//System.out.println("else fall");
			index = trainingsSets.size();
			//System.out.println("index: " +index);
			refreshDatabase(numberOfIntensity, numberOfFrequences, timeSteps, (numberOfPhonems+1));
			//System.out.println("database refreshed");
			trainingsSets.add(new TrainingsSet(index, name, database));
			//System.out.println("Ins Trainingsset eingefügt");
			trainingsSets.get(index).addTrainingValue(stream);
			//System.out.println("added to index: " + index);
			//refresh the new Database
			
		}
	}
	
	//create new Database and train it
	private void refreshDatabase(int numberOfIntensity, int numberOfFrequences, int timeSteps, int numberOfPhonems){
		this.numberOfIntensity = numberOfIntensity;
		this.numberOfFrequences = numberOfFrequences;
		this.timeSteps = timeSteps;
		this.numberOfPhonems = numberOfPhonems;
		//System.out.println("datenbanck erneuern");
		database = null;
		//System.out.println("datenbank null");
		//System.out.println(numberOfIntensity + ", " + numberOfFrequences + ", " + timeSteps + ", " + numberOfPhonems);
		database = new Matrix(numberOfIntensity, numberOfFrequences, timeSteps, numberOfPhonems);
		//System.out.println("datenbank erneuert!");
		for(int i=0;i<trainingsSets.size();i++){
			trainingsSets.get(i).refresh();
		}
		
	}
		
	
	//get the Database
	public Matrix getDatabase(){
		return database;
	}
	
}
