package dhbw.ai13.ann;

import static dhbw.ai13.ann.ArrayUtil.getMaxIndex;
import static dhbw.ai13.ann.Configuration.DEBUG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

	public SpeakerNet(){
		net = new BasicNetwork();
		net.addLayer(new BasicLayer(new ActivationTANH(), false, COEFFICIENTS_TO_USE));
		net.addLayer(new BasicLayer(new ActivationTANH(), false, NEURONS));
		net.addLayer(new BasicLayer(new ActivationSoftMax(), false, 1));
		net.getStructure().finalizeStructure();
		net.reset();
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

	public void saveWeightsToFile(String filepath){
		int nLayer = net.getLayerCount();
		List<String> lines = new LinkedList<>();
		for(int l = 0; l < nLayer-1; l++){
			StringBuilder sb = new StringBuilder();
			//System.out.printf("Layer %d: nNeurons %d\n",l,net.getLayerTotalNeuronCount(l));
			//System.out.printf("Layer %d: nNeurons %d\n",l+1,net.getLayerTotalNeuronCount(l+1));
			int nNeuronsCurrentLayer = net.getLayerTotalNeuronCount(l);
			int nNeuronsNextLayer = net.getLayerTotalNeuronCount(l+1);
			for(int j = 0; j < nNeuronsCurrentLayer; j++) {
				for (int jj = 0; jj < nNeuronsNextLayer; jj++) {
					//System.out.printf("[Layer%d] [Neuron%d->%d] n weight: %f\n",l,j,jj,net.getWeight(l, j, jj));
					sb.append(String.format(Locale.ENGLISH,"%f;",net.getWeight(l, j, jj)));
				}
			}
			lines.add(sb.toString());
		}
		try {
			Files.write(Paths.get(filepath),lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void readWeightFromFile(String filepath){
		try {
			List<String> lines = Files.readAllLines(Paths.get(filepath));
			int nLayer = net.getLayerCount();
			for(int l = 0; l < nLayer-1; l++) {
				String[] weights = lines.get(l).split(";");
				int nNeuronsCurrentLayer = net.getLayerTotalNeuronCount(l);
				int nNeuronsNextLayer = net.getLayerTotalNeuronCount(l+1);
				for(int j = 0; j < nNeuronsCurrentLayer; j++) {
					for (int jj = 0; jj < nNeuronsNextLayer; jj++) {
						net.setWeight(l, j, jj, Double.valueOf(weights[j*nNeuronsNextLayer+jj]));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
