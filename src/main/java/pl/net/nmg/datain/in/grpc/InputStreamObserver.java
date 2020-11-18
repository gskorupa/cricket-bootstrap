package pl.net.nmg.datain.in.grpc;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import pl.net.nmg.datain.Service;
import pl.net.nmg.datain.grpc.DataReply;
import pl.net.nmg.datain.grpc.IntervalDataRequest;

public class InputStreamObserver implements StreamObserver<IntervalDataRequest> {

    private final StreamObserver<DataReply> outputStreamObserver;
    
    public ArrayList<IntervalDataRequest> receivedData = new ArrayList<>();

    public InputStreamObserver(StreamObserver<DataReply> outputStreamObserver) {
        this.outputStreamObserver = outputStreamObserver;
    }

    @Override
    public void onNext(IntervalDataRequest input) {
        System.out.println(input.getTimestamp()+" "+input.getValue());
        receivedData.add(input);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Service error");
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("Service done");
        DataReply output = DataReply.newBuilder().setMessage("OK").build();
        outputStreamObserver.onNext(output);
        outputStreamObserver.onCompleted();
        Service.getInstance();
    }

}