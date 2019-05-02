package main.java.com.github.mkale.tsa.ml.stats;

import java.io.Serializable; 

public class FixedStreamFeatureStatistics implements StreamFeatureStatistics, Serializable { 
 
 private static final long serialVersionUID = -7406184811401750690L; 
 
 private long count = 0L; 
 private double mean = 0.0; 
 private double m2 = 0.0; 
 
 public void update(double feature) { 
  this.count = this.count + 1; 
  double delta = feature - this.mean; 
  this.mean = this.mean + delta / this.count; 
  this.m2 = this.m2 + delta * (feature - this.mean); 
 } 
 
 @Override 
 public Long getCount() { 
  return count; 
 } 
 
 @Override 
 public Double getMean() { 
  return mean; 
 } 
 
 @Override 
 public Double getVariance() { 
  return m2 / (count - 1); 
 } 
 
 @Override 
 public Double getStdDev() { 
  return Math.sqrt(this.getVariance()); 
 } 
 
 @Override 
 public String toString() { 
  return "SimpleStreamFeatureStatistics [m2=" + m2 + ", count=" + count + ", mean=" + mean + ", variance=" + getVariance() + ", stdDev=" + getStdDev() 
    + "]"; 
 } 
 
}