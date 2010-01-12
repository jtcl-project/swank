package com.onemoonscientific.swank.canvas;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;

class ItemTreeNode extends DefaultMutableTreeNode {

    public Enumeration postorderEnumeration() {
          return new PostorderEnumeration(this);
    }
    public Enumeration reversePostorderEnumeration() {
          return new PostorderEnumeration(this,true);
    }

   public Enumeration depthFirstEnumeration() {
        return postorderEnumeration();
   }
   public Enumeration reverseDepthFirstEnumeration() {
        return reversePostorderEnumeration();
   }
   boolean checkHidden(TreeNode node) {
      boolean ok=true;
      if (node instanceof ItemTreeNode) {
          SwkShape swkShape = (SwkShape) ((ItemTreeNode) node).getUserObject();
          if (swkShape instanceof ItemNode) {
              if (swkShape.state == SwkShape.HIDDEN) {
                   ok=false;
              }
          }
      }
      return ok;
  }
  public Enumeration children(boolean reversed) {
      if (reversed) {
          return new ReversedEnumeration();
      } else {
          return super.children();
      }
  }
  final class ReversedEnumeration implements Enumeration {
     int nElements = getChildCount();
     public boolean hasMoreElements() {
         return nElements > 0;
     }
     public Object nextElement() {
         nElements--;
         return getChildAt(nElements);
     }
  }

  final class PostorderEnumeration implements Enumeration<TreeNode> {
     protected TreeNode root;
     protected TreeNode current=null;
     protected Enumeration<TreeNode> children;
     protected Enumeration<TreeNode> subtree;
     boolean reversed = false; 
     public PostorderEnumeration(TreeNode rootNode) {
         super();
         root = rootNode;
         children = root.children();
         subtree = EMPTY_ENUMERATION;
     }
     public PostorderEnumeration(TreeNode rootNode,boolean reversed) {
         super();
         root = rootNode;
         this.reversed = true;
         children = ((ItemTreeNode) root).children(reversed);
         subtree = EMPTY_ENUMERATION;
     }
 
     public boolean hasMoreElements() {
         current = scanForNextElement();
         return current != null;
          //return (root != null) || (current != null);
     }

     public TreeNode nextElement() {
         TreeNode returnValue = current;
         current = null;
         return returnValue;
     }
 
     public TreeNode scanForNextElement() {
         TreeNode retval=null;
 
         if (subtree.hasMoreElements()) {
             retval = subtree.nextElement();
         } else {
             while (children.hasMoreElements()) {
                 TreeNode testNode =  (TreeNode) children.nextElement();
                 if (checkHidden(testNode)) {
                     subtree = new PostorderEnumeration(testNode,reversed);
                     if (subtree.hasMoreElements()) {
                         retval = subtree.nextElement();
                         break;
                     }
                 }
             }
             if (retval == null) {
                 retval = root;
                 root = null;
             }
         }
         return retval;
     }
   }
}
