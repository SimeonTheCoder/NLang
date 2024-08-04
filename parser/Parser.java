package parser;

import data.DataEnums;
import data.ObjType;
import memory.MemoryManager;
import nodes.Node;
import operations.BasicOperation;
import operations.Operation;
import transformer.ProgramTransformer;
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
    public Node[] nodesArr;
    public HashMap<String, String> aliases;

    public Parser() {
        nodesArr = new Node[MemoryManager.LOCAL_AMOUNT / MemoryManager.NODE_SLOTS];
    }

    public int extractGlobalAddress(String data, float[] memory) {
        char first = data.charAt(0);
        char second = data.charAt(1);

        if (second >= '0' && second <= '9') {
            return Integer.parseInt(data.substring(1)) + (first == '$' ? 0 : MemoryManager.LOCAL_AMOUNT);
        } else {
            return Integer.parseInt(data.substring(2)) + MemoryManager.LOCAL_AMOUNT + MemoryManager.TOTAL_AMOUNT;
        }
    }

    public int extractLocalAddress(String data, Node node, float[] memory) {
        int parentPath = StringTools.extractParentPath(data);

        int slot = parentPath % MemoryManager.NODE_SLOTS;
        int parentLevel = parentPath / MemoryManager.NODE_SLOTS;

        Node curr = new Node(node);
        for (int j = 0; j < parentLevel; j++) curr = new Node(curr.parentNode);

        return curr.id * MemoryManager.NODE_SLOTS + slot;
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
        args[8] = node.id * MemoryManager.NODE_SLOTS;

        boolean multiple = false;

        for (int i = 1; i < 9; i++) {
            if(i >= tokens.length) break;
            if(!multiple && i > operation.getArguments().length) break;

            if(multiple) {
                args[i] = extractNumber(tokens[i], node, memory);
                continue;
            }

            switch (operation.getArguments()[i - 1]) {
                case NUMBER:
                case MULTIPLE: {
                    if (aliases.containsKey(tokens[i])) tokens[i] = aliases.get(tokens[i]);
                    args[i] = extractNumber(tokens[i], node, memory);

                    if (operation.getArguments()[i - 1] == ObjType.MULTIPLE) multiple = true;
                    if (multiple) args[i] = extractNumber(tokens[i], node, memory);

                    break;
                }

                case FUNCTION: {
                    if(tokens[i].equals("null")) {
                        Node node1 = new Node();
                        node1.instruction[0] = BasicOperation.NULL;
                        args[i] = node1;
                        break;
                    } else if(tokens[i].charAt(0) == '@') {
                        args[i] = nodesArr[Integer.parseInt(tokens[i].substring(1))];
                    } else {
                        args[i] = functions.get(tokens[i]);
                    }

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
                String nextToken = tokens[i + 1];

                if(aliases.containsKey(nextToken)) {
                    nextToken = aliases.get(nextToken);
                }

                char first = nextToken.charAt(0);

                if (first == '&') {
                    int valSlot = tokens[i + 1].charAt(1) - '0';
                    args[8] = node.parentNode.id * MemoryManager.NODE_SLOTS + --valSlot;
                } else if(first == '.') {
                    args[8] = extractLocalAddress(nextToken, node, memory);
                } else if (first == '%' || first == '$') {
                    args[8] = extractGlobalAddress(nextToken, memory);
                }
            }
            if (tokens[i].equals("repeat")) {
                String nextToken = tokens[i + 1];

                if(aliases.containsKey(nextToken)) {
                    nextToken = aliases.get(nextToken);
                }

                if(nextToken.charAt(0) >= '0' && nextToken.charAt(0) <= '9') {
                    args[7] = Integer.parseInt(nextToken);
                } else if (tokens[i + 1].charAt(0) == '.') {
                    args[7] = extractLocalAddress(nextToken, node, memory);
                } else {
                    args[7] = extractGlobalAddress(nextToken, memory);
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

        int maxIndentation = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lines.add(line);
        }

        ProgramTransformer.transform(lines);

        for (String line : lines) {
            maxIndentation = Math.max(maxIndentation, StringTools.indentation(line));
        }

        List<Node> nodes = new ArrayList<>();
        Node[] nodeMap = new Node[lines.size()];

        int currId = 0;

        for (int level = 0; level <= maxIndentation; level+=4) {
            for (int currLine = 0; currLine < lines.size(); currLine++) {
                if(lines.get(currLine).trim().isEmpty() || lines.get(currLine).trim().equals("}")) continue;

                if(lines.get(currLine).startsWith("alias")) {
                    aliases.put(lines.get(currLine).split("\\s+")[3], lines.get(currLine).split("\\s+")[1]);
                    continue;
                }

                if (StringTools.indentation(lines.get(currLine)) == level) {
                    Node node = new Node();
                    boolean func = false;

                    if (lines.get(currLine).trim().endsWith("{")) {
                        if (lines.get(currLine).trim().contains("func")) {
                            functions.put(lines.get(currLine).trim().split("\\s+")[1], node);
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
                        nodesArr[node.id] = node;

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
