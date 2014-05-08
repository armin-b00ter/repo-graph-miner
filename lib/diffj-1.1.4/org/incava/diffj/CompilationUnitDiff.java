package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.Report;
import org.incava.util.TimedEvent;


public class CompilationUnitDiff extends DiffComparator
{
    private Report _report;
    
    private boolean _flush;
    
    public CompilationUnitDiff(Report report, boolean flush)
    {
        super(report);

        _report = report;
        _flush = flush;
    }

    public CompilationUnitDiff(Collection diffs)
    {
        super(diffs);
    }

    public CompilationUnitDiff()
    {
    }

    @SuppressWarnings("unchecked")
	public void compare(ASTCompilationUnit a, ASTCompilationUnit b)
    {
        Collection diffs = get();
        tr.Ace.log("diffs: " + diffs);
        
        if (a != null && b != null) {
            /*PackageDiff pd = new PackageDiff(diffs);
            pd.compare(a, b);
            
            if (_flush && _report != null) {
                _report.flush();
            }
            
            ImportsDiff id = new ImportsDiff(diffs);
            id.compare(a, b);
            
            if (_flush && _report != null) {
                _report.flush();
            }*/

            // TimedEvent typetime = new TimedEvent("type");
            TypesDiff td = new TypesDiff(diffs);
            td.compare(a, b);
            // typetime.close();
        }
    }
}
