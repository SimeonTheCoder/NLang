package utils;

import operations.BasicOperation;
import operations.Operation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class EnumUtils {
    static URLClassLoader classLoader;

    static {
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
            System.out.println(classLoader.getURLs()[0].getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static Class<?> customOperation;

    public static void initClass() throws ClassNotFoundException {
        customOperation = Class.forName("build.CustomOperation", true, classLoader);
    }

    public static Operation getOperation(String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Operation op;

        try {
            op = BasicOperation.valueOf(name);
            return op;
        } catch (Exception e) {
            Method valueOf = customOperation.getMethod("valueOf", String.class);
            Object value = valueOf.invoke(null, name);
            return (Operation) value;
        }
    }
}
