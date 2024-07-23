package data;

public enum ObjType {
    NUMBER,
    FUNCTION,
    ENUM,
    STRING,
    MULTIPLE;

    @Override
    public String toString() {
        return name();
    }
}
