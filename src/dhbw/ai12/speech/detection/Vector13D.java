package dhbw.ai12.speech.detection;

import java.util.Arrays;
/**
 * class  for representing a filtered and shrunk mfcc, that is characteristic for a person and their vocal
 * @author Stefan Schultes
 * @version 13.04.2015
 */
public class Vector13D{
	private double[] vector;	//filtered mfcc
	private String vocal;		//name of the recorded vocal
	private String user;		//name of the recorded person
	private double dist=0; 		//distance to the best suitable found solution (used in VQ)
	
	/**Creates a new Vector13D
	 * @param vector vector(average and filtered)
	 * @param vocal	 recorded vocal
	 * @param user	 recorded person
	 */
	public Vector13D(double[] vector,String vocal,String user){
		this.vector=vector;
		this.vocal=vocal;
		this.user=user;
	}
	
	public double[] getVector() {
		return vector;
	}
	
	public String getVocal() {
		return vocal;
	}

	public String getUser() {
		return user;
	}

	public double getDist() {
		return dist;
	}

	/** Calculates the Euclidean distance to a given other Vector13D
	 *  This calculation does not use the first parameter of the mfcc!
	 * @param v Vector13D
	 * @return  Euclidean distance without first parameter
	 */
	public double distance(Vector13D v){
		double sum=0;
		for(int i=1;i<13;i++){
			sum+=(vector[i]-v.vector[i])*(vector[i]-v.vector[i]);
		}
		return Math.sqrt(sum);
	}
	
	public String toString(){
		return user+" "+vocal+ " "+dist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(dist);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + Arrays.hashCode(vector);
		result = prime * result + ((vocal == null) ? 0 : vocal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector13D other = (Vector13D) obj;
		if (Double.doubleToLongBits(dist) != Double
				.doubleToLongBits(other.dist))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (!Arrays.equals(vector, other.vector))
			return false;
		if (vocal == null) {
			if (other.vocal != null)
				return false;
		} else if (!vocal.equals(other.vocal))
			return false;
		return true;
	}
	
	public void setDist(double dist) {
		this.dist = dist;
	}
}

