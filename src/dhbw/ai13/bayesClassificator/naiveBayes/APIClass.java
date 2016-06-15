package dhbw.ai13.bayesClassificator.naiveBayes;

import java.util.ArrayList;

import dhbw.ai13.bayesClassificator.trainer.NaiveBayesTrainer;
import dhbw.ai13.spectrogram.SpectrogrammErsteller;

/**
 * 
 * @author Tim Tiede
 *
 *Diese Klasse ist dafür da, um den BayesKlassifikator zu nutzen.
 *Dafuer sind alle moeglichen Einstellungsmöglichkeiten abgebildet.
 *
 */

public class APIClass {
	//Eingangsdatei
	private String input;
	
	//wie viele Results maximal
	private int anzahlMaxResult = 5;
	//hier werden die ausgeschnittenen Phoneme gespeichert
	private String zielOrdner;
	//ordner mit den Trainingsdateien
	private String trainingsOrdner = "resources";
	
	//fuer die Datenbank
	private int numberOfIntensity = 100;
	private int numberOfFrequencies = 128;
	private int timeSteps = 3;
	private int numberOfPhonems = 0;

	//Wenn Intensitaet ueber diesem Wert liegt, wird sie auf diesen gesetzt
	private int maxIntensity = 1000;
	
	//Wert mit den die normalisierten Werte multipliziert werden, damit sie nicht zu klein werden
	private int normalizeMultiplicator;
	//hoechste prozentzahl, die ein normalisierter wert annehmen kann
	private int procent;
	
	//so viel wird zu einer Wahrscheinlichkeit addiert, damit es beim multiplizieren nicht null werden kann
	private double trainingsAddition = 0.7;
	//mindestwahrscheinlichkeit, um ein ResultObjekt zu erstellen
	private double minResult = 0;
	//mindestwahrscheinlichkeit, damit ein ResultObjekt ausgeschnitten wird
	private double minEndResult = 10;
	//so lang wird die ausgeschnittene Datei (in ms)
	private int cutTime = 200;
	
	//Angenommen Laenge eises timeSteps in ms (liegt wohl zwischen 10 und 11)
	private int timeStepLength = 10;

	//default Constructor
	public APIClass(){
		
	}
	
	
	//doClassifikation
	public void doClassifikation(String input){
		this.input = input;
		NaiveBayesTrainer nbt = new NaiveBayesTrainer(numberOfIntensity, numberOfFrequencies, timeSteps, 0);
		
		//For TrainingOrdner
		//Train
		
		numberOfPhonems = nbt.getDatabase().getPhonem().length;
		
		
		SpectrogrammErsteller creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/php10 zum testen.wav");
		double[][]d = creater.getSpectrogramData();
		
		ClassificatorAlgorithm alg = new ClassificatorAlgorithm(nbt.getDatabase(), d, minResult);
		ArrayList<Result> res = alg.getBestResults();
		int count = 1;
		for(int i=0;i<res.size() && i<anzahlMaxResult;i++){
			if(res.get(i).getProbability() > minEndResult){
				String p = zielOrdner + "/" + res.get(i).getName() +count+".wav";
				HelpMethod.copyAudio(input, p, res.get(i).getTimeIndex(), cutTime, timeStepLength);
				count++;
				System.out.println(res.get(i).toString() + "      count: " +  count);	
			}		
		}
		
	}

	//veraenderungen
	public int getAnzahlMaxResult() {
		return anzahlMaxResult;
	}


	public void setAnzahlMaxResult(int anzahlMaxResult) {
		this.anzahlMaxResult = anzahlMaxResult;
	}


	public String getZielOrdner() {
		return zielOrdner;
	}


	public void setZielOrdner(String zielOrdner) {
		this.zielOrdner = zielOrdner;
	}


	public String getTrainingsOrdner() {
		return trainingsOrdner;
	}


	public void setTrainingsOrdner(String trainingsOrdner) {
		this.trainingsOrdner = trainingsOrdner;
	}


	public int getNumberOfIntensity() {
		return numberOfIntensity;
	}


	public void setNumberOfIntensity(int numberOfIntensity) {
		this.numberOfIntensity = numberOfIntensity;
	}


	public int getNumberOfFrequencies() {
		return numberOfFrequencies;
	}


	public void setNumberOfFrequencies(int numberOfFrequencies) {
		this.numberOfFrequencies = numberOfFrequencies;
	}


	public int getTimeSteps() {
		return timeSteps;
	}


	public void setTimeSteps(int timeSteps) {
		this.timeSteps = timeSteps;
	}


	public int getNumberOfPhonems() {
		return numberOfPhonems;
	}


	public int getMaxIntensity() {
		return maxIntensity;
	}


	public void setMaxIntensity(int maxIntensity) {
		this.maxIntensity = maxIntensity;
	}


	public int getNormalizeMultiplicator() {
		return normalizeMultiplicator;
	}


	public void setNormalizeMultiplicator(int normalizeMultiplicator) {
		this.normalizeMultiplicator = normalizeMultiplicator;
	}


	public int getProcent() {
		return procent;
	}


	public void setProcent(int procent) {
		this.procent = procent;
	}


	public double getTrainingsAddition() {
		return trainingsAddition;
	}


	public void setTrainingsAddition(double trainingsAddition) {
		this.trainingsAddition = trainingsAddition;
	}


	public double getMinResult() {
		return minResult;
	}


	public void setMinResult(double minResult) {
		this.minResult = minResult;
	}


	public double getMinEndResult() {
		return minEndResult;
	}


	public void setMinEndResult(double minEndResult) {
		this.minEndResult = minEndResult;
	}


	public int getCutTime() {
		return cutTime;
	}


	public void setCutTime(int cutTime) {
		this.cutTime = cutTime;
	}


	
}
