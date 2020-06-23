package multicloud;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.spi.merge.PassThroughMergePolicy;


public class WanReplicatedMember {

    private static void configureClusterWAN(Config config) {
        WanReplicationConfig wrConfig = new WanReplicationConfig();
        wrConfig.setName("WanConfigLaptop");

        WanSyncConfig syncConfig = new WanSyncConfig();
        syncConfig.setConsistencyCheckStrategy(ConsistencyCheckStrategy.MERKLE_TREES);

        WanBatchPublisherConfig openShiftPublisherConfig = new WanBatchPublisherConfig();
        openShiftPublisherConfig.setClusterName("openshift")
                .setPublisherId("openshift")
                .setSyncConfig(syncConfig)
                .setDiscoveryPeriodSeconds(20)
                .setQueueFullBehavior(WanQueueFullBehavior.THROW_EXCEPTION)
                .setQueueCapacity(1000)
                .setBatchSize(10)
                .setBatchMaxDelayMillis(1000)
                .setResponseTimeoutMillis(10000)
                .setSnapshotEnabled(false)
                .setAcknowledgeType(WanAcknowledgeType.ACK_ON_OPERATION_COMPLETE)
                .setTargetEndpoints("15.236.118.148:5701");
        wrConfig.addBatchReplicationPublisherConfig(openShiftPublisherConfig);

        WanBatchPublisherConfig ec2PublisherConfig = new WanBatchPublisherConfig();
        ec2PublisherConfig.setClusterName("ec2")
                .setPublisherId("ec2")
                .setSyncConfig(syncConfig)
                .setDiscoveryPeriodSeconds(20)
                .setQueueFullBehavior(WanQueueFullBehavior.THROW_EXCEPTION)
                .setQueueCapacity(1000)
                .setBatchSize(10)
                .setBatchMaxDelayMillis(1000)
                .setResponseTimeoutMillis(10000)
                .setSnapshotEnabled(false)
                .setAcknowledgeType(WanAcknowledgeType.ACK_ON_OPERATION_COMPLETE)
                .setTargetEndpoints("ec2-54-193-228-96.us-west-1.compute.amazonaws.com");
        wrConfig.addBatchReplicationPublisherConfig(ec2PublisherConfig);

        config.addWanReplicationConfig(wrConfig);
    }


    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("laptop");
        config.setLicenseKey(rahul.License.KEY);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        config.getNetworkConfig().setPort(5701).setPortAutoIncrement(true).setPortCount(20);
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");

        configureClusterWAN(config);

        WanReplicationRef wanRef = new WanReplicationRef();
        wanRef.setName("WanConfigLaptop");
        wanRef.setMergePolicyClassName(PassThroughMergePolicy.class.getName());
        wanRef.setRepublishingEnabled(false);

        MerkleTreeConfig merkleTreeConfig = new MerkleTreeConfig();
        merkleTreeConfig.setEnabled(true);
        merkleTreeConfig.setDepth(5);

        config.getMapConfig("Multicloud_Map").setWanReplicationRef(wanRef).setMerkleTreeConfig(merkleTreeConfig);
        config.getMapConfig("Test_Map").setWanReplicationRef(wanRef).setMerkleTreeConfig(merkleTreeConfig);

        Hazelcast.newHazelcastInstance(config);
    }

}
