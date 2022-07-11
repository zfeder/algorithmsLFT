public class Main {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == '0')
                        state = 1;
                    else
                        state = 3;
                break;
                case 1:
                    if (ch == '1')
                        state = 3;
                    else
                        state = 2;
                    break;
                case 2:
                    if (ch == '1')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3:
                    if (ch == '1')
                        state = 3;
                    else
                        state = 1;
                break;
            }
        }
        return state == 1 || state ==2 || state == 3;
    }
    public static void main(String[] args) {
        System.out.println(scan("010101") ? "NOPE" : "OK");
        System.out.println(scan("1100011001") ? "NOPE" : "OK");
        System.out.println(scan("10214") ? "NOPE" : "OK");
    }
}
