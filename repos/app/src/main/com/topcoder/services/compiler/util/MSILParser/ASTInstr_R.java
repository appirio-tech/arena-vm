/* Generated By:JJTree: Do not edit this line. ASTInstr_R.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTInstr_R extends SimpleNode {
  public ASTInstr_R(int id) {
    super(id);
  }

  public ASTInstr_R(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
