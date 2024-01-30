import data.ObjType;
import data.ReadableFile;
import data.WritableFile;
import operations.Operation;
import javax.swing.*;

import java.io.IOException;
import java.util.HashMap;

public enum CustomOperation implements Operation {
    PING {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) throws IOException {
            System.out.println("Pong!");
        }
    };

    public CustomOperation value(String str) {
        switch (str) {
            case "PING":
                return PING;

            default:
                return null;
        }
    }

    CustomOperation() {
    }
}