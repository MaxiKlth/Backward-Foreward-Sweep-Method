package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CalculateCurrentVoltageValues {

	int nominalVoltag = 230;
	boolean congestionExists = false;
	int maxCurrent = 100; 
	int amountNodes = 33;
	boolean afterMatching = false;
	public float identifyGridCongestion() {
        // Configure Grid
		GridConfiguration gridMatrix = new GridConfiguration();
        double maxCurrentValue = 0;
        
        int[][] matrix = (amountNodes == 5) ? gridMatrix.get5Matrix() : gridMatrix.get33Matrix();
        double[][] impedanceMatrix = gridMatrix.impedanceMatrix33;

        List<Integer> lineEndNodes = identifyLineEndNodes(matrix);
        List<Integer> lineStartNodes = identifyLineStartNodes(matrix);
        List<Integer> lineSideNodes = identifyLineSideNodes(matrix);
//        lineStartNodes.add(0);

        // Calculate Power Flow
        HashMap<Integer, Double> currentValues = initializeCurrentValues(matrix.length-1);
        HashMap<Integer, Double> energyValues = getEnergyValues(gridMatrix);
        HashMap<Integer, Double> voltageValues = initializeVoltageValues (matrix.length-1, nominalVoltag);
        
        if (energyValues != null) {
        	
        	for (int i = 0; i < 5; i++) { // 5 is a defaultValue
        		calculateCurrentValues(matrix, lineEndNodes, lineStartNodes, currentValues, energyValues, voltageValues);
        		
        		// After calculating current, perform a forward sweep for voltage adjustment
        		calculateVoltages(matrix, impedanceMatrix, lineStartNodes, lineEndNodes, lineSideNodes, currentValues, voltageValues);
			}

            // Write to Excel regardless of afterMatching value
            //writeCurrentValues2Excel(currentValues);

            // Identify Grid Congestion
            return congestionExists ? (float) (maxCurrentValue - maxCurrent) : 0;
        } else {
            return 0;
        }
    }



	private HashMap<Integer, Double> initializeVoltageValues(int length, double voltage) {
    	  HashMap<Integer, Double> voltageValues = new HashMap<>();
          for (int i = 0; i <= length; i++) {
              voltageValues.put(i, voltage);
          }
          return voltageValues;
	}

	private List<Integer> identifyLineEndNodes(int[][] matrix) {
        List<Integer> lineEndNodes = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            int temporarySumStartConnection = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                temporarySumStartConnection += matrix[i][j];
            }
            if (temporarySumStartConnection == 0) {
                lineEndNodes.add(i + 1);
            }
        }
        return lineEndNodes;
    }

    private List<Integer> identifyLineStartNodes(int[][] matrix) {
        List<Integer> lineStartNodes = new ArrayList<>();
        lineStartNodes.add(1); // Add always the first value of line
        for (int i = 1; i < matrix.length; i++) {
            int temporarySumEndConnection = 0;
            if (matrix[i-1][i]==0) {
            	lineStartNodes.add(i);
            }
        }
        return lineStartNodes;
    }
    
    private List<Integer> identifyLineSideNodes(int[][] matrix) {
        List<Integer> lineSideNodes = new ArrayList<>();
        lineSideNodes.add(1); // Add always the first value of line
        for (int i = 1; i < matrix.length; i++) {
        	int sumNodes = 0;
        	for (int j = 1; j < matrix.length; j++) {
        		sumNodes += matrix[i][j];
        	}
        	if (sumNodes>1) {
        		lineSideNodes.add(i);
        	}
        }
        return lineSideNodes;
	}

    private HashMap<Integer, Double> initializeCurrentValues(int length) {
        HashMap<Integer, Double> currentValues = new HashMap<>();
        for (int i = 1; i <= length; i++) {
            currentValues.put(i, 0.0);
        }
        return currentValues;
    }
    


    private HashMap<Integer, Double> getEnergyValues(GridConfiguration gridMatrix) {
    	HashMap<Integer, Double> energyValues = gridMatrix.defaultInitializationEnergyValues(amountNodes);
		if (this.afterMatching==true) {
			HashMap<Integer, Double> energyAdaptedValues = new HashMap<Integer, Double>(); //To be stored in a data model
			for (HashMap.Entry<Integer, Double> energyTempoValues : energyValues.entrySet()) {
				Integer intAid = energyTempoValues.getKey();
				Double energyFloatValue = energyTempoValues.getValue();
				if (energyAdaptedValues.containsKey(intAid)) {
					energyFloatValue = energyFloatValue - energyAdaptedValues.get(intAid);
					energyValues.replace(intAid, energyFloatValue);
				}
			}
			
		} 
        return energyValues; // Placeholder
    }

    private void calculateCurrentValues(int[][] matrix, List<Integer> lineEndNodes, List<Integer> lineStartNodes, HashMap<Integer, Double> currentValues, HashMap<Integer, Double> energyValues, HashMap<Integer, Double> voltageValues) {
        boolean congestionExists = false;
        double maxCurrentValue = 0;

        for (int endNode : lineEndNodes) {
            int actualNode = endNode - 1;
            double temporaryCurrentValue = 0.0;
            boolean alreadyFinished = false;
            boolean isSideLine = false;
            double temporarySideLineValue = 0.0;

            do {
                for (int j = actualNode; j > 0; j--) {
                    if (matrix[j - 1][actualNode] == 1) {
                        if (energyValues.containsKey(actualNode)) {
                        	if (j==actualNode && isSideLine==false) {
                        		 temporaryCurrentValue +=energyValues.get(actualNode) / voltageValues.get(actualNode);
                        	} else if (j != actualNode && isSideLine== false) {
                        		isSideLine = true;
                        		temporarySideLineValue = temporaryCurrentValue + energyValues.get(actualNode) / voltageValues.get(actualNode);
                        	} else if (isSideLine == true) {
                        		temporaryCurrentValue = currentValues.get(actualNode) + temporarySideLineValue;
                        	}
                            currentValues.put(actualNode, temporaryCurrentValue);

                            if (temporaryCurrentValue > maxCurrentValue) {
                                congestionExists = true;
                                maxCurrentValue = temporaryCurrentValue;
                            }

                            actualNode = j - 1;
                            if (j == lineStartNodes.get(0)) {
                                alreadyFinished = true;
                                break;
                            }
                        }
                    }
                }
            } while (!alreadyFinished);
        }
    }
    

	private void calculateVoltages(int[][] matrix,  double[][] impedanceMatrix, List<Integer> lineStartNodes, List<Integer> lineEndNodes,List<Integer> lineSideNodes, HashMap<Integer, Double> currentValues, HashMap<Integer, Double> voltageValues ) {
	    
//	    double baseVoltage = 1.0; // Assuming base voltage of 1 p.u. (per unit)
	    HashMap<Integer, Double> voltageDropValues = initializeVoltageValues(matrix.length -1, 0);
		double previousVoltageDrop = 0.0;
	    for (int k = 0; k < lineStartNodes.size(); k++) {
	    	int startNode = lineStartNodes.get(k);
	    	int actualNode = startNode;
	    	double voltageDrop = 0.0;
	    	do {
	    		if (matrix[actualNode-1][actualNode] == 1 || matrix[lineSideNodes.get(k)][actualNode]==1) { // If there's a connection
	    			int tempoStartNode;
	    			
	    			if (matrix[actualNode-1][actualNode] == 1) {
	    				tempoStartNode = actualNode-1;
	    			} else {
	    				tempoStartNode = lineSideNodes.get(k);
	    			}
	    			
	    			double current = currentValues.getOrDefault(actualNode, 0.0);
	    			voltageDrop += calculateVoltageDrop(current, matrix, impedanceMatrix, tempoStartNode, actualNode); // Method to calculate voltage drop in the line
	    			double newVoltage = 0.0;
	    			if(startNode!=actualNode) {
	    				newVoltage = voltageValues.get(actualNode) - voltageDrop;
	    			} else {
	    				int sideNodeStart = lineSideNodes.get(k);
	    				newVoltage = voltageValues.get(actualNode) - voltageDrop - voltageDropValues.get(sideNodeStart);
	    			}
	    			voltageDropValues.put(actualNode, voltageDrop);
	    			voltageValues.put(actualNode, newVoltage);
	    		}
	    		actualNode++;
	    	} while (actualNode < lineEndNodes.get(k));
	    }
	    // Now voltageValues contains the voltage at each node
	}
	
	private double calculateVoltageDrop(double current, int[][] matrix,  double[][] impedanceMatrix, int startNode, int endNode) {
	    double impedance = impedanceMatrix[startNode][endNode]; // Example impedance value
	    return current * impedance;
	}

