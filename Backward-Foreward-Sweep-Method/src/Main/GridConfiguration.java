package Main;

import java.util.HashMap;

public class GridConfiguration {
	
	int[][] matrix33 = new int[34][34];
	double[][] impedanceMatrix33 = new double [34][34];
	int[][] matrix5 = new int[6][6];
	HashMap<Integer, Double> energyValues; 
	int maxDefaultEnergyValue = 400;
	
	//default Initialization 
	public HashMap<Integer, Double> defaultInitializationEnergyValues(int size){
		for (int i = 0; i < size; i++) {
			energyValues.put(i, Math.random()*maxDefaultEnergyValue);
		}
		return energyValues;
	}
	
	public int[][] get33Matrix(){
		return matrix33;
	}
	
	public int[][] get5Matrix(){
		return matrix5;
	}
		
	public GridConfiguration() {
		HashMap<Integer, Double> energyValues = new HashMap<Integer, Double>();
		
		// Initiale 33x33 Matrix mit 0
        
        // Verbindungen gemäß dem bereitgestellten Bild
        int[][] connections33 = {
        	{0, 1},
        	{1, 2},
            {2, 3},
            {3, 4},
            {4, 5},
            {5, 6},
            {6, 7},
            {7, 8},
            {8, 9},
            {9, 10},
            {10, 11},
            {11, 12},
            {12, 13},
            {13, 14},
            {14, 15},
            {15, 16},
            {16, 17},
            {17, 18},
            {2, 19},
            {3, 23},
            {6, 26},
            {19, 20},
            {20, 21},
            {21, 22},
            {23, 24},
            {24, 25},
            {26, 27},
            {27, 28},
            {28, 29},
            {29, 30},
            {30, 31},
            {31, 32},
            {32, 33}
        };
        
        int[][] connections5 = {
        	 {0, 1},
        	 {1, 2},
             {2, 3},
             {3, 4},
             {4, 5}
        };
        
        // Werte aus: Kakueinejad et al. 2020, Optimal Planning for the Development of Power System in Respect to Distributed Generations Based on the Binary Dragonfly Algorithm
        impedanceMatrix33[0][1] = 0.047;
        impedanceMatrix33[1][2] = 0.2511;
        impedanceMatrix33[2][3] = 0.1864;
        impedanceMatrix33[3][4] = 0.1941;
        impedanceMatrix33[4][5] = 0.707;
        impedanceMatrix33[5][6] = 0.6188;
        impedanceMatrix33[6][7] = 0.2351;
        impedanceMatrix33[7][8] = 0.74;
        impedanceMatrix33[8][9] = 0.74;
        impedanceMatrix33[9][10] = 0.065;
        impedanceMatrix33[10][11] = 0.1238;
        impedanceMatrix33[11][12] = 1.155;
        impedanceMatrix33[12][13] = 0.7129;
        impedanceMatrix33[13][14] = 0.526;
        impedanceMatrix33[14][15] = 0.545;
        impedanceMatrix33[15][16] = 1.721;
        impedanceMatrix33[16][17] = 0.574;
        impedanceMatrix33[1][18] = 0.1565;
        impedanceMatrix33[18][19] = 1.3554;
        impedanceMatrix33[19][20] = 0.4784;
        impedanceMatrix33[20][21] = 0.9373;
        impedanceMatrix33[21][22] = 0.3083;
        impedanceMatrix33[22][23] = 0.7091;
        impedanceMatrix33[23][24] = 0.7011;
        impedanceMatrix33[24][25] = 0.1034;
        impedanceMatrix33[25][26] = 0.1447;
        impedanceMatrix33[26][27] = 0.9337;
        impedanceMatrix33[27][28] = 0.7006;
        impedanceMatrix33[28][29] = 0.2585;
        impedanceMatrix33[29][30] = 0.963;
        impedanceMatrix33[30][31] = 0.3619;
        impedanceMatrix33[31][32] = 0.5302;
        
        
        
        // Fülle die Matrix mit den Verbindungen
        for(int[] connection33 : connections33) {
            int start = connection33[0];
            int end = connection33[1];    
            matrix33[start][end] = 1;
        }
        
        for (int[] connection5 : connections5) {
            int start = connection5[0]; 
            int end = connection5[1];   
            matrix5[start][end] = 1;
        }
    }
}
