/* Generated By:JJTree: Do not edit this line. ASTVTFixupAttr.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTVTFixupAttr extends SimpleNode {
  public ASTVTFixupAttr(int id) {
    super(id);
  }

  public ASTVTFixupAttr(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
