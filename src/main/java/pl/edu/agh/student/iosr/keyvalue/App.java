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
            printManual();
            System.exit(1);
        } else if (args.length == 1) {
            Address address = parseAddress(args[0]);
            createCluster(address);
        } else if (args.length == 2) {
            Address serverAddress = parseAddress(args[0]);
            Address clusterAddress = parseAddress(args[1]);
            joinCluster(serverAddress, clusterAddress);
        } else {
            printManual();
        }
    }

    private static void printManual() {
        System.out.println("Usage:");
        System.out.println("  Create cluster:   <program> ip:port");
        System.out.println("  Join cluster:     <program> ip:port clusterIp:clusterPort");
        System.out.println("IP can always be skipped (eg. ':5000' instead of '127.0.0.1:5000')");
    }

    private static String getDefaultIP()  {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static Address parseAddress(String str) {
        if (str.charAt(0) == ':') {
            int port = Integer.valueOf(str.substring(1));
            return new Address(getDefaultIP(), port);
        } else {
            return new Address(str);
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

    private static void joinCluster(Address serverAddress, Address clusterAddress) {
        CopycatServer server = getServer(serverAddress);
        Collection<Address> cluster = Collections.singleton(clusterAddress);
        server.join(cluster).join();
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
