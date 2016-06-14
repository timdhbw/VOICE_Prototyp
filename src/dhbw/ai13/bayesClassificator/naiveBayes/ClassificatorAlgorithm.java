package dhbw.ai13.bayesClassificator.naiveBayes;

import java.util.ArrayList;


/**
 * Erklaerung
 * import stream[Real Time][Frequence] = Intensity
 * Matrix database[Intensity][Frequence][time in phoneme][index of phoneme] = probability
 * times = how many time steps is the length of a possible phonem
 * @author Tim Tiede
 */


public class ClassificatorAlgorithm {
	private Matrix database;
	private double[][] stream;
	private double minimumPossibility;
	
	
	//Constructors
	public ClassificatorAlgorithm(Matrix database, double[][] stream){
		this.database = database;
		double[][] helpStream =  HelpMethod.convertStream(stream, database.getNumOfIntensities(), database.getMaxIntensity()/database.getNumOfIntensities(), stream[0].length/database.getNumOfFrequencies());
		this.stream = HelpMethod.streamNormalizer(helpStream);
		this.minimumPossibility = 5;//default 
		//System.out.println(minimumPossibility);
		//System.out.println("Times and minimum Possibility are default! Times: " + this.times +" minimumPossibility: " + this.minimumPossibility);
	}
	
	public ClassificatorAlgorithm(Matrix database, double[][] stream, double minimumPossibility){
		this.database = database;
		double[][] helpStream = HelpMethod.convertStream(stream, database.getNumOfIntensities(), database.getMaxIntensity()/database.getNumOfIntensities(), stream[0].length/database.getNumOfFrequencies());
		this.stream = HelpMethod.streamNormalizer(helpStream);
		this.minimumPossibility = minimumPossibility;
	}
	
	
	//=================================================================================================
	//Gives an arrayList of final Results, that could be possible
	//==================================================================================================
	
	public ArrayList<Result> getBestResults(){
		ArrayList<Result> bestResult = new ArrayList<Result>();
		//startobjekte suchen, (-times, damit givenProbability nicht outOfBounce geht
		//for timeSteps
		for(int i=0;i<=(stream.length-database.getNumOfTimeSteps());i++){
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
	 * Eine Spalte des Streams wird durchsucht. Die Ergebnisse werden fuer je die ersten Zeitspalte der Matrix
	 * berechnet (Frequenzen weren durchgegangen)(berechnung nach Bayes mit mal)
	 * Dieser Array wird nach Wahrscheinlichkeiten durchsucht, die ueber dem Minimum liegen, fuer die jeweiligen Indexe
	 * werden Results erstellt;
	 */
	private ArrayList<Result> findStartResult(int timeIndex) {
		//System.out.println("in find Start result");
		ArrayList<Result> res = new ArrayList<Result>();
		double[] startStream = stream[timeIndex];
		double[] buffer =  database.getProbArray((int)startStream[0], 0);
		
		//calculate probabilitys
		for (int i = 1; i < startStream.length; i++) {
			buffer = this.convert(buffer, database.getProbArray((int)startStream[i], i));
		}
		//System.out.println(buffer[0]);
		//create results for good probabilitys
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] >= minimumPossibility) {
				//System.out.println("added");
				res.add(new Result(database.getPhonem()[i], buffer[i], i, timeIndex));
			}
		}
		return res;
	}
	
	// multiply the two "start arrays"
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
		 * Der Methode wird ein StartResult uebergeben und , an welchem Zeitindex im Stream dieses ist.
		 * In der Database werden nun von diesem Startzeitpunkt an fuer times Zeitschritte die Wahrscheinlichkeiten
		 * errechnet.
		 */
		private Result givenProbability(Result startResult){
			//stream[t = time][i = frequence] j = intensity
			//System.out.println("in given probability");
//			System.out.println("TimeIndex: " + timeIndex);
			int intensity;
			double pos = 1;
			
			int timeInMatrix = 1;
			//time
			for(int t=startResult.getTimeIndex()+1;t<(database.getNumOfTimeSteps()+startResult.getTimeIndex())&& t<stream.length;t++){
				//System.out.println("TimeIndex: " + t);
				for(int i=0;i<stream[t].length;i++){
					intensity = (int)stream[t][i];
					pos = pos*(database.getValue(intensity, i, timeInMatrix, startResult.getIndex()));
				}
				//stop if OutOfBounce
					timeInMatrix++;
			}
			pos = pos*100*startResult.getProbability();
			return new Result(startResult.getName(), pos, startResult.getIndex() , startResult.getTimeIndex());
		}
}
