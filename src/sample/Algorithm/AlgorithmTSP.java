package sample.Algorithm;

import java.util.List;

public class AlgorithmTSP {

    private static int numberOfNodes;
    private static final int startPositionIndex = 0;
    private static int bestPathLength;
    private static int pathLengthTemp;
    private static int numberOfVisitedNodes;
    private static int numberOfVisitedNodesTemp;
    private static int weights[][];
    private static int bestSequence[];
    private static int actualSequence[];
    private static boolean wasVisited[];

    public static int[] runAlgorithm(List<Position> positions) {
        initializeVariables(positions);

        TSP(startPositionIndex);

        if (numberOfVisitedNodes != 0) {
            return bestSequence;
        } else {
            return new int[]{};
        }
    }

    private static void initializeVariables(List<Position> positions) {
        numberOfNodes = positions.size();
        bestPathLength = Integer.MAX_VALUE;
        pathLengthTemp = numberOfVisitedNodes = numberOfVisitedNodesTemp = 0;

        bestSequence = new int[numberOfNodes];
        actualSequence = new int[numberOfNodes];

        wasVisited = new boolean[numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            wasVisited[i] = false;
        }

        weights = new int[numberOfNodes][numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            for (int j = 0; j < numberOfNodes; j++) {
                weights[i][j] = weights[j][i] = positions.get(i).calculateDistance(positions.get(j));
            }
        }
    }

    private static int calculateNumberOfPossiblePaths(int numberOfNodes) {
        if (numberOfNodes == 0)
            return 1;
        else
            return (numberOfNodes * calculateNumberOfPossiblePaths(numberOfNodes - 1));
    }


    private static void TSP(int v) {
        actualSequence[numberOfVisitedNodesTemp] = v;
        numberOfVisitedNodesTemp++;

        if (numberOfVisitedNodesTemp < numberOfNodes) {
            wasVisited[v] = true;
            for (int u = 0; u < numberOfNodes; u++)
                if (!wasVisited[u]) {
                    pathLengthTemp += weights[v][u];
                    TSP(u);
                    pathLengthTemp -= weights[v][u];
                }
            wasVisited[v] = false;
        } else  {
            pathLengthTemp += weights[v][startPositionIndex];
            if (pathLengthTemp < bestPathLength) {
                bestPathLength = pathLengthTemp;
                for (int u = 0; u < numberOfVisitedNodesTemp; u++){
                    bestSequence[u] = actualSequence[u];
                }

                numberOfVisitedNodes = numberOfVisitedNodesTemp;
            }
            pathLengthTemp -= weights[v][startPositionIndex];
        }
        numberOfVisitedNodesTemp--;
    }

}
