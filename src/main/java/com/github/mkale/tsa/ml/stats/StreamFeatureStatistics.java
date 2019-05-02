package main.java.com.github.mkale.tsa.ml.stats;

public interface StreamFeatureStatistics { 
	 
	 void update(double feature); 
	 
	 Long getCount(); 
	 
	 Double getMean(); 
	 
	 Double getVariance(); 
	 
	 Double getStdDev(); 
	}