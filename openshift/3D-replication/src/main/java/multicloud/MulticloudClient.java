package rahul;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientFailoverConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.concurrent.TimeUnit;


public class MulticloudClient {

    private static final String ADD_EC2_INSTANCE_1 = "IP or DNS Of EC2 Instance 1";
    private static final String ADD_EC2_INSTANCE_2 = "IP or DNS of EC2 Instance 2";
    private static final String ADD_ON_PREM_INSTANCE = "127.0.0.1:5701";
    private static final String ADD_OPENSHIFT_LB_SVC = "IP or DNS of OpenShift LoadBalancer Service";

    private static final String EC2_CLUSTER_ID = "ec2";
    private static final String ON_PREM_CLUSTER_ID = "laptop";
    private static final String OPENSHIFT_CLUSTER_ID = "openshift";

    private static final String MAP_1 = "Multicloud_Map";
    private static final String MAP_2 = "Test_Map";



    private static ClientFailoverConfig getClientFailoverConfig() {
        ClientFailoverConfig failoverConfig = new ClientFailoverConfig();

        ClientConfig laptopConfig = new ClientConfig();
        laptopConfig.setClusterName(ON_PREM_CLUSTER_ID);
        ClientNetworkConfig networkConfig = laptopConfig.getNetworkConfig();
        networkConfig.addAddress("127.0.0.1");

        ClientConfig ec2Config = new ClientConfig();
        ec2Config.setClusterName(EC2_CLUSTER_ID);
        ClientNetworkConfig networkConfig2 = ec2Config.getNetworkConfig();
        networkConfig2.addAddress( ADD_EC2_INSTANCE_1, ADD_EC2_INSTANCE_2);

        failoverConfig.addClientConfig(laptopConfig).addClientConfig(ec2Config);
        failoverConfig.setTryCount(2);
        return failoverConfig;
    }

    private static void testOpenShiftPublisher(ClientConfig config) throws InterruptedException {
        config.getNetworkConfig().addAddress(ADD_OPENSHIFT_LB_SVC);
        config.setClusterName(OPENSHIFT_CLUSTER_ID);
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IMap map_1 = instance.getMap(MAP_1);
        map_1.clear();
        for(int i=0; i<20; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map1 Set: "+i);
        }
        IMap map_2 = instance.getMap(MAP_2);
        map_2.clear();
        for(int i=0; i<25; i++) {
            map_2.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map2 Set: "+i);
        }
        System.out.println("Map Set Complete");
    }

    private static void testEC2Publisher(ClientConfig config) throws InterruptedException {
        config.getNetworkConfig().addAddress(ADD_EC2_INSTANCE_1);
        config.setClusterName(EC2_CLUSTER_ID);
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IMap map_1 = instance.getMap(MAP_1);
        map_1.clear();
        for(int i=0; i<100; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map1 Set: "+i);
        }
        IMap map_2 = instance.getMap(MAP_2);
        map_2.clear();
        for(int i=0; i<150; i++) {
            map_2.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map2 Set: "+i);
        }
        System.out.println("Map Set Complete");
    }

    private static void testOnPremPublisher(ClientConfig config) throws InterruptedException {
        config.getNetworkConfig().addAddress(ADD_ON_PREM_INSTANCE);
        config.setClusterName(ON_PREM_CLUSTER_ID);
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IMap map_1 = instance.getMap(MAP_1);
        map_1.clear();
        for(int i=0; i<300; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map1 Set: "+i);
        }
        IMap map_2 = instance.getMap(MAP_2);
        map_2.clear();
        for(int i=0; i<700; i++) {
            map_2.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map2 Set: "+i);
        }
        System.out.println("Map Set Complete");
    }

    private static void testOnPremPublisherFailover(ClientFailoverConfig config) throws InterruptedException {
        HazelcastInstance instance = HazelcastClient.newHazelcastFailoverClient(config);

        IMap map_1 = instance.getMap(MAP_1);
        map_1.clear();
        for(int i=0; i<300; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("Map1 Set: "+i);
        }
        System.out.println("Map Set Complete");
    }


    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();
       //testOnPremPublisher(config);
       //testEC2Publisher(config);
       testOpenShiftPublisher(config);
       //testOnPremPublisherFailover(getClientFailoverConfig());
    }
}
