/* Generated By:JJTree: Do not edit this line. ASTFieldInit.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTFieldInit extends SimpleNode {
  public ASTFieldInit(int id) {
    super(id);
  }

  public ASTFieldInit(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}