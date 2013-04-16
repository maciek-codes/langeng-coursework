

public class Reg {

  static int num = 1;

  static public String newReg() {
    String result = "R"+String.valueOf(num);
    num = num + 1;
    return result;
  }

  static public void releaseLast() {
    num = num - 1;
  }
}
