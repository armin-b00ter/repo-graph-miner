package org.incava.diffj;

import java.io.*;
import java.util.*;

import net.sourceforge.pmd.ast.*;

import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.lang.*;
import org.incava.util.TimedEvent;

import com.sun.org.apache.bcel.internal.generic.ClassObserver;


public class TypeDiff extends ItemDiff
{
    public static final String TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface";

    public static final String TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class";

    public static final String METHOD_REMOVED = "method removed: {0}";

    public static final String METHOD_ADDED = "method added: {0}";

    public static final String METHOD_CHANGED = "method changed from {0} to {1}";

    public static final String CONSTRUCTOR_REMOVED = "constructor removed: {0}";

    public static final String CONSTRUCTOR_ADDED = "constructor added: {0}";

    public static final String FIELD_REMOVED = "field removed: {0}";

    public static final String FIELD_ADDED = "field added: {0}";

    public static final String INNER_INTERFACE_ADDED = "inner interface added: {0}";

    public static final String INNER_INTERFACE_REMOVED = "inner interface removed: {0}";

    public static final String INNER_CLASS_ADDED = "inner class added: {0}";

    public static final String INNER_CLASS_REMOVED = "inner class removed: {0}";

    public static final String EXTENDED_TYPE_REMOVED = "extended type removed: {0}";

    public static final String EXTENDED_TYPE_ADDED = "extended type added: {0}";

    public static final String EXTENDED_TYPE_CHANGED = "extended type changed from {0} to {1}";

    public static final String IMPLEMENTED_TYPE_REMOVED = "implemented type removed: {0}";

    public static final String IMPLEMENTED_TYPE_ADDED = "implemented type added: {0}";

    public static final String IMPLEMENTED_TYPE_CHANGED = "implemented type changed from {0} to {1}";

