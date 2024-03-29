/* Generated By:JJTree: Do not edit this line. ASTTypeSpec.java */

package com.topcoder.services.compiler.util.MSILParser;

public class ASTTypeSpec extends SimpleNode {
  public ASTTypeSpec(int id) {
    super(id);
  }

  public ASTTypeSpec(MSILParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MSILParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public String getType() {
      for(int i =0  ; i < children.length; i++) {
          if(children[i] instanceof ASTType) {
                return ((ASTType)children[i]).getType();
          } else if(children[i] instanceof ASTInspectableType) {
                return ((ASTInspectableType)children[i]).getType();
          }
          
      }
      
      return null;
  }
}
