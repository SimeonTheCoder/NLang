package parser;

import data.DataEnums;
import data.ObjType;
import nodes.Node;
import operations.Operation;
import utils.EnumUtils;
import utils.StringTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public HashMap<String, Node> functions;
    public HashMap<String, String> aliases;

    public int extractGlobalAddress(String data, float[] memory) {
        if(data.charAt(0) == '$') {
            return Integer.parseInt(data.substring(1)) + 1536;
        }

        if(data.charAt(1) >= '0' && data.charAt(1) <= '9') {
            int address = Integer.parseInt(data.substring(1));
            return address + 1536;
        }
        else if (data.charAt(1) == '%') {
            return Integer.parseInt(data.substring(2)) + 1536 + 2048;
        }
        else {
            throw new IllegalArgumentException("Address " + data + " doesn't exist");
        }
    }

    public int extractLocalAddress(String data, Node node, float[] memory) {
        int parentPath = StringTools.extractParentPath(data);

        int slot = parentPath % 10;
        int parentLevel = parentPath / 10;

        Node curr = new Node(node);
        for (int j = 0; j < parentLevel; j++) curr = new Node(curr.parentNode);

        return curr.id * 10 + slot;
    }

    public Object extractNumber(String token, Node node, float[] memory) {
        if (token.charAt(0) == '.') {
            return extractLocalAddress(token, node, memory);
        } else if (token.charAt(0) == '%' || token.charAt(0) == '$') {
            return extractGlobalAddress(token, memory);
        }

        return Float.parseFloat(token);
    }

    public Object[] parseInstruction(String instruction, Node node, float[] memory) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        String[] tokens = instruction.split("\\s+");
        Operation operation = EnumUtils.getOperation(tokens[0].toUpperCase());

        Object[] args = new Object[9];
        args[0] = operation;
        args[8] = node.id * 10;

        boolean multiple = false;

        for (int i = 1; i <= 10; i++) {
            if(i >= tokens.length) break;
            if(!multiple && i > operation.getArguments().length) break;

            if(multiple) {
                args[i] = extractNumber(tokens[i], node, memory);
                break;
            }

            switch (operation.getArguments()[i - 1]) {
                case NUMBER:
                case MULTIPLE: {
                    if (aliases.containsKey(tokens[i])) tokens[i] = aliases.get(tokens[i]);
                    args[i] = extractNumber(tokens[i], node, memory);

                    if (operation.getArguments()[i - 1] == ObjType.MULTIPLE) multiple = true;

                    break;
                }

                case FUNCTION: {
                    args[i] = functions.get(tokens[i]);
                    break;
                }

                case ENUM: {
                    DataEnums result = DataEnums.EQUAL;

                    switch (tokens[i]) {
                        case ">" -> result = DataEnums.GREATER_THAN;
                        case "<" -> result = DataEnums.LESS_THAN;
                        case "==" -> result = DataEnums.EQUAL;
                        case ">=" -> result = DataEnums.GREATER_EQUAL;
                        case "<=" -> result = DataEnums.LESS_EQUAL;
                        case "!=" -> result = DataEnums.NOT_EQUAL;
                    }

                    args[i] = result.ordinal();
                    break;
                }

                case STRING: {
                    args[i] = tokens[i];
                    break;
                }
            }
        }

        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].equals("as")) {
                if(aliases.containsKey(tokens[i + 1])) {
                    tokens[i + 1] = aliases.get(tokens[i + 1]);
                }

                if (tokens[i + 1].charAt(0) == '&') {
                    int valSlot = tokens[i + 1].charAt(1) - '0';
                    args[8] = "a" + node.parentNode.id * 10 + --valSlot;
                } else if(tokens[i + 1].charAt(0) == '.') {
                    args[8] = extractLocalAddress(tokens[i + 1], node, memory);
                } else if (tokens[i + 1].charAt(0) == '%' || tokens[i].charAt(0) == '$') {
                    args[8] = extractGlobalAddress(tokens[i + 1], memory);
                }
            }
            if (tokens[i].equals("repeat")) {
                try {
                    args[7] = Integer.parseInt(tokens[i + 1]);
                } catch (Exception exception) {
                    if(tokens[i + 1].charAt(0) == '.') {
                        args[7] = extractLocalAddress(tokens[i + 1], node, memory);
                    } else {
                        args[7] = extractGlobalAddress(tokens[i + 1], memory);
                    }
                }
            }
        }

        return args;
    }

    public Node parse(File file, float[] memory) throws FileNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Scanner scanner = new Scanner(file);

        List<String> lines = new ArrayList<>();
        functions = new HashMap<>();
        aliases = new HashMap<>();

        while (scanner.hasNextLine()) lines.add(scanner.nextLine());

        List<Node> nodes = new ArrayList<>();
        Node[] nodeMap = new Node[lines.size()];

        int currId = 0;

        for (int level = 0; level < 64; level++) {
            for (int currLine = 0; currLine < lines.size(); currLine++) {
                if(lines.get(currLine).trim().isEmpty()) continue;

                if(lines.get(currLine).startsWith("alias")) {
                    aliases.put(lines.get(currLine).split(" ")[3], lines.get(currLine).split(" ")[1]);
                    continue;
                }

                if (StringTools.indentation(lines.get(currLine)) == level) {
                    Node node = new Node();
                    boolean func = false;

                    if (lines.get(currLine).trim().endsWith("{")) {
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

                                node.level = level + (func ? 0 : 4);
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

        if(nodes.isEmpty()) return null;
        return nodes.getFirst();
    }
}
