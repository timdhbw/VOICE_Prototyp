package dhbw.ai13.bayesClassificator.trainer;

import java.util.ArrayList;

import dhbw.ai13.bayesClassificator.naiveBayes.Algorithm;
import dhbw.ai13.bayesClassificator.naiveBayes.HelpMethod;
import dhbw.ai13.bayesClassificator.naiveBayes.Result;
import dhbw.ai13.spectrogram.SpectrogrammErsteller;

public class Test {

	public static void main(String[] args) {
		//Spectogramm erstellen
		SpectrogrammErsteller creater = new SpectrogrammErsteller(1024, 0, "resources/A_Eric_Zenker_01.wav");
		double [][] d = creater.getSpectrogramData();	
		
		//NaiveBayesTrainer erstellen
		NaiveBayesTrainer nbt = new NaiveBayesTrainer(10000, 256, 10, 0); 		
		
		nbt.trainDatabase(d, "a");
		System.out.println("Training 1 beendet");
		 creater = new SpectrogrammErsteller(1024, 0, "resources/A_Eric_Zenker_02.wav");
		 
		 d = creater.getSpectrogramData();
		
		nbt.trainDatabase(d, "a");
		System.out.println("Training 2 beendet");
//		System.out.println("Database");
//		System.out.print("Intensitaetsspalten: " + nbt.getDatabase().getNumberOfIntensity());
//		System.out.print(", Frequenzreihen: " + nbt.getDatabase().getNumberOfFrequece());
//		System.out.print(", TimeStet Spalten: " + nbt.getDatabase().getData(0, 0).getNumOfColumn());
//		System.out.println(", Phonem Reihen: " + nbt.getDatabase().getData(0, 0).getNumOfRows());
//		System.out.println("Stichprobe(2,20,5,0):  " + nbt.getDatabase().getData(2, 20).getPossibility(5, 0));
//		System.out.println("Stichprobe(5,12,7,0):  " + nbt.getDatabase().getData(5, 12).getPossibility(7, 0));
//		for(int j=0;j<30;j++){
//		for(int i=0;i<32;i++){
//			System.out.println("Stichprobe("+j+","+i+",7,0):  " + nbt.getDatabase().getData(j, i).getPossibility(7, 0));
//		}}
//		for(int i=0;i<d.length;i++){
//			for(int j=0;j<d[0].length;j++){
//				d[i][j] = d[i][j]/100;
//			}
//		}
		Algorithm alg = new Algorithm(nbt.getDatabase(), d);
		ArrayList<Result> res = alg.getBestResults();
		System.out.println("Results berechnet");
		for(int i=0;i<res.size();i++){
			System.out.println(res.get(i).toString());
		}
		
		HelpMethod.copyAudio("resources/A_Eric_Zenker_01.wav", "resources/A_Eric_Zenker_01_cut.wav", 6, 600);
		System.out.println("Done");
	}

}
