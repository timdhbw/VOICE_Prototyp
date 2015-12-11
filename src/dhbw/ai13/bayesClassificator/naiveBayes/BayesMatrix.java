package tim.naiveBayes;

/*
 *In der BayesMatrix wird eine Matrix gespeichert.
 *Die Matrix beschreibt in den Zeilen jeweils ein Phonem, jede Spalte steht fuer eine Zeit 
 */
public class BayesMatrix {

	private double[][] matrix;
	private int row;
	private String[] nameOfRow;
	private int column;

	// constructor
	public BayesMatrix(double[][] matrix) {
		this.matrix = matrix;
		this.row = matrix[0].length;
		this.column = matrix.length;
		this.nameOfRow = new String[row];
	}

	//create random matrix
	public BayesMatrix(int column, int row) {
		this.matrix = new double[column][row];
		this.row = row;
		this.column = column;
		this.nameOfRow = new String[row];
		
		for(int i=0;i<column;i++){
			for(int j=0;j<row;j++){
				matrix[i][j] = Math.random();
			}
		}
	}

	public BayesMatrix(double[][] matrix, String[] nameOfRow) {
		this.matrix = matrix;
		this.row = matrix[0].length;
		this.column = matrix.length;
		this.nameOfRow = nameOfRow;
	}
	

	// methods

	public double getPossibility(int column, int row) {
		return matrix[column][row];
	}

	// gives the first column
	public double[] getBeginningArray() {
		double[] d = new double[row];
		for (int i = 0; i < row; i++) {
			d[i] = matrix[0][i];
		}
		return d;
	}
	
	public void setData(double data,int column, int row){
		if((this.column>column)&&(this.row>row)){
			matrix[column][row] = data;
		}else{
			System.out.println("OutOfBouce: Set BayesMatrix!");
		}
	}

	public int getNumOfColumn() {
		return column;
	}

	public int getNumOfRows() {
		return row;
	}

	public String[] getNameOfRow() {
		return nameOfRow;
	}

	// toString Method
	public String toString() {
		String str = null;
		for (int i = 0; i <= column; i++) {
			if (i == 0) {
				str = "\t";
			} else {
				str = str + Integer.toString(i) + "\t";
			}
		}
		str = str + "\n";
		for (int i = 0; i < row; i++) {
			str = str + nameOfRow[i] + "\t";

			for (int j = 0; j < column; j++) {
				str = str + Double.toString(matrix[j][i]).substring(0, 5)
						+ "\t";
			}
			str = str + "\n";
		}
		return str;
	}
}
