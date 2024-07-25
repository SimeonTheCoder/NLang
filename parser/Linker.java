package parser;

import memory.MemoryManager;

public class Linker {
    public void linkArgs(Float[] args, float[] memory) {
        for(int i = 0; i < args.length; i ++)
            memory[i + 1 + MemoryManager.LOCAL_AMOUNT] = args[i];
    }
}
