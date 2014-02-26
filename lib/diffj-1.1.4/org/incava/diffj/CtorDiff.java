package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;

import org.incava.analysis.Report;
import org.incava.java.*;


public class CtorDiff extends FunctionDiff
{
    public CtorDiff(Report report)
    {
        super(report);
    }

    public CtorDiff(Collection differences)
    {
        super(differences);
    }

    public void compare(ASTConstructorDeclaration a, ASTConstructorDeclaration b)
    {
        tr.Ace.log("a: " + a + "; b: " + b);
        
        compareParameters(a, b);
        compareThrows(a, b);
        compareBodies(a, b);
    }

    protected void compareParameters(ASTConstructorDeclaration a, ASTConstructorDeclaration b)
    {
        ASTFormalParameters afp = CtorUtil.getParameters(a);
        ASTFormalParameters bfp = CtorUtil.getParameters(b);
        
        String packageAndClass = TypeDiff.getClassPrefix(afp.getParentsOfType(ASTClassOrInterfaceDeclaration.class)); 
        String ctorName = packageAndClass + CtorUtil.getFullName(a);
        String newCtorName = packageAndClass + CtorUtil.getFullName(b);

        compareParameters(afp, bfp, ctorName, newCtorName);
    }

    protected void compareThrows(ASTConstructorDeclaration a, ASTConstructorDeclaration b)
    {
        ASTNameList at = CtorUtil.getThrowsList(a);
        ASTNameList bt = CtorUtil.getThrowsList(b);

        String packageAndClass = TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class)); 
        String ctorName = packageAndClass + CtorUtil.getFullName(a);
        
        compareThrows(a, at, b, bt, ctorName);
    }

    protected List getCodeSerially(ASTConstructorDeclaration ctor)
    {
        // removes all tokens up to the first left brace. This is because ctors
        // don't have their own blocks, unlike methods.
        
        List children = SimpleNodeUtil.getChildrenSerially(ctor);
        
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Token && ((Token)obj).kind == JavaParserConstants.LBRACE) {
                // tr.Ace.log("breaking at: " + obj);
                break;
            }
            else {
                // tr.Ace.log("deleting: " + obj);
                it.remove();
            }
        }

        return children;
    }

    protected void compareBodies(ASTConstructorDeclaration a, ASTConstructorDeclaration b)
    {
        List aCode = getCodeSerially(a);
        List bCode = getCodeSerially(b);
        
        String aName = TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + CtorUtil.getFullName(a);
        String bName = CtorUtil.getFullName(b);
        
        compareCode(aName, aCode, bName, bCode);
    }

}
