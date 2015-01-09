package graph_builder.newgraph.filter;

import graph_builder.newgraph.NewGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Mohammad on 1/9/2015.
 */
public class MinWeightsRemoveFilter implements NewGraphFilter {
    private int minWeightsToRemove = 1;

    public MinWeightsRemoveFilter(int minWeightsToRemove) {
        this.minWeightsToRemove = minWeightsToRemove;
    }

    @Override
    public NewGraph execute(NewGraph graph) {
        Set<Integer> weightsSet = graph.getEdgesWeights();
        List<Integer> weights = new ArrayList<Integer>(weightsSet);
        Collections.sort(weights);
        List<Integer> weightsToRemove = weights.subList(0, minWeightsToRemove);
        graph.removeWeights(weightsToRemove);
        return graph;
    }

}
