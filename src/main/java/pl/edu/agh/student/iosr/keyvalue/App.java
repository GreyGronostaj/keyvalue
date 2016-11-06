package pl.edu.agh.student.iosr.keyvalue;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            Address address = getDefaultAddress();
            System.out.format("Creating cluster at %s:%d.\n", address.host(), address.port());
            createCluster(address);
        } else if (args[0].toLowerCase().equals("join")) {
            Address address = new Address(args[1]);
            Address clasterAddress;
            if (args.length < 3) {
                clasterAddress = new Address("127.0.1.1", 5000);
            } else {
                clasterAddress = new Address(args[2]);
            }
            System.out.format("Joining cluster at %s:%d.\n", clasterAddress.host(), clasterAddress.port());
            joinCluster(address, clasterAddress);
        } else {
            Address address = new Address(args[0]);
            System.out.format("Creating cluster at %s:%d.\n", address.host(), address.port());
            createCluster(address);
        }
    }

    private static void createCluster(Address address) throws UnknownHostException {
        CopycatServer server = getServer(address);
        CompletableFuture<CopycatServer> future = server.bootstrap();
        future.join();

        server.cluster().onJoin(member -> {
            System.out.println(member.address() + " joined the cluster.");
        });

        server.cluster().onLeave(member -> {
            System.out.println(member.address() + " left the cluster.");
        });
    }

    private static void joinCluster(Address address, Address clasterAddress) {
        CopycatServer server = getServer(address);
        Collection<Address> cluster = Collections.singleton(clasterAddress);
        server.join(cluster).join();
    }

    private static Address getDefaultAddress() throws UnknownHostException {
        String localHostAddress = InetAddress.getLocalHost().getHostAddress();
        int port = 5000;
        return new Address(localHostAddress, port);
    }

    private static CopycatServer getServer(Address address) {
        System.out.format("Creating server at %s:%d.\n", address.host(), address.port());
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
