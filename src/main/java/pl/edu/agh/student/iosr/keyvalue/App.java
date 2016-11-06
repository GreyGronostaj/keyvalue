package pl.edu.agh.student.iosr.keyvalue;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Random;

public class App {
    public static void main(String[] args) throws UnknownHostException {
        Optional<InetAddress> ipFromArgs = getIpFromArgs(args);
        InetAddress ip = ipFromArgs.orElse(InetAddress.getLocalHost());
        if (ipFromArgs.isPresent()) {
            System.out.format("Using IP %s", ip);
        } else {
            System.out.format("Using default IP %s; provide argument to use custom IP", ip);
        }

        int port = new Random().nextInt(1000) + 5000;
        System.out.format("Using random port %d", port);

        Address address = new Address(ip.toString(), port);
        startServer(address);
    }

    private static Optional<InetAddress> getIpFromArgs(String[] args) throws UnknownHostException {
        if (args.length > 1) {
            return Optional.of(InetAddress.getByName(args[1]));
        } else {
            return Optional.empty();
        }
    }

    private static void startServer(Address address) {
        CopycatServer server = CopycatServer.builder(address)
                .withStateMachine(KeyValueStateMachine::new)
                .withTransport(NettyTransport.builder().withThreads(2).build())
                .withStorage(Storage.builder().withDirectory(new File("logs")).withStorageLevel(StorageLevel.DISK).build())
                .build();
        server.serializer().register(PutCommand.class);
        server.serializer().register(GetQuery.class);

        // TODO create a cluster or join it: http://atomix.io/copycat/docs/getting-started/#bootstrapping-the-cluster
    }
}
