package memory;

public class MemoryManager {
    public static int TOTAL_AMOUNT;
    public static int GLOBAL_AMOUNT;
    public static int LOCAL_AMOUNT;
    public static int NODE_SLOTS;

    public static void set(int totalAmount, int globalAmount, int nodeSlots) {
        TOTAL_AMOUNT = totalAmount;
        GLOBAL_AMOUNT = globalAmount;
        LOCAL_AMOUNT = TOTAL_AMOUNT - GLOBAL_AMOUNT;

        NODE_SLOTS = nodeSlots;
    }
}
