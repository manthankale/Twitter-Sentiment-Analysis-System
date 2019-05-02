package main.java.com.github.mkale.tsa.ml.regression;

import java.util.Arrays; 
import java.util.List; 
 
import com.github.pmerienne.trident.ml.core.Instance; 
import com.github.pmerienne.trident.ml.util.KeysUtil; 
 
import storm.trident.operation.TridentCollector; 
import storm.trident.state.BaseStateUpdater; 
import storm.trident.state.map.MapState; 
import storm.trident.tuple.TridentTuple; 
 
public class RegressionUpdater extends BaseStateUpdater<MapState<Regressor>> { 
 
  
 
 /**
  *  
  */ 
 private static final long serialVersionUID = -4860370637415723032L; 
 
 private String classifierName; 
 
 private Regressor initialRegressor; 
 
 public RegressionUpdater(String classifierName, Regressor initialRegressor) { 
  this.classifierName = classifierName; 
  this.initialRegressor = initialRegressor; 
 } 
 
 @SuppressWarnings("unchecked") 
 @Override 
 public void updateState(MapState<Regressor> state, List<TridentTuple> tuples, TridentCollector collector) { 
  // Get model 
  List<Regressor> regressors = state.multiGet(KeysUtil.toKeys(this.classifierName)); 
  Regressor regressor = null; 
  if (regressors != null && !regressors.isEmpty()) { 
   regressor = regressors.get(0); 
  } 
 
  // Init it if necessary 
  if (regressor == null) { 
   regressor = this.initialRegressor; 
  } 
 
  // Update model 
  Instance<Double> instance; 
  for (TridentTuple tuple : tuples) { 
   instance = (Instance<Double>) tuple.get(0); 
   regressor.update(instance.label, instance.features); 
  } 
 
  // Save model 
  state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(regressor)); 
 } 
 
}