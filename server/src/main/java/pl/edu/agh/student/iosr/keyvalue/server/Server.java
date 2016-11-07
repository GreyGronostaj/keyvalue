package pl.edu.agh.student.iosr.keyvalue.server;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import pl.edu.agh.student.iosr.keyvalue.AddressUtils;
import pl.edu.agh.student.iosr.keyvalue.GetQuery;
import pl.edu.agh.student.iosr.keyvalue.PutCommand;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class Server {
    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            printManual();
        } else if (args.length == 1) {
            Address address = AddressUtils.parseAddress(args[0]);
            createCluster(address);
        } else if (args.length == 2) {
            Address serverAddress = AddressUtils.parseAddress(args[0]);
            Address clusterAddress = AddressUtils.parseAddress(args[1]);
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
        System.exit(1);
    }

    private static void createCluster(Address address) throws UnknownHostException {
        CopycatServer server = getServer(address);
        System.out.println("Creating cluster");
        CompletableFuture<CopycatServer> future = server.bootstrap();
        future.join();

        server.cluster().onJoin(member -> System.out.println(member.address() + " joined the cluster."));
        server.cluster().onLeave(member -> System.out.println(member.address() + " left the cluster."));
    }

    private static void joinCluster(Address serverAddress, Address clusterAddress) {
        CopycatServer server = getServer(serverAddress);
        System.out.format("Joining cluster %s\n", clusterAddress.toString());
        Collection<Address> cluster = Collections.singleton(clusterAddress);
        server.join(cluster).join();
    }

    private static CopycatServer getServer(Address address) {
        System.out.format("Creating server at %s:%d\n", address.host(), address.port());
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
