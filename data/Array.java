package data;

public class Array {
    public int start;
    public int end;

    public int length;

    public Array(int start, int length) {
        this.start = start;
        this.length = length;

        this.end = this.start + this.length;
    }

    public int index(int index) {
        return this.start + index;
    }
}
