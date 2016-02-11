package dhbw.ai13.VOICETest;

import java.util.ArrayList;

import dhbw.ai13.bayesClassificator.naiveBayes.Algorithm;
import dhbw.ai13.bayesClassificator.naiveBayes.Result;
import dhbw.ai13.bayesClassificator.trainer.NaiveBayesTrainer;
import dhbw.ai13.spectrogram.SpectrogrammErsteller;

public class TestClass {

	public static void main(String[] args) {
		System.out.println("Starte VOICE");
		SpectrogrammErsteller creater = new SpectrogrammErsteller(1024, 0, "resources/A_Eric_Zenker_01.wav");
		System.out.println("Eric Zenker 1 eingelesen");
		NaiveBayesTrainer nbt = new NaiveBayesTrainer(10000, 256, 10, 0); 
		String[] st = {"a"};
		//nbt.getDatabase().setNameOfRow(st);
		double[][] d = change1(creater);
		System.out.println("Eric Zenker 1 eingefügt");
		
		nbt.trainDatabase(d, "a");
		 creater = new SpectrogrammErsteller(1024, 0, "resources/A_Eric_Zenker_02.wav");
		d = change1(creater);
		System.out.println("Eric Zenker 2 eingelesen");
		
		nbt.trainDatabase(d, "a");
		System.out.println("Eric Zenker 2 eingefügt");
		
		System.out.println("Training beendet");
		
		d = changer2(d);
		
		Algorithm alg = new Algorithm(nbt.getDatabase(), d);
		ArrayList<Result> res = alg.getBestResults();
		System.out.println("Datei 1 verglichen");

		for(int i=0;i<res.size();i++){
			System.out.println(res.get(i).toString());
		}

	}
	
	
	
	
	
	
	
	
	private static double[][] change1(SpectrogrammErsteller creater ){
		double[][] d = new double[10][32];
		int t = 0;
		double help = 0;
		for(int i=0;i<d.length;i++){
			for(int j=0;j<256;j++){
				help = help + creater.getSpectrogramData()[i][j];
				if(t==8){
					d[i][j/8] = help;
					t = t+1;
					t=0;
					help=0;
				}
			}
		}
		return d;
	}
	
	private static double[][] changer2(double[][] d){
		for(int i=0;i<d.length;i++){
			for(int j=0;j<d[0].length;j++){
				d[i][j] = d[i][j]/100;
			}
		}
		return d;
	}

}
