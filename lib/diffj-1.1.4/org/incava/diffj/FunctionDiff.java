package org.incava.diffj;

import java.awt.Point;
import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;

import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.util.*;
import org.incava.util.diff.*;


public class FunctionDiff extends ItemDiff
{
    public static final String RETURN_TYPE_CHANGED = "return type changed from {0} to {1}: {2}";

    public static final String PARAMETER_REMOVED = "parameter {0} removed: {1} > {2}";

    public static final String PARAMETER_ADDED = "parameter {0} added: {1} > {2}";

    public static final String PARAMETER_REORDERED = "parameter {0} reordered from argument {1} to {2}: {3} > {4}";

    public static final String PARAMETER_TYPE_CHANGED = "parameter type changed from {0} to {1}: {2} > {3}";
    
    public static final String PARAMETER_NAME_CHANGED = "parameter name changed from {0} to {1}: {2} > {3}";

    public static final String PARAMETER_REORDERED_AND_RENAMED = "parameter {0} reordered from argument {1} to {2} and renamed {3}: {4} > {5}";

    public static final String THROWS_REMOVED = "throws {0} removed: {1}";

    public static final String THROWS_ADDED = "throws {0} added: {1}";

    public static final String THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}: {3}";

    public FunctionDiff(Report report)
    {
        super(report);
    }

    public FunctionDiff(Collection differences)
    {
        super(differences);
    }

    protected void compareReturnTypes(SimpleNode a, SimpleNode b)
    {
        SimpleNode art    = (SimpleNode)a.jjtGetChild(0);
        SimpleNode brt    = (SimpleNode)b.jjtGetChild(0);
        String     artStr = SimpleNodeUtil.toString(art);
        String     brtStr = SimpleNodeUtil.toString(brt);
        // tr.Ace.log("art: " + art + "; brt: " + brt);
        
        if (artStr.equals(brtStr)) {
            // tr.Ace.log("no change in return types");
        }
        else {
        	String packageAndClass = TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class));
            String methodName = packageAndClass + MethodUtil.getFullName((ASTMethodDeclaration)a);
            addChanged(RETURN_TYPE_CHANGED, new Object[] { artStr, brtStr, methodName }, art, brt);
        }
    }

    protected void compareParameters(ASTFormalParameters afp, ASTFormalParameters bfp, String methodName, String newMethodName)
    {
        List aParams = ParameterUtil.getParameterList(afp);
        List bParams = ParameterUtil.getParameterList(bfp);
        
        List aParamTypes = ParameterUtil.getParameterTypes(afp);
        List bParamTypes = ParameterUtil.getParameterTypes(bfp);

        int aSize = aParamTypes.size();
        int bSize = bParamTypes.size();

        // tr.Ace.log("aParamTypes.size: " + aSize + "; bParamTypes.size: " + bSize);

        if (aSize == 0) {
            if (bSize == 0) {
                // tr.Ace.log("no change in parameters");
            }
            else {
                Token[] names = ParameterUtil.getParameterNames(bfp);
                for (int ni = 0; ni < names.length; ++ni) {
                    addChanged(PARAMETER_ADDED, 
                               new Object[] { 
                                   names[ni].image,
                                   methodName,
                                   newMethodName
                               }, 
                               afp.getFirstToken(), 
                               afp.getLastToken(), 
                               names[ni], 
                               names[ni]);
                }
            }
        }
        else if (bSize == 0) {
            Token[] names = ParameterUtil.getParameterNames(afp);
            for (int ni = 0; ni < names.length; ++ni) {
                addChanged(PARAMETER_REMOVED,
                           new Object[] {
                               names[ni].image,
                               methodName,
                               newMethodName
                           },
                           names[ni],
                           names[ni],
                           bfp.getFirstToken(),
                           bfp.getLastToken());
            }
        }
        else {
            for (int ai = 0; ai < aSize; ++ai) {
                Object[] aValues = (Object[])aParams.get(ai);

                // tr.Ace.log("a.param: " + aValues[0] + "; a.type: " + aValues[1] + "; a.name: " + aValues[2]);

                int[] paramMatch = ParameterUtil.getMatch(aParams, ai, bParams);

                // tr.Ace.log("paramMatch", paramMatch);

                ASTFormalParameter aParam = ParameterUtil.getParameter(afp, ai);
                Token aNameTk = ParameterUtil.getParameterName(aParam);

                if (paramMatch[0] == ai && paramMatch[1] == ai) {
                    // tr.Ace.log("exact match");
                }
                else if (paramMatch[0] == ai) {
                    // tr.Ace.log("name changed");
                    Token bNameTk = ParameterUtil.getParameterName(bfp, ai);
                    addChanged(PARAMETER_NAME_CHANGED,
                               new Object[] {
                                   aNameTk.image,
                                   bNameTk.image,
                                   methodName,
                                   newMethodName
                               },
                               aNameTk,
                               bNameTk);
                }
                else if (paramMatch[1] == ai) {
                    // tr.Ace.log("type changed");
                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, ai);
                    String             bType  = ParameterUtil.getParameterType(bParam);
                    // tr.Ace.log("bParam: " + bParam + "; bType: " + bType);

                    addChanged(PARAMETER_TYPE_CHANGED, 
                               new String[] { 
                                   (String)aValues[1], 
                                   bType,
                                   methodName,
                                   newMethodName
                               }, 
                               (ASTFormalParameter)aValues[0], 
                               bParam);
                }
                else if (paramMatch[0] >= 0) {
                    // tr.Ace.log("misordered match by type");
                    Token bNameTk = ParameterUtil.getParameterName(bfp, paramMatch[0]);
                    // tr.Ace.log("aNameTk: " + aNameTk + "; bNameTk: " + bNameTk);
                    // tr.Ace.log("aNameTk.image: " + aNameTk.image + "; bNameTk.image: " + bNameTk.image);
                    if (aNameTk.image.equals(bNameTk.image)) {
                        addChanged(PARAMETER_REORDERED, 
                                   new Object[] { 
                                       aNameTk.image, 
                                       new Integer(ai), 
                                       new Integer(paramMatch[0]),
                                       methodName,
                                       newMethodName
                                   }, 
                                   aNameTk, 
                                   bNameTk);
                    }
                    else {
                        addChanged(PARAMETER_REORDERED_AND_RENAMED, 
                                   new Object[] { 
                                       aNameTk.image,
                                       new Integer(ai),
                                       new Integer(paramMatch[0]),
                                       bNameTk.image,
                                       methodName,
                                       newMethodName
                                   },
                                   aNameTk, 
                                   bNameTk);
                    }
                }
                else if (paramMatch[1] >= 0) {
                    System.out.println("misordered match by name");
                    
                    tr.Ace.log("misordered match by name");

                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, paramMatch[1]);

                    addChanged(PARAMETER_REORDERED, new Object[] { aNameTk.image, new Integer(ai), new Integer(paramMatch[1]), methodName }, aParam, bParam);
                }
                else {
                    // tr.Ace.log("not a match");
                    // tr.Ace.log("aNameTk: " + aNameTk);
                    addChanged(PARAMETER_REMOVED, new Object[] { aNameTk.image, methodName, newMethodName }, aParam, bfp);
                }
            }

            // tr.Ace.log("aParams: " + aParams);
            // tr.Ace.log("bParams: " + bParams);

            Iterator bit = bParams.iterator();
            for (int bi = 0; bit.hasNext(); ++bi) {
                Object[] bType = (Object[])bit.next();
                if (bType == null) {
                    // tr.Ace.log("already processed");
                }
                else {
                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, bi);
                    Token bName = ParameterUtil.getParameterName(bParam);
                    // tr.Ace.log("bName: " + bName);
                    addChanged(PARAMETER_ADDED, new Object[] { bName.image, methodName, newMethodName }, afp, bParam);
                }
            }
        }
    }

    protected void compareThrows(SimpleNode a, ASTNameList at, SimpleNode b, ASTNameList bt, String methodName)
    {
        if (at == null) {
            if (bt == null) {
                // tr.Ace.log("no change in throws");
            }
            else {
                ASTName[] names = (ASTName[])SimpleNodeUtil.findChildren(bt, ASTName.class);
                for (int ni = 0; ni < names.length; ++ni) {
                    addChanged(THROWS_ADDED, new Object[] { SimpleNodeUtil.toString(names[ni]), methodName }, a, names[ni]);
                }
            }
        }
        else if (bt == null) {
            ASTName[] names = (ASTName[])SimpleNodeUtil.findChildren(at, ASTName.class);
            for (int ni = 0; ni < names.length; ++ni) {
                addChanged(THROWS_REMOVED, new Object[] { SimpleNodeUtil.toString(names[ni]), methodName }, names[ni], b);
            }
        }
        else {
            ASTName[] aNames = (ASTName[])SimpleNodeUtil.findChildren(at, ASTName.class);
            ASTName[] bNames = (ASTName[])SimpleNodeUtil.findChildren(bt, ASTName.class);

            for (int ai = 0; ai < aNames.length; ++ai) {
                // save a reference to the name here, in case it gets removed
                // from the array in getMatch.
                ASTName aName = aNames[ai];

                int throwsMatch = getMatch(aNames, ai, bNames);

                // tr.Ace.log("throwsMatch: " + throwsMatch);

                if (throwsMatch == ai) {
                    // tr.Ace.log("exact match");
                }
                else if (throwsMatch >= 0) {
                    // tr.Ace.log("misordered match");
                    ASTName bName = ThrowsUtil.getNameNode(bt, throwsMatch);
                    // tr.Ace.log("aName: " + aName + "; bName: " + bName);
                    String aNameStr = SimpleNodeUtil.toString(aName);
                    addChanged(THROWS_REORDERED,
                               new Object[] { aNameStr, new Integer(ai), new Integer(throwsMatch), methodName },
                               aName,
                               bName);
                }
                else {
                    // tr.Ace.log("not a match; aName: " + aName);
                    addChanged(THROWS_REMOVED, new Object[] { SimpleNodeUtil.toString(aName), methodName }, aName, bt);
                }
            }

            for (int bi = 0; bi < bNames.length; ++bi) {
                // tr.Ace.log("b: " + bNames[bi]);

                if (bNames[bi] == null) {
                    // tr.Ace.log("already processed");
                }
                else {
                    ASTName bName = ThrowsUtil.getNameNode(bt, bi);
                    // tr.Ace.log("bName: " + bName);
                    addChanged(THROWS_ADDED, new Object[] { SimpleNodeUtil.toString(bName), methodName }, at, bName);
                }
            }
        }
    }

    protected int getMatch(ASTName[] aNames, int aIndex, ASTName[] bNames)
    {
        String aNameStr = SimpleNodeUtil.toString(aNames[aIndex]);

        // // tr.Ace.log("aNameStr: " + aNameStr);

        for (int bi = 0; bi < bNames.length; ++bi) {
            if (bNames[bi] == null) {
                // // tr.Ace.log("already consumed");
            }
            else if (SimpleNodeUtil.toString(bNames[bi]).equals(aNameStr)) {
                aNames[aIndex] = null;
                bNames[bi]     = null;
                return bi;
            }
        }

        return -1;
    }

}
