package main.utils;

public class Utility {


    /**
     * Implementation of Dijkstra algorithm.
     *
     * @param source Source node.
     * @param matrix Matrix of nodes.
     * @return Shortest distance to each node, from source.
     */
    public static int[] calculateDistances(int source, int[][] matrix) {
        int numOfNodes = matrix.length;
        int[] distances = new int[numOfNodes];
        boolean[] isVisited = new boolean[numOfNodes];

        for (int i = 0; i < numOfNodes; i++) {
            distances[i] = Integer.MAX_VALUE;
            isVisited[i] = false;
        }

        distances[source] = 0;

        for (int i = 0; i < numOfNodes; i++) {
            int nearestNodeIndex = minDistance(distances, isVisited);

            isVisited[nearestNodeIndex] = true;

            for (int j = 0; j < numOfNodes; j++) {
                if (!isVisited[j] &&
                        matrix[nearestNodeIndex][j] != 0 &&
                        distances[nearestNodeIndex] != Integer.MAX_VALUE &&
                        distances[nearestNodeIndex] + matrix[nearestNodeIndex][j] < distances[j]) {
                    distances[j] = distances[nearestNodeIndex] + matrix[nearestNodeIndex][j];
                }
            }
        }
        return distances;
    }

    private static int minDistance(int[] distance, boolean[] visited) {

        int minValue = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < distance.length; i++)
            if (visited[i] == false && distance[i] <= minValue) {
                minValue = distance[i];
                minIndex = i;
            }

        return minIndex;
    }

}
