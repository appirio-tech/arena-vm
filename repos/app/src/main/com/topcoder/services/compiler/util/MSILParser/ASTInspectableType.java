/* Generated By:JJTree: Do not edit this line. ASTInspectableType.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTInspectableType extends SimpleNode {
  public ASTInspectableType(int id) {
    super(id);
  }

  public ASTInspectableType(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public String getType() {
      return ((ASTTypeReference)children[0]).getType();
  }
}
