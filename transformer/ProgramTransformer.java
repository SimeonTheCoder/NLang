package transformer;

import java.util.List;

public class ProgramTransformer {
    public static void transform(List<String> lines) {
        lines.replaceAll(String::trim);

        int indentation = 0;
        int currGlobal = 1;
        boolean inFunction = false;
        boolean joined = false;
        int nonFunctionIndent = 0;

        for(int i = 0; i < lines.size(); i ++) {
            String line = lines.get(i);

            if(line.trim().isEmpty() || line.startsWith("//")) {
                lines.remove(i--);
                continue;
            }

            if(line.contains("\"")) {
                int start = -1, end = -1;

                for(int j = 0; j < line.length(); j ++) {
                    if(line.charAt(j) == '"' && start == -1) {
                        start = j;
                    } else if(line.charAt(j) == '"') {
                        end = j;
                    }
                }

                String content = "";

                StringBuilder builder = new StringBuilder();

                for(int j = 0; j < line.length(); j ++) {
                    if(line.charAt(j) == '"') continue;

                    if(j >= start && j <= end && line.charAt(j) == ' ') builder.append("_");
                    else builder.append(line.charAt(j));
                }

                lines.set(i, builder.toString());
            }

            if(line.contains(";")) {
                int index = line.indexOf(";");
                String previous = line.substring(index + 1).trim();
                String currLine = line.substring(0, index - 1).trim() + " .";

                lines.add(i, previous);
                lines.set(i-- + 1, currLine);

                continue;
            }

            if(line.startsWith(": ")) {
                lines.remove(i--);
                lines.set(i, lines.get(i) + line.substring(1));
                joined = true;
            }

            if(line.endsWith("++")) {
                String var = line.substring(0, line.length() - 2).trim();
                lines.set(i, String.format("add %s 1 as %s", var, var));
            }

            if(line.endsWith("--")) {
                String var = line.substring(0, line.length() - 2).trim();
                lines.set(i, String.format("sub %s 1 as %s", var, var));
            }

            if(line.startsWith("def")) {
                String currLine = lines.get(i);
                lines.remove(i);

                String[] names = currLine.trim().split("\\s+");
                int size = names.length;

                for (int j = 1; j < size; j ++) {
                    String varName = names[j];
                    lines.add(j + i - 1, String.format("alias %%%d as %s", currGlobal++, varName));
                }
            }

            boolean isSign = false;

            if(lines.get(i).contains("+") || lines.get(i).contains("-") ||lines.get(i).contains("*") || lines.get(i).contains("/")
                    || (lines.get(i).contains("=") && !lines.get(i).contains("==")) || (lines.get(i).contains(">") && !lines.get(i).contains("if") && !lines.get(i).startsWith(">"))
                    || (lines.get(i).contains("<") && !lines.get(i).contains("if") && !lines.get(i).startsWith("<"))) {
                String[] tokens = lines.get(i).split("\\s+");
                StringBuilder builder = new StringBuilder();

                if((lines.get(i).contains("+") || lines.get(i).contains("-"))) {
                    int index = lines.get(i).contains("+") ? lines.get(i).indexOf('+') : lines.get(i).indexOf('-');
                    if(lines.get(i).charAt(index + 1) >= '0' && lines.get(i).charAt(index + 1) <= '9') isSign = true;
                }

                if(!isSign) {
                    String temp = tokens[1];
                    tokens[1] = tokens[0];
                    tokens[0] = temp;

                    if (lines.get(i).contains(">") && !lines.get(i).contains("if") && !lines.get(i).startsWith(">")) {
                        tokens[0] = "set";
                        String temp2 = tokens[2];
                        tokens[2] = tokens[1];
                        tokens[1] = temp2;
                    }

                    if (lines.get(i).contains("<") && !lines.get(i).contains("if") && !lines.get(i).startsWith("<")) {
                        tokens[0] = "set";
//                    String temp2 = tokens[2];
//                    tokens[2] = tokens[1];
//                    tokens[1] = temp2;
                    }

                    for (String token : tokens) {
                        builder.append(token);
                        builder.append(" ");
                    }

                    lines.set(i, builder.toString().trim());
                }
            }

            if(line.contains("()")) {
                if(line.endsWith("{")) {
                    lines.set(i, "func " + lines.get(i));
                } else {
                    lines.set(i, "call " + lines.get(i));
                }
            } else if(line.contains("[]")) {
                lines.set(i, lines.get(i).replace("[", ""));
                lines.set(i, lines.get(i).replace("]", ""));
                lines.set(i, String.format("arr " + lines.get(i)));
            }

            if(line.startsWith("func") || line.startsWith("}") || line.startsWith("{")) {
                inFunction = inFunction || lines.get(i).startsWith("func");

                if(line.startsWith("{")) {
                    nonFunctionIndent = indentation;
                }

                indentation = (line.startsWith("{") || !inFunction) ? indentation : 0;

                if(line.startsWith("}") && !inFunction) indentation = nonFunctionIndent;

                if(!joined) lines.set(i, "\t".repeat(indentation) + lines.get(i));

                if(lines.get(i).startsWith("func") || (lines.get(i).contains("{") && !inFunction)) indentation++;
                if(lines.get(i).startsWith("}") && inFunction) inFunction = false;
            } else {
                if (lines.get(i).startsWith("alias")) continue;

                if(!joined) lines.set(i, "\t".repeat(indentation) + lines.get(i));

                if (!line.endsWith("|")) {
                    indentation ++;
                } else {
                    lines.set(i, lines.get(i).replace("|", ""));
                }
            }

            lines.set(i, lines.get(i).replace("(", ""));
            lines.set(i, lines.get(i).replace(")", ""));

            lines.set(i, lines.get(i).replace("[", ""));
            lines.set(i, lines.get(i).replace("]", ""));

            if(lines.get(i).trim().startsWith(">") || lines.get(i).trim().startsWith("<")) {
                lines.set(i, lines.get(i).replace(">", "inp"));
                lines.set(i, lines.get(i).replace("<", "print"));

                if(lines.get(i).contains("println")) {
                    lines.set(i, lines.get(i).replace("ln", ""));
                    lines.add(i+1, "ln");
                }
            }

            if(!isSign) {
                lines.set(i, lines.get(i).replace("+", "add"));
                lines.set(i, lines.get(i).replace("-", "sub"));
            }

            lines.set(i, lines.get(i).replace("*", "mul"));
            lines.set(i, lines.get(i).replace("/", "div"));
            if(!lines.get(i).contains("if")) lines.set(i, lines.get(i).replace("=", "set"));

            joined = false;
        }
    }
}
