package tim.naiveBayes;
/*
 *In dieser Klasse ist die Gesamtheit der Warsheinlichkeiten in einem zweidimensionalen BayesMatrix Array gespeichert.
 *Jedes Feld der Matrix besteht aus einer BayesMatrix, die die Wahrscheinlichkeiten fuer eine Bestimmte Zahl, die an
 *einer bestimmten Stelle des Eingangsarrays steht, bestimmt.
 *Die Spalten sind eine ganzzahl(0,1,2,3,..) 
 *Die Reihen sind der Indexwert aus dem Eingangsarray (quasi der Intesitaetsbereich).
 */
public class Database {
	private BayesMatrix[][] data;
	private String[] nameOfRow;
	private int row; //frequence
	private int column;//intensity
	
	//constructor
	public Database(BayesMatrix[][] data){
		this.data = data;
		this.row = data[0].length;
		this.column = data.length;
		this.nameOfRow = data[0][0].getNameOfRow();
		
	}
	
	//create random Database
	public Database(int columnIntensity, int rowFrequence, int bmColumnTimeStep, int bmRowPhonem){
		this.data = new BayesMatrix[columnIntensity][rowFrequence];
		this.column = columnIntensity;
		this.row = rowFrequence;
		for(int i=0;i<columnIntensity;i++){
			for(int j=0;j<rowFrequence;j++){
				data[i][j] = new BayesMatrix(bmColumnTimeStep, bmRowPhonem);
			}
		}
		this.nameOfRow = data[0][0].getNameOfRow();
	}
	
	//method
	//get BayesMatrix out of Database
	public BayesMatrix getData(int column, int row){
		if(this.column >= column && this.row >= row){
			return data[column][row];
		}else{
			System.out.println("Out of bounce(Row or Column!) return is null!!");
			return null;
		}
	}
	
	public String[] getNameOfRow(){
		return nameOfRow;
	}
	
	public int getNumberOfIntensity(){
		return column;
	}
}
