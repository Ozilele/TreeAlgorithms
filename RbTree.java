import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

enum Color {
  RED,
  BLACK
}

class Node { // Node class in Red-Black Tree
  int key;
  Node parent;
  Node left, right;
  Color color; // store the color of particular node

  public Node(int item) {
    key = item;
    color = Color.RED;
    left = right = null; 
    parent = null;
  }
}

public class RbTree {

  private Node root;
  public int swapIndicator = 0;
  public int comparisionCounter = 0;
  private char[] leftTrace;
  private char[] rightTrace;
  
  public RbTree() {
    root = null;
  }

  public void insert(int key) {
    Node newNode = new Node(key);

    Node y = null;
    Node x = this.root;

    while(x != null) {
      y = x;
      comparisionCounter++;
      if(newNode.key < x.key) {
        swapIndicator++;
        x = x.left; // left subtree
      } else {
        swapIndicator++;
        x = x.right; // right subtree
      } 
    }

    newNode.parent = y; // ustawienie parenta dla wstawianego node
    if(y == null) { // ustawienie korzenia
      root = newNode;
    } else if(newNode.key < y.key) { // lewe dziecko
      y.left = newNode;
    } else { // prawe dziecko
      y.right = newNode;
    }

    if(newNode.parent == null) {
      newNode.color = Color.BLACK;
      return;
    }

    if(newNode.parent.parent == null) {
      return;
    }

    fixInsertion(newNode);
  }

  private void fixInsertion(Node node) {
    Node uncle;
    while(node.parent.color == Color.RED) { // if the parent of new node is Red, check the color of the parent's sibling of a new node
      if(node.parent == node.parent.parent.right) {
        uncle = node.parent.parent.left; // sibling of parent
        
        if(uncle != null && uncle.color == Color.RED) {
          uncle.color = Color.BLACK;
          node.parent.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          node = node.parent.parent; // reassigning as new node
        } else {
          if(node == node.parent.left) { // RL relationship
            node = node.parent;
            rotateRight(node);
          }
          node.parent.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          rotateLeft(node.parent.parent); // RR relationship
        }
      } else {
        uncle = node.parent.parent.right;

        if(uncle != null && uncle.color == Color.RED) {
          uncle.color = Color.BLACK;
          node.parent.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          node = node.parent.parent;
        } else { // there is no uncle
          if(node == node.parent.right) { // LR relationship
            node = node.parent;
            rotateLeft(node);
          }
          node.parent.color = Color.BLACK; 
          node.parent.parent.color = Color.RED;
          rotateRight(node.parent.parent); // LL relationship
        }
      }
      if(node == root) {
        break;
      }
      swapIndicator += 7; // 6 podstawień wskaźników w kazdej iteracji pętli while
    }

    root.color = Color.BLACK; // Black color for the root
  }

  private void rotateRight(Node node) {
    Node leftChild = node.left; // 16
    if(leftChild != null) {
      node.left = leftChild.right; // null
    }

    if(leftChild != null && leftChild.right != null) {
      leftChild.right.parent = node;
    }

    if(leftChild != null) {
      leftChild.parent = node.parent; // 10
    }

    if(node.parent == null) {
      this.root = leftChild;
    } else if(node == node.parent.right) {
      node.parent.right = leftChild;
    } else {
      node.parent.left = leftChild; // 16
    }

    if(leftChild != null) {
      leftChild.right = node; // 18
    }
    node.parent = leftChild; // 16
  }

  private void rotateLeft(Node node) { // function for left rotation(moving upward)
    Node rightChild = node.right; // 25
    if(rightChild != null) {
      node.right = rightChild.left; // null
    }

    if(rightChild != null && rightChild.left != null) {
      rightChild.left.parent = node; // new node
    }

    if(rightChild != null) {
      rightChild.parent = node.parent; // 16
    }
    
    if(node.parent == null) {
      this.root = rightChild;
    } else if(node == node.parent.left) {
      node.parent.left = rightChild; 
    } else {
      node.parent.right = rightChild;
    }
    
    if(rightChild != null) {
      rightChild.left = node;
    }
    node.parent = rightChild;
  }


