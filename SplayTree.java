import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

class Node {
  int key;
  Node left, right;
  Node parent;

  public Node(int key) {
    this.key = key;
    this.left = null;
    this.right = null;
    this.parent = null;
  }
}


public class SplayTree {
  
  private Node root;
  private char[] leftTrace;
  public int swapIndicator = 0;
  public int comparisionCounter = 0;
  private char[] rightTrace;

  public SplayTree() {
    root = null;
  }

  public void splay(Node node) { // operacja splay polega na przesunięciu węzła w górę drzewa, aby uczynić go korzeniem albo jednym z najblizszych sasiadów korzenia
    while(node.parent != null) {
      Node parent = node.parent;
      Node grandParent = parent.parent;

      if(grandParent == null) {

        if(node == parent.left) {
          leftRotation(node, parent);
          swapIndicator++;
        } else {
          rightRotation(node, parent);
          swapIndicator++;
        }
      }
      else {
        if(node == parent.left) {
          if(parent == grandParent.left) { // left-left
            leftRotation(parent, grandParent);
            swapIndicator++;
            leftRotation(node, parent);
            swapIndicator++;
          }
          else { // right-left
            leftRotation(node, node.parent);
            swapIndicator++;
            rightRotation(node, node.parent);
            swapIndicator++;
          }
        }
        else {
          if(parent == grandParent.left) { // left-right
            rightRotation(node, node.parent);
            swapIndicator++;
            leftRotation(node, node.parent);
            swapIndicator++;
          }
          else {
            rightRotation(parent, grandParent);
            swapIndicator++;
            rightRotation(node, parent);
            swapIndicator++;
          }
        }
      }
    }
    root = node;
  }

  public void insert(int key) {
    Node z = root;
    Node p = null;

    while(z != null) {
      p = z;
      comparisionCounter++;
      if(z.key > key) {
        z = z.left;
      } else {
        z = z.right;
      }
    }

    z = new Node(key);
    z.parent = p;

    if(p == null) {
      root = z;
      swapIndicator++;
    } else if(z.key > p.key) { // prawe dziecko
      p.right = z;
      swapIndicator++;
    } else { // lewe dziecko
      p.left = z;
      swapIndicator++;
    }

    splay(z);

  }

  public void delete(int key) {
    Node node = findNode(key);
    remove(node);
  }

  private void remove(Node node) { // function to remove node
    if(node == null) 
      return;
    
    splay(node);
    if( (node.left != null) && (node.right != null) ) {
      Node min = node.left;
      while(min.right != null) {
        min = min.right;
        swapIndicator++;
      }
      min.right = node.right;
      swapIndicator++;
      node.right.parent = min;
      swapIndicator++;
      node.left.parent = null;
      swapIndicator++;
      root = node.left;
      swapIndicator++;
    }
    else if(node.right != null) {
      node.right.parent = null;
      swapIndicator++;
      root = node.right;
      swapIndicator++;
    }
    else if(node.left != null) {
      node.left.parent = null;
      swapIndicator++;
      root = node.left;
      swapIndicator++;
    }
    else {
      root = null;
      swapIndicator++;
    } 

    node.parent = null;
    node.left = null;
    node.right = null;
    node = null;
  }

  private Node findNode(int key) {
    Node prev = null;
    Node curr = root;

    while(curr != null) {
      prev = curr;
      comparisionCounter++;
      if(curr.key > key) {
        curr = curr.left;
        swapIndicator++;
      } else if(curr.key < key) {
        comparisionCounter++;
        curr = curr.right;
        swapIndicator++;
      } else if(key == curr.key) {
        splay(curr);
        return curr;
      }
    }

    if(prev != null) {
      splay(prev);
      return null;
    }
    return null;
  }

  private void rightRotation(Node node, Node parent) {
    if(parent.parent != null) {
      if(parent == parent.parent.left) {
        parent.parent.left = node;
      } else {
        parent.parent.right = node;
      }
    }

    if(node.left != null) {
      node.left.parent = parent;
    }
    node.parent = parent.parent;
    parent.parent = node;
    parent.right = node.left;
    node.left = parent;
  }

  private void leftRotation(Node node, Node parent) {
    if(parent.parent != null) {
      if(parent == parent.parent.left) {
        parent.parent.left = node;
      } else {
        parent.parent.right = node;
      }
    }

    if(node.right != null) {
      node.right.parent = parent;
    }

    node.parent = parent.parent;
    parent.parent = node;
    parent.left = node.right;
    node.right = parent;
  }

