package main.java.com.github.mkale.tsa.ml.regression;

import java.io.Serializable; 


public interface Regressor extends Serializable { 
 
 Double predict(double[] features); 
 
 void update(Double expected, double[] features); 
 
 void reset(); 
}