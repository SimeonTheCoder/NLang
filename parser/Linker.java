package parser;

import java.util.HashMap;

public class Linker {
    public void linkArgs(Float[] args, HashMap<String, Float> memory) {
        for(int i = 0; i < args.length; i ++) {
            memory.put(
                String.format("g%d", i + 1), args[i]
            );
        }
    }
}
