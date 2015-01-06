package graph_builder.newgraph.filter;

import graph_builder.newgraph.NewGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohammad on 1/6/2015.
 */
public class FiltersApplier implements NewGraphFilter {

    private List<NewGraphFilter> newGraphFilters;

    public FiltersApplier(NewGraphFilter... filters) {
        newGraphFilters = new ArrayList<NewGraphFilter>();
        for (NewGraphFilter filter : filters) {
            if (filter != null) {
                newGraphFilters.add(filter);
            } else {
                System.out.println("filter is null");
            }
        }

    }


    @Override
    public NewGraph execute(NewGraph graph) {
        for (NewGraphFilter newGraphFilter : newGraphFilters) {
            graph = newGraphFilter.execute(graph);
        }
        return graph;
    }
}
