package evaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class F1Calculator {
	ArrayList<Set<String>> ccc;
	
	private int setIntersectionCount(Set<String> set1, Set<String> set2)
	{
		int intersectionCount=0;
		for(String s:set1)
			if(set2.contains(s))
				intersectionCount++;
		
		return intersectionCount;		
	}
	
	private int calculatePrecision(Set<String> exactSet, Set<String> testSet)
	{
        return setIntersectionCount(exactSet, testSet)/testSet.size();		
	}
	
	private int calculateRecall(Set<String> exactSet, Set<String> testSet)
	{
		return setIntersectionCount(exactSet, testSet)/exactSet.size();
	}
	
	public F1ReturnType calculateF1(Set<String> testSet)
	{
		int max = 0;
        int maxPrecision = 0;
        int maxRecall = 0;
        Set<String> maxSet = new HashSet<String>();
        for(Set<String> set: ccc)
        {
            int precision = calculatePrecision(set, testSet);
            int recall = calculateRecall(set, testSet);
            if(precision+recall==0)
                continue;
            int f1 = (2*recall*precision)/(recall+precision);
            if(f1>max)
            {
                max = f1;
                maxSet = set;
                maxPrecision = precision;
                maxRecall = recall;
            }
        }
        return new F1ReturnType(max, maxPrecision, maxRecall, maxSet);
	}
}

class F1ReturnType
{
	int maxF1;
	int maxPrecision;
	int maxRecall;
	Set<String> maxF1Set;
	
	public F1ReturnType(int maxF1, int maxPrecision, int maxRecall, Set<String> maxF1Set)
	{
		this.maxF1 = maxF1;
		this.maxPrecision = maxPrecision;
		this.maxRecall = maxRecall;
		this.maxF1Set = maxF1Set;
	}
}
