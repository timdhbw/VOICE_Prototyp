package dhbw.ai12.speech.detection;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import dhbw.ai12.speech.DetectionLogic;
import dhbw.ai12.speech.mfcc.FeatureExtractor;

/**
 * Stores characteristic filtered mfcc arrays in csv file and retrieves them.
 * Searches for the best vectors to a given input file inspired by the Vector
 * Quantization algorithm.
 * 
 * @author Stefan Schultes
 * @version 13.04.2015
 */
public class VQ implements IMfccCsv {
	private static final String PATH_TO_CSV = "/csv.csv"; // csv-file for storing
															// mfcc-characterisitc
															// values for
															// speaker+vocal
	private static ArrayList<Vector13D> al = new ArrayList<Vector13D>(); // Arraylist
															public static ArrayList<Vector13D> getAl() {
		return al;
	}
	// containing all
															// known
															// speaker-vocal
															// pairs
	private static VQ vq = null; // single instance of this class
	private static final int NUMBER_OF_VQ_RESULTS = 2;

	/**
	 * TEST Tests the detection rate of the algorithm - files, that are not
	 * included in the csv must be used!
	 *
	 * result 13.04.2015: Detection rate - 98,4% (740 vectors in CSV, 71
	 * test-files in folder) test with liftering (11) -> Detection rate 95,4%
	 * test with derivation -> Detection rate 98,4%
	 */
	public void testSpeakerAndVocalRecognition(String folderpath) {
		File folder = new File(folderpath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()
					&& listOfFiles[i].toString().endsWith(".wav")) {
				String filename = listOfFiles[i].getName();
				String vocal = filename.substring(0, 1);
				String speaker = filename.substring(2);
				try {
					System.out.print("- " + speaker + " - " + vocal + " --> ");
					System.out.println(Arrays.toString(VQ
							.detectSpeakerAndVocalFromWave(listOfFiles[i]
									.toString())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void testVocalRecognition(String folderpath) {
		File folder = new File(folderpath);
		File[] listOfFiles = folder.listFiles();
		int all = listOfFiles.length;
		int count = 0;
		HashMap<String, Integer> fails = new HashMap<String, Integer>();
		fails.put("A", 0);
		fails.put("E", 0);
		fails.put("I", 0);
		fails.put("O", 0);
		fails.put("U", 0);
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()
					&& listOfFiles[i].toString().endsWith(".wav")) {
				String filename = listOfFiles[i].getName();
				String vocal = filename.substring(0, 1);
				try {
					String detected = "";
					double[] tempd = VQ.detectVocalFromWave(listOfFiles[i]
							.toString());
					Map<String, Integer> prob = DetectionLogic
							.matchUnknown(tempd);
					SortedSet<Entry<String, Integer>> sorted = DetectionLogic
							.entriesSortedByValues(prob);

					int a = prob.get("A");
					int e = prob.get("E");
					int ii = prob.get("I");
					int o = prob.get("O");
					int u = prob.get("U");
					Iterator<Entry<String, Integer>> iter = sorted.iterator();
					String first = iter.next().toString().substring(0, 1);
					// String second = iter.next().toString().substring(0, 1);
					// String third = iter.next().toString().substring(0, 1);
					// String fourth = iter.next().toString().substring(0, 1);
					// String fifth = iter.next().toString().substring(0, 1);
					detected = first;
					/*
					 * if (first.equals("U") && second.equals("E") && a == -2) {
					 * detected = "E"; } if (first.equals("U") &&
					 * second.equals("E") && third.equals("O") && a > 0) {
					 * detected = "O"; } if (first.equals("U") &&
					 * second.equals("O") && (third.equals("E") ||
					 * (third.equals("I") && ii == e))) { detected = "O"; }
					 * trying to improve recognition
					 */
					System.out.println(i + 1 + "/" + all + ":" + vocal
							+ " --> " + detected);
					if (vocal.equals(detected)) {
						count++;

					} else {
						fails.put(vocal, fails.get(vocal) + 1);
						System.out.println("A:" + a);
						System.out.println("E:" + e);
						System.out.println("I:" + ii);
						System.out.println("O:" + o);
						System.out.println("U:" + u);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(count + "/" + all);
		System.out.println("A:" + fails.get("A"));
		System.out.println("E:" + fails.get("E"));
		System.out.println("I:" + fails.get("I"));
		System.out.println("O:" + fails.get("O"));
		System.out.println("U:" + fails.get("U"));
	}
	
	/**
	 * Entspricht testVocalRecognition fuer eine einzelne Datei
	 */
	public static char testVocalRecognitionSingleFile(String filepath) {
		File analyzeFile = new File(filepath);
		HashMap<String, Integer> fails = new HashMap<String, Integer>();
		fails.put("A", 0);
		fails.put("E", 0);
		fails.put("I", 0);
		fails.put("O", 0);
		fails.put("U", 0);

			if (analyzeFile.isFile()
					&& analyzeFile.toString().endsWith(".wav")) {
				try {
					String detected = "";
					double[] tempd = VQ.detectVocalFromWave(analyzeFile
							.toString());
					Map<String, Integer> prob = DetectionLogic
							.matchUnknown(tempd);
					SortedSet<Entry<String, Integer>> sorted = DetectionLogic
							.entriesSortedByValues(prob);
					Iterator<Entry<String, Integer>> iter = sorted.iterator();
					String first = iter.next().toString().substring(0, 1);
					detected = first;

					return detected.charAt(0);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return 0;
		
	}

	public static void main(String[] args)
			throws UnsupportedAudioFileException, IOException,
			URISyntaxException {
		String folder = "C:/Users/Stefan/Desktop/testVocal/";
		// writeWaveFilesInFolderToCsv("");
		 VQ.getInstance().testSpeakerAndVocalRecognition(folder);
		//testVocalRecognition(folder);
	}

	/**
	 * @param filename
	 *            Path to wav-file
	 * @return Mfcc-Frames
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private double[][] calculateMfcc(String filename)
			throws NumberFormatException, IOException,
			UnsupportedAudioFileException {
		FeatureExtractor extractor = new FeatureExtractor();
		AudioInputStream audioInputStream = AudioSystem
				.getAudioInputStream(new File(filename));
		return extractor.getMfcc(audioInputStream, 0.050, 0.025, 512, 0.0, 1.0,
				true);
	}

	private double[][] calculateMfccShort(String filename)
			throws NumberFormatException, IOException,
			UnsupportedAudioFileException {
		FeatureExtractor extractor = new FeatureExtractor();
		AudioInputStream audioInputStream = AudioSystem
				.getAudioInputStream(new File(filename));
		return extractor.getMfcc(audioInputStream, 0.050, 0.025, 512, 0.0, 1.0,
				false);
	}

	/**
	 * Static method to return the single instance of this class. Creates new
	 * instance, if not existing
	 * 
	 * @return VQ
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static VQ getInstance() throws NumberFormatException, IOException {
		if (vq == null) {
			vq = new VQ();
		}
		return vq;
	}

	/**
	 * When creating an instance, the class reads first all data in the CSV-file
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private VQ() throws NumberFormatException, IOException {
		readCSVData();
	}

	/**
	 * Data in the CSV-file is extracted and stored as Vector13D in the global
	 * Arraylist
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void readCSVData() throws NumberFormatException, IOException {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		try {
			br = new BufferedReader(new InputStreamReader(VQ.class.getResourceAsStream(PATH_TO_CSV)));
			while ((line = br.readLine()) != null) {
				String[] words = line.split(cvsSplitBy);
				double[] d = new double[26];
				for (int i = 0; i <= 25; i++)
					d[i] = Double.parseDouble(words[i]);
				al.add(new Vector13D(d, words[26], words[27]));
			}
			br.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		System.out.println("CSV import completed");
	}

	/**
	 * Writes a characteristic mfcc-vector calculated from a wav-file to the
	 * CSV-file
	 * 
	 * @param filename
	 *            path to the wav-file
	 * @param vocal
	 *            recorded vocal
	 * @param speaker
	 *            recorded speaker
	 * @return true: file is imported the first time, false: already in CSV-file
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static boolean writeWaveToCsv(String filename, String vocal,
			String speaker) throws NumberFormatException, IOException, UnsupportedAudioFileException {
		VQ vq = VQ.getInstance();
		return vq.writeMfccToCSV((FilterMFCCResult.filterAndShrinkFrames(vq
				.calculateMfcc(filename))), vocal, speaker);
	}

	/**
	 * Writes a characteristic mfcc-vector to the CSV-file
	 * 
	 * @param avgMfcc
	 *            filtered and shrunk mfcc-array
	 * @param vocal
	 *            recorded vocal
	 * @param speaker
	 *            recorded speaker
	 * @return true: mfcc-vector is imported the first time, false: already in
	 *         CSV-file
	 * @throws IOException
	 */
	private boolean writeMfccToCSV(double[] avgMfcc, String vocal,
			String speaker) throws IOException {
		Vector13D vnew = new Vector13D(avgMfcc, vocal, speaker);
		for (Vector13D v : al) {
			if (v.equals(vnew))
				return false;
		}
		String outtext = "";
		for (int i = 0; i < avgMfcc.length; i++) {
			outtext += avgMfcc[i] + ";";
		}
		outtext += vocal + ";" + speaker + ";";
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				VQ.class.getResource(PATH_TO_CSV).getFile(), true)));
		out.println(outtext);
		out.flush();
		out.close();
		al.add(vnew);
		System.out.println(Arrays.toString(avgMfcc) + " -- " + vocal + " -- "
				+ speaker);
		return true;
	}

	/**
	 * Writes the contents of a folder containing wav-files into the csv-file
	 * 
	 * @param folderpath
	 *            Path to the folder with wav-files
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static void writeWaveFilesInFolderToCsv(String folderpath)
			throws NumberFormatException, IOException,
			UnsupportedAudioFileException,NullPointerException {
		File folder = new File(folderpath);
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles==null)
			throw new NullPointerException();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()
					&& listOfFiles[i].toString().endsWith(".wav")) {
				String filename = listOfFiles[i].getName();
				String vocal = filename.substring(0, 1);
				String speaker = filename.substring(2);
				try {
					VQ.writeWaveToCsv(listOfFiles[i].toString(), vocal, speaker);
					System.out.println("--> " + speaker + " - " + vocal
							+ " imported");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Detects most suitable speaker and vocal to a given wav-file
	 * 
	 * @param filename
	 *            Path to wav-file
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @return Array with best near vectors
	 */
	public static Vector13D[] detectSpeakerAndVocalFromWave(String filename)
			throws UnsupportedAudioFileException, IOException {
		VQ vq = VQ.getInstance();
		return vq.nearestVec(FilterMFCCResult.filterAndShrinkFrames(vq
				.calculateMfcc(filename)));
	}
	


	private static double[] detectVocalFromWave(String filename)
			throws UnsupportedAudioFileException, IOException {
		VQ vq = VQ.getInstance();
		return FilterMFCCResult.averageFrames(vq.calculateMfccShort(filename));
	}

	/**
	 * Converts filtered shrunk mfcc-array to Vector13D for distance-detection
	 * 
	 * @param avgMfcc
	 *            mfcc-array
	 * @return
	 */
	private Vector13D avgMfccToVector13D(double[] avgMfcc) {
		return new Vector13D(avgMfcc, "", "");
	}

	/**
	 * Calculates distance to vectors in csv-file, finds nearest vectors
	 * 
	 * @param avgMfcc
	 *            filtered shrunk mfcc-array
	 * @return
	 */
	private Vector13D[] nearestVec(double[] avgMfcc) {
		Vector13D v1 = avgMfccToVector13D(avgMfcc);
		double[] distance = new double[NUMBER_OF_VQ_RESULTS];
		Vector13D[] results = new Vector13D[NUMBER_OF_VQ_RESULTS];
		for (int i = 0; i < distance.length; i++) {
			distance[i] = Double.MAX_VALUE;
		}
		for (Vector13D v2 : al) {
			double actDistance = v1.distance(v2);
			double highestDistance = 0;
			int highestDistanceID = 0;

			for (int i = 0; i < distance.length; i++) {
				if (distance[i] >= highestDistance) {
					highestDistance = distance[i];
					highestDistanceID = i;
				}
			}
			if (actDistance < highestDistance) {
				v2.setDist(actDistance);
				results[highestDistanceID] = v2;
				distance[highestDistanceID] = actDistance;
			}
		}
		return results;
	}

	@Override
	public ArrayList<Vector13D> leseGemittelteMfccDaten()
			throws NumberFormatException, IOException {
		getInstance();
		return al;
	}
	@Override
	public  double[] mfccFromWave(String filename) throws NumberFormatException, IOException, UnsupportedAudioFileException {
		getInstance();
		return FilterMFCCResult.filterAndShrinkFrames(vq.calculateMfcc(filename));
	}

}
