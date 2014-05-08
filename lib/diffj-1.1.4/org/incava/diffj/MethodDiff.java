package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;

import org.incava.java.*;


public class MethodDiff extends FunctionDiff
{
    public static final String METHOD_BLOCK_ADDED = "method block added: {0}";

    public static final String METHOD_BLOCK_REMOVED = "method block removed: {0}";

    protected static final int[] VALID_MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.NATIVE,
        JavaParserConstants.STATIC,
        JavaParserConstants.STRICTFP
    };

    public MethodDiff(Collection differences)
    {
        super(differences);
    }

    public void compare(ASTMethodDeclaration a, ASTMethodDeclaration b)
    {
        compareModifiers(a, b);
        compareReturnTypes(a, b);
        compareParameters(a, b);
        compareThrows(a, b);
        compareBodies(a, b);
    }

    protected void compareModifiers(ASTMethodDeclaration a, ASTMethodDeclaration b)
    {
        compareModifiers(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b), VALID_MODIFIERS);
    }

    protected void compareParameters(ASTMethodDeclaration a, ASTMethodDeclaration b)
    {
        ASTFormalParameters afp = MethodUtil.getParameters(a);
        ASTFormalParameters bfp = MethodUtil.getParameters(b);
        
        String packageAndClass = TypeDiff.getClassPrefix(afp.getParentsOfType(ASTClassOrInterfaceDeclaration.class)); 
        String methodName = packageAndClass + MethodUtil.getFullName(a);
        String newMethodName = packageAndClass + MethodUtil.getFullName(b);

        compareParameters(afp, bfp, methodName, newMethodName);
    }

    protected void compareThrows(ASTMethodDeclaration a, ASTMethodDeclaration b)
    {
        ASTNameList at = MethodUtil.getThrowsList(a);
        ASTNameList bt = MethodUtil.getThrowsList(b);
        
        String packageAndClass = TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class));
        String methodName = packageAndClass + MethodUtil.getFullName(a);

        compareThrows(a, at, b, bt, methodName);
    }

    protected void compareBodies(ASTMethodDeclaration a, ASTMethodDeclaration b)
    {
        final int ABSTRACT_METHOD_CHILDREN = 2;

        ASTBlock aBlock = (ASTBlock)SimpleNodeUtil.findChild(a, ASTBlock.class);
        ASTBlock bBlock = (ASTBlock)SimpleNodeUtil.findChild(b, ASTBlock.class);
        
        /*Object[] params = toParameters(a, b);
        List<Object> newParams = new ArrayList(Arrays.asList(params));
        newParams.add(MethodUtil.getFullName(a));*/
        
        
        if (aBlock == null) {
            if (bBlock == null) {
                // neither has a block, so no change
            }
            else {
//            	addChanged(METHOD_BLOCK_ADDED, newParams.toArray(), a, b);
            	addChanged(METHOD_BLOCK_ADDED, new String[] { TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + MethodUtil.getFullName(a) } , a, b);
            }
        }
        else if (bBlock == null) {
//            addChanged(METHOD_BLOCK_REMOVED, newParams.toArray(), a, b);
        	addChanged(METHOD_BLOCK_REMOVED, new String[] { TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + MethodUtil.getFullName(a) }, a, b);
        }
        else {
            String aName = TypeDiff.getClassPrefix(a.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + MethodUtil.getFullName(a);
            String bName = MethodUtil.getFullName(b);
            
            compareBlocks(aName, aBlock, bName, bBlock);
        }
    }

    protected void compareBlocks(String aName, ASTBlock aBlock, String bName, ASTBlock bBlock)
    {
        List a = SimpleNodeUtil.getChildrenSerially(aBlock);
        List b = SimpleNodeUtil.getChildrenSerially(bBlock);

        compareCode(aName, a, bName, b);
    }

}
