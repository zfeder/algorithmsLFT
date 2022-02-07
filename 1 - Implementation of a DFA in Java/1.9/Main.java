public class Main {
    public static boolean scan(String s) {
        System.out.print(s);
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == '/')
                        state = 1;
                    else
                        state = -1;
                    System.out.print("Case 0: " + state + "\n");
                    break;
                case 1:
                    if (ch == '*')
                        state = 2;
                    else
                        state = -1;
                    System.out.print("Case 1: " + state + "\n");
                    break;
                case 2:
                    if (ch == 'a')
                        state = 5;
                    else if (ch == '*')
                        state = 3;
                    else
                        state = -1;
                    System.out.print("Case 2: " + state + "\n");
                    break;
                case 3:
                    if (ch == '*')
                        state = 3;
                    else if (ch == '/')
                        state = 4;
                    else if (ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    System.out.print("Case 3: " + state + "\n");
                    break;
                case 4:
                    state = -1;
                    System.out.print("Case 4: " + state + "\n");
                    break;
                case 5:
                    if ((ch == 'a') || (ch == '/'))
                        state = 5;
                    else if (ch == '*')
                        state = 3;
                    else
                        state = -1;
                    System.out.print("Case 5: " + state + "\n");
                    break;
            }
        }
        return (state == 4);
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
