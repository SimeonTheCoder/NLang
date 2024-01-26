package parser;

import data.WritableFile;
import nodes.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Interpreter {
    public static HashMap<String, WritableFile> files = new HashMap<>();

    public static void interpret(Node node, HashMap<String, Float> memory) throws IOException {
        if (node.instruction != null) {
            executeInstruction(node.instruction, memory);
            if(node.childNodes.size() > 0) {
                for (Node childNode : node.childNodes) interpret(childNode, memory);
            }
        } else {
            for(int l = 0; l < node.repetitions; l ++) {
                for (Node parallelNode : node.parallelNodes) interpret(parallelNode, memory);
                for (Node childNode : node.childNodes) interpret(childNode, memory);
            }
        }
    }

    public static float getValue(Object instruction, HashMap<String, Float> memory) {
        float val;

        try {
            val = (Float) instruction;
        } catch (Exception exception) {
            if(String.valueOf(instruction).startsWith("gg")) {
                int index = Math.round(
                        memory.get(
                                String.format("g%d", (String.valueOf(instruction).charAt(2) - '0'))
                        )
                );

                val = memory.get(String.format("g%d", index));
            } else {
                val = memory.get((String) instruction);
            }
        }

        return val;
    }

    public static void executeInstruction(Object[] instruction, HashMap<String, Float> memory) throws IOException {
        int opCode = (Integer) instruction[0];

        int repetitions = 0;

        try {
            repetitions = (Integer) instruction[7];
        } catch (Exception exception) {
            repetitions = Math.round(memory.get((String) instruction[7]));
        }

        for(int i = 0; i < instruction.length; i ++) {
            if(String.valueOf(instruction[i]).equals("%gg")) {

            }
        }

        for(int l = 0; l < repetitions; l++) {
            switch (opCode) {
                //ADD
                case 0: {
                    float valA = getValue(instruction[1], memory);
                    float valB = getValue(instruction[2], memory);

                    float result = valA + valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //SUB
                case 1: {
                    float valA = getValue(instruction[1], memory);
                    float valB = getValue(instruction[2], memory);

                    float result = valA - valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //MUL
                case 2: {
                    float valA = getValue(instruction[1], memory);
                    float valB = getValue(instruction[2], memory);

                    float result = valA * valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //DIV
                case 3: {
                    float valA = getValue(instruction[1], memory);
                    float valB = getValue(instruction[2], memory);

                    float result = valA / valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //SET
                case 4: {
                    float val = getValue(instruction[2], memory);

                    if(String.valueOf(instruction[1]).startsWith("gg")) {
                        int index = Math.round(memory.get(
                                String.format("g%d", String.valueOf(instruction[1]).charAt(2) - '0')
                        ));

                        memory.put(String.format("g%d", index), val);
                    } else {
                        memory.put((String) instruction[1], val);
                    }

                    break;
                }

                //PRINT
                case 5: {
                    float val = getValue(instruction[1], memory);

                    if (val == Math.round(val)) {
                        System.out.print((int) val);
                    } else {
                        System.out.print(val);
                    }

                    break;
                }

                case 6: {
                    Scanner scanner = new Scanner(System.in);

                    float val = Float.parseFloat(scanner.nextLine());

                    memory.put((String) instruction[8], val);

                    break;
                }

                case 7: {
                    interpret((Node) instruction[1], memory);

                    break;
                }

                case 8: {
                    float valA = getValue(instruction[1], memory);
                    float valB = getValue(instruction[2], memory);

                    switch ((Integer) instruction[3]) {
                        case 0:
                            if (valA == valB) {
                                interpret((Node) instruction[4], memory);
                            } else {
                                interpret((Node) instruction[5], memory);
                            }

                            break;

                        case 1:
                            if (valA > valB) {
                                interpret((Node) instruction[4], memory);
                            } else {
                                interpret((Node) instruction[5], memory);
                            }

                            break;

                        case 2:
                            if (valA < valB) {
                                interpret((Node) instruction[4], memory);
                            } else {
                                interpret((Node) instruction[5], memory);
                            }

                            break;

                        case 3:
                            if (valA >= valB) {
                                interpret((Node) instruction[4], memory);
                            } else {
                                interpret((Node) instruction[5], memory);
                            }

                            break;

                        case 4:
                            if (valA <= valB) {
                                interpret((Node) instruction[4], memory);
                            } else {
                                interpret((Node) instruction[5], memory);
                            }

                            break;

                        case 5:
                            if (valA != valB) {
                                interpret((Node) instruction[4], memory);
                            } else {
                                interpret((Node) instruction[5], memory);
                            }

                            break;
                    }

                    float result = valA + valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //PRINTLN
                case 9: {
                    float val = getValue(instruction[1], memory);

                    if (val == Math.round(val)) {
                        System.out.println((int) val);
                    } else {
                        System.out.println(val);
                    };

                    break;
                }

                //ALLOC
                case 10: {
                    float val = getValue(instruction[1], memory);

                    for(int i = 1; i <= Math.round(val) + 1; i++) {
                        String key = String.format("g%d", i);

                        if(memory.containsKey(key)) continue;

                        memory.put(key, 0f);
                    }

                    break;
                }

                //WRITE
                case 11: {
                    String filename = String.valueOf(instruction[1]);
                    float val = getValue(instruction[2], memory);

                    files.get(filename).content.append(val).append(System.lineSeparator());

                    break;
                }

                //MKFILE
                case 12: {
                    String filename = String.valueOf(instruction[1]);

                    files.put(
                            filename,
                            new WritableFile(
                                    new File(filename)
                            )
                    );

                    break;
                }

                //CLOSE
                case 13: {
                    String filename = String.valueOf(instruction[1]);

                    FileWriter writer = new FileWriter(filename);

                    writer.write(files.get(filename).content.toString());
                    writer.close();

                    break;
                }
            }
        }
    }
}
