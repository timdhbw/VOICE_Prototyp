package dhbw.ai13.bayesClassificator.trainer;

import java.util.ArrayList;

import dhbw.ai13.bayesClassificator.naiveBayes.Algorithm;
import dhbw.ai13.bayesClassificator.naiveBayes.HelpMethod;
import dhbw.ai13.bayesClassificator.naiveBayes.Result;
import dhbw.ai13.spectrogram.SpectrogrammErsteller;

public class Test {

	public static void main(String[] args) {
		//Spectogramm erstellen
		SpectrogrammErsteller creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/A_Til_Schweiger_01.wav");
		double [][] d = creater.getSpectrogramData();	
		
		//NaiveBayesTrainer erstellen
		NaiveBayesTrainer nbt = new NaiveBayesTrainer(10000, 256, 2, 0); 		
		nbt.trainDatabase(d, "a");
		
		 creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/A_Til_Schweiger_02.wav");
    	 d = creater.getSpectrogramData();	
		nbt.trainDatabase(d, "a");
		
		 creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/E_Til_Schweiger_02.wav"); 
		 d = creater.getSpectrogramData();
		 nbt.trainDatabase(d, "e");
		 
		 creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/E_Til_Schweiger_02.wav");
		 d = creater.getSpectrogramData();
		 nbt.trainDatabase(d, "e");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/A_Til_Schweiger_03.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("Training beendet");
//================================================================================================
		//Ende Training
		
		//Stream einstellen
		creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/Til_Schweiger_Test.wav");
		 d = creater.getSpectrogramData();
		
		//Stream durchsuchen
		Algorithm alg = new Algorithm(nbt.getDatabase(), d);
		ArrayList<Result> res = alg.getBestResults();
		System.out.println("Results berechnet");
		
		Result p = null;
		if(!res.isEmpty()){
			p = res.get(0);
		}
		for(int i=0;i<res.size();i++){
			if(p.getProbability()<res.get(i).getProbability()){
				p = res.get(i);
			}
		}
		//bestes Result ausgeben
		System.out.println(p.toString());
		
		//wav-Datei ausschneiden
		HelpMethod.copyAudio("resources/Til Schweiger/Til_Schweiger_Test.wav", "resources/Til Schweiger/TilCut.wav", p.getTimeIndex()*21, 200);
		System.out.println("Done");
	}

}
