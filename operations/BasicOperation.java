package operations;

import data.Array;
import data.ObjType;
import data.ReadableFile;
import data.WritableFile;
import memory.MemoryManager;
import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum BasicOperation implements Operation {
    ADD {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA + valB;
            memory[(Integer) instruction[8]] = result;
        }

        @Override
        public String help() {
            return "Calculates the sum arg0 + arg1";
        }
    },
    SUB {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA - valB;
            memory[(Integer) instruction[8]] = result;
        }

        @Override
        public String help() {
            return "Calculates the difference arg0 - arg1";
        }
    },
    MUL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA * valB;
            memory[(Integer) instruction[8]] = result;
        }

        @Override
        public String help() {
            return "Calculates the product arg0 * arg1";
        }
    },
    DIV {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA / valB;
            memory[(Integer) instruction[8]] = result;
        }

        @Override
        public String help() {
            return "Calculates arg0 / arg1";
        }
    },
    SET {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            float val = Interpreter.getValue(instruction[2], memory);

            int address = (instruction[1] instanceof Float) ? (int) Math.floor((Float) instruction[1]) : (Integer) instruction[1];

            if (address > MemoryManager.TOTAL_AMOUNT) {
                address -= MemoryManager.TOTAL_AMOUNT;
                address = (int) Math.floor(memory[address]) + MemoryManager.LOCAL_AMOUNT;
            } else if (address < MemoryManager.LOCAL_AMOUNT) {
                address = (int) Math.floor(memory[address]);
            }

            memory[address] = val;
        }

        @Override
        public String help() {
            return "The value of the arg0 is set to arg1";
        }
    },
    PRINT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            System.out.print(Interpreter.getValue(instruction[1], memory));
        }

        @Override
        public String help() {
            return "Prints a number, without putting a new line at the end";
        }
    }, PRINTLN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            System.out.println(Interpreter.getValue(instruction[1], memory));
        }

        @Override
        public String help() {
            return "Prints a number, putting a new line at the end";
        }
    },
    INP {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            Scanner scanner = new Scanner(System.in);
            memory[(Integer) instruction[8]] = Float.parseFloat(scanner.nextLine());
        }

        @Override
        public String help() {
            return "Reads a number from the console";
        }
    },
    CALL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.FUNCTION};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            if (instruction[1] == null) throw new IllegalArgumentException("Function does not exist.");
            Interpreter.interpret((Node) instruction[1], memory);
        }

        @Override
        public String help() {
            return "Calls a the function under the name of arg0";
        }
    },
    IF {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{
                    ObjType.NUMBER, ObjType.ENUM, ObjType.NUMBER, ObjType.FUNCTION, ObjType.FUNCTION
            };
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[3], memory);
//
//            if(instruction[4] instanceof Integer || instruction[5] instanceof Integer) {
//                if(instruction[4] instanceof Integer) {
//                    instruction[4] =
//                } else {
//
//                }
//            }

            int choice;

            switch ((Integer) instruction[2]) {
                case 0 -> choice = valA == valB ? 4 : 5;
                case 1 -> choice = valA > valB ? 4 : 5;
                case 2 -> choice = valA < valB ? 4 : 5;
                case 3 -> choice = valA >= valB ? 4 : 5;
                case 4 -> choice = valA <= valB ? 4 : 5;
                case 5 -> choice = valA != valB ? 4 : 5;
                default -> throw new IllegalArgumentException("Invalid comparison");
            }

            if (((Node) instruction[choice]).instruction[0] == BasicOperation.NULL) {
                return;
            }

            Interpreter.interpret((Node) instruction[choice], memory);
        }

        @Override
        public String help() {
            return "Compares arg0 and arg1 based on arg2. Valid values for arg2 are:\n" +
                    "== (EQUAL)\n" +
                    "> (GREATER_THAN)\n" +
                    "< (LESS_THAN)\n" +
                    ">= (GREATER_EQUAL)\n" +
                    "<= (LESS_EQUAL)\n" +
                    "!= (NOT_EQUAL)\n" +
                    "If the result of the comparison is true, the function arg3 is called, otherwise - arg4";
        }
    },
    LN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            System.out.println();
        }

        @Override
        public String help() {
            return "Prints a new line";
        }
    },
    PRINTSTR {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            String str = ((String) instruction[1]);

            if (((String) instruction[1]).startsWith("#")) {
                int stringIndex = Integer.parseInt(((String) instruction[1]).substring(1));
                str = stringTable[stringIndex];
            } else if(((String) instruction[1]).startsWith("%")) {
                int stringIndex = (int) memory[(int) Integer.parseInt(((String) instruction[1]).substring(1)) + MemoryManager.LOCAL_AMOUNT];
                str = stringTable[stringIndex];
            }

            System.out.print(str.replace("_", " "));
        }

        @Override
        public String help() {
            return "Prints a string to the console";
        }
    },
    //    ALLOC {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[] {ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
