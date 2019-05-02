package main.java.com.github.mkale.tsa.ml.evaluation;

import java.io.Serializable; 

public interface Evaluation<L> extends Serializable { 
 
 double getEvaluation(); 
 
}