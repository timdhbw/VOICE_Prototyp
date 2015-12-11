package tim.naiveBayes;

import java.util.ArrayList;

public class MainTest {

	public static void main(String[] args) {
		
		System.out.println(Math.random());
		System.out.println(Math.random());
		
		int[][] a = new int[30][10];
		for(int i=0;i<30;i++){
			for(int j=0;j<10;j++){
				a[i][j] = (int)(Math.random()*10);
			}
		}
		int[]b = a[0];
		System.out.println(a[0][0]+" "+a[0][1] + " " +a[0][2]);
		System.out.println(b[0] + " " + b[1] + " " +b[2]);

		Database data = new Database(10,10,6,8);
//		System.out.println(data.getData(2,3).toString());
//		System.out.println(data.getData(4,4).toString());
//		System.out.println(data.getData(2, 2).getPossibility(2, 2));
		
		Algorithm al = new Algorithm(data, a, 5, 0.006);
		ArrayList<Result> aray = al.getBestResults();
		if(aray.isEmpty()){
			System.out.println("empty");
		}else{
			for(int i=0;i<aray.size();i++){
				System.out.println(aray.get(i).toString());
			}
		}
	}

	
	
}
