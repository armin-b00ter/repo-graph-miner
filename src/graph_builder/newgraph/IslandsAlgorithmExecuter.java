package graph_builder.newgraph;

import graph_builder.newgraph.filter.FiltersApplier;
import graph_builder.newgraph.filter.FirstDecilesRemoveFilter;
import graph_builder.newgraph.filter.MinNodeFilter;
import graph_builder.newgraph.filter.MinWeightsRemoveFilter;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Mohammad on 1/8/2015.
 */
public class IslandsAlgorithmExecuter {

    public static final String MIN_NODES = "minNodes";
    public static final String FIRST_DECILES_NUM = "firstDecilesNum";
    public static final String MIN_WEIGHTS_NUM = "minWeightsNum";

    public HashMap<String, String> getParams() {
        return params;
    }

    private HashMap<String, String> params = new HashMap<String, String>();

    String outputDir;
    String inputFile;

    public IslandsAlgorithmExecuter(String outputDir, String inputDir) {
        this.outputDir = outputDir;
        this.inputFile = inputDir;
        params.put(MIN_NODES, "2");
//        params.put(FIRST_DECILES_NUM, "1.0");
        params.put(MIN_WEIGHTS_NUM, "1");

    }

    public void execute() throws IOException {
        String outputFile = outputDir + "/out";

        for (String key : params.keySet()) {
            outputFile += "_" + key + "-" + params.get(key);
        }
        NewGraph newGraph = new NewGraph(inputFile);
//        FirstDecilesRemoveFilter firstDecileRemoveFilter = new FirstDecilesRemoveFilter(Double.valueOf(params.get(FIRST_DECILES_NUM)));
        MinWeightsRemoveFilter minWeightsRemoveFilter = new MinWeightsRemoveFilter(Integer.valueOf(params.get(MIN_WEIGHTS_NUM)));
        MinNodeFilter minNodeFilter = new MinNodeFilter(Integer.valueOf(params.get(MIN_NODES)));
//        FiltersApplier filtersApplier = new FiltersApplier(firstDecileRemoveFilter, minNodeFilter);
        FiltersApplier filtersApplier = new FiltersApplier(minWeightsRemoveFilter, minNodeFilter);
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
