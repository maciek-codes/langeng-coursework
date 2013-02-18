public class Reg {

  static int num = 1;

  static public String newReg() {
    num++;
    return "R"+String.valueOf(num);
  }
}
