package utils;

import memory.MemoryManager;

public class StringTools {
    public static int indentation(String s) {
        int offset = 0;

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) != ' ' && s.charAt(i) != '\t') break;
            offset+=4;
        }

        return offset;
    }

    public static int extractParentPath(String s) {
        int parentLevel = 0;
        int slot;

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '.') {
                parentLevel ++;
            } else {
                slot = s.charAt(i) - '0';
                return parentLevel * MemoryManager.NODE_SLOTS + slot;
            }
        }

        return parentLevel * MemoryManager.NODE_SLOTS;
    }
}
