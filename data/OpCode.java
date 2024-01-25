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
    },
    IF {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {
                    ObjType.NUMBER, ObjType.NUMBER, ObjType.ENUM, ObjType.FUNCTION, ObjType.FUNCTION
            };
        }
    },
    PRINTLN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER};
        }
    };

    public ObjType[] getArguments() {
        return null;
    }
}
