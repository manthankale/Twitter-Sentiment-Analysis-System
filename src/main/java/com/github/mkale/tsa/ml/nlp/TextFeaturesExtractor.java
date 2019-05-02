package main.java.com.github.mkale.tsa.ml.nlp;

import java.util.List; 

public interface TextFeaturesExtractor { 
 
 double[] extractFeatures(List<String> documentWords); 
}