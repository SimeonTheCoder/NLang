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
    },
    ALLOC {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.NUMBER};
        }
    },
    WRITE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.FILE, ObjType.NUMBER};
        }
    },
    MKFILE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.FILE};
        }
    },
    CLOSE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.FILE};
        }
    };

    public ObjType[] getArguments() {
        return null;
    }
}
