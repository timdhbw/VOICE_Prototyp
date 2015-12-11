package dhbw.ai12.ann;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import dhbw.ai12.speech.Buchstabendaten;
import dhbw.ai12.speech.IMfccGUI;
import dhbw.ai12.speech.detection.Vector13D;

/**
* The class ANNSpeechRecognizer has functions for the identification of persons and characters.
* @author KI-Team
*/
public class ANNSpeechRecognizer implements IMfccGUI {

	private ArrayList<Vector13D> mfccs;
	private VowelNet vowelNet;
	private SpeakerNet speakerNet;

	public ANNSpeechRecognizer() {
		mfccs = new ArrayList<>();
	}

	private void init() throws NumberFormatException, IOException {
		if (mfccs.size() == 0) {
			ArrayList<Vector13D> m = MFCCSupplier.readCSV(ANNSpeechRecognizer.class.getResourceAsStream("/mfccKNN.csv"));
			if (!m.equals(mfccs)) {
				mfccs = m;
				vowelNet = new VowelNet(mfccs);
				speakerNet = new SpeakerNet(mfccs);
			}
		}
	}

	/**
	* The function recognizeChar identifies the char.
	* @param pathFile Path of the file.	
	*/
	@Override
	public char recognizeChar(String pathFile) {
		try {
			init();
			double[] mfcc = MFCCSupplier.computeMFCCOfFile(pathFile).getVector();
			return vowelNet.identify(mfcc);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Wrong number format..");
			return 0;
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			System.out.println("Unsupported Audio File Format!");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading file!");
			return 0;
		}
	}

	/**
	* The function recognizeChar identifies the char from the mfcc data.
	* @param mfcc Mfcc data.	
	*/
	public char recognizeChar(double[] mfcc) {
		try {
			init();
			return vowelNet.identify(mfcc);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Wrong number format..");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading file!");
			return 0;
		}
	}


	/**
	* The function recognizePerson identifies a person from given data.
	* @param charData Buchstabendaten-object with file and char data. 	
	*/
	@Override
	public String recognizePerson(Buchstabendaten[] charData) {
		try {
			init();
			double[][] mfccs = new double[charData.length][];
			for (int i = 0; i < mfccs.length; i++) {
				mfccs[i] = MFCCSupplier.computeMFCCOfFile(charData[i].getDateiPfad()).getVector();
			}
			return speakerNet.identify(mfccs);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Wrong number format..");
			return "ERROR";
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading file!");
			return "ERROR";
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			System.out.println("Unsupported Audio File Format!");
			return "ERROR";
		}
	}
	
	/**
	* The function recognizePerson identifies a person from the mfcc data.
	* @param mfcc Mfcc data.	
	*/	
	public String recognizePerson(double[] mfcc) {
		try {
			init();
			return speakerNet.identify(mfcc);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Wrong number format..");
			return "ERROR";
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading file!");
			return "ERROR";
		}
	}

	public static void main(String[] args) {
		ANNSpeechRecognizer net = new ANNSpeechRecognizer();
//		System.out.println(net.recognizeChar("/"));
		try {net.init();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
