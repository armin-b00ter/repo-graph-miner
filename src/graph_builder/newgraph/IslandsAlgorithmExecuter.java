package graph_builder.newgraph;

import graph_builder.newgraph.filter.FiltersApplier;
import graph_builder.newgraph.filter.FirstDecileRemoveFilter;
import graph_builder.newgraph.filter.MinNodeFilter;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Mohammad on 1/8/2015.
 */
public class IslandsAlgorithmExecuter {

    public static final String MIN_NODES = "minNodes";

    public HashMap<String, String> getParams() {
        return params;
    }

    private HashMap<String, String> params = new HashMap<String, String>();

    String outputDir;
    String inputFile;

    public IslandsAlgorithmExecuter(String outputDir, String inputDir) {
        this.outputDir = outputDir;
        this.inputFile = inputDir;
        params.put(MIN_NODES, "3");
    }

    public void execute() throws IOException {
        String outputFile = outputDir + "/out";

        for (String key : params.keySet()) {
            outputFile += "_" + key + "-" + params.get(key);
        }
        NewGraph newGraph = new NewGraph(inputFile);
        FirstDecileRemoveFilter firstDecileRemoveFilter = new FirstDecileRemoveFilter();
        MinNodeFilter minNodeFilter = new MinNodeFilter(Integer.valueOf(params.get(MIN_NODES)));
        FiltersApplier filtersApplier = new FiltersApplier(firstDecileRemoveFilter, minNodeFilter);
        newGraph = filtersApplier.execute(newGraph);
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(outputFile));
        for (Island island : newGraph.getIslands()) {
            String cccs = "";
            for (Node node : island.getNodes()) {
                cccs += node.getId() + " ";
            }
            cccs = cccs.substring(0, cccs.length() - 1) + "\n";
            dataOutputStream.writeBytes(cccs);
        }
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    public String getParamValues() {
        String ret = "";
        for (String key : params.keySet())
            ret += key + "\t:\t" + params.get(key) + "\n";

        return ret;
    }

}
