package org.incava.diffj;

import java.awt.Point;
import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;

import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.util.*;
import org.incava.util.diff.*;


public class FieldDiff extends ItemDiff
{
    public static final String VARIABLE_REMOVED = "variable removed: {0}";

    public static final String VARIABLE_ADDED = "variable added: {0}";

    public static final String VARIABLE_CHANGED = "variable changed from {0} to {1}";

    public static final String INITIALIZER_REMOVED = "initializer removed";

    public static final String INITIALIZER_ADDED = "initializer added";

    protected static final int[] VALID_MODIFIERS = new int[] {
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC,
    };

    public FieldDiff(Collection differences)
    {
        super(differences);
    }

    public void compare(ASTFieldDeclaration a, ASTFieldDeclaration b)
    {
        compareModifiers(a, b);
        compareVariables(a, b);
    }

    protected void compareModifiers(ASTFieldDeclaration a, ASTFieldDeclaration b)
    {
        compareModifiers((SimpleNode)a.jjtGetParent(), (SimpleNode)b.jjtGetParent(), VALID_MODIFIERS);
    }

    protected void compareVariables(ASTVariableDeclarator a, ASTVariableDeclarator b)
    {
        ASTVariableInitializer ainit = (ASTVariableInitializer)SimpleNodeUtil.findChild(a, ASTVariableInitializer.class);
        ASTVariableInitializer binit = (ASTVariableInitializer)SimpleNodeUtil.findChild(b, ASTVariableInitializer.class);
        
        if (ainit == null) {
            if (binit == null) {
                // no change in init
            }
            else {
                tr.Ace.stack(tr.Ace.YELLOW, "binit", binit);
                addChanged(INITIALIZER_ADDED, a, binit);
            }
        }
        else if (binit == null) {
            addChanged(INITIALIZER_REMOVED, ainit, b);
        }
        else {
            List aCode = SimpleNodeUtil.getChildrenSerially(ainit);
            List bCode = SimpleNodeUtil.getChildrenSerially(binit);

            // It is logically impossible for this to execute where "b"
            // represents the from-file, and "a" the to-file, since "a.name"
            // would have matched "b.name" in the first loop of
            // compareVariableLists

            String aName = TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + FieldUtil.getName(a).image;
            String bName = FieldUtil.getName(b).image;
            
            compareCode(aName, aCode, bName, bCode);
        }
    }

    protected static Map makeVDMap(ASTVariableDeclarator[] vds)
    {
        Map namesToVD = new HashMap();
        for (int vi = 0; vi < vds.length; ++vi) {
            ASTVariableDeclarator vd = vds[vi];
            String name = FieldUtil.getName(vd).image;
            namesToVD.put(name, vd);
        }
        return namesToVD;
    }

    protected void compareVariables(ASTFieldDeclaration a, ASTFieldDeclaration b)
    {
        ASTVariableDeclarator[] avds = (ASTVariableDeclarator[])SimpleNodeUtil.findChildren(a, ASTVariableDeclarator.class);
        ASTVariableDeclarator[] bvds = (ASTVariableDeclarator[])SimpleNodeUtil.findChildren(b, ASTVariableDeclarator.class);

        Map aNamesToVD = makeVDMap(avds);
        Map bNamesToVD = makeVDMap(bvds);

        Collection names = new TreeSet();
        names.addAll(aNamesToVD.keySet());
        names.addAll(bNamesToVD.keySet());
        
        Iterator nit = names.iterator();
        while (nit.hasNext()) {
            String name = (String)nit.next();
            tr.Ace.log("name", name);
            ASTVariableDeclarator avd = (ASTVariableDeclarator)aNamesToVD.get(name);
            ASTVariableDeclarator bvd = (ASTVariableDeclarator)bNamesToVD.get(name);

            if (avd == null || bvd == null) {
                if (avds.length == 1 && bvds.length == 1) {
                    tr.Ace.log("avd", avd);
                    tr.Ace.log("bvd", bvd);
                    Token aTk = FieldUtil.getName(avds[0]);
                    Token bTk = FieldUtil.getName(bvds[0]);
                    addChanged(VARIABLE_CHANGED, aTk, bTk);
                    compareVariables(avds[0], bvds[0]);
                }
                else if (avd == null) {
                    tr.Ace.log("avd", avd);
                    tr.Ace.log("bvd", bvd);
                    Token aTk = FieldUtil.getName(avds[0]);
                    Token bTk = FieldUtil.getName(bvd);
                    addChanged(VARIABLE_ADDED, new Object[] { name }, aTk, bTk);
                }
                else {
                    tr.Ace.log("avd", avd);
                    tr.Ace.log("bvd", bvd);
                    Token aTk = FieldUtil.getName(avd);
                    Token bTk = FieldUtil.getName(bvds[0]);
                    addChanged(VARIABLE_REMOVED, new Object[] { name }, aTk, bTk);
                }
            }
            else {
                tr.Ace.log("avd", avd);
                tr.Ace.log("bvd", bvd);
                compareVariables(avd, bvd);
            }
        }
    }

}
