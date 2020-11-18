/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.net.nmg.datain;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import java.util.Scanner;
import pl.net.nmg.datain.grpc.DriverGrpc;
import pl.net.nmg.datain.grpc.IntervalDataRequest;
import pl.net.nmg.datain.client.OutputStreamObserver;
/**
 *
 * @author greg
 */
public class Client {

    DriverGrpc.DriverBlockingStub blockingStub;
    DriverGrpc.DriverStub stub;
    Channel channel;
    public static long transferStart, transferEnd;
    
    public Client(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing
     * channel.
     */
    public Client(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = DriverGrpc.newBlockingStub(channel);
        stub=DriverGrpc.newStub(channel);
    }

    public static void main(String[] args) {
        String url = args[0];
        int port = Integer.parseInt(args[1]);
        Client client = new Client(url, port);
        Metadata metadata = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("request-metadata1", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(key, "meta1");
        key = Metadata.Key.of("request-metadata2", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(key, "meta2");
        IntervalDataRequest idReq = IntervalDataRequest.newBuilder()
                .setAcquisitionTimestamp(System.currentTimeMillis())
                .setDatasourceId(1000)
                .setDeviceId(100)
                .setRawStatus(8)
                .setStatus(8123)
                .setTimeZone("Europe/Warsaw")
                .setTimestamp(System.currentTimeMillis())
                .setValue(0)
                .build();
        //DriverBlockingStub blockingStub = MetadataUtils.attachHeaders(client.blockingStub, metadata);
        //System.out.println(client.blockingStub.sayHello(hello).getMessage());
        System.out.println(client.blockingStub.putIntervalData(idReq));
        client.sendStream();
        System.out.println("Data transfer in progress");
        System.out.println("Press Enter to stop");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
    
    private void sendStream(){
        transferStart=System.currentTimeMillis();
        long records=1000000;
        System.out.println("Sending stream");
        // pass the output stream observer & receive the input stream observer
        StreamObserver<IntervalDataRequest> inputStreamObserver = this.stub.putIntervalDataList(new OutputStreamObserver());

        for (int i = 0; i <= records; i++) {
            // build the request object
            IntervalDataRequest input = IntervalDataRequest.newBuilder()
                    .setAcquisitionTimestamp(System.currentTimeMillis())
                    .setTimestamp(System.currentTimeMillis())
                    .setDatasourceId(i)
                    .setDeviceId(100)
                    .setRawStatus(8)
                    .setStatus(8123)
                    .setTimeZone("Europe/Warsaw")
                    .setValue((double)i)
                    .build();
            // pass the request object via input stream observer
            inputStreamObserver.onNext(input);
        }

        // client side is done. this method makes the server respond with the sum value
        inputStreamObserver.onCompleted();
        System.out.println("Data transfer: "+records+" records");
    }

}
