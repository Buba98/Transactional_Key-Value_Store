package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.handler;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerRequest;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerResponse;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;

import java.io.IOException;

public class ServerRequestHandler extends Thread {
    final ServerRequest serverRequest;
    final Server server;

    public ServerRequestHandler(ServerRequest serverRequest, Server server) {
        this.serverRequest = serverRequest;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            if (serverRequest.optimizedOperation.lockType == LockType.FREE) {
                int serverId = server.scheduler.serverLockHolderId(serverRequest.optimizedOperation.key);
                if (serverId == server.serverId) {
                    server.scheduler.free(serverRequest.optimizedOperation, serverRequest.schedulerTransactionHandlerId);
                } else {
                    server.scheduler.executeOperation(serverRequest.optimizedOperation, serverRequest.schedulerTransactionHandlerId);
                }

            } else {
                server.sendResponse(new ServerResponse(server.scheduler.executeOperation(serverRequest.optimizedOperation, serverRequest.schedulerTransactionHandlerId), serverRequest.schedulerTransactionHandlerId), serverRequest.sourceId);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
