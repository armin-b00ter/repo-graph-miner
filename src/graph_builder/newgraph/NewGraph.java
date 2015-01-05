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
    private Set<Edge> edges = new HashSet<Edge>();

    public NewGraph(String intputDir) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(intputDir));
        String line = null;
        nodes = new HashMap<String, Node>();
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

            Edge edge = new Edge(firstNode, secondNode, Integer.valueOf(splittedLine[2]));
            edges.add(edge);

            firstNode.addEdge(edge);
            secondNode.addEdge(edge);
        }
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

    public static void main(String[] args) throws IOException {
        NewGraph newGraph = new NewGraph("testResources/testGraph.txt");
        System.out.println(newGraph);
        System.out.println(newGraph.getIslands());
        for (Island island : newGraph.getIslands()) {
            System.out.println("island: " + island.getNodes());
            for (Node node : island.getNodes()) {
                List<Edge> sortedEdges = new ArrayList<Edge>(node.getEdges());
                Collections.sort(sortedEdges);
                System.out.println("node " + node.getId() + " " + sortedEdges);
            }

        }

    }

    public Set<Island> getIslands() {
        Set<Island> islands = new HashSet<Island>();

        List<Node> notVisitedNodes = new ArrayList<Node>(nodes.values());

        while (notVisitedNodes.size() != 0) {
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
}
