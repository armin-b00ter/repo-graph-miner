package graph_builder.newgraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohammad on 1/4/2015.
 */
public class Edge implements Comparable<Edge> {
    private Node firstNode;
    private Node secondNode;
    private Integer weight;

    public Edge(Node firstNode, Node secondNode, Integer weight) {
        if (firstNode == null || secondNode == null || weight == null || weight == 0.0) {
            throw new IllegalArgumentException("bad parameter for creating Edge");
        }
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.weight = weight;
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(firstNode);
        nodes.add(secondNode);
        return nodes;
    }

    @Override
    public int hashCode() {
        return (firstNode.getId() + "\t" + secondNode.getId()).hashCode();
    }

    @Override
    public String toString() {
        return firstNode.getId() + "\t" + secondNode.getId() + "\t" + weight;
    }

    @Override
    public int compareTo(Edge other) {
        if (this.weight < other.weight) {
            return -1;
        } else if (this.weight == other.weight) {
            return 0;
        } else {
            return 1;
        }
    }
}