    public static final int[] VALID_TYPE_MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC, // valid only for inner types
        JavaParserConstants.STRICTFP
    };
    
    public TypeDiff(Report report)
    {
        super(report);
    }

    public TypeDiff(Collection differences)
    {
        super(differences);
    }

    public void compare(SimpleNode aType, SimpleNode bType)
    {
        tr.Ace.log("aType", aType);
        tr.Ace.log("bType", bType);
        
        // should have only one child, the type itself, either an interface or a
        // class declaration

        ASTTypeDeclaration a = (ASTTypeDeclaration)aType;
        ASTTypeDeclaration b = (ASTTypeDeclaration)bType;

        compare(a, b);
    }

    public void compare(ASTTypeDeclaration a, ASTTypeDeclaration b)
    {
        tr.Ace.log("a", a);
        tr.Ace.log("b", b);

        // should have only one child, the type itself, either an interface or a
        // class declaration

        ASTClassOrInterfaceDeclaration at = TypeDeclarationUtil.getType(a);
        ASTClassOrInterfaceDeclaration bt = TypeDeclarationUtil.getType(b);

        tr.Ace.log("at", at);
        tr.Ace.log("bt", bt);

        if (at == null && bt == null) {
            tr.Ace.log("skipping 'semicolon declarations'");
        }
        else {
            compare(at, bt);
        }
    }

    public void compare(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt)
    {
        // SimpleNodeUtil.dump(at, "a");
        // SimpleNodeUtil.dump(bt, "b");

    	/*if (at != null && bt != null) {
	        if (at.isInterface() == bt.isInterface()) {
	            tr.Ace.log("no change in types");
	        }
	        else if (bt.isInterface()) {
	            addChanged(TYPE_CHANGED_FROM_CLASS_TO_INTERFACE, null, at, bt);
	        }
	        else {
	            addChanged(TYPE_CHANGED_FROM_INTERFACE_TO_CLASS, null, at, bt);
	        }
	        
	        SimpleNode atParent = SimpleNodeUtil.getParent(at);
	        SimpleNode btParent = SimpleNodeUtil.getParent(bt);
	
	        compareAccess(atParent, btParent);
	
	        compareModifiers(atParent, btParent, VALID_TYPE_MODIFIERS);
	
	        compareExtends(at, bt);
	        compareImplements(at, bt);
    	}*/
    	
        compareDeclarations(at, bt);
    }

    protected void addAllDeclarations(ASTClassOrInterfaceBodyDeclaration[] decls, ASTClassOrInterfaceDeclaration other, boolean added)
    {
        for (int di = 0; di < decls.length; ++di) {
            addDeclaration(decls[di], other, added);
        }
    }

    protected Map<String, ASTClassOrInterfaceType> getExtImpMap(ASTClassOrInterfaceDeclaration coid, Class extImpClass)
    {
        Map<String, ASTClassOrInterfaceType> map = new HashMap<String, ASTClassOrInterfaceType>();
        SimpleNode list = SimpleNodeUtil.findChild(coid, extImpClass);

        if (list == null) {
            // tr.Ace.log("no list");
        }
        else {
            ASTClassOrInterfaceType[] types = (ASTClassOrInterfaceType[])SimpleNodeUtil.findChildren(list, ASTClassOrInterfaceType.class);
            
            for (int ti = 0; ti < types.length; ++ti) {
                ASTClassOrInterfaceType type     = types[ti];
                String                  typeName = SimpleNodeUtil.toString(type);
                map.put(typeName, type);
            }
        }
        
        return map;
    }

    protected void compareImpExt(ASTClassOrInterfaceDeclaration at, 
                                 ASTClassOrInterfaceDeclaration bt, 
                                 String addMsg,
                                 String chgMsg,
                                 String delMsg,
                                 Class extImpCls)
    {
        Map<String, ASTClassOrInterfaceType> aMap = getExtImpMap(at, extImpCls);
        Map<String, ASTClassOrInterfaceType> bMap = getExtImpMap(bt, extImpCls);

        // tr.Ace.log("aMap", aMap);
        // tr.Ace.log("bMap", bMap);

        // I don't like this special case, but it is better than two separate
        // "add" and "remove" messages.

        if (aMap.size() == 1 && bMap.size() == 1) {
            String aName = aMap.keySet().iterator().next();
            String bName = bMap.keySet().iterator().next();

            if (aName.equals(bName)) {
                // tr.Ace.log("no change");
            }
            else {
                ASTClassOrInterfaceType a = aMap.get(aName);
                ASTClassOrInterfaceType b = bMap.get(bName);
                
                addChanged(chgMsg, new Object[] { aName, bName }, a, b);
            }
        }
        else {
            List<String> typeNames = new ArrayList<String>();
            typeNames.addAll(aMap.keySet());
            typeNames.addAll(bMap.keySet());

            tr.Ace.log("typeNames", typeNames);

            Iterator<String> tit = typeNames.iterator();
            while (tit.hasNext()) {
                String typeName = tit.next();

                ASTClassOrInterfaceType aType = aMap.get(typeName);
                ASTClassOrInterfaceType bType = bMap.get(typeName);

                if (aType == null) {
                    addChanged(addMsg, new Object[] { typeName }, at, bType);
                }
                else if (bType == null) {
                    addChanged(delMsg, new Object[] { typeName }, aType, bt);
                }
                else {
                    // tr.Ace.log("no change");
                }
            }
        }
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt)
    {
        compareImpExt(at, bt, EXTENDED_TYPE_ADDED, EXTENDED_TYPE_CHANGED, EXTENDED_TYPE_REMOVED, ASTExtendsList.class);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt)
    {
        compareImpExt(at, bt, IMPLEMENTED_TYPE_ADDED, IMPLEMENTED_TYPE_CHANGED, IMPLEMENTED_TYPE_REMOVED, ASTImplementsList.class);
    }

    protected void addDeclaration(boolean isAdded, String addMsg, String remMsg, String name, SimpleNode from, SimpleNode to)
    {
        String msg = null;
        String type = null;
        if (isAdded) {
            msg = addMsg;
            type = CodeReference.ADDED;
        }
        else {
            msg = remMsg;
            SimpleNode x = from;
            from = to;
            to = x;
            type = CodeReference.DELETED;
        }

        add(type, msg, new Object[] { name }, from, to);
    }

    protected void addDeclaration(SimpleNode decl, SimpleNode other, boolean added)
    {
        tr.Ace.log("decl: " + decl);

        if (decl instanceof ASTClassOrInterfaceBodyDeclaration) {
            decl = TypeDeclarationUtil.getDeclaration((ASTClassOrInterfaceBodyDeclaration)decl);
        }
        
        if (decl instanceof ASTMethodDeclaration) {
            ASTMethodDeclaration method = (ASTMethodDeclaration)decl;
            String fullName = MethodUtil.getFullName(method);
            fullName = getClassPrefix(decl.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + fullName;
            addDeclaration(added, METHOD_ADDED, METHOD_REMOVED, fullName, other, method);
        }
        /*else if (decl instanceof ASTFieldDeclaration) {
            ASTFieldDeclaration field = (ASTFieldDeclaration)decl;
            String names = FieldUtil.getNames(field);
            addDeclaration(added, FIELD_ADDED, FIELD_REMOVED, names, other, field);
        }*/
        else if (decl instanceof ASTConstructorDeclaration) {
            ASTConstructorDeclaration ctor = (ASTConstructorDeclaration)decl;
            String fullName = CtorUtil.getFullName(ctor);
            fullName = getClassPrefix(decl.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) + fullName;
            addDeclaration(added, CONSTRUCTOR_ADDED, CONSTRUCTOR_REMOVED, fullName, other, ctor);
        }
        else if (decl instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceDeclaration coid = (ASTClassOrInterfaceDeclaration)decl;
            String name = ClassUtil.getName(coid).image;
            String addMsg = null;
            String remMsg = null;
            if (coid.isInterface()) {
                addMsg = INNER_INTERFACE_ADDED;
                remMsg = INNER_INTERFACE_REMOVED;
            }
            else {
                addMsg = INNER_CLASS_ADDED;
                remMsg = INNER_CLASS_REMOVED;
            }
//            addDeclaration(added, addMsg, remMsg, name, other, coid);
            compareDeclarations((ASTClassOrInterfaceDeclaration) other, coid);
            
        }/*
        else if (decl == null) {
            // nothing.
        }
        else {
            tr.Ace.stack(tr.Ace.REVERSE, "WTF? decl: " + decl);
        }*/
    }

    /**
     * Adds the declarations in <code>declared</code> that are not in the
     * <code>compared</code> list. <code>isAdd</code> denotes whether the
     * declarations are added or removed.
     */
    protected void addDeclarations(SimpleNode[] declared, List<ASTClassOrInterfaceBodyDeclaration> compared, SimpleNode other, boolean isAdd)
    {
        for (int di = 0; di < declared.length; ++di) {
            if (!compared.contains(declared[di])) {
                addDeclaration(declared[di], other, isAdd);
            }
        }
    }

    protected void compareDeclarations(ASTClassOrInterfaceDeclaration aNode, ASTClassOrInterfaceDeclaration bNode)
    {
        // now the children, below the modifiers in the AST.

        ASTClassOrInterfaceBodyDeclaration[] aDecls = TypeDeclarationUtil.getDeclarations(aNode);
        ASTClassOrInterfaceBodyDeclaration[] bDecls = TypeDeclarationUtil.getDeclarations(bNode);

        // compare declarations in A against B

        if (aDecls.length == 0) {
            if (bDecls.length == 0) {
                // tr.Ace.log("no change (both of zero length)");
            }
            else {
                addAllDeclarations(bDecls, aNode, true);
            }
        }
        else if (bDecls.length == 0) {
            addAllDeclarations(aDecls, bNode, false);
        }
        else {
            MethodUtil methodUtil = new MethodUtil();
            Map        matches    = TypeDeclarationUtil.matchDeclarations(aDecls, bDecls, methodUtil);
            Iterator   sit        = matches.keySet().iterator();
            List<ASTClassOrInterfaceBodyDeclaration>       aSeen       = new ArrayList<ASTClassOrInterfaceBodyDeclaration>();
            List<ASTClassOrInterfaceBodyDeclaration>       bSeen      = new ArrayList<ASTClassOrInterfaceBodyDeclaration>();

            // TimedEvent dte = new TimedEvent("diffs");

            Collection diffs = get();

            while (sit.hasNext()) {
                Double dScore  = (Double)sit.next();
                List   atScore = (List)matches.get(dScore);

                Iterator vit = atScore.iterator();
                while (vit.hasNext()) {
                    Object[] values = (Object[])vit.next();

                    ASTClassOrInterfaceBodyDeclaration aDecl = (ASTClassOrInterfaceBodyDeclaration)values[0];
                    ASTClassOrInterfaceBodyDeclaration bDecl = (ASTClassOrInterfaceBodyDeclaration)values[1];
                    
                    aSeen.add(aDecl);
                    bSeen.add(bDecl);

                    tr.Ace.log("aDecl", aDecl);
                    tr.Ace.log("bDecl", bDecl);

                    SimpleNode ad = TypeDeclarationUtil.getDeclaration(aDecl);
                    SimpleNode bd = TypeDeclarationUtil.getDeclaration(bDecl);                    

                    // we know that the classes of aDecl and bDecl are the same
                    if (ad instanceof ASTMethodDeclaration) {
                        MethodDiff differ = new MethodDiff(diffs);
                        differ.compareAccess(aDecl, bDecl);
                        differ.compare((ASTMethodDeclaration)ad, (ASTMethodDeclaration)bd);
                    }/*
                    else if (ad instanceof ASTFieldDeclaration) {
                        FieldDiff differ = new FieldDiff(diffs);
                        differ.compareAccess(aDecl, bDecl);
                        differ.compare((ASTFieldDeclaration)ad, (ASTFieldDeclaration)bd);
                    }
                    else if (ad instanceof ASTConstructorDeclaration) {
                        CtorDiff differ = new CtorDiff(diffs);
                        differ.compareAccess(aDecl, bDecl);
                        differ.compare((ASTConstructorDeclaration)ad, (ASTConstructorDeclaration)bd);
                    }*/
                    else if (ad instanceof ASTClassOrInterfaceDeclaration) {
                        // access comparison is done in TypeDiff; confusing.
                        compare((ASTClassOrInterfaceDeclaration)ad, (ASTClassOrInterfaceDeclaration)bd);
                    }/*
                    else if (ad == null && bd == null) {
                        // a match between semicolons.
                        tr.Ace.log("matched 'semicolon declarations'");
                    }
                    else {
                        tr.Ace.reverse("WTF? aDecl: " + ad.getClass());
                    }*/
                }
            }

            // dte.close();

            addDeclarations(aDecls, aSeen, bNode, false);
            addDeclarations(bDecls, bSeen, aNode, true);
        }
    }

    public static String getClassPrefix(List<?> list) {
    	String result = "";
    	// Go backwards through the list, so the topmost class comes first
    	for (ListIterator<?> iterator = list.listIterator(list.size()); iterator.hasPrevious();) {
			ASTClassOrInterfaceDeclaration decl = (ASTClassOrInterfaceDeclaration) iterator.previous();
			result += ClassUtil.getName(decl);
			if (iterator.hasPrevious()) {
				result += "$";
			}
			else {
				// Prepend the package name
				ASTCompilationUnit cu = (ASTCompilationUnit) decl.getFirstParentOfType(ASTCompilationUnit.class);
				ASTPackageDeclaration pkg = CompilationUnitUtil.getPackage(cu);
				if (pkg != null) {
			        Token tk = pkg.getFirstToken();
			        Token last = pkg.getLastToken();
			        String packageName = "";
			        while (tk.next != last) {
			            tk = tk.next;
			            packageName += tk.image;
			        }
					result = packageName + "." + result;
				}
			}
		}
    	result += ".";
    	return result;
    }
}
