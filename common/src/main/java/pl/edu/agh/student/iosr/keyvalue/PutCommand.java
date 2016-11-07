package pl.edu.agh.student.iosr.keyvalue;

import io.atomix.copycat.Command;

public class PutCommand implements Command<Object> {
    private final Object key;
    private final Object value;

    public PutCommand(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