//	private void writeCurrentValues2Excel(HashMap<Integer, Double> currentValues) {
//	    Path filePath = Paths.get(OUTPUT_FILE_PATH + this.congestionManagingAgent.getLocalName() + ".csv");
//	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))) {
//	        int tradingPeriod = this.congestionManagingAgent.getInternalDataModel().getTradingPeriod();
//	        // Update the current values map only once.
//	        this.congestionManagingAgent.getInternalDataModel().updateCurrentValuesMap(tradingPeriod, currentValues, afterMatching);
//	        
//	        HashMap<Integer, HashMap<Integer, Double>> currentValuesAfterMatching = this.congestionManagingAgent.getInternalDataModel().getCurrentValuesAfterMatching();
//
//	        writer.write("TradingPeriod;AgentName;CurrentValue;AfterMatching\n");
//	        for (Entry<Integer, Double> entry : currentValues.entrySet()) {
//	            Integer agentName = entry.getKey();
//	            Double current = entry.getValue();
//	            writer.write(String.format(Locale.GERMAN, "%d;%d;%.2f;", tradingPeriod, agentName, current));
//	            
//	            // Check for after matching values only if necessary.
//	            Double afterMatchingValue = currentValuesAfterMatching.getOrDefault(tradingPeriod, new HashMap<>()).get(agentName);
//	            if (afterMatchingValue != null) {
//	                writer.write(String.format(Locale.GERMAN, "%.2f", afterMatchingValue));
//	            }
//	            writer.write("\n");
//	        }
//	    } catch (IOException e) {
//	        // Consider logging this exception or re-throwing as a custom exception
//	        e.printStackTrace();
//	    }
//	}
	
}
