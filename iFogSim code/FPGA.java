package org.fog.test.perfeval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;
// based on DNCS example
//FPGA at Cloud level
public class FPGA {
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	private static boolean CLOUD = false;
	
	public static void main(String[] args) {

		Log.printLine("Starting DCNS...");

		try {
			Log.disable();
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			String appId = "dcns"; // identifier of the application
			
			FogBroker broker = new FogBroker("broker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			createFogDevices(broker.getId(), appId);
			
			Controller controller = null;
			
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
			for(FogDevice device : fogDevices){
				if(device.getName().startsWith("m")){ // names of all Smart Cameras start with 'm' 
					moduleMapping.addModuleToDevice("motion_detector", device.getName());  // fixing 1 instance of the Motion Detector module to each Smart Camera
				}
			}
			moduleMapping.addModuleToDevice("user_interface", "cloud"); // fixing instances of User Interface module in the Cloud
			if(CLOUD){
				// if the mode of deployment is cloud-based
				moduleMapping.addModuleToDevice("object_detector", "cloud"); // placing all instances of Object Detector module in the Cloud
				moduleMapping.addModuleToDevice("object_tracker", "cloud"); // placing all instances of Object Tracker module in the Cloud
			}
			
			controller = new Controller("master-controller", fogDevices, sensors, 
					actuators);
			
			controller.submitApplication(application, 
					(CLOUD)?(new ModulePlacementMapping(fogDevices, application, moduleMapping))
							:(new ModulePlacementEdgewards(fogDevices, sensors, actuators, application, moduleMapping)));
			
			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());
			
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			Log.printLine("Example finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	
	/**
	 * Creates the fog devices in the physical topology of the simulation.
	 * @param userId
	 * @param appId
	 */
	private static void createFogDevices(int userId, String appId) {
		FogDevice cloud = createFogDeviceFPGA("cloud", 2660, 512, 1776, 1776, 0, 0.01 , 300, 300);//create the  FPGA cloud
		cloud.setParentId(-1);// set the parent id
		fogDevices.add(cloud);// add the cloud to the list of devices.
		FogDevice router = createFogDevice("d-"+1, 2800, 4000, 10000, 10000, 1, 0.0, 107.339, 83.4333);//create a router gateway.
		fogDevices.add(router);// add the router to the list
		router.setUplinkLatency(2); // latency of connection between router and cloud server is 2 ms
		router.setParentId(cloud.getId());//sets the cloud to the parent.
		String mobileId = 0+"-"+1;// creates a mobile id.
		FogDevice camera = addCamera(mobileId, userId, appId, router.getId(),1); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera.setUplinkLatency(2); // latency of connection between camera and router is 1 ms
		fogDevices.add(camera);
		mobileId = 0+"-"+2;
		FogDevice camera1 = addCamera(mobileId, userId, appId, router.getId(),2); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera1.setUplinkLatency(2); // latency of connection between camera and router is 1 ms
		fogDevices.add(camera1);
		mobileId = 0+"-"+3;
		FogDevice camera2 = addCamera(mobileId, userId, appId, router.getId(),3); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera2.setUplinkLatency(2); // latency of connection between camera and router is 1 ms
		fogDevices.add(camera2);
		//comment this block out to eliminate a branch of the topology 
		FogDevice router2 = createFogDevice("d-"+2, 2800, 4000, 10000, 10000, 1, 0.0, 107.339, 83.4333);//create a router gateway.
		fogDevices.add(router2);// add the router to the list
		router2.setUplinkLatency(2); // latency of connection between router and cloud server is 2 ms
		router2.setParentId(cloud.getId());//sets the cloud to the parent.
		mobileId = 0+"-"+4;// creates a mobile id.
		FogDevice camera4 = addCamera(mobileId, userId, appId, router2.getId(),1); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera4.setUplinkLatency(2); // latency of connection between camera and router is 2 ms
		fogDevices.add(camera4);
		mobileId = 0+"-"+5;
		FogDevice camera5 = addCamera(mobileId, userId, appId, router2.getId(),2); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera5.setUplinkLatency(2); // latency of connection between camera and router is 2 ms
		fogDevices.add(camera5);
		mobileId = 0+"-"+6;
		FogDevice camera6 = addCamera(mobileId, userId, appId, router2.getId(),3); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera6.setUplinkLatency(2); // latency of connection between camera and router is 2 ms
		fogDevices.add(camera6);
		
		
		//comment this block out to eliminate a branch of the topology
		FogDevice router3 = createFogDevice("d-"+3, 2800, 4000, 10000, 10000, 1, 0.0, 107.339, 83.4333);//create a router gateway.
		fogDevices.add(router3);// add the router to the list
		router3.setUplinkLatency(2); // latency of connection between router and cloud server is 2 ms
		router3.setParentId(cloud.getId());//sets the cloud to the parent.
		mobileId = 0+"-"+7;// creates a mobile id.
		FogDevice camera7 = addCamera(mobileId, userId, appId, router3.getId(),1); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera4.setUplinkLatency(2); // latency of connection between camera and router is 1 ms
		fogDevices.add(camera7);
		mobileId = 0+"-"+8;
		FogDevice camera8 = addCamera(mobileId, userId, appId, router3.getId(),2); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera8.setUplinkLatency(2); // latency of connection between camera and router is 1 ms
		fogDevices.add(camera8);
		mobileId = 0+"-"+9;
		FogDevice camera9 = addCamera(mobileId, userId, appId, router3.getId(),3); // adding a smart camera to the physical topology. Smart cameras have been modeled as fog devices as well.
		camera9.setUplinkLatency(2); // latency of connection between camera and router is 1 ms
		fogDevices.add(camera9);
	}

	
	private static FogDevice addCamera(String id, int userId, String appId, int parentId,int opt){
		if(opt==1) {//Options to determine the stats of the camera to be created. 
			FogDevice camera = createFogDevice("m-"+id, 1256, 1024, 40, 40, 2, 0, 87.53, 82.44);Sensor sensor = new Sensor("s-"+id, "CAMERA", userId, appId, new DeterministicDistribution(5)); // inter-transmission time of camera (sensor) follows a deterministic distribution
			sensors.add(sensor);
			Actuator ptz = new Actuator("ptz-"+id, userId, appId, "PTZ_CONTROL");
			actuators.add(ptz);
			sensor.setGatewayDeviceId(camera.getId());
			sensor.setLatency(1.0);  // latency of connection between camera (sensor) and the parent Smart Camera is 1 ms
			ptz.setGatewayDeviceId(camera.getId());
			ptz.setLatency(1.0);  // latency of connection between PTZ Control and the parent Smart Camera is 1 ms
			return camera;
		}
		else if (opt==2) {
			FogDevice camera = createFogDevice("m-"+id, 1536, 1024, 70, 70, 2, 0, 87.53, 82.44);Sensor sensor = new Sensor("s-"+id, "CAMERA", userId, appId, new DeterministicDistribution(5)); // inter-transmission time of camera (sensor) follows a deterministic distribution
			sensors.add(sensor);
			Actuator ptz = new Actuator("ptz-"+id, userId, appId, "PTZ_CONTROL");
			actuators.add(ptz);
			sensor.setGatewayDeviceId(camera.getId());
			sensor.setLatency(1.0);  // latency of connection between camera (sensor) and the parent Smart Camera is 1 ms
			ptz.setGatewayDeviceId(camera.getId());
			ptz.setLatency(1.0);  // latency of connection between PTZ Control and the parent Smart Camera is 1 ms
			return camera;
		}
		else if (opt==3) {
			FogDevice camera = createFogDevice("m-"+id, 847, 1024, 55, 55, 2, 0, 87.53, 82.44);Sensor sensor = new Sensor("s-"+id, "CAMERA", userId, appId, new DeterministicDistribution(5)); // inter-transmission time of camera (sensor) follows a deterministic distribution
			sensors.add(sensor);
			Actuator ptz = new Actuator("ptz-"+id, userId, appId, "PTZ_CONTROL");
			actuators.add(ptz);
			sensor.setGatewayDeviceId(camera.getId());
			sensor.setLatency(1.0);  // latency of connection between camera (sensor) and the parent Smart Camera is 1 ms
			ptz.setGatewayDeviceId(camera.getId());
			ptz.setLatency(1.0);  // latency of connection between PTZ Control and the parent Smart Camera is 1 ms
			return camera;
		}
		else {
			FogDevice camera = createFogDevice("m-"+id, 500, 1000, 10000, 10000, 3, 0, 87.53, 82.44);Sensor sensor = new Sensor("s-"+id, "CAMERA", userId, appId, new DeterministicDistribution(5)); // inter-transmission time of camera (sensor) follows a deterministic distribution
			sensors.add(sensor);
			Actuator ptz = new Actuator("ptz-"+id, userId, appId, "PTZ_CONTROL");
			actuators.add(ptz);
			sensor.setGatewayDeviceId(camera.getId());
			sensor.setLatency(1.0);  // latency of connection between camera (sensor) and the parent Smart Camera is 1 ms
			ptz.setGatewayDeviceId(camera.getId());
			ptz.setLatency(1.0);  // latency of connection between PTZ Control and the parent Smart Camera is 1 ms
			return camera;
		}
	}
	private static FogDevice createFogDeviceFPGA(String nodeName, long mips,
			int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new StreamOperatorScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "ARM"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		 FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
				arch, os,vmm, host, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fogdevice.setLevel(level);
		return fogdevice;
	}

	
	/**
	 * Creates a vanilla fog device
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS 
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static FogDevice createFogDevice(String nodeName, long mips,
			int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new StreamOperatorScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.3; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		 FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
				arch, os,vmm, host, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fogdevice.setLevel(level);
		return fogdevice;
	}

	/**
	 * Function to create the Intelligent Surveillance application in the DDF model. 
	 * @param appId unique identifier of the application
	 * @param userId identifier of the user of the application
	 * @return
	 */
	@SuppressWarnings({"serial" })
	private static Application createApplication(String appId, int userId){
		
		Application application = Application.createApplication(appId, userId);
		/*
		 * Adding modules (vertices) to the application model (directed graph)
		 */
		application.addAppModule("object_detector", 10);
		application.addAppModule("motion_detector", 10);
		application.addAppModule("object_tracker", 10);
		application.addAppModule("user_interface", 10);
		
		/*
		 * Connecting the application modules (vertices) in the application model (directed graph) with edges
		 */
		application.addAppEdge("CAMERA", "motion_detector", 1000, 20000, "CAMERA", Tuple.UP, AppEdge.SENSOR); // adding edge from CAMERA (sensor) to Motion Detector module carrying tuples of type CAMERA
		application.addAppEdge("motion_detector", "object_detector", 2000, 2000, "MOTION_VIDEO_STREAM", Tuple.UP, AppEdge.MODULE); // adding edge from Motion Detector to Object Detector module carrying tuples of type MOTION_VIDEO_STREAM
		application.addAppEdge("object_detector", "user_interface", 500, 2000, "DETECTED_OBJECT", Tuple.UP, AppEdge.MODULE); // adding edge from Object Detector to User Interface module carrying tuples of type DETECTED_OBJECT
		application.addAppEdge("object_detector", "object_tracker", 1000, 100, "OBJECT_LOCATION", Tuple.UP, AppEdge.MODULE); // adding edge from Object Detector to Object Tracker module carrying tuples of type OBJECT_LOCATION
		application.addAppEdge("object_tracker", "PTZ_CONTROL", 100, 28, 100, "PTZ_PARAMS", Tuple.DOWN, AppEdge.ACTUATOR); // adding edge from Object Tracker to PTZ CONTROL (actuator) carrying tuples of type PTZ_PARAMS
		
		/*
		 * Defining the input-output relationships (represented by selectivity) of the application modules. 
		 */
		application.addTupleMapping("motion_detector", "CAMERA", "MOTION_VIDEO_STREAM", new FractionalSelectivity(1.0)); // 1.0 tuples of type MOTION_VIDEO_STREAM are emitted by Motion Detector module per incoming tuple of type CAMERA
		application.addTupleMapping("object_detector", "MOTION_VIDEO_STREAM", "OBJECT_LOCATION", new FractionalSelectivity(1.0)); // 1.0 tuples of type OBJECT_LOCATION are emitted by Object Detector module per incoming tuple of type MOTION_VIDEO_STREAM
		application.addTupleMapping("object_detector", "MOTION_VIDEO_STREAM", "DETECTED_OBJECT", new FractionalSelectivity(0.05)); // 0.05 tuples of type MOTION_VIDEO_STREAM are emitted by Object Detector module per incoming tuple of type MOTION_VIDEO_STREAM
	
		/*
		 * Defining application loops (maybe incomplete loops) to monitor the latency of. 
		 * Here, we add two loops for monitoring : Motion Detector -> Object Detector -> Object Tracker and Object Tracker -> PTZ Control
		 */
		final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("motion_detector");add("object_detector");add("object_tracker");}});
		final AppLoop loop2 = new AppLoop(new ArrayList<String>(){{add("object_tracker");add("PTZ_CONTROL");}});
		List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);add(loop2);}};
		
		application.setLoops(loops);
		return application;
	}
}