package pl.edu.agh.student.iosr.keyvalue.server;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.Snapshottable;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.snapshot.SnapshotReader;
import io.atomix.copycat.server.storage.snapshot.SnapshotWriter;
import pl.edu.agh.student.iosr.keyvalue.GetQuery;
import pl.edu.agh.student.iosr.keyvalue.PutCommand;

import java.util.HashMap;
import java.util.Map;

class KeyValueStateMachine extends StateMachine implements Snapshottable {
    private Map<Object, Object> map = new HashMap<>();

    public Object put(Commit<PutCommand> commit) {
        try {
            Object oldValue = map.put(commit.operation().getKey(), commit.operation().getValue());
            System.out.format("Putting '%s': '%s', previously '%s'\n", commit.operation().getKey(), commit.operation().getValue(), oldValue);
            return oldValue;
        } finally {
            commit.close();
        }
    }

    public Object get(Commit<GetQuery> commit) {
        try {
            Object value = map.get(commit.operation().getKey());
            System.out.format("Getting '%s': '%s'\n", commit.operation().getKey(), value);
            return value;
        } finally {
            commit.close();
        }
    }

    @Override
    public void snapshot(SnapshotWriter writer) {
        writer.writeObject(map);
    }

    @Override
    public void install(SnapshotReader reader) {
        map = reader.readObject();
    }
}
