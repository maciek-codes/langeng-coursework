// COMS22303: IR tree

import java.util.*;

class IRTree
{
  private String op;
  private ArrayList<IRTree> sub;

// Constructors for IR tree nodes with various numbers of subtrees

  public IRTree()
  {
    this.op = op;
    sub = new ArrayList<IRTree>();
  }

  public IRTree(String op)
  {
    this.op = op;
    sub = new ArrayList<IRTree>();
  }

  public IRTree(String op, IRTree sub1)
  {
    this.op = op;
    sub = new ArrayList<IRTree>();
    sub.add(sub1);
  }

  public IRTree(String op, IRTree sub1, IRTree sub2)
  {
    this.op = op;
    sub = new ArrayList<IRTree>();
    sub.add(sub1);
    sub.add(sub2);
  }

  public IRTree(String op, IRTree sub1, IRTree sub2, IRTree sub3)
  {
    this.op = op;
    sub = new ArrayList<IRTree>();
    sub.add(sub1);
    sub.add(sub2);
    sub.add(sub3);
  }

  public IRTree(String op, IRTree sub1, IRTree sub2, IRTree sub3, IRTree sub4, IRTree sub5)
  {
    this.op = op;
    sub = new ArrayList<IRTree>();
    sub.add(sub1);
    sub.add(sub2);
    sub.add(sub3);
    sub.add(sub4);
    sub.add(sub5);
  }

// Methods to add operator and subtrees

  public void setOp(String op)
  {
    this.op = op;
  }

  public void addSub(IRTree sub1)
  {
    sub.add(sub1);
  }

// Methods to access operator and subtrees

  public String getOp()
  {
    return op;
  }

  public IRTree getSub(int i)
  {
    if (i >= sub.size()) {
      System.out.println("IRTree error accessing subtree "+i+" of "+op+" node");
    }
    return sub.get(i);
  }

  public IRTree setSub(int i, IRTree newSub)
  {
    if (i >= sub.size()) {
      System.out.println("IRTree error accessing subtree "+i+" of "+op+" node");
    }
    return sub.set(i, newSub);
  }

// toString

  public String toString()
  {
    int i;
    if (sub.size() == 0) {
      return op;
    }
    String s = "("+op;
    for (i=0; i<sub.size(); i++) {
      s += " "+sub.get(i).toString();
    }
    s += ")";
    return s;
  }
}
