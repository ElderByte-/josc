package com.elderbyte.josc.spring.support;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.stream.Stream;


public final class LanInetAddressUtil {

    /*
    public static void main(String[] args) throws SocketException {
        //System.out.println(getPublicIp().get());
        getPublicIps().stream().forEach(ia -> System.out.println(ia));
    }*/


    public static Optional<InetAddress> getPublicIp() throws SocketException{
       return getPublicIps().findFirst();
    }

    public static Stream<InetAddress> getPublicIps() throws SocketException {
        return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                        .filter(ni -> {
                            try {
                                return (!ni.isLoopback() && !ni.isPointToPoint());
                            } catch (SocketException e) {
                                throw new RuntimeException("Failed to check network interface!", e);
                            }
                        })
                        .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
                        .filter(ia -> !ia.isLinkLocalAddress());
    }
}
