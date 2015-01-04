package graph_builder.newgraph;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mohammad on 1/4/2015.
 */
public class Island {

    private Set<Node> nodes = new HashSet<Node>();

    public void addNode(Node node) {
        if (node == null) {
            throw new NullPointerException("input node is null.");
        }
        if (nodes.contains(node)) {
            throw new IllegalArgumentException("island already contains node.");
        }
        nodes.add(node);
    }

    @Override
    public String toString() {
        return nodes.toString();
    }

    public Set<Node> getNodes() {
        return nodes;
    }
}
