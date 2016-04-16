package dhbw.ai13.bayesClassificator.naiveBayes;

/**
 * Um die möglichen Resultate zu speichern
 * name = name
 * index = phonem
 * timeIndex = timeIndex im Stream
 * @author Tim Tiede
 */

public class Result {
	private String name;
	private int index;
	private int timeIndex;
	private double probability;

	//constructor	
	public Result(String name, double possibility, int index, int timeIndex) {
		this.name = name;
		this.probability = possibility;
		this.index = index;
		this.timeIndex = timeIndex;
	}
	
	//toString
	public String toString(){
		return new String("Name: " + name + ", Wahrscheinlichkeit: " + Double.toString(probability) + " Anfang: " + timeIndex);
	}

	//getter
	public String getName() {
		return name;
	}

	public double getProbability() {
		return probability;
	}
	
	public int getIndex(){
		return index;
	}
	
	public int getTimeIndex(){
		return timeIndex;
	}
}
