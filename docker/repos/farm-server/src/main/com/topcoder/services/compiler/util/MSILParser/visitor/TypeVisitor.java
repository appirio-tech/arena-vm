/*
 * TypeVisitor.java
 *
 * Created on June 21, 2006, 7:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.compiler.util.MSILParser.visitor;

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
import java.util.ArrayList;

/**
 *
 * @author rfairfax
 */
public class TypeVisitor implements MSILParserVisitor {
    
    /** Creates a new instance of TypeVisitor */
    public TypeVisitor() {
    }
    
    private ArrayList types = new ArrayList();
    private ArrayList methods = new ArrayList();
    
    public String[] getTypes() {
        return (String[])types.toArray(new String[0]);
    }
    
    public String[] getMethods() {
        return (String[])methods.toArray(new String[0]);
    }

    public Object visit(SimpleNode node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTILFile node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTId node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDottedName node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTLabelOrOffset node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTLabels node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTCodeLabel node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDataLabel node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTBytes node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTFloat32 node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTFloat64 node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTExternSourceDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTFilename node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTClass node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTLanguageDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTSecurityDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTNameValPairs node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTNameValPair node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTSecAction node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTVTFixupDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTVTFixupAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTManResDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTLocalsSignature node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTLocal node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_Tok node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_Type node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_Field node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_Method node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_Br node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_R node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_I node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_Var node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInstr_None node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTScopeBlock node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTSEHBlock node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTTryBlock node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTSEHClause node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTHandlerBlock node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTFieldInit node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTMethodBodyItem node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTMethodHeader node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTPinvAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTMethAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTImplAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTFieldDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTFieldAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDataDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDdBody node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDdItemList node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTDdItem node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTCustomDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTCtor node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTExportAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTExternClassDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTClassHeader node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTClassName node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTGenPars node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTGenPar node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTGenParAttribs node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTGenConstraints node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTClassAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTClassMember node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTGenArity node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTPropHeader node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTPropMember node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTMethodName node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTEventHeader node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTEventMember node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTAsmRefDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTAsmDecl node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTType node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTTypeBase node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInspectableType node, Object data) {
        node.childrenAccept(this, new Boolean(true));
        return new Boolean(false);
    }

    public Object visit(ASTTypeBase2 node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTTypeSpec node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTBound node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTGenArgs node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTParameters node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTParam node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTParamAttr node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTCallConv node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTCallKind node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTTypeReference node, Object data) {
        Boolean b = (Boolean)data;
        if(b.booleanValue()) {
            types.add(node.getType());
            //System.out.println("FOUND: " + node.getType());
        }
        
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTResolutionScope node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTAssemblyRefName node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTNativeType node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTValueTypeReference node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTField node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTInt32 node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTHexByte node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTRealNumber node, Object data) {
        node.childrenAccept(this, new Boolean(false));
        return new Boolean(false);
    }

    public Object visit(ASTMethodCall node, Object data) {
        String type = null;
        String method = null;
        for(int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(i);
            if(n instanceof ASTTypeSpec) {
                type = ((ASTTypeSpec)n).getType();
                data = n.jjtAccept(this, data);
            } else if(n instanceof ASTType) {
                type = ((ASTType)n).getType();
                data = n.jjtAccept(this, data);
            } else if(n instanceof ASTMethodName) {
                method = ((ASTMethodName)n).getName();
                data = n.jjtAccept(this, data);
            } else {
                data = n.jjtAccept(this, data);
            }
            //System.out.println("DATA IS:" + data);
        }
        if(type != null) {
            methods.add(type + "::" + method);
        }
        
        //if(type == null)
            //type = "Built-In Type";
        
        //System.out.println("T:" + type + "::" + method);
        
        return new Boolean(false);
    }
    
}