//                throws IOException {
//            float val = Interpreter.getValue(instruction[1], memory);
//
//            if(val <= 0) throw new IllegalArgumentException("ALLOC supports only positive values");
//
//            for (int i = 1; i <= Math.round(val) + 1; i++) {
//                String key = "g" + i;
//
//                if (memory.containsKey(key)) continue;
//
//                memory.put(key, 0f);
//            }
//        }
//    },
    WRITE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException {
            String filename = String.valueOf(instruction[1]);
            float val = Interpreter.getValue(instruction[2], memory);

            writableFiles.get(filename).content.append(val).append(System.lineSeparator());
        }

        @Override
        public String help() {
            return "Appends arg1 to a file with filename arg0. Does nothing unless a file was made first using MKFILE and closed with CLOSE after the writing is finished";
        }
    },
    MKFILE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException {
            String filename = String.valueOf(instruction[1]);

            writableFiles.put(
                    filename, new WritableFile(new File(filename))
            );
        }

        @Override
        public String help() {
            return "Creates a file with a filename arg0";
        }
    },
    CLOSE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException {
            String filename = String.valueOf(instruction[1]);

            if (writableFiles.containsKey(filename)) {
                FileWriter writer = new FileWriter(filename);

                if (!writableFiles.containsKey(filename))
                    throw new IllegalArgumentException("File " + filename + " isn't open");

                writer.write(writableFiles.get(filename).content.toString());
                writer.close();
            } else if (readableFiles.containsKey(filename)) {
                readableFiles.get(filename).scanner.close();
            }
        }

        @Override
        public String help() {
            return "Closes a file for writing and reading. Cannot do that unless a file has already been opened / made";
        }
    },
    OPEN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException {
            String filename = String.valueOf(instruction[1]);

            readableFiles.put(
                    filename, new ReadableFile(new File(filename))
            );
        }

        @Override
        public String help() {
            return "Opens a file with filename arg0 for reading";
        }
    },
    READLINE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING, ObjType.ENUM};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException {
            if ((Integer) instruction[2] != 6) return;

            String filename = String.valueOf(instruction[1]);

            if (!readableFiles.containsKey(filename))
                throw new IllegalArgumentException("File " + filename + " isn't open");

            float data = Float.parseFloat(
                    readableFiles.get(String.valueOf(instruction[1])).scanner.nextLine()
            );

            memory[(Integer) instruction[8]] = data;
        }

        @Override
        public String help() {
            return "Reads a single line from an already opened file with filename arg0. Arg1 specifies what value should be read. The only valid value for arg1 is NUMBER";
        }
    },
    IMPORT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING, ObjType.STRING, ObjType.MULTIPLE};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable)
                throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            float[] memory2 = new float[MemoryManager.TOTAL_AMOUNT];

            List<String> args = new ArrayList<>();

            for (int i = 3; i < 7; i++) {
                if (instruction[i] == null) continue;
                args.add(String.valueOf(Interpreter.getValue(instruction[i], memory)));
            }

            Parser parser = new Parser();
            parser.parse(new File(instruction[1] + ".nlp"), memory2);

            Linker linker = new Linker();

            linker.linkArgs(
                    args.stream().map(Float::parseFloat)
                            .toList()
                            .toArray(new Float[]{}),
                    memory2
            );

            try {
                Interpreter.interpret(
                        parser.functions.get(String.valueOf(instruction[2])),
                        memory2
                );
            } catch (Exception e) {
                throw new IllegalArgumentException("Function " + instruction[2] + " doesn't exist");
            }

            memory[(Integer) instruction[8]] = memory2[1 + MemoryManager.LOCAL_AMOUNT];
        }

        @Override
        public String help() {
            return "Calls the function with name arg1 from NLang file with .nlp extension and name arg0. The arguments passed down are treated as registers by the called function. The returned output by the IMPORT instruction is the value at g1 in the function's memory";
        }
    }, NULL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[0];
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            return;
        }

        @Override
        public String help() {
            return "Does absolutely nothing";
        }
    }, ARR {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            int firstFreeIndex = MemoryManager.ARR_OFFSET;

            for (Map.Entry<String, Array> entry : arrays.entrySet()) {
                firstFreeIndex = Math.max(firstFreeIndex, entry.getValue().end);
            }

            int size = (int) Math.floor(Interpreter.getValue(instruction[2], memory));

            arrays.put((String) instruction[1], new Array(firstFreeIndex, size));
        }

        @Override
        public String help() {
            return "Creates an array";
        }
    }, AT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            int index = (int) Math.floor(Interpreter.getValue(instruction[2], memory));

            float val = Interpreter.getValue(arrays.get((String) instruction[1]).index(index), memory);
            memory[(Integer) instruction[8]] = val;
        }

        @Override
        public String help() {
            return "Returns the value at the current index of the array";
        }
    }, LEN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            memory[(Integer) instruction[8]] = arrays.get((String) instruction[1]).length;
        }

        @Override
        public String help() {
            return "Returns the length of the array";
        }
    }, INDEX {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            int index = (int) Math.floor(Interpreter.getValue(instruction[2], memory));
            memory[(Integer) instruction[8]] = arrays.get((String) instruction[1]).index(index);
        }

        @Override
        public String help() {
            return "Returns the address of a certain value in the array";
        }
    }, RAND {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            Random random = new Random();

            memory[(Integer) instruction[8]] = random.nextFloat((Float) instruction[1], (Float) instruction[2]);
        }

        @Override
        public String help() {
            return "";
        }
    }, INPARR {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) {
            Scanner scanner = new Scanner(System.in);

            List<Float> content = Arrays.stream(scanner.nextLine().split(" ")).map(Float::parseFloat).toList();

            int firstFreeIndex = MemoryManager.ARR_OFFSET;

            for (Map.Entry<String, Array> entry : arrays.entrySet()) {
                firstFreeIndex = Math.max(firstFreeIndex, entry.getValue().end);
            }

            int size = content.size();

            arrays.put((String) instruction[1], new Array(firstFreeIndex, size));

            for (int i = firstFreeIndex; i < firstFreeIndex + size; i++) {
                memory[i] = content.get(i - firstFreeIndex);
            }
        }

        @Override
        public String help() {
            return "Reads an array from the console";
        }
    }, DELARR {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            arrays.remove((String) instruction[1]);
        }

        @Override
        public String help() {
            return "";
        }
    }, EXIT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[0];
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            new java.util.Scanner(System.in).nextLine();
            System.exit(0);
        }

        @Override
        public String help() {
            return "Exits the program";
        }
    }, INPSTR {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[0];
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();

            for (int i = 0; i < stringTable.length; i++) {
                if (stringTable[i] == null) {
                    stringTable[i] = str;
                    break;
                }
            }
        }

        @Override
        public String help() {
            return "Reads a string from the console and saves it into the string table";
        }
    }, SETSTR {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            int index = (int) Interpreter.getValue(instruction[1], memory);

            String str = ((String) instruction[2]);

            if (((String) instruction[2]).startsWith("#")) {
                int stringIndex = Integer.parseInt(((String) instruction[2]).substring(1));
                str = stringTable[stringIndex];
            } else if(((String) instruction[2]).startsWith("$")) {
                int stringIndex = (int) memory[Integer.parseInt(((String) instruction[2]).substring(1))];
                str = stringTable[stringIndex];
            } else if(((String) instruction[2]).startsWith("%")) {
                int stringIndex = (int) memory[Integer.parseInt(((String) instruction[2]).substring(1)) + MemoryManager.LOCAL_AMOUNT];
                str = stringTable[stringIndex];
            }

            stringTable[index] = str;
        }

        @Override
        public String help() {
            return "Sets the string with the current index to be equal to the entered string";
        }
    }, STREQU {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING, ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            String a = ((String) instruction[1]);

            if (((String) instruction[1]).startsWith("#")) {
                int stringIndex = Integer.parseInt(((String) instruction[1]).substring(1));
                a = stringTable[stringIndex];
            } else if(((String) instruction[1]).startsWith("$")) {
                int stringIndex = (int) memory[Integer.parseInt(((String) instruction[1]).substring(1))];
                a = stringTable[stringIndex];
            } else if(((String) instruction[1]).startsWith("%")) {
                int stringIndex = (int) memory[Integer.parseInt(((String) instruction[1]).substring(1)) + MemoryManager.LOCAL_AMOUNT];
                a = stringTable[stringIndex];
            }

            String b = ((String) instruction[2]);

            if (((String) instruction[2]).startsWith("#")) {
                int stringIndex = Integer.parseInt(((String) instruction[2]).substring(1));
                b = stringTable[stringIndex];
            } else if(((String) instruction[2]).startsWith("$")) {
                int stringIndex = (int) memory[Integer.parseInt(((String) instruction[2]).substring(1))];
                b = stringTable[stringIndex];
            } else if(((String) instruction[2]).startsWith("%")) {
                int stringIndex = (int) memory[Integer.parseInt(((String) instruction[2]).substring(1)) + MemoryManager.LOCAL_AMOUNT];
                b = stringTable[stringIndex];
            }

            memory[(Integer) instruction[8]] = a.equals(b) ? 1 : 0;
        }

        @Override
        public String help() {
            return "Returns 1 if 2 strings equal each other";
        }
    };
}
