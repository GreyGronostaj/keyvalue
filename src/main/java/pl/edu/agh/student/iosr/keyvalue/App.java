package pl.edu.agh.student.iosr.keyvalue;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class App {
    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            Address address = getDefaultAddress();
            System.out.format("Creating cluster at %s:%d\n", address.host(), address.port());
            createCluster(address);
        } else if (args[0].toLowerCase().equals("join")) {
            Address address = new Address(args[1]);
            System.out.format("Joining cluster at %s:%d\n", address.host(), address.port());
            joinCluster(address);
        } else {
            Address address = new Address(args[0]);
            System.out.format("Creating cluster at %s:%d\n", address.host(), address.port());
            createCluster(address);
        }
    }

    private static void createCluster(Address address) throws UnknownHostException {
        CopycatServer server = getServer(address);
        // TODO do something
    }

    private static void joinCluster(Address address) {
        CopycatServer server = getServer(address);
        // TODO do something
    }

    private static Address getDefaultAddress() throws UnknownHostException {
        int port = 5000;
        String ip = InetAddress.getLocalHost().toString();
        int slashPos = ip.indexOf('/');
        if (slashPos != -1) {
            return new Address(ip, port);
        } else {
            return new Address(ip.substring(slashPos + 1), port);
        }
    }

    private static CopycatServer getServer(Address address) {
        CopycatServer server = CopycatServer.builder(address)
                .withStateMachine(KeyValueStateMachine::new)
                .withTransport(NettyTransport.builder().withThreads(2).build())
                .withStorage(Storage.builder()
                        .withDirectory(new File("logs"))
                        .withStorageLevel(StorageLevel.DISK)
                        .build())
                .build();
        server.serializer().register(PutCommand.class);
        server.serializer().register(GetQuery.class);
        return server;
    }
}
