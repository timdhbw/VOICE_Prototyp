package dhbw.ai12.ann;

/**
* The class ArrayUtil has supporting functions.
* @author KI-Team
*/
public class ArrayUtil {

	/**
	* The function getMaxIndex determines the minimum value in an array.
	* @param data Double array with data.
	* @return The index that has the minimum value is returned. If the array is empty, -1 is returned. 
	*/
	public static int getMaxIndex(double[] data) {
		double d = Double.MIN_VALUE;
		int index = -1;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > d) {
				d = data[i];
				index = i;
			}
		}
		return index;
	}
}
