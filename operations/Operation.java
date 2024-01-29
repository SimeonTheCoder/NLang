package operations;

import data.ObjType;
import data.ReadableFile;
import data.WritableFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public interface Operation {
    ObjType[] getArguments();

    default void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

    }
}
