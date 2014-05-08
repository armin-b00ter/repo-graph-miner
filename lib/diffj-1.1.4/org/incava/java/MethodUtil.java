package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for method declarations. The instance method contains
 * a cache for method lookup.
 */
public class MethodUtil extends FunctionUtil
{
    private Map _methodCriterias;
    
    public MethodUtil()
    {
        _methodCriterias = new HashMap();
    }

    public static ASTMethodDeclarator getDeclarator(ASTMethodDeclaration method)
    {
        return (ASTMethodDeclarator)SimpleNodeUtil.findChild(method, ASTMethodDeclarator.class);
    }

    public static Token getName(ASTMethodDeclaration method)
    {
        ASTMethodDeclarator decl = getDeclarator(method);
        return decl.getFirstToken();
    }

    public static ASTFormalParameters getParameters(ASTMethodDeclaration method)
    {
        ASTMethodDeclarator decl = getDeclarator(method);
        return (ASTFormalParameters)SimpleNodeUtil.findChild(decl, ASTFormalParameters.class);
    }

    public static String getFullName(ASTMethodDeclaration method)
    {
        Token nameTk = getName(method);
        ASTFormalParameters params = getParameters(method);
        String fullName = toFullName(nameTk, params);
        return fullName;
    }

    public static double getMatchScore(ASTMethodDeclaration a, ASTMethodDeclaration b) 
    {
        MethodMatchCriteria aCriteria = new MethodMatchCriteria(a);
        MethodMatchCriteria bCriteria = new MethodMatchCriteria(b);
        
        return aCriteria.compare(bCriteria);
    }

    /**
     * This is the same as the static method, but it uses the cache.
     */
    public double fetchMatchScore(ASTMethodDeclaration a, ASTMethodDeclaration b) 
    {
        // caching the criteria (instead of extracting it every time) is around 25% faster.
        MethodMatchCriteria aCriteria = getCriteria(a);
        MethodMatchCriteria bCriteria = getCriteria(b);

        return aCriteria.compare(bCriteria);
    }

    protected MethodMatchCriteria getCriteria(ASTMethodDeclaration method)
    {
        MethodMatchCriteria crit = (MethodMatchCriteria)_methodCriterias.get(method);
        if (crit == null) {
            crit = new MethodMatchCriteria(method);
            _methodCriterias.put(method, crit);
        }
        return crit;
    }

}
