package dhbw.ai13.VOICETest;

import dhbw.ai13.spectrogram.SpectrogrammErsteller;

public class Test2 {

	public static void main(String[] args) {
		SpectrogrammErsteller creater = new SpectrogrammErsteller(1024, 0, "resources/Test As/erstes.wav");
		System.out.println("0");
		double[][] d = creater.getSpectrogramData();
		
		int c = 0;
		for(double[] b:d){
			for(double a:b){
				if(a>1000) c++;
			}
		}
		
		System.out.println("erstes: " + c);
		
		creater = new SpectrogrammErsteller(1024, 0, "resources/Til Schweiger/A_Til_Schweiger_01.wav");
		System.out.println("0");
		d = creater.getSpectrogramData();
		
		c = 0;
		for(double[] b:d){
			for(double a:b){
				if(a>3000) c++;
			}
		}
		
		System.out.println("Til: " + c);
		
		
		// TODO Auto-generated method stub

	}

}
