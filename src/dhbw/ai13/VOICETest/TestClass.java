package dhbw.ai13.VOICETest;

import java.util.ArrayList;

import dhbw.ai13.bayesClassificator.naiveBayes.Algorithm;
import dhbw.ai13.bayesClassificator.naiveBayes.HelpMethod;
import dhbw.ai13.bayesClassificator.naiveBayes.Result;
import dhbw.ai13.bayesClassificator.trainer.NaiveBayesTrainer;
import dhbw.ai13.spectrogram.SpectrogrammErsteller;

public class TestClass {

	public static void main(String[] args) {
		System.out.println("Starte VOICE");
		SpectrogrammErsteller creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/erstes.wav");
		System.out.println("0");
		double[][] d = creater.getSpectrogramData();
		
		NaiveBayesTrainer nbt = new NaiveBayesTrainer(100, 128, 3, 0);
		
		nbt.trainDatabase(d, "a");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/erstes.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("1");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/zweites.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("2");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/drittes.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("3");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/viertes.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("4");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/fuenftes.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("5");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/sechstes.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("6");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/siebtens.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("7");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/achtens.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("8");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/neuntens.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("9");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/zehn.wav");
		d = creater.getSpectrogramData();
		nbt.trainDatabase(d, "a");
		
		System.out.println("Fertig");
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/php10 zum testen.wav");
		d = creater.getSpectrogramData();
		
		Algorithm alg = new Algorithm(nbt.getDatabase(), d, 5);
		ArrayList<Result> res = alg.getBestResults();
		int count = 1;
		for(int i=0;i<res.size();i++){
			if(res.get(i).getProbability() > 2700000){
				String p = "resources/Test As/cutted/A"+count+".wav";
				HelpMethod.copyAudio("resources/Test As/php10 zum testen.wav", p, res.get(i).getTimeIndex(), 200, 10);
				count++;
				System.out.println(res.get(i).toString() + "      count: " +  count);	
			}		
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
