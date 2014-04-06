package com.enable.service.stepdetector;

/**
 * StrideLengthEstimator class, contains model for estimation of the stride
 * length based on stride duration.
 * 
 */
public class StrideLengthEstimator {

	private static final double DEFAULT_STRIDE_LENGTH = 0.7;
	
	private double stepLength;
	private double factor;
	
	/**
	 * Constructor
	 * 
	 * @param factor
	 *            is a linear correction factor (leg length or person height can
	 *            be used as basis)
	 */
	public StrideLengthEstimator(double factor) {
		this.factor = factor;
	}

	/**
	 * The estimator uses the stride length vs. frequency dependency to compute
	 * stride length estimate from stride duration.
	 * 
	 * @param duration
	 *            stride duration
	 */
	public double getStrideLengthFromDuration(double duration) {
		return factor * (0.3608 + 0.1639 / duration);
	}
}
