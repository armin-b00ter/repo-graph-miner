package graph_builder.newgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mohammad on 1/4/2015.
 */
public class NewGraph {
    private Map<String, Node> nodes = new HashMap<String, Node>();
    private Map<Integer, Set<Edge>> edges = new HashMap<Integer, Set<Edge>>();

    public NewGraph(String inputDir) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputDir));
        nodes = new HashMap<String, Node>();
        buildGraph(bufferedReader);
        bufferedReader.close();
    }

    private void buildGraph(BufferedReader bufferedReader) throws IOException {
        String line;
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splittedLine = line.split("\\t");
            if (splittedLine.length != 3) {
                System.err.println("error in reading line: " + line);
                continue;
            }
            String firstNodeStr = splittedLine[0];
            String secondNodeStr = splittedLine[1];

            Node firstNode = getOrCreateNode(firstNodeStr);
            Node secondNode = getOrCreateNode(secondNodeStr);
            if (!firstNode.equals(secondNode)) {
                Integer weight = Integer.valueOf(splittedLine[2]);
                Edge edge = new Edge(firstNode, secondNode, weight);

                addEdgeToEdges(this.edges, weight, edge);

                firstNode.addEdge(edge);
                secondNode.addEdge(edge);
            } else {
                System.out.println("line " + lineNumber + " is corrupted: " + line);
            }
            lineNumber++;
        }
    }

    private void addEdgeToEdges(Map<Integer, Set<Edge>> edges, Integer weight, Edge edge) {
        Set<Edge> weightEdges = edges.get(weight);
        if (weightEdges == null) {
            weightEdges = new HashSet<Edge>();
            edges.put(weight, weightEdges);
        }
        weightEdges.add(edge);
    }

    private Node getOrCreateNode(String nodeStr) {
        Node node = nodes.get(nodeStr);
        if (node == null) {
            node = new Node(nodeStr);
            nodes.put(nodeStr, node);
        }
        return node;
    }

    @Override
    public String toString() {
        return nodes.values() + "\n" + edges;
    }

    public Set<Island> getIslands() {
        Set<Island> islands = new HashSet<Island>();

        List<Node> notVisitedNodes = new ArrayList<Node>(nodes.values());

        while (!notVisitedNodes.isEmpty()) {
            Stack<Node> nodeStack = new Stack<Node>();
            Node startNode = notVisitedNodes.remove(0);
            nodeStack.push(startNode);
            Island island = new Island();
            island.addNode(startNode);
            while (!nodeStack.isEmpty()) {
                Node poppedNode = nodeStack.pop();
                for (Node node : poppedNode.getNeighborNodes()) {
                    if (notVisitedNodes.contains(node)) {
                        notVisitedNodes.remove(node);
                        nodeStack.push(node);
                        island.addNode(node);
                    }
                }
            }
            islands.add(island);
        }

        return islands;
    }

    public void removeNodes(Collection<Node> nodesToRemove) {
        Map<Integer, Set<Edge>> edgesToRemove = new HashMap<Integer, Set<Edge>>();
        Set<String> nodesIdsToRemove = new HashSet<String>();
        for (Node node : nodesToRemove) {
            nodesIdsToRemove.add(node.getId());
            node.removeNodeFromNeighbors();
            for (Edge edge : node.getEdges()) {
                addEdgeToEdges(edgesToRemove, edge.getWeight(), edge);
            }
        }
        for (String nodeId : nodesIdsToRemove) {
            nodes.remove(nodeId);
        }

        for (Map.Entry<Integer, Set<Edge>> integerSetEntry : edgesToRemove.entrySet()) {
            edges.get(integerSetEntry.getKey()).removeAll(integerSetEntry.getValue());
        }

    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Set<Integer> getEdgesWeights() {
        return edges.keySet();
    }

    public void removeWeights(Collection<Integer> firstDecile) {
        for (Integer weight : firstDecile) {
            for (Edge edge : edges.get(weight)) {
                for (Node node : edge.getNodes()) {
                    node.removeEdge(edge);
                }

            }
            edges.remove(weight);
        }
    }

}