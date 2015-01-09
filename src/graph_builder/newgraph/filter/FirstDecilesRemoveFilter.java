package graph_builder.newgraph.filter;

import graph_builder.newgraph.NewGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Mohammad on 1/5/2015.
 */
public class FirstDecilesRemoveFilter implements NewGraphFilter {

    private double decilesNum = 1.0;

    public FirstDecilesRemoveFilter(double decilesNum) {
        this.decilesNum = decilesNum;
    }

    public FirstDecilesRemoveFilter() {
    }

    @Override
    public NewGraph execute(NewGraph graph) {
        Set<Integer> weightsSet = graph.getEdgesWeights();
        List<Integer> weights = new ArrayList<Integer>(weightsSet);
        Collections.sort(weights);
        Double firstDecileFloat = (Double.valueOf(weights.size())) / 10.0;
        int firstDecileSize = (int) Math.floor(firstDecileFloat * decilesNum);
        List<Integer> firstDecile = weights.subList(0, firstDecileSize);
        graph.removeWeights(firstDecile);
        return graph;
    }
}
