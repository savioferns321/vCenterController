package com.sjsu.cmpe281.vcenter.main;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VmCommandCenter {

	private ServiceInstance _instance;
	private static final double TIMEOUT_IN_MINUTES = 3.0;

	public VmCommandCenter(String vCenterIp, String username, String password){
		try {
			_instance = new ServiceInstance(new URL("https://"+vCenterIp+"/sdk"), username, password, true);
		} catch (RemoteException e) {
			System.out.println(String.format("Error encountered while trying to connect to IP : %s with username : %s"
					+ " and password : %s", vCenterIp, username, password));
		} catch (MalformedURLException e) {
			System.out.println(String.format("URL %s is not formed correctly",vCenterIp));
		}
	}

	/**
	 * Closes the connection to the vCenterServer ServiceInstance.
	 */
	public void closeConnection(){
		_instance.getServerConnection().logout();
	}

	/**
	 * Searches for a host system specified by IP address
	 * @param hostIp The IP address of the requested host system
	 * @return The requested Host System, or null if not found
	 * @throws Exception 
	 */
	public HostSystem searchHostSystem(String hostIp) throws Exception{
		ManagedEntity[] mes = new
				InventoryNavigator(_instance.getRootFolder()).searchManagedEntities("HostSystem");
		for (ManagedEntity managedEntity : mes) {
			HostSystem currHostSystem = (HostSystem)managedEntity;
			if(currHostSystem.getName().equals(hostIp))
				return currHostSystem;
		}
		throw new Exception(String.format("Host System with IP address : %s not found.", hostIp));
	}

	/**
	 * Searches for a virtual machine specified by VM name
	 * @param vmName The name of the virtual machine
	 * @return The requested Virtual Machine, or null if not found
	 * @throws Exception 
	 */
	public VirtualMachine searchVirtualMachine(String vmName) throws Exception{
		ManagedEntity[] mes = new
				InventoryNavigator(_instance.getRootFolder()).searchManagedEntities("VirtualMachine");
		for (ManagedEntity managedEntity : mes) {
			VirtualMachine virtualMachine = (VirtualMachine)managedEntity;
			if(virtualMachine.getName().equals(vmName))
				return virtualMachine;
		}
		throw new Exception(String.format("VM with name : %s not found.", vmName));
	}

	/**
	 * Prints the host IP addresses of all the host systems found on the network
	 * @throws InvalidProperty
	 * @throws RuntimeFault
	 * @throws RemoteException
	 */
	public void printHostSystems() throws InvalidProperty, RuntimeFault, RemoteException{
		ManagedEntity[] mes = new
				InventoryNavigator(_instance.getRootFolder()).searchManagedEntities("HostSystem");
		for (int i = 0; i < mes.length; i++) {
			HostSystem currHostSystem = (HostSystem)mes[i];
			System.out.printf("host[%d]: Name = %s\n", i, currHostSystem.getName());
		}
	}

	/**
	 * Prints the names of all the virtual machines found on the network
	 * @throws InvalidProperty
	 * @throws RuntimeFault
	 * @throws RemoteException
	 */
	public void printVirtualMachines() throws InvalidProperty, RuntimeFault, RemoteException{
		ManagedEntity[] mes = new
				InventoryNavigator(_instance.getRootFolder()).searchManagedEntities("VirtualMachine");
		for (int i = 0; i < mes.length; i++) {
			VirtualMachine currVm = (VirtualMachine)mes[i];
			System.out.printf("vm[%d]: Name = %s\n", i, currVm.getName());
		}
	}

	/**
	 * Function called for a 'help' command. Prints out the list of commands and
	 * their descriptions
	 */
	public void printHelpCommands(){
		//Construct the help command output
		printAsTable("exit",		 				"Exit the  program.");
		printAsTable("help",		 				"Print out the usage.");
		printAsTable("host",						"Enumerate all hosts.");
		printAsTable("host hname info", 			"Show info of host name.");
		printAsTable("host hname datastore", 		"Enumerate datastores of host hname.");
		printAsTable("host hname network", 			"Enumerate networks of host hname.");
		printAsTable("vm", 							"Enumerate all virtual machines.");
		printAsTable("vm vname info", 				"Show info of VM vname.");
		printAsTable("vm vname on", 				"Power on VM vname and wait until task completes.");
		printAsTable("vm vname off", 				"Power off VM vname and wait until task completes.");
		printAsTable("vm vname shutdown", 			"Shutdown guest of VM vname.");			
	}

	/**
	 * Helper function to print out the values of the 'help' command
	 * in tabular format
	 * @param command The command name
	 * @param description The command description
	 */
	private void printAsTable(String command, String description){
		System.out.printf("%-30.30s  %-60.60s%n", command, description);
	}

	/**
	 * Method for the 'host hname info' command.
	 * @param hostName The hostname(IP address) whose info is to be printed.
	 * @throws Exception
	 */
	public void printHostInfo(String hostName){
		HostSystem hostSystem;
		try {
			hostSystem = searchHostSystem(hostName);
			System.out.println("Name = "+hostName);
			System.out.println("ProductFullName = "+hostSystem.getConfig().getProduct().getFullName());
			System.out.println("Cpu cores = "+hostSystem.getHardware().getCpuInfo().getNumCpuCores());
			System.out.println("RAM = "+readableFileSize(hostSystem.getHardware().getMemorySize()));
		} catch (Exception e) {
			printInvalidHostNameMessage(hostName);
		}
	}

	/**
	 * Generates the human readable byte size from the number of bytes
	 * sent in input.
	 * @param size The memory size in bytes.
	 * @return A string representation of the memory in readable format.
	 */
	public String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * Method for the 'host hname datastore' command.
	 * @param hostName The info of all the data stores of this host name are printed.
	 */
	public void printHostDatastoreInfo(String hostName){
		HostSystem hostSystem;
		try {
			hostSystem = searchHostSystem(hostName);
			Datastore[] dataStoreArray = hostSystem.getDatastores();
			System.out.println("Name = "+hostName);
			for (int i = 0; i < dataStoreArray.length; i++) {
				System.out.printf("Datastore[%d]: name = %s, capacity = %s, FreeSpace = %s\n", 
						i, dataStoreArray[i].getName(), 
						readableFileSize(dataStoreArray[i].getSummary().getCapacity()),
						readableFileSize(dataStoreArray[i].getSummary().getFreeSpace()));
			}
		} catch (Exception e) {
			printInvalidHostNameMessage(hostName);
		}
	}

	/**
	 * Method for the 'host hname network' command.
	 * @param hostName The info of all the networks of this host name are printed.
	 */
	public void printHostNetworkInfo(String hostName){
		HostSystem hostSystem;
		try {
			hostSystem = searchHostSystem(hostName);
			Network[] networkArray = hostSystem.getNetworks();
			System.out.println("Name = "+hostName);
			for (int i = 0; i < networkArray.length; i++) {
				System.out.printf("Network[%d]: name = %s\n", i, networkArray[i].getName());
			}
		} catch (Exception e) {
			printInvalidHostNameMessage(hostName);
		}

	}

	/**
	 * Method for the 'vm vname info' command.
	 * @param vmName The vm name whose info is to be printed.
	 */
	public void printVmInfo(String vmName){
		try {
			VirtualMachine virtualMachine = searchVirtualMachine(vmName);
			GuestInfo guestInfo = virtualMachine.getGuest();
			System.out.println("Name = "+vmName);
			System.out.println("Guest full name = "+guestInfo.getGuestFullName());
			System.out.println("Guest state = "+guestInfo.getGuestState());
			System.out.println("IP addr = "+guestInfo.getIpAddress());
			System.out.println("Tool running status = "+guestInfo.getToolsRunningStatus());
			System.out.println("Power state = "+virtualMachine.getRuntime().getPowerState());
		} catch (Exception e) {
			printInvalidVmMessage(vmName);
		}
	}

	/**
	 * Method for the 'vm vName on' command. Starts up the specified vm
	 * @param vmName Name of the specified VM.
	 */
	public void powerOnVm(String vmName){
		VirtualMachine virtualMachine;
		String status;
		try {
			virtualMachine = searchVirtualMachine(vmName);
		} catch (Exception e) {
			printInvalidVmMessage(vmName);
			return;
		}
		System.out.println("Name = "+vmName);
		Task startupVmTask = null;
		try {
			startupVmTask = virtualMachine.powerOnVM_Task(null);
			System.out.print("Power on VM: status = ");
			status = startupVmTask.waitForTask();
			if(status.equals("error")){
				status = startupVmTask.getTaskInfo().getError().getLocalizedMessage();
			}
			System.out.println(status+", completion time = "+formatCompletionTime(startupVmTask.getTaskInfo().getCompleteTime().getTimeInMillis()));
		} catch (RemoteException e) {
			try {
				System.out.println(e.getLocalizedMessage()
						+ ", completion time : "+formatCompletionTime(startupVmTask.getTaskInfo().getCompleteTime().getTimeInMillis())+"\n");
			} catch (RemoteException e1){
				e1.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println();
		}
	}

	/**
	 * Method for the 'vm vName off' command. Powers off the specified vm.
	 * @param vmName Name of the specified VM.
	 */
	public void powerOffVm(String vmName){
		VirtualMachine virtualMachine;
		String status;
		Task powerOffVmTask = null;
		try {
			virtualMachine = searchVirtualMachine(vmName);
		} catch (Exception e) {
			printInvalidVmMessage(vmName);
			return;
		}
		System.out.println("Name = "+vmName);
		try {
			powerOffVmTask = virtualMachine.powerOffVM_Task();
			status = powerOffVmTask.waitForTask();
			System.out.print("Power off VM: status = ");
			if(status.equals("error")){
				status = powerOffVmTask.getTaskInfo().getError().getLocalizedMessage();
			}
			System.out.println(status+", completion time = "+formatCompletionTime(powerOffVmTask.getTaskInfo().getCompleteTime().getTimeInMillis()));
		} catch (RemoteException e) {
			try {
				System.out.println(e.getLocalizedMessage()
						+",completion time : "+formatCompletionTime(powerOffVmTask.getTaskInfo().getCompleteTime().getTimeInMillis()));
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println();
		}


	}

	/**
	 * Method for the 'vm vName shutdown' command. Shuts down the specified vm.
	 * @param vmName Name of the specified VM.
	 */
	public void shutDownVm(String vmName){
		VirtualMachine virtualMachine;
		boolean isShutdownComplete = false;
		long startTime;
		long completionTime = 0;
		try {
			virtualMachine = searchVirtualMachine(vmName);
		} catch (Exception e) {
			printInvalidVmMessage(vmName);
			return;
		}
		System.out.println("Name = "+vmName);
		try {
			virtualMachine.shutdownGuest();
			startTime = System.currentTimeMillis();

			/**
			 * Extra credit part
			 */
			//Poll the machine status in each loop to get the status.
			while(!isTimeUp(startTime)){
				//Check the machine status.
				if(VirtualMachinePowerState.poweredOff.equals(virtualMachine.getRuntime().getPowerState())){
					isShutdownComplete = true;
					completionTime = System.currentTimeMillis();
					break;
				}
			}
			if(!isShutdownComplete){
				throw new RemoteException();
			}else{
				System.out.println("Shutdown guest: completed, completion time = "+formatCompletionTime(completionTime));
			}
		} catch (RemoteException e) {
			//Shutdown operation is not complete. Force shutdown.
			System.out.print("Graceful shutdown failed. Now try a hard power off.\nPower off VM: status: ");
			Task forcePowerOffTask;
			try {
				forcePowerOffTask = virtualMachine.powerOffVM_Task();
				String forceShutdownStatus = forcePowerOffTask.waitForTask();
				System.out.println(forceShutdownStatus+" completion time = "+formatCompletionTime(completionTime));
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		}
	}

	/**
	 * Helper method to get the completion time e.g. 08/19/2016 15:05:22 for the specified timestamp.
	 * @param completionTime The time-stamp to be formatted.
	 * @return The formatted date and time.
	 */
	public String formatCompletionTime(long completionTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return dateFormat.format(new Date(completionTime));
	}

	/**
	 * Helper function to print out an "Invalid hostname" message.
	 * @param hostname The invalid hostname.
	 */
	public void printInvalidHostNameMessage(String hostname){
		System.out.println("Host name is invalid : "+hostname);
	}

	/**
	 * Helper function to print out an "Invalid hostname" message.
	 * @param vmName The invalid VM name.
	 */
	public void printInvalidVmMessage(String vmName){
		System.out.println("VM name is invalid : "+vmName);
	}

	/**
	 * Helper function to determine whether the difference between 
	 * current time and the input time is greater than the timeout value.
	 * (set to 3 minutes by default)
	 * @param startTime The start time.
	 * @return True, if the time difference is greater than timeout value, false otherwise.
	 */
	public boolean isTimeUp(long startTime){
		long diff = System.currentTimeMillis() - startTime;
		return diff/(1000 * 60)%60 > TIMEOUT_IN_MINUTES;
	}
}
