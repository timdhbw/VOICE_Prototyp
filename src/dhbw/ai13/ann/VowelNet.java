package dhbw.ai13.ann;

import static dhbw.ai13.ann.ArrayUtil.getMaxIndex;
import static dhbw.ai13.ann.Configuration.DEBUG;

import java.util.ArrayList;
import java.util.HashMap;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import dhbw.ai13.speech.detection.Vector13D;
/**
* The class VowelNet determines the char to mfcc data.
* @author KI-Team
*/
public class VowelNet {
	static protected final double TRAIN_ERROR = 0.0000000001;
	static protected final int NEURONS = 200;
	static protected final int COEFFICIENTS_TO_USE = 26;
	private HashMap<Integer, String> indexToVowelMap;
	private BasicNetwork net;

	/**
	* The constructor VowelNet initializes the neuronal network.
	* @param mfccs Array with mfcc data for the neuronal network. 
	*/
	public VowelNet(ArrayList<Vector13D> mfccs) {
		long start = System.nanoTime();
		// Set up network
		net = new BasicNetwork();
		net.addLayer(new BasicLayer(new ActivationTANH(), false, COEFFICIENTS_TO_USE));
		net.addLayer(new BasicLayer(new ActivationTANH(), false, NEURONS));
		net.addLayer(new BasicLayer(new ActivationSoftMax(), false, 5));
		net.getStructure().finalizeStructure();
		net.reset();

		// prepare datasets
		double[][] input = new double[mfccs.size()][COEFFICIENTS_TO_USE];
		double[][] output = new double[mfccs.size()][5];
		HashMap<String, Integer> h = new HashMap<>();
		indexToVowelMap = new HashMap<>();
		int c = 0;
		for (Vector13D v : mfccs) {
			String vocal = v.getVocal();
			System.arraycopy(v.getVector(), 0, input[c], 0, COEFFICIENTS_TO_USE);
			if (!h.containsKey(vocal)) {
				indexToVowelMap.put(h.size(), vocal);
				h.put(vocal, h.size());
			}
			output[c][h.get(vocal)] = 1;
			c++;
		}

		// Training
		MLDataSet data = new BasicMLDataSet(input, output);
		MLTrain train = new ResilientPropagation(net, data);

		int epoch = 1;
		do {
			train.iteration();
			if (DEBUG)
				System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > TRAIN_ERROR);
		train.finishTraining();
		long time = System.nanoTime() - start;

		// Debug messages: Compute training error
		if (DEBUG) {
			System.out.println("Neural Network Results:");
			int right = 0, wrong = 0;
			for (MLDataPair pair : data) {
				final MLData out = net.compute(pair.getInput());
				// System.out.println(Arrays.toString(out.getData()));
				String suggestion = indexToVowelMap.get(getMaxIndex(out.getData()));
				String ideal = indexToVowelMap.get(getMaxIndex(pair.getIdealArray()));
				if (suggestion.equals(ideal))
					right++;
				else {
					wrong++;
					System.out.println("Wrong:  " + suggestion + " / " + ideal);
				}
			}
			System.out.println(indexToVowelMap);
			System.out.println("Training Error: " + (wrong) + "/" + (wrong + right) + " (" + (100.0 * wrong / (wrong + right) + "%)"));
			System.out.println("Training VowelNet took: " + time + " ns");
		}
		Encog.getInstance().shutdown();
	}
	
	/**
	* The function identify determines the character to the mfcc data.
	* @param mfcc Array with the mfcc data. 
	* @return The char which is detected is returned. If no char is detected, a space is returned. 
	*/
	public char identify(double[] mfcc) {
		long start = System.nanoTime();
		double[] input = new double[COEFFICIENTS_TO_USE];
		System.arraycopy(mfcc, 0, input, 0, COEFFICIENTS_TO_USE);
		MLData m = new BasicMLData(input);
		char c = indexToVowelMap.get(getMaxIndex(net.compute(m).getData())).charAt(0);
		if (DEBUG)
			System.out.println("Identifying took: " + (System.nanoTime() - start) + " ns");
		return c;
	}
}
