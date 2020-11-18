package pl.net.nmg.datain.client;

import io.grpc.stub.StreamObserver;
import pl.net.nmg.datain.Client;
import pl.net.nmg.datain.grpc.DataReply;

public class OutputStreamObserver implements StreamObserver<DataReply> {

    @Override
    public void onNext(DataReply output) {
        System.out.println("Received : " + output.getMessage());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error");
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("Done in "+(System.currentTimeMillis()-Client.transferStart)/1000+"s");
    }

}
