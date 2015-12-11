package dhbw.ai13.speech.detection;

/**
 * Filter the result of the mfcc calculation. Detects and drops silence as well
 * as noise.
 * 
 * @author Stefan Schultes
 * @version 13.04.2015
 */
public class FilterMFCCResult {
	// private static final double FILTER_NOISE=60; //vocal as a uniform signal
	// doesn't change to much between two frames -> filter noise and bad speech
	// private static final double FILTER_SILENCE=-400; //first parameter of
	// mfcc must be greater than this value -> filters silence

	// for liferingAmount=11
	// private static final double FILTER_SILENCE=-180;
	// private static final double FILTER_NOISE=180;

	// mfcc with dervivation
	private static final double FILTER_SILENCE = -400;
	private static final double FILTER_NOISE = 60;


	/**
	 * filters the mfcc frames for silence and noise. Calculates average of the
	 * remaining values.
	 * 
	 * @param mfccFrames
	 *            Mfcc frames
	 * @return filtered and shrunk characteristic mfcc array
	 */
	public static double[] filterAndShrinkFrames(double[][] mfccFrames) {
		double[] avgMfcc = new double[mfccFrames[0].length];
		int cnt = 0;

		for (int j = 1; j < mfccFrames.length; j++) {
			double difference = 0;

			for (int i = 0; i < 13; i++) {
				difference += Math.abs(mfccFrames[j][i] - mfccFrames[j - 1][i]);
			}

			if (difference < FILTER_NOISE && mfccFrames[j][0] > FILTER_SILENCE) {
				for (int i = 0; i < mfccFrames[j].length; i++) {
					avgMfcc[i] += mfccFrames[j][i];
				}
				cnt++;
			}
		}
		for (int i = 0; i < avgMfcc.length; i++) {
			avgMfcc[i] = avgMfcc[i] / cnt;
		}
		return avgMfcc;
	}

	public static double[] averageFrames(double[][] mfccFrames) {
		double[] avgMfcc = new double[mfccFrames[0].length];
		int cnt = 0;
		for (int j = 1; j < mfccFrames.length; j++) {
			for (int i = 0; i < mfccFrames[j].length; i++) {
				avgMfcc[i] += mfccFrames[j][i];
			}
			cnt++;
		}
		for (int i = 0; i < avgMfcc.length; i++) {
			avgMfcc[i] = avgMfcc[i] / cnt;
		}
		return avgMfcc;
	}
}
