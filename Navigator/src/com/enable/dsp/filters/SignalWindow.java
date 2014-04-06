package com.enable.dsp.filters;

public abstract class SignalWindow {
	
	protected double[] signal;
	
	public SignalWindow(int size) {
		signal = new double[size];
	}
	
	public double [] getSignal() {
		return signal;
	}
	

}
