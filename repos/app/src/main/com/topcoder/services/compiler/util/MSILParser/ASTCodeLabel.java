/* Generated By:JJTree: Do not edit this line. ASTCodeLabel.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTCodeLabel extends SimpleNode {
  public ASTCodeLabel(int id) {
    super(id);
  }

  public ASTCodeLabel(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
