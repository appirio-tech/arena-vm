/* Generated By:JJTree: Do not edit this line. ASTInstr_Tok.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTInstr_Tok extends SimpleNode {
  public ASTInstr_Tok(int id) {
    super(id);
  }

  public ASTInstr_Tok(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
