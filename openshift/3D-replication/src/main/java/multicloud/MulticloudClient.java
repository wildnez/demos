package multicloud;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientFailoverConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.concurrent.TimeUnit;


public class MulticloudClient {

    private static ClientFailoverConfig getClientFailoverConfig() {
        ClientFailoverConfig failoverConfig = new ClientFailoverConfig();

        ClientConfig laptopConfig = new ClientConfig();
        laptopConfig.setClusterName("laptop");
        ClientNetworkConfig networkConfig = laptopConfig.getNetworkConfig();
        networkConfig.addAddress("127.0.0.1");

        ClientConfig ec2Config = new ClientConfig();
        ec2Config.setClusterName("ec2");
        ClientNetworkConfig networkConfig2 = ec2Config.getNetworkConfig();
        networkConfig2.addAddress( "13.52.250.90:5701", "13.52.98.42:5701");

        failoverConfig.addClientConfig(laptopConfig).addClientConfig(ec2Config);
        failoverConfig.setTryCount(2);
        return failoverConfig;
    }

    private static void testOpenShiftPublisher(ClientConfig config) throws InterruptedException {
        config.getNetworkConfig().addAddress("15.236.118.148:5701");
        config.setClusterName("openshift");
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IMap map_1 = instance.getMap("Multicloud_Map");
        map_1.clear();
        for(int i=0; i<20; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map1 Set: "+i);
        }
        IMap map_2 = instance.getMap("Test_Map");
        map_2.clear();
        for(int i=0; i<25; i++) {
            map_2.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map2 Set: "+i);
        }
        System.out.println("Map Set Complete");
    }

    private static void testEC2Publisher(ClientConfig config) throws InterruptedException {
        config.getNetworkConfig().addAddress("ec2-54-193-228-96.us-west-1.compute.amazonaws.com:5701");
        config.setClusterName("ec2");
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IMap map_1 = instance.getMap("Multicloud_Map");
        map_1.clear();
        for(int i=0; i<100; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map1 Set: "+i);
        }
        IMap map_2 = instance.getMap("Test_Map");
        map_2.clear();
        for(int i=0; i<150; i++) {
            map_2.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map2 Set: "+i);
        }
        System.out.println("Map Set Complete");
    }

    private static void testOnPremPublisher(ClientConfig config) throws InterruptedException {
        config.getNetworkConfig().addAddress("127.0.0.1:5701");
        config.setClusterName("laptop");
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IMap map_1 = instance.getMap("Multicloud_Map");
        map_1.clear();
        for(int i=0; i<300; i++) {
            map_1.set(i, i);
            TimeUnit.MILLISECONDS.sleep(1);
            System.out.println("Map1 Set: "+i);
        }
        IMap map_2 = instance.getMap("Test_Map");
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

        IMap map_1 = instance.getMap("Multicloud_Map");
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
