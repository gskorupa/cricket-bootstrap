package pl.net.nmg.datain;

import pl.net.nmg.datain.in.grpc.HeaderServerInterceptor;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.in.http.Result;
import org.cricketmsf.in.http.StandardResult;
import org.cricketmsf.services.MinimalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import pl.net.nmg.datain.events.DataReceived;
import pl.net.nmg.datain.grpc.DataReply;
import pl.net.nmg.datain.grpc.DriverGrpc;
import pl.net.nmg.datain.grpc.IntervalDataRequest;
import pl.net.nmg.datain.in.grpc.InputStreamObserver;
import pl.net.nmg.datain.out.db.IntervalDataDao;

public class Service extends MinimalService {

    private static final Logger logger = LoggerFactory.getLogger(Service.class);
    private Server gRPCServer;
    
    public IntervalDataDao dataDao;

    /**
     * The event from an inbound adapter can be processed here
     *
     * @param event
     * @return
     */
    @EventHook(className = "pl.net.nmg.datain.events.DataReceived", procedureName = "add")
    public Object handleGreeting(DataReceived event) {
        Result result = new StandardResult();
        String data = event.getData();
        return result;
    }

    @Override
    public void getAdapters() {
        super.getAdapters();
    }

    @Override
    public void runInitTasks() {
        super.runInitTasks();
        int port = 50051;
        try {
            gRPCServer = ServerBuilder.forPort(port)
                    //.addService(new DriverImpl())
                    .addService(ServerInterceptors.intercept(new DriverImpl(), new HeaderServerInterceptor()))
                    .build()
                    .start();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("gRPC server listening on " + port);
    }

    @Override
    public void shutdown() {
        setStatus(SHUTDOWN);
        if (gRPCServer != null) {
            try {
                gRPCServer.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
            }
        }
        super.shutdown();
    }

    static class DriverImpl extends DriverGrpc.DriverImplBase {

        @Override
        public void putIntervalData(IntervalDataRequest req, StreamObserver<DataReply> responseObserver) {
            DataReply reply = DataReply.newBuilder().setMessage("Hello " + req.getDatasourceId()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<IntervalDataRequest> putIntervalDataList(StreamObserver<DataReply> responseObserver) {
            return new InputStreamObserver(responseObserver);
        }
    }
}
