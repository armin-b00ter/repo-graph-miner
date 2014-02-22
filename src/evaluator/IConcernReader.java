package evaluator;

import java.util.ArrayList;
import java.util.Set;

public interface IConcernReader {
	ArrayList<Set<String>> getConcernList(boolean getMethod);
}
