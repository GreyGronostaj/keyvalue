package pl.edu.agh.student.iosr.keyvalue;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.Snapshottable;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.snapshot.SnapshotReader;
import io.atomix.copycat.server.storage.snapshot.SnapshotWriter;

import java.util.HashMap;
import java.util.Map;

public class KeyValueStateMachine extends StateMachine implements Snapshottable {
    private Map<Object, Object> map = new HashMap<>();

    public Object put(Commit<PutCommand> commit) {
        try {
            return map.put(commit.operation().getKey(), commit.operation().getValue());
        } finally {
            commit.close();
        }
    }

    public Object get(Commit<GetQuery> commit) {
        try {
            return map.get(commit.operation().getKey());
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
