/* Generated By:JJTree: Do not edit this line. ASTInstr_None.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTInstr_None extends SimpleNode {
  public ASTInstr_None(int id) {
    super(id);
  }

  public ASTInstr_None(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
