/* Generated By:JJTree: Do not edit this line. ASTMethodBodyItem.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTMethodBodyItem extends SimpleNode {
  public ASTMethodBodyItem(int id) {
    super(id);
  }

  public ASTMethodBodyItem(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
