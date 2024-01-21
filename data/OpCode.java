package data;

public enum OpCode {
    ADD {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }
    },
    SUB {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }
    },
    MUL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }
    },
    DIV {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }
    },
    SET {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER, ObjType.NUMBER};
        }
    },
    PRINT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER};
        }
    },
    INP {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {};
        }
    },
    CALL {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.FUNCTION};
        }
    };

    public ObjType[] getArguments() {
        return null;
    }
}
