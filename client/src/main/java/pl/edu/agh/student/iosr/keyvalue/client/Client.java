package pl.edu.agh.student.iosr.keyvalue.client;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import pl.edu.agh.student.iosr.keyvalue.AddressUtils;
import pl.edu.agh.student.iosr.keyvalue.GetQuery;
import pl.edu.agh.student.iosr.keyvalue.PutCommand;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class Client {
    public static void main(String[] args) {
        if (args.length == 0) {
            printManual();
        } else {
            Address address = AddressUtils.parseAddress(args[0]);
            String operation = args[1].toLowerCase();
            if (!Arrays.asList("get", "put").contains(operation)) {
                printManual();
            }
            String key = args[2];
            String value = operation.equals("put") ? args[3] : null;

            System.err.format("Connecting to %s\n", address.toString());
            CopycatClient client = connectToNode(address);

            if (operation.equals("get")) {
                performGet(client, key);
            } else {
                performPut(client, key, value);
            }
        }
    }

    private static CopycatClient connectToNode(Address address) {
        CopycatClient client = CopycatClient.builder()
                .withTransport(NettyTransport.builder().withThreads(2).build())
                .build();
        client.serializer().register(PutCommand.class);
        client.serializer().register(GetQuery.class);

        Collection<Address> addresses = Collections.singleton(address);
        CompletableFuture<CopycatClient> future = client.connect(addresses);
        future.join();
        return client;
    }

    private static void performGet(CopycatClient client, String key) {
        System.err.format("Querying '%s'\n", key);
        GetQuery get = new GetQuery(key);
        CompletableFuture<Object> future = client.submit(get);
        Object result = future.join();
        System.out.println(result);
        System.exit(0);
    }

    private static void performPut(CopycatClient client, String key, String value) {
        System.err.format("Setting '%s': '%s'\n", key, value);
        PutCommand get = new PutCommand(key, value);
        CompletableFuture<Object> future = client.submit(get);
        Object result = future.join();
        System.out.println(result);
        System.exit(0);
    }

    private static void printManual() {
        System.out.println("Usage:");
        System.out.println("  Connect to node:   <program> ip:port <operation>");
        System.out.println("  Available operations:");
        System.out.println("    - get key");
        System.out.println("    - put key value");
        System.out.println("IP can always be skipped (eg. ':5000' instead of '127.0.0.1:5000')");
        System.exit(1);
    }
}
