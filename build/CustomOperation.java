package build;

import data.Array;
import data.ObjType;
import data.ReadableFile;
import data.WritableFile;
import operations.Operation;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public enum CustomOperation implements Operation {
    PING {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
            System.out.println("Pong!");
        }

        @Override
        public String help() {
            return "";
        }
    };
//    WINMAKE {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            CustomOperation.window = new Window(
//                    "Hello, world!", Math.round((Float) instruction[1]), Math.round((Float) instruction[2])
//            );
//        }
//    },
//    PLOT {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            CustomOperation.window.points.add(new Point(
//                    Math.round(Interpreter.getValue(instruction[1], memory)),
//                    Math.round(Interpreter.getValue(instruction[2], memory))
//            ));
//        }
//    },
//    CLEARWIN {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            CustomOperation.window.points.clear();
//
//            CustomOperation.window.linesStart.clear();
//            CustomOperation.window.linesEnd.clear();
//        }
//    },
//    LINE {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER, ObjType.NUMBER, ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
//            CustomOperation.window.linesStart.add(new Point(
//                    Math.round(Interpreter.getValue(instruction[1], memory)),
//                    Math.round(Interpreter.getValue(instruction[2], memory))
//            ));
//
//            CustomOperation.window.linesEnd.add(new Point(
//                    Math.round(Interpreter.getValue(instruction[3], memory)),
//                    Math.round(Interpreter.getValue(instruction[4], memory))
//            ));
//        }
//    },
//    COS {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.cos(Interpreter.getValue(instruction[1], memory)));
//        }
//    },
//    SIN {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.sin(Interpreter.getValue(instruction[1], memory)));
//        }
//    },
//    ABS {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.abs(Interpreter.getValue(instruction[1], memory)));
//        }
//    },
//    LOG {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.log(Interpreter.getValue(instruction[1], memory)));
//        }
//    },
//    EXP {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.exp(Interpreter.getValue(instruction[1], memory)));
//        }
//    },
//    POW {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.pow(Interpreter.getValue(instruction[1], memory), Interpreter.getValue(instruction[2], memory)));
//        }
//    },
//    ROUND {
//        @Override
//        public ObjType[] getArguments() {
//            return new ObjType[]{ObjType.NUMBER};
//        }
//
//        @Override
//        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException {
//            memory.put((String) instruction[8], (float) Math.round(Interpreter.getValue(instruction[1], memory)));
//        }
//    };

//    public static Window window;

    public CustomOperation value(String str) {
        switch (str) {
            case "PING":
                return PING;

//            case "WINMAKE":
//                return WINMAKE;
//
//            case "PLOT":
//                return PLOT;
//
//            case "COS":
//                return COS;
//
//            case "SIN":
//                return SIN;
//
//            case "ABS":
//                return ABS;
//
//            case "ROUND":
//                return ROUND;
//
//            case "POW":
//                return POW;
//
//            case "LOG":
//                return LOG;

//	    case "EXP":
//		return EXP;

            default:
                return null;
        }
    }

    CustomOperation() {
    }

    class Window extends JPanel {
        public java.util.List<Point> points;
        public java.util.List<Point> linesStart;
        public java.util.List<Point> linesEnd;

        public Window(String title, int sizeX, int sizeY) {
            points = new ArrayList<>();

            linesStart = new ArrayList<>();
            linesEnd = new ArrayList<>();

            JFrame frame = new JFrame(title);
            frame.setSize(sizeX, sizeY);

            frame.add(this);

            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            g.setColor(new Color(0, 0, 0, 16));

            for (int i = 0; i < points.size(); i ++) {
                g.fillRect(points.get(i).x, points.get(i).y, 5, 5);
            }

            for (int i = 0; i < Math.min(linesStart.size(), linesEnd.size()); i ++) {
                g.drawLine(linesStart.get(i).x, linesStart.get(i).y, linesEnd.get(i).x, linesEnd.get(i).y);
            }

            repaint();
        }
    }
}