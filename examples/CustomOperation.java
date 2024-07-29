//package examples;
//
//import data.Array;
//import data.ObjType;
//import data.ReadableFile;
//import data.WritableFile;
//import memory.MemoryManager;
//import nodes.Node;
//import operations.Operation;
//import parser.Interpreter;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//import java.util.Map;
//
//public enum CustomOperation implements Operation {
//    PING {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            System.out.println("Pong!");
//        }
//
//        @Override
//        public String help() {
//            return "";
//        }
//    }, ADDNODE {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.MULTIPLE};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            CustomOperation.pointer = new Node();
//
//            int count = 0;
//            for(int i = 1; i < 9; i ++) {
//                if(instruction[i] == null) break;
//                instruction[i] = Interpreter.getValue(instruction[i], memory);
//                count++;
//            }
//
//            CustomOperation.pointer.instruction = new Object[count];
//
//            for(int i = 1; i <= count; i ++) {
//                CustomOperation.pointer.instruction[i-1] = instruction[i];
//            }
//        }
//
//        @Override
//        public String help() {
//            return "Adds a new node as a root of a graph and sets the pointer to it";
//        }
//    }, BRANCH {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            Node node = new Node();
//            node.parentNode = CustomOperation.pointer;
//
//            int count = 0;
//            for(int i = 1; i < 9; i ++) {
//                if(instruction[i] == null) break;
////                instruction[i] = Interpreter.getValue(instruction[i], memory);
//                count++;
//            }
//
//            node.instruction = new Object[count];
//
//            for(int i = 1; i <= count; i ++) {
//                node.instruction[i - 1] = (Interpreter.getValue(instruction[i], memory)) + 0f;
//            }
//
//            CustomOperation.pointer.childNodes.add(node);
//        }
//
//        @Override
//        public String help() {
//            return "Creates a branch from the pointer node";
//        }
//    }, CHILD {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            int val = (int) Interpreter.getValue(instruction[1], memory);
//            if(CustomOperation.pointer.childNodes.size() <= val) return;
//            CustomOperation.pointer = CustomOperation.pointer.childNodes.get(val);
//        }
//
//        @Override
//        public String help() {
//            return "Sets the pointer to a child of the pointer node";
//        }
//    }, PARENT {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[0];
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            if(CustomOperation.pointer.parentNode == null) return;
//            CustomOperation.pointer = CustomOperation.pointer.parentNode;
//        }
//
//        @Override
//        public String help() {
//            return "Sets the pointer to its parent node";
//        }
//    }, PRINTNODE {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.STRING};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            int firstFreeIndex = MemoryManager.ARR_OFFSET;
//
//            for (Map.Entry<String, Array> entry : arrays.entrySet()) {
//                firstFreeIndex = Math.max(firstFreeIndex, entry.getValue().end);
//            }
//
//            int size = CustomOperation.pointer.instruction.length;
//
//            Array arr = new Array(firstFreeIndex, size);
//            arrays.put((String) instruction[1], arr);
//
//            for(int i = arr.start; i < arr.end; i ++) {
//                memory[i] = (Float) CustomOperation.pointer.instruction[i - arr.start];
//            }
//        }
//
//        @Override
//        public String help() {
//            return "Returns the values, contained in the pointer node in a newly created array";
//        }
//    }, CHCOUNT {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[0];
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            memory[(Integer) instruction[8]] = CustomOperation.pointer.childNodes.size();
//        }
//
//        @Override
//        public String help() {
//            return "Returns the child count";
//        }
//    };
//
//    public static Node pointer;
//
//    public CustomOperation value(String str) {
//        return switch (str) {
//            case "PING" -> PING;
//            default -> null;
//        };
//    }
//
//    CustomOperation() {
//    }
//}