package pl.edu.agh.student.iosr.keyvalue;

import io.atomix.catalyst.transport.Address;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddressUtils {
    private static String getDefaultIP()  {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static Address parseAddress(String str) {
        if (str.charAt(0) == ':') {
            int port = Integer.valueOf(str.substring(1));
            return new Address(getDefaultIP(), port);
        } else {
            return new Address(str);
        }
    }
}
