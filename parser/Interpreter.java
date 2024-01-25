package parser;

import nodes.Node;

import java.util.HashMap;
import java.util.Scanner;

public class Interpreter {
    public static void interpret(Node node, HashMap<String, Float> memory) {
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

    public static void executeInstruction(Object[] instruction, HashMap<String, Float> memory) {
        int opCode = (Integer) instruction[0];

        int repetitions = 0;

        try {
            repetitions = (Integer) instruction[7];
        } catch (Exception exception) {
            repetitions = Math.round(memory.get((String) instruction[7]));
        }

        for(int l = 0; l < repetitions; l++) {
            switch (opCode) {
                //ADD
                case 0: {
                    float valA;

                    try {
                        valA = (Float) instruction[1];
                    } catch (Exception exception) {
                        valA = memory.get((String) instruction[1]);
                    }

                    float valB;

                    try {
                        valB = (Float) instruction[2];
                    } catch (Exception exception) {
                        valB = memory.get((String) instruction[2]);
                    }

                    float result = valA + valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //SUB
                case 1: {
                    float valA;

                    try {
                        valA = (Float) instruction[1];
                    } catch (Exception exception) {
                        valA = memory.get((String) instruction[1]);
                    }

                    float valB;

                    try {
                        valB = (Float) instruction[2];
                    } catch (Exception exception) {
                        valB = memory.get((String) instruction[2]);
                    }

                    float result = valA - valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //MUL
                case 2: {
                    float valA;

                    try {
                        valA = (Float) instruction[1];
                    } catch (Exception exception) {
                        valA = memory.get((String) instruction[1]);
                    }

                    float valB;

                    try {
                        valB = (Float) instruction[2];
                    } catch (Exception exception) {
                        valB = memory.get((String) instruction[2]);
                    }

                    float result = valA * valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                //DIV
                case 3: {
                    float valA;

                    try {
                        valA = (Float) instruction[1];
                    } catch (Exception exception) {
                        valA = memory.get((String) instruction[1]);
                    }

                    float valB;

                    try {
                        valB = (Float) instruction[2];
                    } catch (Exception exception) {
                        valB = memory.get((String) instruction[2]);
                    }

                    float result = valA / valB;
                    memory.put((String) instruction[8], result);

                    break;
                }

                case 4: {
                    float val;

                    try {
                        val = (Float) instruction[2];
                    } catch (Exception exception) {
                        val = memory.get((String) instruction[2]);
                    }

                    memory.put((String) instruction[1], val);

                    break;
                }

                //PRINT
                case 5: {
                    float val;

                    try {
                        val = (Float) instruction[1];
                    } catch (Exception exception) {
                        val = memory.get((String) instruction[1]);
                    }

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
                    float valA;

                    try {
                        valA = (Float) instruction[1];
                    } catch (Exception exception) {
                        valA = memory.get((String) instruction[1]);
                    }

                    float valB;

                    try {
                        valB = (Float) instruction[2];
                    } catch (Exception exception) {
                        valB = memory.get((String) instruction[2]);
                    }

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
                    float val;

                    try {
                        val = (Float) instruction[1];
                    } catch (Exception exception) {
                        val = memory.get((String) instruction[1]);
                    }

                    if (val == Math.round(val)) {
                        System.out.println((int) val);
                    } else {
                        System.out.println(val);
                    };

                    break;
                }
            }
        }
    }
}
