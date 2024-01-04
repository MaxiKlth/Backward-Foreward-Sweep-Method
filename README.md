# CalculateCurrentVoltageValues

## Introduction
`CalculateCurrentVoltageValues` is a Java class designed for simulation and analysis of electrical grids. It calculates the power flow through nodes and identifies potential grid congestion by evaluating current and voltage values using backward/forward sweep methodologies.

## Features
- Supports both 5-bus and 33-bus grid systems.
- Performs power flow calculations to evaluate the grid's performance.
- Detects grid congestion by comparing current flow against predefined thresholds.
- Adjusts voltage values to account for line impedances and load demands.
- Input: dynamic: `energyValues`; static: `gridConfiguration` (in the class `GridConfiguration`)
- OutPut: `currentValues`; `voltageValues`; a boolean `congestionExists` indicating if a current-related grid congestion occured

## Getting Started
To utilize this class in your grid analysis:

1. Instantiate the `CalculateCurrentVoltageValues` class in your Java project.
2. Configure your grid parameters (e.g., nominal voltage, max current, node count).
3. Call the `identifyGridCongestion` method to start the analysis process.
4. In default mode the `energyValues`are randomly assigned - send the corrent `energyValues`accordingly.
5. The `currentValues`are at the moment not written into a file or the console. If you want to so, please implement it.
6. If based on that the `energyValues` are recalculated (e.g. by using ADMM), the code provides the possible to calculate again the `afterMatching`

## Usage
The main functionalities of the class are:

- **Grid Congestion Identification**: `public float identifyGridCongestion()`
  Begins the analysis and returns a float indicating the level of congestion.

- **Initialization of Voltage Values**: `private HashMap<Integer, Double> initializeVoltageValues(int length, double voltage)`
  Sets initial voltage values for all nodes.

- **Node Type Identification**:
  - `private List<Integer> identifyLineEndNodes(int[][] matrix)`
  - `private List<Integer> identifyLineStartNodes(int[][] matrix)`
  - `private List<Integer> identifyLineSideNodes(int[][] matrix)`

- **Initialization of Current Values**: `private HashMap<Integer, Double> initializeCurrentValues(int length)`
  Initializes current values to zero for all nodes.

- **Energy Value Retrieval**: `private HashMap<Integer, Double> getEnergyValues(GridConfiguration gridMatrix)`
  Retrieves the energy values for each node, adjusting post-matching as necessary.

- **Current and Voltage Calculation**:
  - `private void calculateCurrentValues(...)`
  - `private void calculateVoltages(...)`

- **Voltage Drop Calculation**: `private double calculateVoltageDrop(double current, int[][] matrix, double[][] impedanceMatrix, int startNode, int endNode)`
  Calculates the voltage drop across a line segment between two nodes.

## Configuration
The class properties like `nominalVoltage`, `maxCurrent`, and `amountNodes` can be configured to match the specifics of your grid.

## Dependencies
This class relies on a `GridConfiguration` object for grid matrices and initial energy values.

## Contribution
Feel free to fork the repository, make your changes, and submit a pull request. We appreciate your contributions to improve grid simulation and analysis.

## License
Distributed under the MIT License. See `LICENSE` for more information.

## Contact
For support, please open an issue in the GitHub issue tracker.

## Acknowledgements
- Special thanks to the contributors and maintainers of this project.
- This README follows best practices from [Make a README](https://www.makeareadme.com/) and [GitHub](https://docs.github.com/en).