  public void delete(int key) {
    Node nodeToDelete = searchNode(key);
    if(nodeToDelete == null) { // Węzeł do usunięcia nie istnieje
      return;
    }

    Node nodeToFix = null;
    Color originalColor = nodeToDelete.color;

    if(nodeToDelete.left == null) {
      nodeToFix = nodeToDelete.right;
      swapIndicator += 2;
      transplant(nodeToDelete, nodeToDelete.right);
    } else if(nodeToDelete.right == null) {
      nodeToFix = nodeToDelete.left;
      swapIndicator += 2;
      transplant(nodeToDelete, nodeToDelete.left);
    } else { // Case 3: Finding successor in case nodeToDelete has 2 children
      Node successor = findSuccessor(nodeToDelete.right);
      originalColor = successor.color;
      nodeToFix = successor.right;

      if(successor.parent != nodeToDelete) {
        swapIndicator += 2;
        transplant(successor, successor.right);
        successor.right = nodeToDelete.right;
        successor.right.parent = successor;
      }

      swapIndicator += 2;
      transplant(nodeToDelete, successor); // setting new parent of the successor node
      successor.left = nodeToDelete.left; // setting left child of successor, setting the same color
      successor.left.parent = successor;
      successor.color = nodeToDelete.color;
    }

    if(originalColor == Color.BLACK) {
      if(nodeToFix != null) {
        swapIndicator++;
        deleteFixup(nodeToFix);
      } else if(nodeToDelete.parent != null) {
        swapIndicator++;
        deleteFixup(nodeToDelete.parent);
      }
    }
  }

  private void deleteFixup(Node node) { // Metoda do naprawy równowagi i zachowania właściwości drzewa RB
    while(node != root && node != null && node.color == Color.BLACK) {
      if(node == node.parent.left) {
        Node sibling = node.parent.right;

        if(sibling == null) {
          break;
        }

        if(sibling.color == Color.RED) { // sibling is red
          sibling.color = Color.BLACK;
          node.parent.color = Color.RED;
          rotateLeft(node.parent);
          swapIndicator += 2;
          sibling = node.parent.right;
        }

        swapIndicator += 3;
        if(sibling != null && sibling.left != null && sibling.right != null && sibling.left.color == Color.BLACK && sibling.right.color == Color.BLACK ) {
          sibling.color = Color.RED;
          node = node.parent;
        } else {
          swapIndicator++;
          if(sibling != null) {
            if(sibling.right == null || sibling.right.color == Color.BLACK) {
              if(sibling.left != null) {
                sibling.left.color = Color.BLACK;
                swapIndicator++;
              }
              sibling.color = Color.RED;
              rotateRight(sibling);
              sibling = node.parent.right;
              swapIndicator += 2;
            }
          }
          swapIndicator += 2;
          if(sibling != null && node.parent != null) {
            sibling.color = node.parent.color;
            node.parent.color = Color.BLACK;
          }
          swapIndicator++;
          if(sibling != null && sibling.right != null) {
            sibling.right.color = Color.BLACK;
          }

          rotateLeft(node.parent);
          swapIndicator++;
          node = root;
        }
      } else {
        Node sibling = node.parent.left;

        if(sibling == null) {
          break;
        }

        if(sibling.color == Color.RED) {
          sibling.color = Color.BLACK;
          node.parent.color = Color.RED;
          rotateRight(node.parent);
          swapIndicator += 2;
          sibling = node.parent.left;
        }
        swapIndicator += 3;
        if(sibling != null && sibling.right != null && sibling.left != null && sibling.right.color == Color.BLACK && sibling.left.color == Color.BLACK) {
          sibling.color = Color.RED;
          node = node.parent;
        } else {
          swapIndicator++;
          if(sibling != null) {
            if(sibling.left == null || sibling.left.color == Color.BLACK) {
              if(sibling.right != null) {
                sibling.right.color = Color.BLACK;
                swapIndicator++;
              }
              sibling.color = Color.RED;
              if(sibling != null) {
                rotateLeft(sibling);
                swapIndicator++;
              }
              sibling = node.parent.left;
            }
          }

          swapIndicator += 2;
          if(sibling != null && node.parent != null) {
            sibling.color = node.parent.color;
            node.parent.color = Color.BLACK;
          }

          // sibling.color = node.parent.color;
          // node.parent.color = Color.BLACK;
          swapIndicator++;
          if(sibling != null && sibling.left != null) {
            sibling.left.color = Color.BLACK;
          }

          rotateRight(node.parent);
          swapIndicator++;
          // sibling.left.color = Color.BLACK;
          node = root;
        } 
      }
    }

    if(node != null) {
      node.color = Color.BLACK;
    }
  }

