// Label.java
// Helps to keep track of labels used
public class Label {

  static int count = 0;

  static public String newLabel() {
    String result = "L"+String.valueOf(count);
    count = count + 1;
    return result;
  }
}