  private void printBST(Node node, int depth, char prefix) {
    if(node == null) 
      return;
    if(node.left != null) {
      printBST(node.left, depth + 1, '/');
    }
    if(prefix == '/') { 
      leftTrace[depth - 1] = '|';
    }
    if(prefix == '\\') { 
      rightTrace[depth - 1] = ' ';
    }
    if(depth == 0) {
      System.out.print("-");
    }
    if(depth > 0) {
      System.out.print(" ");
    }

    for(int i = 0; i < depth - 1; i++) {
      if(leftTrace[i] == '|' || rightTrace[i] == '|') {
        System.out.print("| ");
      } else {
        System.out.print("  ");
      }
    }

    if(depth > 0) {
      System.out.print(prefix + "-");
    }
    System.out.println("[" + node.key + "]");
    leftTrace[depth] = ' ';
    
    if(node.right != null) {
      rightTrace[depth] = '|';
      printBST(node.right, depth + 1, '\\');
    }
  }

  public void printBST(int height) {
    leftTrace = new char[height];
    rightTrace = new char[height];
    printBST(root, 0, ' ');
  }

  public int getHeight() {
    return calculateTreeHeight(root);
  }

  private int calculateTreeHeight(Node node) {
    if(node == null) // root is null, height of tree is zero
      return -1;
    // height of each subtree
    int leftHeight = calculateTreeHeight(node.left);
    int rightHeight = calculateTreeHeight(node.right);
    return Math.max(leftHeight, rightHeight) + 1;
  }

  private void writeComps(FileWriter compWriter, int comps, int n) {
    try {
      StringBuilder sBuild = new StringBuilder();
      sBuild.append("" + comps);
      sBuild.append(",");
      sBuild.append("" + n);
      sBuild.append("\n");
      compWriter.write(sBuild.toString());
    } catch(IOException ex) {
      System.out.println(ex);
    }
  }

  public static void main(String[] args) {
    SplayTree splayTree = new SplayTree();
    SecureRandom random = new SecureRandom();
    FileWriter compWriter = null;
    File compFile = new File("splay-swap-sorted.csv");

    try {
      compWriter = new FileWriter(compFile);

      for(int i = 0; i < 20; i++) {
        for(int n = 10000; n <= 100000; n += 10000) {
          // arr = new int[n];
          for(int k = 0; k < n; k++) {
            // int randomEl = random.nextInt(2 * n);
            int randomElToDelete = random.nextInt(2 * n);
            splayTree.insert(k); // wstawianie losowego elementu
            splayTree.delete(randomElToDelete); // usuwanie losowego elementu
          }
          splayTree.writeComps(compWriter, splayTree.swapIndicator, n);
          splayTree.swapIndicator = 0;
        }
      }
      compWriter.flush();
      compWriter.close();
    } catch(IOException ex) {
      System.out.println(ex);
    }


    // for(int i = 1; i <= n; i++) {
    //   System.out.println("insert " + i);
    //   splayTree.insert(i);
    //   splayTree.printBST(i);
    //   System.out.println();
    // }

    // for(int i = 1; i <= n; i++) {
    //   // int deletedKey = random.nextInt(1, 51);
    //   System.out.println("delete " + i);
    //   splayTree.delete(i);
    //   splayTree.printBST(50 - i);
    //   System.out.println();
    // }

    // for(int i = 0; i < n; i++) {
    //   int randomVal = random.nextInt(0, 2 * n);
    //   System.out.println("insert " + randomVal);
    //   splayTree.insert(randomVal);
    //   splayTree.printBST(i + 1);
    //   System.out.println();
    // }

    // for(int i = 0; i < n; i++) {
    //   int randomVal = random.nextInt(0, 2 * n);
    //   System.out.println("delete " + randomVal);
    //   System.out.println();
    //   splayTree.delete(randomVal);
    //   splayTree.printBST(n);
    //   System.out.println();
    // }

    // int size = splayTree.getHeight();
    // System.out.println("Size of red-black tree is: " + size);
    // splayTree.insert(15);
    // splayTree.insert(10);
    // splayTree.insert(17);
    // splayTree.delete(10);
    // splayTree.printBST(4);
  }

}
