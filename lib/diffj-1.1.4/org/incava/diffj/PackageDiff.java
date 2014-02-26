package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;


public class PackageDiff extends DiffComparator
{
    public static final String PACKAGE_REMOVED = "package removed: {0}";

    public static final String PACKAGE_ADDED = "package added: {0}";

    public static final String PACKAGE_RENAMED = "package renamed from {0} to {1}";

    public PackageDiff(Collection differences)
    {
        super(differences);        
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b)
    {
        ASTPackageDeclaration aPkg = CompilationUnitUtil.getPackage(a);
        ASTPackageDeclaration bPkg = CompilationUnitUtil.getPackage(b);

        if (aPkg == null) {
            if (bPkg == null) {
                tr.Ace.log("neither has packages");
            }
            else {
                ASTName    name = (ASTName)SimpleNodeUtil.findChild(bPkg, ASTName.class);
                SimpleNode aPos = SimpleNodeUtil.findChild(a, null);
                tr.Ace.log("name: " + name + "; aPos: " + aPos);
                if (aPos == null) {
                    aPos = a;
                }
                addAdded(PACKAGE_ADDED, aPos, name);
            }
        }
        else if (bPkg == null) {
            ASTName    name = (ASTName)SimpleNodeUtil.findChild(aPkg, ASTName.class);
            SimpleNode bPos = SimpleNodeUtil.findChild(b, null);
            tr.Ace.log("name: " + name + "; bPos: " + bPos);
            if (bPos == null) {
                bPos = b;
            }
            addDeleted(PACKAGE_REMOVED, name, bPos);
        }
        else {
            ASTName aName = (ASTName)SimpleNodeUtil.findChild(aPkg, ASTName.class);
            String  aStr  = SimpleNodeUtil.toString(aName);
            ASTName bName = (ASTName)SimpleNodeUtil.findChild(bPkg, ASTName.class);
            String  bStr  = SimpleNodeUtil.toString(bName);

            tr.Ace.log("aName: " + aName + "; bName: " + bName);
            tr.Ace.log("aStr: " + aStr + "; bStr: " + bStr);
            
            if (aStr.equals(bStr)) {
                tr.Ace.log("no change");
            }
            else {
                addChanged(PACKAGE_RENAMED, aName, bName);
            }
        }
    }
    
}
