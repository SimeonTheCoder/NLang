package utils;

import operations.BasicOperation;
import operations.Operation;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    static HashMap<String, Class<Enum>> libraries;

    public static void initClass() throws ClassNotFoundException, FileNotFoundException {
        libraries = new HashMap<>();

        Scanner scanner = new Scanner(new File("dependencies.nld"));

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            libraries.put(line, (Class<Enum>) Class.forName("build." + line, true, classLoader));
        }
    }

    public static boolean contains(String test, Class<Enum> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (Object c : clazz.getEnumConstants()) {
            if (c.toString().equals(test)) return true;
        }

        return false;
    }

    public static Operation getOperation(String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Operation op;

        Class customOperation = null;

        try {
            return BasicOperation.valueOf(name);
        } catch (Exception e) {
            for (Map.Entry<String, Class<Enum>> entry : libraries.entrySet()) {
                if(contains(name, entry.getValue())) {
                    customOperation = entry.getValue();
                    break;
                }
            }

            Method valueOf = customOperation.getMethod("valueOf", String.class);
            Object value = valueOf.invoke(null, name);
            return (Operation) value;
        }
    }
}
