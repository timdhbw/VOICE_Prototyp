package dhbw.ai13.bayesClassificator.naiveBayes;

/*
 * Das hier ist die Datenbank fuer den Bayes Classificator
 */

public class Matrix {
	//double array for probability values [(int)Intensity][Frequence][TimeStep][Phonem]
	private double[][][][] matrix;
	
	//size of matrix
	private int numOfIntensities;
	private int numOfFrequence;
	private int numOfTimeStep;
	private int numOfPhonem;
	
	//string for name of phonems
	private String[] phonem;
	
	
	
	public Matrix(){
	}
	
	public Matrix(int intensity, int frequence, int timeStep, int phonem){
		this.setMatrix(new double[intensity][frequence][timeStep][phonem]);
		this.setPhon(new String[phonem]);
		this.numOfIntensities = intensity;
		this.numOfFrequence = frequence;
		this.numOfTimeStep = timeStep;
		this.numOfPhonem = phonem;
	}
	
	//gets Array of start (timeStep == 0) probabilities
	public double[] getProbArray(int intensity, int frequence){
		double[] startArray = new double[numOfPhonem];
		for(int i=0; i < numOfPhonem; i++){
			startArray[i] = matrix[intensity][frequence][0][i];
		}
		return startArray;
	}
	
	
	//getter and setter
	
	//set a probability value
	public void setValue(double value, int intensity, int frequence, int timeStep, int phonem){
		matrix[intensity][frequence][timeStep][phonem] = value;
	}
	
	//get probability value
	public double getValue(int intensity, int frequence, int timeStep, int phonem){
		//System.out.println("Intensitaet: " + intensity + " Frequenz: " + frequence + " TimeStep: " + timeStep + " Phonem: " + phonem );
		return matrix[intensity][frequence][timeStep][phonem];
	}
	
	//set phonem
	public void setPhonem(String concretePhonem, int phonem){
		this.phonem[phonem] = concretePhonem;
	}
	
	//get phonem
	public String getPhonem(int phonem){
		return this.phonem[phonem];
	}
	
	
	public double[][][][] getMatrix() {
		return matrix;
	}

	public void setMatrix(double[][][][] matrix) {
		this.matrix = matrix;
		this.numOfIntensities = matrix.length;
		this.numOfFrequence = matrix[0].length;
		this.numOfTimeStep = matrix[0][0].length;
		this.numOfPhonem = matrix[0][0][0].length;
		setPhon(new String[numOfPhonem]);
	}

	public String[] getPhonem() {
		return phonem;
	}

	public void setPhon(String[] phonem) {
		this.phonem = phonem;
	}
	
	public int getNumOfIntensities(){
		return numOfIntensities;
	}
	
	public int getNumOfFrequencies(){
		return numOfFrequence;
	}
	
	public int getNumOfTimeSteps(){
		return numOfTimeStep;
	}
	
}
