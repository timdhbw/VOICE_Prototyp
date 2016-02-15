package dhbw.ai13.bayesClassificator.trainer;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import dhbw.ai13.bayesClassificator.naiveBayes.*;

/**
 * TrainingsSet for one phonem Das Trainingsmodell ist ein zweidimensionaler
 * double Array mit double[Zeitschritt][reihe]=frequenz Die Trainingsmodelle
 * werden in einer ArrayList gespeichert.
 * 
 * @author Tim Tiede
 */

public class TrainingsSet {
	private ArrayList<double[][]> trainingValues;
	private int index;
	private String name;
	private Matrix database;

	// constructor
	public TrainingsSet(int index, String name, Matrix database) {
		this.trainingValues = new ArrayList<double[][]>();
		this.index = index;
		this.name = name;
		this.database = database;
	}

	// trainingsmodellwird zugefuegt
	public void addTrainingValue(double[][] train) {
		trainingValues.add(train);
		database.setPhonem(name, index);

		for (int timeSteps = 0; timeSteps < database.getNumOfTimeSteps(); timeSteps++) {
			for (int frequence = 0; frequence < database.getNumOfFrequencies(); frequence++) {
				double[] values = new double[trainingValues.size()];
				double counter = 0;

				for (int trainVal = 0; trainVal < trainingValues.size(); trainVal++) {
					values[trainVal] = trainingValues.get(trainVal)[timeSteps][frequence];
					counter = counter + values[trainVal];
				}
				counter = counter/trainingValues.size();
				// System.out.println("normalverteilung erstellen");
				NormalDistribution normDistr = auxiliary(values, counter);
				// System.out.println("normalverteilung erstellt");
				// for every intensity one value of the BayesMatrix is chanced+
				for (int intensity = 0; intensity < database.getNumOfIntensities(); intensity++) {
					database.setValue(
							1 + (normDistr.cumulativeProbability((double) (intensity))
									- normDistr.cumulativeProbability((double) (intensity-1))),
							intensity, frequence, timeSteps, index);
				}
			}
		}

	}

	// train database again for phonem of this TrainingsSet
	public void refresh() {
		if (trainingValues.isEmpty()) {
			return;
		}
		database.setPhonem(name, index);
		
		for (int timeSteps = 0; timeSteps < database.getNumOfTimeSteps(); timeSteps++) {

			//System.out.println("läuft");
			for (int frequence = 0; frequence < database.getNumOfFrequencies(); frequence++) {

				double[] values = new double[trainingValues.size()];
				double counter = 0;

				for (int trainVal = 0; trainVal < trainingValues.size(); trainVal++) {
					values[trainVal] = trainingValues.get(trainVal)[timeSteps][frequence];
					counter = counter + values[trainVal];
				}
				counter = counter/trainingValues.size();
				// System.out.println("normalverteilung erstellen");
				NormalDistribution normDistr = auxiliary(values, counter);
				// System.out.println("normalverteilung erstellt");
				// for every intensity one value of the BayesMatrix is chanced+
				for (int intensity = 0; intensity < database.getNumOfIntensities(); intensity++) {
					database.setValue(
							1 + (normDistr.cumulativeProbability((double) (intensity))
									- normDistr.cumulativeProbability((double) (intensity-1))),
							intensity, frequence, timeSteps, index);
				}
			}
		}
	}

	// get Normal Distribution
	private NormalDistribution auxiliary(double[] values, double counter) {
		double mean = counter / ((double) trainingValues.size());
		// System.out.println("Mean geht" + mean);
		double standardDeviation;
		StandardDeviation sd = new StandardDeviation();
		standardDeviation = sd.evaluate(values);
		// System.out.println("standart deviation geht" + standardDeviation);
		if (standardDeviation == 0)
			standardDeviation = 100;
		return new NormalDistribution(mean, standardDeviation);

	}

	// getter
	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

}
