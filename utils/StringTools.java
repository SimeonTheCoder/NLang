package utils;

public class StringTools {
    public static int indentation(String s) {
        int offset = 0;

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == ' ') {
                offset ++;
            } else if(s.charAt(i) == '\t') {
                offset += 4;
            } else {
                break;
            }
        }

        return offset;
    }

    public static int extractParentPath(String s) {
        int parentLevel = 0;
        int slot = 0;

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '.') {
                parentLevel ++;
            } else {
                slot = s.charAt(i) - '0';
                break;
            }
        }

        return parentLevel * 10 + slot;
    }
}
