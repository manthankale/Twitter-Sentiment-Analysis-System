package main.java.com.github.mkale.tsa.ml.preprocessing;

import java.util.List; 

public interface TextTokenizer { 
 
 List<String> tokenize(String text); 
}