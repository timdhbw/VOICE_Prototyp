package dhbw.ai13.bayesClassificator.trainer;



import java.util.ArrayList;


import dhbw.ai13.bayesClassificator.naiveBayes.*;;

/*
 * Der Trainer ist das Zentrale Objekt, hier sind alle bisher zum Training genutzten Objekte, sowie die Database
 * gespeichert.
 */

public class NaiveBayesTrainer {
	private Database database;
	private ArrayList<TrainingsSet> trainingsSets;
	private int numberOfIntensity;
	private int numberOfFrequences;
	private int timeSteps;
	private int numberOfPhonems;
	
	
	//constructor
	public NaiveBayesTrainer(int numberOfIntensity, int numberOfFrequences, int timeSteps, int numberOfPhonems){
		this.numberOfIntensity = numberOfIntensity;
		this.numberOfFrequences = numberOfFrequences;
		this.timeSteps = timeSteps;
		this.numberOfPhonems = numberOfPhonems;
		database = new Database(numberOfIntensity, numberOfFrequences, timeSteps, numberOfPhonems);
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
				return true;
			}else{
				System.out.println("Trainingsstream hat die falsche Anzahl von Reihen!!");
				return false;
			}
		}else{
			System.out.println("Trainingsstream hat die falsche Länge!!");
			return false;
		}
	}
	
	//Adds to the rigt TrainingsSet
	private void addToTrainingsSet(double[][] stream, String name){
		int index = Integer.MAX_VALUE;
		for(int i=0;i<trainingsSets.size();i++){
			if(trainingsSets.get(i).getName().equals(name))index = i;
		}
		if(index<trainingsSets.size()){
			trainingsSets.get(index).addTrainingValue(stream);
		}else{
			index = trainingsSets.size();
			trainingsSets.add(new TrainingsSet(index, name, database));
			trainingsSets.get(index).addTrainingValue(stream);
			//refresh the new Database
			refreshDatabase(numberOfIntensity, numberOfFrequences, timeSteps, (numberOfPhonems+1));
		}
	}
	
	//create new Database and train it
	private void refreshDatabase(int numberOfIntensity, int numberOfFrequences, int timeSteps, int numberOfPhonems){
		this.numberOfIntensity = numberOfIntensity;
		this.numberOfFrequences = numberOfFrequences;
		this.timeSteps = timeSteps;
		this.numberOfPhonems = numberOfPhonems;
		database = new Database(numberOfIntensity, numberOfFrequences, timeSteps, numberOfPhonems);
		for(int i=0;i<trainingsSets.size();i++){
			trainingsSets.get(i).refresh();
		}
		
	}
		
	
	//get the Database
	public Database getDatabase(){
		return database;
	}
	
}
