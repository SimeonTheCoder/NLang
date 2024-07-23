package operations;

import data.ObjType;
import data.ReadableFile;
import data.WritableFile;
import nodes.Node;
import parser.Interpreter;
import parser.Linker;
import parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public enum BasicOperation implements Operation{
    ADD {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA + valB;
            memory.put((String) instruction[8], result);
        }
    },
    SUB {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA - valB;
            memory.put((String) instruction[8], result);
        }
    },
    MUL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA * valB;
            memory.put((String) instruction[8], result);
        }
    },
    DIV {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[2], memory);

            float result = valA / valB;
            memory.put((String) instruction[8], result);
        }
    },
    SET {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            float val = Interpreter.getValue(instruction[2], memory);

            if (String.valueOf(instruction[1]).startsWith("gg")) {
                int index = Math.round(memory.get(
                        String.format("g%d", String.valueOf(instruction[1]).charAt(2) - '0')
                ));

                memory.put(String.format("g%d", index), val);
            } else {
                memory.put((String) instruction[1], val);
            }
        }
    },
    PRINT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            System.out.print(Interpreter.getValue(instruction[1], memory));
        }
    },
    INP {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            Scanner scanner = new Scanner(System.in);
            memory.put((String) instruction[8], Float.parseFloat(scanner.nextLine()));
        }
    },
    CALL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.FUNCTION};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            if(instruction[1] == null) throw new IllegalArgumentException("Function does not exist.");
            Interpreter.interpret((Node) instruction[1], memory);
        }
    },
    IF {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {
                    ObjType.NUMBER, ObjType.ENUM, ObjType.NUMBER, ObjType.FUNCTION, ObjType.FUNCTION
            };
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            float valA = Interpreter.getValue(instruction[1], memory);
            float valB = Interpreter.getValue(instruction[3], memory);

            switch ((Integer) instruction[2]) {
                case 0 -> Interpreter.interpret((Node) instruction[valA == valB ? 4 : 5], memory);
                case 1 -> Interpreter.interpret((Node) instruction[valA > valB ? 4 : 5], memory);
                case 2 -> Interpreter.interpret((Node) instruction[valA < valB ? 4 : 5], memory);
                case 3 -> Interpreter.interpret((Node) instruction[valA >= valB ? 4 : 5], memory);
                case 4 -> Interpreter.interpret((Node) instruction[valA <= valB ? 4 : 5], memory);
                case 5 -> Interpreter.interpret((Node) instruction[valA != valB ? 4 : 5], memory);
                default -> throw new IllegalArgumentException("Invalid comparison");
            }

            float result = valA + valB;
            memory.put((String) instruction[8], result);
        }
    },
    PRINTLN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) {
            System.out.println(Interpreter.getValue(instruction[1], memory));
        }
    },
    ALLOC {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException {
            float val = Interpreter.getValue(instruction[1], memory);

            if(val <= 0) throw new IllegalArgumentException("ALLOC supports only positive values");

            for (int i = 1; i <= Math.round(val) + 1; i++) {
                String key = String.format("g%d", i);

                if (memory.containsKey(key)) continue;

                memory.put(key, 0f);
            }
        }
    },
    WRITE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException {
            String filename = String.valueOf(instruction[1]);
            float val = Interpreter.getValue(instruction[2], memory);

            writableFiles.get(filename).content.append(val).append(System.lineSeparator());
        }
    },
    MKFILE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException {
            String filename = String.valueOf(instruction[1]);

            writableFiles.put(
                    filename, new WritableFile( new File(filename) )
            );
        }
    },
    CLOSE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException {
            String filename = String.valueOf(instruction[1]);

            if(writableFiles.containsKey(filename)) {
                FileWriter writer = new FileWriter(filename);

                if(!writableFiles.containsKey(filename)) throw new IllegalArgumentException("File " + filename + " isn't open");

                writer.write(writableFiles.get(filename).content.toString());
                writer.close();
            } else if (readableFiles.containsKey(filename)) {
                readableFiles.get(filename).scanner.close();
            }
        }
    },
    OPEN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException {
            String filename = String.valueOf(instruction[1]);

            readableFiles.put(
                    filename, new ReadableFile( new File(filename) )
            );
        }
    },
    READLINE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING, ObjType.ENUM};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException {
            if ((Integer) instruction[2] != 6) return;

            String filename = String.valueOf(instruction[1]);

            if(!readableFiles.containsKey(filename)) throw new IllegalArgumentException("File " + filename + " isn't open");

            Float data = Float.parseFloat(
                    readableFiles.get(String.valueOf(instruction[1])).scanner.nextLine()
            );

            memory.put((String) instruction[8], data);
        }
    },
    IMPORT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING, ObjType.STRING, ObjType.MULTIPLE};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles)
                throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            HashMap<String, Float> memory2 = new HashMap<>();

            List<String> args = new ArrayList<>();

            for(int i = 3; i < 7; i++) {
                if(instruction[i] == null) continue;
                args.add(String.valueOf(Interpreter.getValue(instruction[i], memory)));
            }

            Parser parser = new Parser();
            parser.parse(new File(instruction[1] + ".nlp"), memory2);

            Linker linker = new Linker();

            linker.linkArgs(
                    args.stream().map(Float::parseFloat)
                            .collect(Collectors.toList())
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

            memory.put((String) instruction[8], memory2.get("g1"));
        }
    };
}
