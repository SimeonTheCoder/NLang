package memory;

public class MemoryManager {
    public static int TOTAL_AMOUNT;
    public static int GLOBAL_AMOUNT;
    public static int LOCAL_AMOUNT;
    public static int NODE_SLOTS;
    public static int ARRAY_AMOUNT;

    public static int ARR_OFFSET;

    public static void set(int totalAmount, int globalAmount, int nodeSlots, int arrayAmount) {
        TOTAL_AMOUNT = totalAmount;
        GLOBAL_AMOUNT = globalAmount;
        ARRAY_AMOUNT = arrayAmount;

        LOCAL_AMOUNT = TOTAL_AMOUNT - GLOBAL_AMOUNT - ARRAY_AMOUNT;

        ARR_OFFSET = LOCAL_AMOUNT + GLOBAL_AMOUNT;

        NODE_SLOTS = nodeSlots;
    }
}
