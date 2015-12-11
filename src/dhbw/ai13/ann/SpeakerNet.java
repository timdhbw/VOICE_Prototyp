package dhbw.ai13.ann;

import static dhbw.ai13.ann.ArrayUtil.getMaxIndex;
import static dhbw.ai13.ann.Configuration.DEBUG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
* The class SpeakerNet determines available speakers.
* @author KI-Team
*/
public class SpeakerNet {
	static protected final double TRAIN_ERROR = 0.0001;
	static protected final int NEURONS = 100;
	static protected final int COEFFICIENTS_TO_USE = 26;
	private HashMap<Integer, String> indexToSpeakerMap;
	private BasicNetwork net;
	
		
	/**
	* The constructor SpeakerNet initializes the neuronal network. 
	* @param mfccs Array with mfcc data.	
	*/
	public SpeakerNet(ArrayList<Vector13D> mfccs) {
		long start = System.nanoTime();
		// Determine available users
		indexToSpeakerMap = new HashMap<>();
		HashMap<String, Integer> h = new HashMap<>();
		for (Vector13D v : mfccs) {
			String user = v.getUser();
			String person = user;
			if (!h.containsKey(person)) {
				indexToSpeakerMap.put(h.size(), person);
				h.put(person, h.size());
			}
		}
		// prepare datasets
		double[][] input = new double[mfccs.size()][COEFFICIENTS_TO_USE];
		double[][] output = new double[mfccs.size()][h.size()];
		int c = 0;
		for (Vector13D v : mfccs) {
			System.arraycopy(v.getVector(), 0, input[c], 0, COEFFICIENTS_TO_USE);
			String user = v.getUser();
			String person = user;
			output[c][h.get(person)] = 1;
			c++;
		}
		int epoch = 1;
		MLDataSet data;
		do {
			// Set up network
			net = new BasicNetwork();
			net.addLayer(new BasicLayer(new ActivationTANH(), false, COEFFICIENTS_TO_USE));
			net.addLayer(new BasicLayer(new ActivationTANH(), false, NEURONS));
			net.addLayer(new BasicLayer(new ActivationSoftMax(), false, h.size()));
			net.getStructure().finalizeStructure();
			net.reset();
			// Training
			data = new BasicMLDataSet(input, output);
			MLTrain train = new ResilientPropagation(net, data);
			epoch = 1;
			do {
				train.iteration();
				if (DEBUG)
					System.out.println("Epoch #" + epoch + " Error:" + train.getError());
				epoch++;
			} while (train.getError() > TRAIN_ERROR && epoch < 200);
			if (epoch >= 200)
				System.out.println("Emergency break");
			train.finishTraining();
		} while (epoch >= 200);

		long time = System.nanoTime() - start;

		// Debug messages: Compute training error
		if (DEBUG) {
			System.out.println("Neural Network Results:");
			int right = 0, wrong = 0;
			for (MLDataPair pair : data) {
				final MLData out = net.compute(pair.getInput());
				String suggestion = indexToSpeakerMap.get(getMaxIndex(out.getData()));
				String ideal = indexToSpeakerMap.get(getMaxIndex(pair.getIdealArray()));
				if (suggestion.equals(ideal))
					right++;
				else {
					wrong++;
					System.out.println("Wrong:  " + suggestion + " / " + ideal);
				}
			}
			System.out.println(indexToSpeakerMap);
			System.out.println("Training Error: " + (wrong) + "/" + (wrong + right) + " (" + (100.0 * wrong / (wrong + right) + "%)"));
			System.out.println("Training SpeakerNet took: " + time + " ns");
		}
		Encog.getInstance().shutdown();

	}
	
	/**
	* The function identify determines the best matching speaker.  
	* @param mfcc Array with mfcc data.
	* @return Name of the speaker.	
	*/
	public String identify(double[] mfcc) {
		double[] input = new double[COEFFICIENTS_TO_USE];
		System.arraycopy(mfcc, 0, input, 0, COEFFICIENTS_TO_USE);
		MLData m = new BasicMLData(input);
		String c = indexToSpeakerMap.get(getMaxIndex(net.compute(m).getData()));
		return c;
	}
	
	/**
	* The function identify determines the best matching speaker.  
	* @param Array with mfcc data.
	* @return Name of the speaker.	
	*/	
	public String identify(double[][] mfcc) {
		HashMap<String, Integer> counts = new HashMap<>();
		for (int i = 0; i < mfcc.length; i++) {
			String person = identify(mfcc[i]);
			if (!counts.containsKey(person)) {
				counts.put(person, 0);
			}
			counts.put(person, counts.get(person) + 1);
		}
		String r = null;
		int max = 0;
		for (Entry<String, Integer> entry : counts.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				r = entry.getKey();
			}
		}
		return r;
	}

}
