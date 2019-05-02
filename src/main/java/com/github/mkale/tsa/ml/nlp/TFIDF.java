package main.java.com.github.mkale.tsa.ml.nlp;


import java.io.Serializable; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
 
import com.github.pmerienne.trident.ml.util.MathUtil; 
 
public class TFIDF implements TextFeaturesExtractor, Serializable { 
 
 private static final long serialVersionUID = -6758089189650946158L; 
 
 private Integer corpusSize; 
 private Map<String, Double> termsInverseDocumentFrequencies; 
 
 public TFIDF() { 
 } 
 
 public TFIDF(List<List<String>> documents, int featureSize) { 
  this.init(documents, featureSize); 
 } 
 
 @Override 
 public double[] extractFeatures(List<String> documentTerms) { 
  double[] features = new double[this.termsInverseDocumentFrequencies.size()]; 
 
  int i = 0; 
  for (String term : this.termsInverseDocumentFrequencies.keySet()) { 
   features[i] = this.tfIdf(term, documentTerms); 
   i++; 
  } 
 
  return MathUtil.normalize(features); 
 } 
 
 public void init(List<List<String>> documents, int featureSize) { 
  // Init vocabulary 
  Vocabulary vocabulary = new Vocabulary(); 
  for (List<String> document : documents) { 
   vocabulary.addAll(document); 
  } 
 
  vocabulary.limitWords(featureSize); 
 
  // Calculates idfs 
  this.corpusSize = documents.size(); 
  this.termsInverseDocumentFrequencies = new HashMap<String, Double>(vocabulary.wordCount()); 
  for (String term : vocabulary) { 
   double idf = this.idf(term, documents); 
   this.termsInverseDocumentFrequencies.put(term, idf); 
  } 
 } 
 
 protected double tf(String term, List<String> documentTerms) { 
  double tf = 0.0; 
 
  for (String documentTerm : documentTerms) { 
   if (documentTerm.equals(term)) { 
    tf++; 
   } 
  } 
 
  return tf; 
 } 
 
 protected double idf(String term, List<List<String>> documents) { 
  // number of documents where term appears 
  double d = 0.0; 
  for (List<String> document : documents) { 
   if (document.contains(term)) { 
    d++; 
   } 
  } 
 
  return Math.log(this.corpusSize / (1 + d)); 
 } 
 
 protected double tfIdf(String term, List<String> documentTerms) { 
  double idf = this.termsInverseDocumentFrequencies.containsKey(term) ? this.termsInverseDocumentFrequencies.get(term) : Math.log(this.corpusSize); 
  double tf = this.tf(term, documentTerms); 
  return tf * idf; 
 } 
 
 public Integer getCorpusSize() { 
  return corpusSize; 
 } 
 
 public void setCorpusSize(Integer corpusSize) { 
  this.corpusSize = corpusSize; 
 } 
 
 public Map<String, Double> getTermsInverseDocumentFrequencies() { 
  return termsInverseDocumentFrequencies; 
 } 
 
 public void setTermsInverseDocumentFrequencies(Map<String, Double> termsInverseDocumentFrequencies) { 
  this.termsInverseDocumentFrequencies = termsInverseDocumentFrequencies; 
 } 
 
}