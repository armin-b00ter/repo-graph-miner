package graph_builder.newgraph.filter;

import graph_builder.newgraph.Island;
import graph_builder.newgraph.NewGraph;

/**
 * Created by Mohammad on 1/5/2015.
 */
public class MinNodeFilter implements NewGraphFilter {

    private int minNodesSize = Integer.MAX_VALUE;

    public MinNodeFilter(int minNodesSize) {
        if (minNodesSize <= 0) {
            throw new IllegalArgumentException("min nodes size must be greater than zero");
        }

        this.minNodesSize = minNodesSize;
    }

    @Override
    public NewGraph execute(NewGraph graph) {
        for (Island island : graph.getIslands()) {
            if (island.getNodes().size() < minNodesSize) {
                graph.removeNodes(island.getNodes());
            }
        }
        return graph;
    }
}
