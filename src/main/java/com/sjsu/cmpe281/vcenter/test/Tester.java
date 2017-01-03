package com.sjsu.cmpe281.vcenter.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DecimalFormat;

import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class Tester {
	public static void main(String[] args) throws RemoteException, MalformedURLException {

		String vmName = "<vmName>";
		ServiceInstance si = new ServiceInstance(new URL("<vCenter Server URL>"), "<vCenter username>",
				"<vCenter password>", true);
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);

		System.out.println("----------Virtual Machines------------");
		ManagedEntity[] vmManagedEntity = new
				InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		if (vmManagedEntity == null || vmManagedEntity.length == 0) {
			return;
		}
		for (ManagedEntity me: vmManagedEntity) {
			
			VirtualMachine vm = (VirtualMachine) me;
			if(vm.getName().equals(vmName)){
				VirtualMachineConfigInfo vminfo = vm.getConfig();
				VirtualMachineCapability vmc = vm.getCapability();
				System.out.println("Hello " + vm.getName());
				System.out.println("GuestOS: " + vminfo.getGuestFullName());
				System.out.println("Guest State: " + vm.getGuest().getGuestState());
				System.out.println("Guest IP address: " + vm.getGuest().getIpAddress());
				System.out.println("Guest Tools running status: " + vm.getGuest().getToolsRunningStatus());
				System.out.println("Guest App state: " + vm.getSummary().getRuntime().getPowerState());
				System.out.println("Multiple snapshot supported: " +
						vmc.isMultipleSnapshotsSupported());
				System.out.println("Trying to start up VM");
				
				Task task = vm.powerOffVM_Task();
				try {
					if(task.waitForTask().equals(Task.SUCCESS)){
						System.out.println("Powered on!");
						System.out.println("Task completed at : " + task.getTaskInfo().getCompleteTime());
						System.out.println("Task state is : " + task.getTaskInfo().getState());
					}else{
						vm.getGuest().getGuestState();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}


		si.getServerConnection().logout();
	}

	public static String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
