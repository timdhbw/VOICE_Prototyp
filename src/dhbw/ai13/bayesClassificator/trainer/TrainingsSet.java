package dhbw.ai13.bayesClassificator.trainer;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import dhbw.ai13.bayesClassificator.naiveBayes.*;

/*
 * TrainingsSet for one phonem
 * Das Trainingsmodell ist ein zweidimensionaler double Array mit double[Zeitschritt][reihe]=frequenz
 * Die Trainingsmodelle werden in einer ArrayList gespeichert.
 * 
 */

public class TrainingsSet {
	private ArrayList<double[][]> trainingValues;
	private int index;
	private String name;
	private Database database;
	
	//constructor
	public TrainingsSet(int index, String name, Database database){
		this.trainingValues = new ArrayList<double[][]>();
		this.index = index;
		this.name = name;
		this.database = database;
	}
	
	
	//trainingsmodellwird zugefügt
	public void addTrainingValue(double[][] train){
		trainingValues.add(train);
		
		for(int timeSteps=0;timeSteps<train.length;timeSteps++){
			for(int frequence=0;frequence<train[0].length;frequence++){
				
				double[] values = new double[trainingValues.size()];
				double counter = 0;
				
				for(int trainVal=0;trainVal<trainingValues.size();trainVal++){
					values[trainVal] = trainingValues.get(trainVal)[timeSteps][frequence];
					counter = counter + values[trainVal];
				}
				
				NormalDistribution normDistr = auxiliary(values, counter);
				//for every intensity one value of the BayesMatrix is chanced+
				for(int intensity=0;intensity<database.getNumberOfIntensity();intensity++){
					database.getData(intensity, frequence).setData(normDistr.density((double)intensity), timeSteps, frequence);
				}	
			}
		}
	}
	
	//train database again for phonem of this TrainingsSet
	public void refresh(){
		
		for(int timeSteps=0;timeSteps<trainingValues.get(0).length;timeSteps++){
			for(int row=0;row<trainingValues.get(0)[0].length;row++){
				
				double[] values = new double[trainingValues.size()];
				double counter = 0;
				
				for(int trainVal=0;trainVal<trainingValues.size();trainVal++){
					values[trainVal] = trainingValues.get(trainVal)[timeSteps][row];
					counter = counter + values[trainVal];
				}
				
				NormalDistribution normDistr = auxiliary(values, counter);
				//for every frequence one value of the BayesMatrix is chanced+
				for(int intensity=0;intensity<database.getNumberOfIntensity();intensity++){
					database.getData(intensity, row).setData(normDistr.density((double)intensity), timeSteps, row);
				}	
			}
		}
	}
	
	
	//get Normal Distribution
	private NormalDistribution auxiliary(double[] values, double counter){
		double mean = counter/((double)trainingValues.size());
		double standardDeviation;
		StandardDeviation sd = new StandardDeviation();
		standardDeviation = sd.evaluate(values);
		
		return new NormalDistribution(mean, standardDeviation);
		
	}
	
	//getter
	public int getIndex(){
		return index;
	}
	
	public String getName(){
		return name;
	}
	
}
