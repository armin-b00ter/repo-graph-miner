package org.incava.diffj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;


public class TypesDiff extends ItemDiff
{
    public static final String TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface";

    public static final String TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class";

    public static final String TYPE_DECLARATION_ADDED = "type declaration added: {0}";

    public static final String TYPE_DECLARATION_REMOVED = "type declaration removed: {0}";

    public TypesDiff(Collection differences)
    {
        super(differences);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b)
    {
        ASTTypeDeclaration[] aTypes = CompilationUnitUtil.getTypeDeclarations(a);
        ASTTypeDeclaration[] bTypes = CompilationUnitUtil.getTypeDeclarations(b);
        
        tr.Ace.log("aTypes", aTypes);
        tr.Ace.log("bTypes", bTypes);

        Map aNamesToTD = makeTDMap(aTypes);
        Map bNamesToTD = makeTDMap(bTypes);

        tr.Ace.log("aNamesToTD", aNamesToTD);
        tr.Ace.log("bNamesToTD", bNamesToTD);

        Collection names = new TreeSet();
        names.addAll(aNamesToTD.keySet());
        names.addAll(bNamesToTD.keySet());

        Iterator nit = names.iterator();
        while (nit.hasNext()) {
            String             name = (String)nit.next();
            ASTTypeDeclaration atd  = (ASTTypeDeclaration)aNamesToTD.get(name);
            ASTTypeDeclaration btd  = (ASTTypeDeclaration)bNamesToTD.get(name);

            tr.Ace.log("name", name);
            tr.Ace.log("atd", atd);
            tr.Ace.log("btd", btd);

            /*if (atd == null) {
                Token bName = TypeDeclarationUtil.getName(btd);
                addAdded(TYPE_DECLARATION_ADDED, new Object[] { bName.image }, a, btd);
            }
            else if (btd == null) {
                Token aName = TypeDeclarationUtil.getName(atd);
                addDeleted(TYPE_DECLARATION_REMOVED, new Object[] { aName.image }, atd, b);
            }*/
            //else {
            	PackageDiff pd = new PackageDiff(get());
            	pd.compare(a, b);
                TypeDiff differ = new TypeDiff(get());
                differ.compare(atd, btd);
            //}
        }
    }

    protected Map makeTDMap(ASTTypeDeclaration[] types)
    {
        Map namesToTD = new HashMap();
        for (int i = 0; i < types.length; ++i) {
            ASTTypeDeclaration type = types[i];
            Token              tk   = TypeDeclarationUtil.getName(type);
            if (tk != null) {
                namesToTD.put(tk.image, type);
            }
        }

        return namesToTD;
    }

}
