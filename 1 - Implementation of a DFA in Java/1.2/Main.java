public class Main {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (Character.isDigit(ch))
                        state = -1;
                    else if (Character.isLetter(ch))
                        state = 1;
                    else if(ch == '_')
                        state = 2;
                    break;
                case 1:
                    if (Character.isDigit(ch) || Character.isLetter(ch) || ch == '_')
                        state = 1;
                    else
                        state = -1;
                    break;
                case 2:
                    if (ch == '_')
                        state = 2;
                    else if (Character.isLetter(ch) || Character.isDigit(ch))
                        state = 1;
                    else
                        state = -1;
                    break;
            }
        }
        return (state == 1);
    }

    public static void main(String[] args){
        System.out.println(scan(args[0])? "OK" : "NOPE");
    }
}