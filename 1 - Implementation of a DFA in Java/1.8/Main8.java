public class Main8 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch >= '0' && ch <= '9')
                        state = 1;
                    else if (ch == '+' || ch == '-')
                        state = 8;
                    else if (ch == '.')
                        state = 9;
                    else
                        state = -1;
                    break;
                case 1:
                    if (ch >= '0' && ch <= '9')
                        state = 1;
                    else if (ch == '.')
                        state = 2;
                    else if (ch == 'e')
                        state = 4;
                    else
                        state = -1;
                    break;
                case 2:
                    if (ch >= '0' && ch <= '9')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3:
                    if (ch >= '0' && ch <= '9')
                        state = 3;
                    else if (ch == 'e')
                        state = 4; 
                    else
                        state = -1;
                    break;
                case 4:
                    if (ch >= '0' && ch <= '9')
                        state = 5;
                    else if (ch == '+' || ch == '-')
                        state = 10;
                    else
                        state = -1;
                    break;
                case 5:
                    if (ch >= '0' && ch <= '9')
                        state = 5;
                    else if (ch == '.')
                        state = 6; 
                    else
                        state = -1;
                    break;
                case 6:
                    if (ch >= '0' && ch <= '9')
                        state = 7;
                    else
                        state = -1;
                    break;
                case 7:
                    if (ch >= '0' && ch <= '9')
                        state = 7;
                    else
                        state = -1;
                    break;
                case 8:
                    if (ch >= '0' && ch <= '9')
                        state = 1;
                    else if (ch == '.')
                        state = 2;
                    else
                        state = -1;
                    break;
                case 9:
                    if(ch >= '0' && ch <= '9')
                        state = 3;
                    else 
                        state = -1;
                    break;
                case 10 :
                    if(ch >= '0' && ch <= '9')
                        state = 5;
                    else
                        state = -1;
                    break;
            }
        }
        return (state == 1) || (state == 3) || (state == 5) || (state == 7);
    }

    public static void main(String[] args) {

        System.out.println(scan("123") ? "OK" : "NOPE");
        System.out.println(scan("123.5") ? "OK" : "NOPE");
        System.out.println(scan(".567") ? "OK" : "NOPE");
        System.out.println(scan("+7.5") ? "OK" : "NOPE");
        System.out.println(scan("-.7") ? "OK" : "NOPE");
        System.out.println(scan("67e10") ? "OK" : "NOPE");
        System.out.println(scan("1e-2") ? "OK" : "NOPE");
        System.out.println(scan("-.7e2") ? "OK" : "NOPE");
        System.out.println(scan("1e2.3") ? "OK" : "NOPE");
        System.out.println(scan(".") ? "OK" : "NOPE");
        System.out.println(scan("e3") ? "OK" : "NOPE");
        System.out.println(scan("123.") ? "OK" : "NOPE");
        System.out.println(scan("+e6") ? "OK" : "NOPE");
        System.out.println(scan("1.2.3") ? "OK" : "NOPE");
        System.out.println(scan("4e5e6") ? "OK" : "NOPE");
        System.out.println(scan("++3") ? "OK" : "NOPE");

    }
}