  private void transplant(Node u, Node v) {
    if(u.parent == null) {
      root = v;
    } else if(u == u.parent.left) {
      u.parent.left = v;
    } else {
      u.parent.right = v;
    }

    if(v != null) {
      v.parent = u.parent;
    }
  }

  private Node findSuccessor(Node node) {
    while(node.left != null) {
      node = node.left;
    }
    return node;
  }

  public Node searchNode(int key) {
    return searchNode(root, key);
  }

  private Node searchNode(Node node, int key) { // procedura do wyszukiwania węzła
    if(node == null || node.key == key) {
      comparisionCounter++;
      return node;
    }

    comparisionCounter++;
    if(key < node.key) {
      if(node.left != null) {
        return searchNode(node.left, key);
      }
    } else {
      if(node.right != null) {
        return searchNode(node.right, key);
      }
    }
    return null;
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
    RbTree rbTree = new RbTree();
    SecureRandom random = new SecureRandom();
    FileWriter compWriter = null;
    int counter = 0;
    File compFile = new File("rb-bst-swap-sorted.csv");

    // try {
    //   compWriter = new FileWriter(compFile);

    //   for(int i = 0; i < 20; i++) {
    //     for(int n = 10000; n <= 100000; n += 10000) {
    //       // arr = new int[n];
    //       for(int k = 0; k < n; k++) {
    //         // int randomEl = random.nextInt(2 * n);
    //         int randomElToDelete = random.nextInt(2 * n);
    //         rbTree.insert(k); // wstawianie rosnących elementów
    //         rbTree.delete(randomElToDelete); // usuwanie losowego elementu
    //       }
    //       rbTree.writeComps(compWriter, rbTree.swapIndicator, n);
    //       rbTree.swapIndicator = 0;
    //     }
    //   }
    //   compWriter.flush();
    //   compWriter.close();
    // } catch(IOException ex) {
    //   System.out.println(ex);
    // }
    // int n = 50;

    // for(int i = 0; i < n; i++) {
    //   rbTree.insert(i);
    //   rbTree.printBST(i + 1);
    //   System.out.println();
    // }

    // for(int i = 0; i < n; i++) {
    //   int randomVal = random.nextInt(0, 2 * n);
    //   System.out.println("insert " + randomVal);
    //   rbTree.insert(randomVal);
    //   rbTree.printBST(i + 1);
    //   System.out.println();
    // }

    // for(int i = 0; i < n; i++) {
    //   int randomVal = random.nextInt(0, 2 * n);
    //   System.out.println("delete " + randomVal);
    //   System.out.println();
    //   rbTree.delete(randomVal);
    //   rbTree.printBST(n);
    //   System.out.println();
    // }

    rbTree.insert(10);
    rbTree.insert(5);
    rbTree.insert(16);
    rbTree.insert(2);
    rbTree.insert(9);
    rbTree.insert(25);
    rbTree.insert(40);
    rbTree.insert(38);
    rbTree.delete(30);
    rbTree.delete(15);
    rbTree.delete(1);

    rbTree.printBST(9);
    int size = rbTree.getHeight();
    System.out.println("Size of red-black tree is: " + size);
  }

}
