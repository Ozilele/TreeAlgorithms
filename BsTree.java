import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

class Node {
  int key;
  Node left, right;

  public Node(int item) {
    key = item;
    left = right = null;
  }
}

public class BsTree {

  private Node root;
  private char[] leftTrace;
  public int swapIndicator = 0;
  public int comparisionCounter = 0;
  public static int arr[];
  private char[] rightTrace;

  public BsTree() {
    root = null;
  }

  public BsTree(int value) {
    root = new Node(value);
  }

  public void delete(int key) {
    root = deleteKey(root, key);
  }

  public void insert(int key) {
    root = insertKey(root, key);
  }
  
  public int getHeight() {
    return calculateTreeHeight(root);
  }
  
  private Node deleteKey(Node root, int key) {
    if(root == null) { // empty tree
      return root;
    }
    else if(key < root.key) {
      comparisionCounter++;
      swapIndicator++;
      root.left = deleteKey(root.left, key);
    }
    else if(key > root.key) {
      comparisionCounter++;
      swapIndicator++;
      root.right = deleteKey(root.right, key);
    }
    else { // znaleziono węzeł z kluczem równym szukanemu kluczowi(znaleziono węzeł do usunięcia)
      if(root.left == null && root.right == null) { // Case 1: Leaf node
        swapIndicator++;
        return null;
      }
      else if(root.left != null && root.right == null) { // Case 2: One child
        swapIndicator++;
        return root.left;
      }
      else if(root.left == null && root.right != null) { // Case 2: One child
        swapIndicator++;
        return root.right;
      }
      else { // Case 3: Two children, finding successor(smallest in right subtree)
        root.key = findSuccessor(root.right);
        root.right = deleteKey(root.right, root.key); // deleting successor
      } 
    }
    return root;
  }

  private int findSuccessor(Node node) { // method for finding successor
    int minValue = node.key;
    while(node.left != null) {
      swapIndicator++;
      minValue = node.left.key;
      node = node.left;
    }
    return minValue;
  }

  private Node insertKey(Node root, int key) { // recursive function for inserting a key
    if(root == null) {
      swapIndicator++; // podstawienie wskaźnika
      root = new Node(key);
      return root;
    }
    else if(key < root.key) { // left subtree
      comparisionCounter++;
      swapIndicator++; // licznik odczytów
      root.left = insertKey(root.left, key);
    }
    else if(key > root.key) { // right subtree
      comparisionCounter++;
      swapIndicator++; // licznik odczytów
      root.right = insertKey(root.right, key);
    }
    return root; // Return the (unchanged) node pointer
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

  private int calculateTreeHeight(Node node) {
    if(node == null) // root is null, height of tree is zero
      return -1;
    // height of each subtree
    int leftHeight = calculateTreeHeight(node.left);
    int rightHeight = calculateTreeHeight(node.right);
    return Math.max(leftHeight, rightHeight) + 1;
  }

  public void printBST(int height) {
    leftTrace = new char[height];
    rightTrace = new char[height];
    printBST(root, 0, ' ');
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
    BsTree binaryTree = new BsTree();
    FileWriter compWriter = null;
    SecureRandom random = new SecureRandom();
    File compFile = new File("bst-swap-random.csv");

    // try {
    //   compWriter = new FileWriter(compFile);

    //   for(int i = 0; i < 20; i++) {
    //     for(int n = 10000; n <= 100000; n += 10000) {
    //       // arr = new int[n];
    //       for(int k = 0; k < n; k++) {
    //         int randomEl = random.nextInt(2 * n);
    //         int randomElToDelete = random.nextInt(2 * n);
    //         binaryTree.insert(randomEl); // wstawianie losowego elementu
    //         binaryTree.delete(randomElToDelete); // usuwanie losowego elementu
    //       }
    //       binaryTree.writeComps(compWriter, binaryTree.swapIndicator, n);
    //       binaryTree.swapIndicator = 0;
    //     }
    //   }
    //   compWriter.flush();
    //   compWriter.close();
    // } catch(IOException ex) {
    //   System.out.println(ex);
    // }
    // for(int i = 1; i <= n; i++) {
    //   System.out.println("insert " + i);
    //   binaryTree.insert(i);
    //   binaryTree.printBST(i);
    // }

    // for(int i = 1; i <= n; i++) {
    //   // int deletedKey = random.nextInt(1, 51);
    //   System.out.println("delete " + i);
    //   binaryTree.delete(i);
    //   binaryTree.printBST(50 - i);
    // }

    for(int i = 0; i < 50; i++) {
      int randomVal = random.nextInt(0, 2 * 50);
      System.out.println("insert " + randomVal);
      System.out.println();
      binaryTree.insert(randomVal);
      binaryTree.printBST(i + 1);
    }

    for(int i = 0; i < 50; i++) {
      int randomVal = random.nextInt(0, 2 * 50);
      System.out.println("delete " + randomVal);
      System.out.println();
      binaryTree.delete(randomVal);
      binaryTree.printBST(50);
    }


    // binaryTree.insert(50);
    // binaryTree.insert(30);
    // binaryTree.insert(20);
    // binaryTree.insert(40);
    // // binaryTree.insert(28);
    // // binaryTree.insert(35);
    // binaryTree.insert(70);
    // binaryTree.insert(60);
    // binaryTree.insert(80);
    // binaryTree.delete(60);
    // binaryTree.delete(28);
    int size = binaryTree.getHeight();
    System.out.println("Size of binary tree is: " + size);
  }

}