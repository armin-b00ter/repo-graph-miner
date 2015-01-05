package graph_builder.newgraph;

import java.util.*;

/**
 * Created by Mohammad on 1/4/2015.
 */
public class Node {
    private String id;
    private Map<Node, Edge> nodeEdgeMap = new HashMap<Node, Edge>();

    public Node(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("input id is null or empty.");
        }
        this.id = id;
    }

    public void addEdge(Edge edge) {
        if (edge == null) {
            throw new NullPointerException("edge is null.");
        }
        List<Node> nodes = edge.getNodes();
        if (!nodes.contains(this)) {
            throw new IllegalArgumentException("node is not one of edge nodes.");
        }
        List<Node> tempNodes = new ArrayList<Node>(nodes);
        tempNodes.remove(this);
        Node secondNode = tempNodes.get(0);
        nodeEdgeMap.put(secondNode, edge);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    public String getId() {
        return id;
    }

    public Set<Node> getNeighborNodes() {
        return nodeEdgeMap.keySet();
    }

    public Collection<Edge> getEdges() {
        Collection<Edge> edges = nodeEdgeMap.values();
        return edges;
    }
}