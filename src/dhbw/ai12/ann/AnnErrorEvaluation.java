package dhbw.ai12.ann;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static dhbw.ai12.ann.Configuration.DEBUG;
import dhbw.ai12.speech.Buchstabendaten;
import dhbw.ai12.speech.detection.Vector13D;

/**
* The class AnnErrorEvaluation has serveral static functions, which test the functions of the package dhbw.ai12.ann.
* @author KI-Team 
*/
public class AnnErrorEvaluation {
	
	/**
	* The function evaluateSpeakerError tests the class ANNSpeechRecognizer.
	* @param path Path of the file.	
	*/
	public static TestResult evaluateSpeakerError(String path) throws IOException {
		ANNSpeechRecognizer net = new ANNSpeechRecognizer();
		int right = 0, wrong = 0;
		File file = new File(path);
		if (file.isDirectory()) {
			for (File p : file.listFiles()) {
				if (!p.isDirectory()) {
					String s = p.getName();
					char in = s.substring(0, 1).charAt(0);
					String userIn = s.substring(0, s.lastIndexOf(".wav"));
					Buchstabendaten[] b = { new Buchstabendaten(in, "") };
					String suggested = net.recognizePerson(b);
					if (!suggested.equals(userIn)) {
						wrong++;
						if (DEBUG)
							System.out.println("ERROR: " + suggested + " --> " + userIn);
					} else
						right++;
				}
			}
		}
		if (path.endsWith(".csv")) {
			ArrayList<Vector13D> data = MFCCSupplier.readCSV(path);
			for (Vector13D vector13d : data) {
				String suggested = net.recognizePerson(vector13d.getVector());
				String userIn = vector13d.getUser();
				userIn = userIn.substring(0, userIn.lastIndexOf("_"));
				if (!suggested.equals(userIn)) {
					wrong++;
					if (DEBUG)
						System.out.println("ERROR: " + suggested + " -> " + userIn);
				} else
					right++;
			}
		}
		return new TestResult(right, wrong);
	}
	
	/**
	* The function evaluateSpeakerErrorDeep tests the class ANNSpeechRecognizer.
	* @param path Path of the file.
	* @param n Count of tests.	
	*/
	public static TestResultStatistics evaluateSpeakerErrorDeep(String path, int n) throws IOException {
		TestResultStatistics trs = new TestResultStatistics();
		for (int i = 0; i < n; i++)
			trs.addTestResult(evaluateSpeakerError(path));
		return trs;
	}

	/**
	* The function evaluateVowelError tests the class ANNSpeechRecognizer.
	* @param path Path of the file.	
	*/
	public static TestResult evaluateVowelError(String path) throws IOException {
		ANNSpeechRecognizer net = new ANNSpeechRecognizer();
		int right = 0, wrong = 0;
		File file = new File(path);
		if (file.isDirectory()) {
			for (File p : file.listFiles()) {
				if (!p.isDirectory()) {
					String s = p.toString();
					char suggested = net.recognizeChar(s);
					int ind = s.lastIndexOf('\\');
					char in = s.substring(ind + 1, ind + 2).charAt(0);
					if (in != suggested) {
						wrong++;
						if (DEBUG)
							System.out.println("ERROR: " + in + "_" + s);
					} else
						right++;
				}
			}
		}
		if (path.endsWith(".csv")) {
			ArrayList<Vector13D> data = MFCCSupplier.readCSV(path);
			for (Vector13D vector13d : data) {
				char suggested = net.recognizeChar(vector13d.getVector());
				char in = vector13d.getVocal().charAt(0);
				if (in != suggested) {
					wrong++;
					if (DEBUG)
						System.out.println("ERROR: " + in + "_" + vector13d.getUser());
				} else
					right++;
			}
		}
		return new TestResult(right, wrong);

	}
	
	/**
	* The function evaluateVowelErrorDeep tests the class ANNSpeechRecognizer.
	* @param path Path of the file.
	* @param n Count of tests.	
	*/
	public static TestResultStatistics evaluateVowelErrorDeep(String path, int n) throws IOException {
		TestResultStatistics trs = new TestResultStatistics();
		for (int i = 0; i < n; i++)
			trs.addTestResult(evaluateVowelError(path));
		return trs;
	}

	public static void main(String[] args) {
		try {
//			System.out.println(evaluateSpeakerErrorDeep("Saller/waveDerPersonUnbekannt/mfcc.csv", 100));
			int sizeOfSample = 30;
			/* TESTING TRAINING DATA */
			System.out.println("----------------------------------------------------------");
			System.out.println("TESTING TRAINING DATA VOWEL DETECTION");
//			System.out.println(evaluateVowelErrorDeep("Saller/Training/mfcc4.csv", sizeOfSample));
			/* TESTING UNKNOWN PERSON VOWEL DETECTION */
			System.out.println("----------------------------------------------------------");
			System.out.println("TESTING UNKNOWN PERSON VOWEL DETECTION");
			System.out.println(evaluateVowelErrorDeep("Saller/komplettUnbekanntePersonen/mfcc3.csv", sizeOfSample));
			/* TESTING UNKNOWN WAVE VOWEL DETECTION */
			System.out.println("----------------------------------------------------------");
			System.out.println("TESTING UNKNOWN WAVE VOWEL DETECTION");
			System.out.println(evaluateVowelErrorDeep("Saller/waveDerPersonUnbekannt/mfcc3.csv", sizeOfSample));
			System.out.println("----------------------------------------------------------");
			System.out.println("TESTING FINSHED");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class TestResultStatistics {
		private ArrayList<TestResult> tests;

		public TestResultStatistics() {
			tests = new ArrayList<>();
		}

		public void addTestResult(TestResult test) {
			tests.add(test);
		}

		public double average() {
			double sum = 0;
			for (TestResult testResult : tests) {
				sum += testResult.relativeError;
			}
			return sum / tests.size();
		}

		public double min() {
			double min = Double.MAX_VALUE;
			for (TestResult testResult : tests) {
				min = testResult.relativeError < min ? testResult.relativeError : min;
			}
			return min == Double.MAX_VALUE ? 0 : min;
		}

		public double max() {
			double min = Double.MIN_VALUE;
			for (TestResult testResult : tests) {
				min = testResult.relativeError > min ? testResult.relativeError : min;
			}
			return min == Double.MIN_VALUE ? 0 : min;
		}

		public double standardDeviation() {
			double avg = average();
			double sum = 0;
			for (TestResult testResult : tests) {
				double d = testResult.relativeError - avg;
				sum += d * d;
			}
			return Math.sqrt(sum / (tests.size() - 1));
		}

		public String toString() {
			String info = "TestResultStatistics of " + tests.size() + " tests: " + Configuration.lineSeparator;
			info += "\t Mininum relative Error: " + min() + Configuration.lineSeparator;
			info += "\t Maximum relative Error: " + max() + Configuration.lineSeparator;
			info += "\t Average relative Error: " + average() + Configuration.lineSeparator;
			info += "\t Standard Deviation    : " + standardDeviation() + Configuration.lineSeparator;
			info += tests;
			return info;
		}
	}

	private static class TestResult {
		private final int right, wrong;
		private final double relativeError;

		public TestResult(int right, int wrong) {
			this.right = right;
			this.wrong = wrong;
			this.relativeError = 1.0 * wrong / (right + wrong);
		}

		public String toString() {
			return wrong + "/" + (wrong + right) + " (" + (100.0 * relativeError) + "%)";
		}
	}

}
