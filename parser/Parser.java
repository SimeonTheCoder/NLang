package parser;

import data.DataEnums;
import data.ObjType;
import data.OpCode;
import nodes.Node;
import utils.StringTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public HashMap<String, Node> functions;

    public Object[] parseInstruction(String instruction, Node node, HashMap<String, Float> memory) {
        String[] tokens = instruction.split(" ");
        OpCode opCode = OpCode.valueOf(tokens[0].toUpperCase());

        Object[] args = new Object[10];
        args[0] = opCode.ordinal();
        args[8] = String.format("a%d", node.id * 10);
        args[7] = 1;
        memory.put((String) args[8], 0f);

        boolean multiple = false;

        for (int i = 1; i <= 10; i++) {
            if(i >= tokens.length) break;
            if(!multiple && i > opCode.getArguments().length) break;

            if(!multiple) {
                switch (opCode.getArguments()[i - 1]) {
                    case NUMBER:
                    case MULTIPLE: {
                        if (tokens[i].startsWith(".")) {
                            int parentPath = StringTools.extractParentPath(tokens[i]);

                            int slot = parentPath % 10;
                            int parentLevel = parentPath / 10;

                            Node curr = new Node(node);
                            for (int j = 0; j < parentLevel; j++) curr = new Node(curr.parentNode);

                            args[i] = String.format("a%d", curr.id * 10 + slot);
                            memory.put((String) args[i], 0f);
                        } else if (tokens[i].startsWith("%")) {
                            if (tokens[i].charAt(1) >= '0' && tokens[i].charAt(1) <= '9') {
                                args[i] = String.format("g%d", tokens[i].charAt(1) - '0');
                                memory.put((String) args[i], 0f);
                            } else {
                                args[i] = String.format("gg%d", tokens[i].charAt(2) - '0');
                            }
                        } else {
                            args[i] = Float.parseFloat(tokens[i]);
                        }

                        if (opCode.getArguments()[i - 1] == ObjType.MULTIPLE) multiple = true;

                        break;
                    }

                    case FUNCTION: {
                        args[i] = functions.get(tokens[i]);
                        break;
                    }

                    case ENUM: {
                        args[i] = DataEnums.valueOf(tokens[i].toUpperCase()).ordinal();
                        break;
                    }

                    case STRING: {
                        args[i] = tokens[i];
                        break;
                    }
                }
            } else {
                if (tokens[i].startsWith(".")) {
                    int parentPath = StringTools.extractParentPath(tokens[i]);

                    int slot = parentPath % 10;
                    int parentLevel = parentPath / 10;

                    Node curr = new Node(node);
                    for (int j = 0; j < parentLevel; j++) curr = new Node(curr.parentNode);

                    args[i] = String.format("a%d", curr.id * 10 + slot);
                    memory.put((String) args[i], 0f);
                } else if (tokens[i].startsWith("%")) {
                    if(tokens[i].charAt(1) >= '0' && tokens[i].charAt(1) <= '9') {
                        args[i] = String.format("g%d", tokens[i].charAt(1) - '0');
                        memory.put((String) args[i], 0f);
                    } else {
                        args[i] = String.format("gg%d", tokens[i].charAt(2) - '0');
                    }
                } else {
                    args[i] = Float.parseFloat(tokens[i]);
                }

                break;
            }
        }

        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].equals("as")) {
                if(tokens[i + 1].charAt(0) == '&') {
                    int valSlot = tokens[i + 1].charAt(1) - '0';
                    args[8] = String.format("a%d", node.parentNode.id * 10 + --valSlot);
                    memory.put((String) args[8], 0f);
                } else if (tokens[i + 1].charAt(0) == '%') {
                    if(tokens[i + 1].charAt(1) >= '0' && tokens[i + 1].charAt(1) <= '9') {
                        args[8] = String.format("g%d", tokens[i + 1].charAt(1) - '0');
                        memory.put((String) args[i + 1], 0f);
                    } else {
                        args[8] = String.format("gg%d", tokens[i + 1].charAt(2) - '0');
                    }
                }
            }
            if (tokens[i].equals("repeat")) {
//                node.repetitions = Integer.parseInt(tokens[i + 1]);
                try {
                    args[7] =  Integer.parseInt(tokens[i + 1]);
                } catch (Exception exception) {
                    if(tokens[i + 1].startsWith(".")) {
                        int parentPath = StringTools.extractParentPath(tokens[i + 1]);

                        int slot = parentPath % 10;
                        int parentLevel = parentPath / 10;

                        Node curr = new Node(node);
                        for (int j = 0; j < parentLevel; j++) curr = new Node(curr.parentNode);

                        args[7] = String.format("a%d", curr.id * 10 + slot);
                        memory.put((String) args[i], 0f);
                    } else {
                        if (tokens[i + 1].startsWith("%%")) {
                            args[7] = String.format("gg%d", tokens[i + 1].charAt(2) - '0');
                        }else {
                            args[7] = String.format("g%d", tokens[i + 1].charAt(1) - '0');
                        }
                    }
                }
            }
        }

        return args;
    }

    public Node parse(File file, HashMap<String, Float> memory) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        List<String> lines = new ArrayList<>();
        functions = new HashMap<>();

        while (scanner.hasNextLine()) lines.add(scanner.nextLine());

        List<Node> nodes = new ArrayList<>();
        Node[] nodeMap = new Node[lines.size()];

        int currId = 0;

        for (int level = 0; level < 64; level++) {
            for (int currLine = 0; currLine < lines.size(); currLine++) {
                if(lines.get(currLine).trim().isEmpty()) continue;

                if (StringTools.indentation(lines.get(currLine)) == level) {
                    Node node = new Node();
                    boolean func = false;

                    if (lines.get(currLine).trim().endsWith("(")) {
                        if (lines.get(currLine).trim().startsWith("repeat")) {
                            int amount = Integer.parseInt(lines.get(currLine).trim().split(" ")[1]);
                            node.repetitions = amount;
                        }else if (lines.get(currLine).trim().contains("func")) {
                            functions.put(lines.get(currLine).trim().split(" ")[1], node);
                            func = true;
                        }

                        for (int pointer = currLine + 1; pointer < lines.size(); pointer++) {
                            if (StringTools.indentation(lines.get(pointer)) == level) {
                                for (int i = currLine; i <= pointer; i++) nodeMap[i] = node;

                                node.level = level;
                                if (level != 0) node.parentNode = nodeMap[currLine - 1];

                                node.id = currId++;

                                if (node.parentNode == null && !func) nodes.add(node);
                                currLine = pointer;

                                if (node.parentNode != null) node.parentNode.childNodes.add(node);

                                break;
                            }
                        }
                    } else {
                        node.level = level;

                        for (int i = currLine; i > -1; i--) {
                            if (StringTools.indentation(lines.get(i)) < node.level) {
                                node.parentNode = nodeMap[i];
                                break;
                            }
                        }

                        if (node.parentNode != null) {
                            if (nodeMap[currLine] != null && node.level == node.parentNode.level) {
                                node.parentNode.parallelNodes.add(node);
                            } else {
                                node.parentNode.childNodes.add(node);
                            }
                        }

                        node.id = currId++;

                        nodeMap[currLine] = node;

                        node.instruction = parseInstruction(lines.get(currLine).trim(), node, memory);

                        if (node.parentNode == null) nodes.add(node);
                    }
                }
            }
        }

        if(nodes.size() == 0) return null;
        return nodes.get(0);
    }
}
