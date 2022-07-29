/*
 * ClassNameVisitor.java
 *
 * Created on June 21, 2006, 2:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.compiler.util.MSILParser.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.topcoder.services.compiler.util.MSILParser.ASTAsmDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTAsmRefDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTAssemblyRefName;
import com.topcoder.services.compiler.util.MSILParser.ASTBound;
import com.topcoder.services.compiler.util.MSILParser.ASTBytes;
import com.topcoder.services.compiler.util.MSILParser.ASTCallConv;
import com.topcoder.services.compiler.util.MSILParser.ASTCallKind;
import com.topcoder.services.compiler.util.MSILParser.ASTClass;
import com.topcoder.services.compiler.util.MSILParser.ASTClassAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTClassHeader;
import com.topcoder.services.compiler.util.MSILParser.ASTClassMember;
import com.topcoder.services.compiler.util.MSILParser.ASTClassName;
import com.topcoder.services.compiler.util.MSILParser.ASTCodeLabel;
import com.topcoder.services.compiler.util.MSILParser.ASTCtor;
import com.topcoder.services.compiler.util.MSILParser.ASTCustomDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTDataDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTDataLabel;
import com.topcoder.services.compiler.util.MSILParser.ASTDdBody;
import com.topcoder.services.compiler.util.MSILParser.ASTDdItem;
import com.topcoder.services.compiler.util.MSILParser.ASTDdItemList;
import com.topcoder.services.compiler.util.MSILParser.ASTDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTDottedName;
import com.topcoder.services.compiler.util.MSILParser.ASTEventHeader;
import com.topcoder.services.compiler.util.MSILParser.ASTEventMember;
import com.topcoder.services.compiler.util.MSILParser.ASTExportAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTExternClassDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTExternSourceDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTField;
import com.topcoder.services.compiler.util.MSILParser.ASTFieldAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTFieldDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTFieldInit;
import com.topcoder.services.compiler.util.MSILParser.ASTFilename;
import com.topcoder.services.compiler.util.MSILParser.ASTFloat32;
import com.topcoder.services.compiler.util.MSILParser.ASTFloat64;
import com.topcoder.services.compiler.util.MSILParser.ASTGenArgs;
import com.topcoder.services.compiler.util.MSILParser.ASTGenArity;
import com.topcoder.services.compiler.util.MSILParser.ASTGenConstraints;
import com.topcoder.services.compiler.util.MSILParser.ASTGenPar;
import com.topcoder.services.compiler.util.MSILParser.ASTGenParAttribs;
import com.topcoder.services.compiler.util.MSILParser.ASTGenPars;
import com.topcoder.services.compiler.util.MSILParser.ASTHandlerBlock;
import com.topcoder.services.compiler.util.MSILParser.ASTHexByte;
import com.topcoder.services.compiler.util.MSILParser.ASTILFile;
import com.topcoder.services.compiler.util.MSILParser.ASTId;
import com.topcoder.services.compiler.util.MSILParser.ASTImplAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTInspectableType;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_Br;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_Field;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_I;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_Method;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_None;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_R;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_Tok;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_Type;
import com.topcoder.services.compiler.util.MSILParser.ASTInstr_Var;
import com.topcoder.services.compiler.util.MSILParser.ASTInt32;
import com.topcoder.services.compiler.util.MSILParser.ASTLabelOrOffset;
import com.topcoder.services.compiler.util.MSILParser.ASTLabels;
import com.topcoder.services.compiler.util.MSILParser.ASTLanguageDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTLocal;
import com.topcoder.services.compiler.util.MSILParser.ASTLocalsSignature;
import com.topcoder.services.compiler.util.MSILParser.ASTManResDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTMethAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTMethodBodyItem;
import com.topcoder.services.compiler.util.MSILParser.ASTMethodCall;
import com.topcoder.services.compiler.util.MSILParser.ASTMethodHeader;
import com.topcoder.services.compiler.util.MSILParser.ASTMethodName;
import com.topcoder.services.compiler.util.MSILParser.ASTNameValPair;
import com.topcoder.services.compiler.util.MSILParser.ASTNameValPairs;
import com.topcoder.services.compiler.util.MSILParser.ASTNativeType;
import com.topcoder.services.compiler.util.MSILParser.ASTParam;
import com.topcoder.services.compiler.util.MSILParser.ASTParamAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTParameters;
import com.topcoder.services.compiler.util.MSILParser.ASTPinvAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTPropHeader;
import com.topcoder.services.compiler.util.MSILParser.ASTPropMember;
import com.topcoder.services.compiler.util.MSILParser.ASTRealNumber;
import com.topcoder.services.compiler.util.MSILParser.ASTResolutionScope;
import com.topcoder.services.compiler.util.MSILParser.ASTSEHBlock;
import com.topcoder.services.compiler.util.MSILParser.ASTSEHClause;
import com.topcoder.services.compiler.util.MSILParser.ASTScopeBlock;
import com.topcoder.services.compiler.util.MSILParser.ASTSecAction;
import com.topcoder.services.compiler.util.MSILParser.ASTSecurityDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTTryBlock;
import com.topcoder.services.compiler.util.MSILParser.ASTType;
import com.topcoder.services.compiler.util.MSILParser.ASTTypeBase;
import com.topcoder.services.compiler.util.MSILParser.ASTTypeBase2;
import com.topcoder.services.compiler.util.MSILParser.ASTTypeReference;
import com.topcoder.services.compiler.util.MSILParser.ASTTypeSpec;
import com.topcoder.services.compiler.util.MSILParser.ASTVTFixupAttr;
import com.topcoder.services.compiler.util.MSILParser.ASTVTFixupDecl;
import com.topcoder.services.compiler.util.MSILParser.ASTValueTypeReference;
import com.topcoder.services.compiler.util.MSILParser.MSILParserVisitor;
import com.topcoder.services.compiler.util.MSILParser.SimpleNode;

/**
 *
 * @author rfairfax
 */
