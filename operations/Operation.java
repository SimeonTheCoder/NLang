package operations;

import data.Array;
import data.ObjType;
import data.ReadableFile;
import data.WritableFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public interface Operation {
    ObjType[] getArguments();

    default void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

    }

    String help();
}
