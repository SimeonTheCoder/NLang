package parser;

import java.util.HashMap;

public class Linker {
    public void linkArgs(Float[] args, float[] memory) {
        for(int i = 0; i < args.length; i ++) {
            memory[i + 1 + 1536] = args[i];
        }
    }
}
