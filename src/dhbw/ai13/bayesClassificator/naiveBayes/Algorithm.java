package dhbw.ai13.bayesClassificator.naiveBayes;

import java.util.ArrayList;

/**
 * Erklaerung
 * import string[Real Time][Row of Spectometre] = Frequence
 * Database database[Intensity][Row of Spectometre] = BayesMatrix -> BayesMatrix[time in phoneme][index of phoneme] = probability
 * times = how many time steps is the length of a possible phonem
 * @author Tim Tiede
 */


public class Algorithm {
	private Database database;
	private double[][] stream;
	private int times;
	private double minimumPossibility;
	private static final int TIME_COUNT = 2;
	
	
	//Constructors
	public Algorithm(Database database, double[][] stream){
		this.database = database;
		this.stream = stream;
		this.times = 10; //default
		this.minimumPossibility = 0.005;//default
		System.out.println("Times and minimum Possibility are default! Times: " + this.times +" minimumPossibility: " + this.minimumPossibility);
	}
	
	public Algorithm(Database database, double[][] stream, int times, double minimumPossibility){
		this.database = database;
		this.stream = stream;
		this.times = times;
		this.minimumPossibility = minimumPossibility;
	}
	
	
	//=================================================================================================
	//Gives an arrayList of final Results, that could be possible
	//==================================================================================================
	
	public ArrayList<Result> getBestResults(){
		ArrayList<Result> bestResult = new ArrayList<Result>();
		//startobjekte suchen, (-times, damit givenProbability nicht outOfBounce geht
		//for timeSteps
		for(int i=0;i<(stream.length-times);i++){
			bestResult.addAll(findStartResult(i));
		}
		
		//Wahrscheinlichkeit der besten Startobjekte berechnen
		for(int i=0;i<bestResult.size();i++){
			bestResult.set(i, givenProbability(bestResult.get(i)));
		}
		return bestResult;
	}
	
	
	
	//====================================================================================================
	//For finding the Start
	//=====================================================================================================
	//gives results, that can be a result
	/*
	 * Eine Spalte des Streams wird durchsucht. Die Ergebnisse werden fuer je die ersten Zeitspalte der BayesMatrix
	 * des Stream Ergebnisses rausgesucht und die einzelenen Spalten zusammengefuegt (nach Bayes mit mal)
	 * Dieser Array wird nach Wahrscheinlichkeiten durchsucht, die ueber dem Minimum liegen, fuer die jeweiligen Indexe
	 * werden Results erstellt;
	 */
	private ArrayList<Result> findStartResult(int timeIndex) {
		ArrayList<Result> res = new ArrayList<Result>();
		double[]startStream = stream[timeIndex];
		double[] buffer =  database.getData((int)startStream[0], 0).getBeginningArray();
		
		//calculate probabilitys
		for (int i = 1; i < startStream.length; i++) {
			buffer = this.convert(buffer, database.getData((int)startStream[i], i).getBeginningArray());
		}
		//create results for good probabilitys
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] >= minimumPossibility) {
				res.add(new Result(database.getNameOfRow()[i], buffer[i], i, timeIndex));
			}
		}
		return res;
	}
	
	// get naive Bayes possibility of two arrays
		private double[] convert(double[] a, double[] b) {
			double[] c = new double[a.length];
			for (int i = 0; i < a.length; i++) {
				c[i] = a[i] * b[i];
			}
			return c;
		}

		
		//========================================================================================
		//Creating final probability by checking the Start Result
		//========================================================================================
		
		/*
		 * Der Methode wird ein StartResult uebergeben und damit, an welchem Zeitindex dieses im Stream ist.
		 * In der Database werden nun von diesem Startzeitpunkt an fuer times Zeitschritte die Wahrscheinlichkeiten
		 * errechnet.
		 */
		private Result givenProbability(Result startResult){
			//stream[t = time][i = frequence] j = intensity
			int timeIndex = startResult.getTimeIndex();
			int intensity;
			int countIndex = 0;
			double pos = 1;
			double countHelper = 0;
			
			int timeInBayesMatrix = 1;
			//time
			for(int t=timeIndex;t<(times+timeIndex);t++){
				for(int i=0;i<stream[0].length;i++){
					intensity = (int)stream[t][i];
					countHelper = countHelper + (database.getData(i,intensity).getPossibility(timeInBayesMatrix, startResult.getIndex()));
					countIndex++;
					if(countIndex == TIME_COUNT){
						pos = pos * countHelper;
						countHelper = 0;
						countIndex = 0;
					}
				}
				//stop if OutOfBounce
				if(t==(stream.length-1)){
					System.out.println("OutOfBounce: Stream is to short for given Probability");
					t = Integer.MAX_VALUE;
				}
					timeInBayesMatrix++;
			}
			pos = pos*100*startResult.getProbability();
			return new Result(startResult.getName(), pos, startResult.getIndex(), timeIndex);
		}
}