public class ClassNameVisitor implements MSILParserVisitor {
    private List classes = new ArrayList();
    private List methods = new ArrayList();
    private boolean inMethodHeader;
    private boolean inMethodName;

    /** Creates a new instance of ClassNameVisitor */
    public ClassNameVisitor() {
    }

    public Object visit(SimpleNode node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTILFile node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTId node, Object data) {
        if (inMethodName && inMethodHeader) {
            String base = ((ClassNameVisitorState) data).getBase();
            if (base.length() > 0) {
                base = base.substring(0, base.length()-1);
                addMethod(base, node.getName());
            }
        }
        data = node.childrenAccept(this, data);
        return data;
    }

    private void addMethod(String className, String methodName) {
        int pos = className.indexOf(".'<>");
        //When delegates are declared and used in methods of the outer type are declare in compiled time inner classes
        if (pos != -1) {
           //This is not sure  but it seems to be always the same naming pattern
            className = className.substring(0, pos);
        }
        methods.add(className+"::"+methodName);
    }

    public Object visit(ASTDottedName node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTLabelOrOffset node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTLabels node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCodeLabel node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTDataLabel node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTBytes node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTFloat32 node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTFloat64 node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExternSourceDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTFilename node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTLanguageDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTSecurityDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTNameValPairs node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTNameValPair node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTSecAction node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTVTFixupDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTVTFixupAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTManResDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTLocalsSignature node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTLocal node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_Tok node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_Type node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_Field node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_Method node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_Br node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_R node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_I node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_Var node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInstr_None node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTScopeBlock node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTSEHBlock node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTryBlock node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTSEHClause node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTHandlerBlock node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTFieldInit node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMethodBodyItem node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMethodHeader node, Object data) {
        inMethodHeader = true;
        data = node.childrenAccept(this, data);
        inMethodHeader = false;
        return data;
    }

    public Object visit(ASTPinvAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMethAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTImplAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTFieldDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTFieldAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTDataDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTDdBody node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTDdItemList node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTDdItem node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCustomDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCtor node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExportAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExternClassDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTClassHeader node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTGenPars node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTGenPar node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTGenParAttribs node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTGenConstraints node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTClassAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTClassMember node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTGenArity node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTPropHeader node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTPropMember node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMethodName node, Object data) {
        inMethodName = true;
        data = node.childrenAccept(this, data);
        inMethodName = false;
        return data;
    }

    public Object visit(ASTEventHeader node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTEventMember node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAsmRefDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAsmDecl node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTType node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeBase node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeBase2 node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeSpec node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTBound node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTGenArgs node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTParameters node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTParam node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTParamAttr node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCallConv node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCallKind node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeReference node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTResolutionScope node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAssemblyRefName node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTNativeType node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTValueTypeReference node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTField node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTInt32 node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTHexByte node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTRealNumber node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTClassName node, Object data) {
        //the only child should be a DottedName, which has our class name
        ClassNameVisitorState base = (ClassNameVisitorState)data;

        String className = base.getBase() + node.getBaseName();

        classes.add(className);

        base.addBase(node.getBaseName());
        data = node.childrenAccept(this, data);

        return data;
    }

    public Object visit(ASTClass node, Object data) {
        //manually visit children to keep data intact
        for(int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(i);
            if(n instanceof ASTClassHeader) {
                data = n.jjtAccept(this, data);
            } else {
                data = n.jjtAccept(this, data);
            }
            //System.out.println("DATA IS:" + data);
        }
        ((ClassNameVisitorState)data).removeBase();
        return data;
    }

    public String[] getClasses() {
        return (String[])classes.toArray(new String[0]);
    }

    public Set getClassesSet() {
        return new HashSet(classes);
    }

    public Set getMethods() {
        return new HashSet(methods);
    }

    public Object visit(ASTInspectableType node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMethodCall node, Object data) {
        data = node.childrenAccept(this, data);
        return data;
    }
}
