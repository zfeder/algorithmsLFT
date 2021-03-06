package valutatore;

public class NumberTok extends Token {
    public int n = 0;

    public NumberTok(int tag, int n) {
        super(tag);
        this.n = n;
    }

    public String toString() {
        return "<" + tag + ", " + n + ">";
    }

}
