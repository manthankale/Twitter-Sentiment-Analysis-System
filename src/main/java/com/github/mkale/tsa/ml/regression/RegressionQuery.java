package main.java.com.github.mkale.tsa.ml.regression;

import java.util.ArrayList; 
import java.util.List; 
 
import com.github.pmerienne.trident.ml.core.Instance; 
import com.github.pmerienne.trident.ml.util.KeysUtil; 
 
import storm.trident.operation.TridentCollector; 
import storm.trident.state.BaseQueryFunction; 
import storm.trident.state.map.MapState; 
import storm.trident.tuple.TridentTuple; 
import backtype.storm.tuple.Values; 
 
public class RegressionQuery extends BaseQueryFunction<MapState<Regressor>, Double> { 
 
 private static final long serialVersionUID = 582183815675337782L; 
 
 private String regressorName; 
 
 public RegressionQuery(String regressorName) { 
  this.regressorName = regressorName; 
 } 
 
 /**
  * What about cyclomatic complexity !! TODO : refactor this mess 
  */ 
 @SuppressWarnings("unchecked") 
 @Override 
 public List<Double> batchRetrieve(MapState<Regressor> state, List<TridentTuple> tuples) { 
  List<Double> labels = new ArrayList<Double>(); 
 
  List<Regressor> regressors = state.multiGet(KeysUtil.toKeys(this.regressorName)); 
  if (regressors != null && !regressors.isEmpty()) { 
   Regressor regressor = regressors.get(0); 
   if (regressor == null) { 
    for (int i = 0; i < tuples.size(); i++) { 
     labels.add(null); 
    } 
   } else { 
    Double label; 
    Instance<Double> instance; 
    for (TridentTuple tuple : tuples) { 
     instance = (Instance<Double>) tuple.get(0); 
     label = regressor.predict(instance.features); 
     labels.add(label); 
    } 
   } 
  } else { 
   for (int i = 0; i < tuples.size(); i++) { 
    labels.add(null); 
   } 
  } 
 
  return labels; 
 } 
 
 public void execute(TridentTuple tuple, Double result, TridentCollector collector) { 
  collector.emit(new Values(result)); 
 } 
 
}