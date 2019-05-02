package main.java.com.github.mkale.tsa.ml.evaluation;

import java.util.ArrayList; 
import java.util.List; 
 
import storm.trident.operation.TridentCollector; 
import storm.trident.state.BaseQueryFunction; 
import storm.trident.state.snapshot.ReadOnlySnapshottable; 
import storm.trident.tuple.TridentTuple; 
import backtype.storm.tuple.Values; 
 
public class EvaluationQuery<L> extends BaseQueryFunction<ReadOnlySnapshottable<Evaluation<L>>, Double> { 
 
 private static final long serialVersionUID = 1L; 
 
 @Override 
 public List<Double> batchRetrieve(ReadOnlySnapshottable<Evaluation<L>> state, List<TridentTuple> args) { 
  List<Double> ret = new ArrayList<Double>(args.size()); 
   
  Evaluation<L> snapshot = state.get(); 
  for (int i = 0; i < args.size(); i++) { 
   ret.add(snapshot.getEvaluation()); 
  } 
  return ret; 
 } 
 
 @Override 
 public void execute(TridentTuple tuple, Double result, TridentCollector collector) { 
  collector.emit(new Values(result)); 
 } 
}