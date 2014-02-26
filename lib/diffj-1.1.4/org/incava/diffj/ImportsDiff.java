package org.incava.diffj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;


public class ImportsDiff extends DiffComparator
{
    public static final String IMPORT_REMOVED = "import removed: {0}";

    public static final String IMPORT_ADDED = "import added: {0}";

    public static final String IMPORT_SECTION_REMOVED = "import section removed";

    public static final String IMPORT_SECTION_ADDED = "import section added";

    public ImportsDiff(Collection differences)
    {
        super(differences);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b)
    {
        ASTImportDeclaration[] aImports = CompilationUnitUtil.getImports(a);
        ASTImportDeclaration[] bImports = CompilationUnitUtil.getImports(b);

        if (aImports.length == 0) {
            if (bImports.length == 0) {
                // tr.Ace.log("neither has imports section");
            }
            else {
                Token a0 = getFirstTypeToken(a);
                Token a1 = a0;
                Token b0 = getFirstToken(bImports);
                Token b1 = getLastToken(bImports);
                addAdded(IMPORT_SECTION_ADDED, null, a0, a1, b0, b1);
            }
        }
        else if (bImports.length == 0) {
            Token a0 = getFirstToken(aImports);
            Token a1 = getLastToken(aImports);
            Token b0 = getFirstTypeToken(b);
            Token b1 = b0;
            addDeleted(IMPORT_SECTION_REMOVED, null, a0, a1, b0, b1);
        }
        else {
            Map aNamesToImp = makeImportMap(aImports);
            Map bNamesToImp = makeImportMap(bImports);
            
            Collection names = new TreeSet();
            names.addAll(aNamesToImp.keySet());
            names.addAll(bNamesToImp.keySet());

            Iterator nit = names.iterator();
            while (nit.hasNext()) {
                String               name = (String)nit.next();
                ASTImportDeclaration aimp = (ASTImportDeclaration)aNamesToImp.get(name);
                ASTImportDeclaration bimp = (ASTImportDeclaration)bNamesToImp.get(name);
            
                if (aimp == null) {
                    addAdded(IMPORT_ADDED, new Object[] { name }, aImports[0], bimp);
                }
                else if (bimp == null) {
                    addDeleted(IMPORT_REMOVED, new Object[] { name }, aimp, bImports[0]);
                }
                else {
                    // tr.Ace.log("no change");
                }
            }
        }
    }

    protected Map makeImportMap(ASTImportDeclaration[] imports)
    {
        Map namesToImp = new HashMap();

        for (int ii = 0; ii < imports.length; ++ii) {
            ASTImportDeclaration imp = imports[ii];
            StringBuffer         buf = new StringBuffer();   
            Token                tk  = imp.getFirstToken().next;
            
            while (tk != null) {
                if (tk == imp.getLastToken()) {
                    break;
                }
                else {
                    buf.append(tk.image);
                    tk = tk.next;
                }
            }

            namesToImp.put(buf.toString(), imp);
        }
        
        return namesToImp;
    }

    protected Token getFirstToken(ASTImportDeclaration[] imports)
    {
        return imports[0].getFirstToken();
    }

    protected Token getLastToken(ASTImportDeclaration[] imports)
    {
        return imports[imports.length - 1].getLastToken();
    }

    protected Token getFirstTypeToken(ASTCompilationUnit cu)
    {
        ASTTypeDeclaration[] types = CompilationUnitUtil.getTypeDeclarations(cu);
        Token t = types.length > 0 ? types[0].getFirstToken() : null;

        // if there are no types (ie. the file has only a package and/or import
        // statements), then just point to the first token in the compilation
        // unit.

        if (t == null) {
            t = cu.getFirstToken();
        }
        return t;
    }
}
