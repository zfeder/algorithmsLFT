public class Main6 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 1;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 2;
                    else
                        state = -1;
               //     System.out.print("Case 0: " + state + "\n");
                    break;
                case 1:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 3;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 4;
                    else
                        state = -1;
               //     System.out.print("Case 1: " + state + "\n");
                    break;
                case 2:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 5;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 6;
                    else
                        state = -1;
               //     System.out.print("Case 2: " + state + "\n");
                    break;
                case 3:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 3;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 4; 
                    else if(ch >= 'A' && ch <= 'K')
                        state = 8;
                    else
                        state = -1;
               //     System.out.print("Case 3: " + state + "\n");
                    break;
                case 4:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 5;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 6; 
                    else if(ch >= 'A' && ch <= 'K')
                        state = 8;
                    else
                        state = -1;
              //      System.out.print("Case 4: " + state + "\n");
                    break;
                case 5:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 3;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 4; 
                    else if(ch >= 'L' && ch <= 'Z')
                        state = 7;
                    else
                        state = -1;
             //       System.out.print("Case 5: " + state + "\n");
                    break;
                case 6:
                    if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 5;
                    else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 6; 
                    else if(ch >= 'L' && ch <= 'Z')
                        state = 7;
                    else
                        state = -1;
             //       System.out.print("Case 6: " + state + "\n");
                    break;
                case 7:
                    if (ch >= 'a' && ch <= 'z')
                        state = 7;
                    else
                        state = -1;
            //        System.out.print("Case 7: " + state + "\n");
                    break;
                case 8:
                    if (ch >= 'a' && ch <= 'z')
                        state = 8;
                    else
                        state = -1;
           //         System.out.print("Case 8: " + state + "\n");
                    break;
            }
        }
        return (state == 7) || (state == 8);
    }

    public static void main(String[] args) {

        System.out.println(scan("123456Bianchi") ? "OK" : "NOPE");
        System.out.println(scan("654321Rossi") ? "OK" : "NOPE");
        System.out.println(scan("654321Bianchi") ? "OK" : "NOPE");
        System.out.println(scan("123456Rossi") ? "OK" : "NOPE");
        System.out.println(scan("2Bianchi") ? "OK" : "NOPE");
        System.out.println(scan("122B") ? "OK" : "NOPE");
        System.out.println(scan("654322") ? "OK" : "NOPE");
        System.out.println(scan("Rossi") ? "OK" : "NOPE");
    }
}