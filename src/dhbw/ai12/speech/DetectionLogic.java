package dhbw.ai12.speech;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sound.sampled.UnsupportedAudioFileException;

import dhbw.ai12.speech.detection.VQ;
import dhbw.ai12.speech.detection.Vector13D;
/**
 * DetectionLogic for easy use of our algorithms
 * @author Florian Volz
 * @author Felix Frank
 */
public class DetectionLogic implements IMfccGUI {
	
	public static void main(String[] args) {
		DetectionLogic d= new DetectionLogic();
		String folderpath="C:/Daten/Saller/checkout/src/main/resources/";
		File folder = new File(folderpath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()	&& listOfFiles[i].toString().endsWith(".wav")) {
				String filename = listOfFiles[i].getName();
				String vocal = filename.substring(0, 1);
				String speaker = filename.substring(2);
				try {
					System.out.println("- " + speaker + " - " + vocal + " --> "+ d.recognizeChar(folderpath+filename));
		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
/**
 * Funktion erkenneBuchstabeMFCC plus Verwendung von neuer testVocalRecognitionSingleFile 
 * Funktion, wenn die Disanz groesser als 25 ist(mit Headset getestet, evtl abzuaendern).
 * Ab 25 konnte ich davon ausgehen, dass der Buchstabe nicht erkannt wurde. 
 * Sollte testVocalRecognition sicherer werden, wird sie mit einem hoeheren Anteil am Ergebnis
 * beteiligt
 * 
 * @param PfadDatei
 * @return
 */
	public char erkenneBuchstabeMFCCEx(String PfadDatei) {
		Vector13D results[];
		Vector13D bestResult = null;
		double bestDistance = Double.MAX_VALUE;
		try {
			VQ.getInstance();
			results = VQ.detectSpeakerAndVocalFromWave(PfadDatei);
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
		
		for (Vector13D vec : results) {
			if (vec.getDist() < bestDistance) {
				bestDistance = vec.getDist();
				bestResult = vec;
			}
		}
		char finalresult = bestResult.getVocal().charAt(0);
		double avarage = calculateAvarage(bestResult, finalresult);
		double variance = calculateVariance(bestResult, finalresult, avarage);
		double standardDeviation = Math.sqrt(variance);
		
		if((bestDistance-standardDeviation)>5){
			finalresult = VQ.testVocalRecognitionSingleFile(PfadDatei);
		}
			
		
		return finalresult;
	}

/**
 * Calculates the variance using all Vector13D data about the detected person
 * and the detected vocal 
 * 
 * @param bestResult
 * @param finalresult
 * @param avarage
 * @return
 */
	private double calculateVariance(Vector13D bestResult, char finalresult,
			double avarage) {
		double variance=0;
		int counter=1;
		for(Vector13D vec: VQ.getAl()){
			if((bestResult.getUser().equals(vec.getUser()))&&(bestResult.getVocal().charAt(0)==finalresult)){
				counter++;
				variance+=(vec.distance(bestResult)-avarage)*(vec.distance(bestResult)-avarage);
			}
		}
		variance=variance/counter;
		return variance;
	}

/**
 * Calculates the avarage distance using all Vector13D data about the detected person
 * and the detected vocal 
 * @param bestResult
 * @param finalresult
 * @return
 */
	private double calculateAvarage(Vector13D bestResult, char finalresult) {
		double sum=0;
		int counter=1;
		for(Vector13D vec: VQ.getAl()){
			if((bestResult.getUser().equals(vec.getUser()))&&(bestResult.getVocal().charAt(0)==finalresult)){
				counter++;
				sum+=vec.distance(bestResult);
				//System.out.println(vec);
			}
		}
		return sum/counter;
	}
	/**
	 * Erkennung von Personen. Sucht den Vector13D mit der geringsten Distanz.
	 * 
	 * @param Buchstabendaten [] Buchstabendaten Array mit Buchstabendaten
	 * @return String erkannter User
	 */
	public String erkennePersonMFCC(Buchstabendaten[] Buchstabendaten) {
		TreeMap<String, Integer> usermap = new TreeMap<String, Integer>();
		Vector13D results[];
		Vector13D bestResult = null;
		String currentBestUser;
		double bestDistance = Double.MAX_VALUE;
		for (Buchstabendaten buchstabe : Buchstabendaten) {
			try {
				VQ.getInstance();
				results = VQ.detectSpeakerAndVocalFromWave(buchstabe
						.getDateiPfad());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Wrong number format..");
				return "";
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				System.out.println("Unsupported Audio File Format!");
				return "";
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error reading file!");
				return "";
			}
			

			// Ermittle Vector13D mit geringstem Abstand
			bestResult = results[0];
			for (Vector13D vec : results) {
				if (vec.getDist() < bestDistance) {
					bestDistance = vec.getDist();
					bestResult = vec;
				}
			}
			// Strip User String
			currentBestUser = bestResult
					.getUser()
					.replace("wav", "")
					.replaceAll(
							"[^A-Za-z\u00c4\u00e4\u00d6\u00f6\u00dc\u00fc\u00df\u005F]",
							"").replace("_", " ");
			currentBestUser = currentBestUser.substring(0,
					currentBestUser.length() - 1);

			// Fuege User TreeMap zu
			if (!usermap.containsKey(currentBestUser)) {
				usermap.put(currentBestUser, 0);
			} else {
				usermap.put(currentBestUser, usermap.get(currentBestUser) + 1);
			}
			bestResult = null;
			bestDistance = Double.MAX_VALUE;
		}
		return entriesSortedByValues(usermap).first().getKey();
	}

/**
 * Sorts a map
 * 
 * @param map
 * @return
 */
	public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e2.getValue().compareTo(e1.getValue());
						return res != 0 ? res : 1;
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public static HashMap<String, Integer> matchUnknown(double[] avg) {
		HashMap<String, Integer> matches = new HashMap<String, Integer>();
		matches.put("A", 0);
		matches.put("E", 0);
		matches.put("I", 0);
		matches.put("O", 0);
		matches.put("U", 0);
		if (avg[1] < 0) {
			matches.put("A", matches.get("A") + 5);
		} else {
			matches.put("E", matches.get("E") + 5);
			matches.put("I", matches.get("I") + 5);
			matches.put("O", matches.get("O") + 5);
			matches.put("U", matches.get("U") + 5);
		}
		if (avg[2] < 0) {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		} else {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		}
		if (avg[3] < 0) {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		} else {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		}
		if (avg[4] < 0) {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		} else {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		}
		if (avg[5] < 0) {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") + 1);
		} else {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") - 1);
		}
		if (avg[6] < 0) {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		} else {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		}
		if (avg[7] < 0) {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		} else {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		}
		if (avg[8] < 0) {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		} else {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		}
		if (avg[9] < 0) {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") + 1);
		} else {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") - 1);
		}
		if (avg[10] < 0) {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") + 1);
		} else {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") - 1);
		}
		if (avg[11] < 0) {
			matches.put("A", matches.get("A") + 1);
			matches.put("E", matches.get("E") + 1);
			matches.put("I", matches.get("I") + 1);
			matches.put("O", matches.get("O") + 1);
			matches.put("U", matches.get("U") + 1);
		} else {
			matches.put("A", matches.get("A") - 1);
			matches.put("E", matches.get("E") - 1);
			matches.put("I", matches.get("I") - 1);
			matches.put("O", matches.get("O") - 1);
			matches.put("U", matches.get("U") - 1);
		}
		return matches;
	}


	//Interfaces
	@Override
	public char recognizeChar(String pathFile) {
		return erkenneBuchstabeMFCCEx(pathFile);
	}


	@Override
	public String recognizePerson(Buchstabendaten[] charData) {
		return erkennePersonMFCC(charData);
	}

}
