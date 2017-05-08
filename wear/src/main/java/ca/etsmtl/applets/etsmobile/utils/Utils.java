package ca.etsmtl.applets.etsmobile.utils;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class Utils {

    public static String getLocalNodeId(GoogleApiClient googleApiClient) {
        NodeApi.GetLocalNodeResult nodeResult = Wearable.NodeApi.getLocalNode(googleApiClient).await();
        return nodeResult.getNode().getId();
    }

    public static String getRemoteNodeId(GoogleApiClient googleApiClient) {
        NodeApi.GetConnectedNodesResult nodesResult =
                Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        List<Node> nodes = nodesResult.getNodes();
        for (Node node : nodes) {
            if(node.isNearby()) {
                return node.getId();
            }
        }
        if (nodes.size() > 0) {
            return nodes.get(0).getId();
        }
        return null;
    }
}
