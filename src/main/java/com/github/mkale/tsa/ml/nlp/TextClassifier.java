package main.java.com.github.mkale.tsa.ml.nlp;

import java.util.List; 

public interface TextClassifier<L> { 
 
 void update(L label, List<String> documentWords); 
 
 L classify(List<String> documentWords); 
}