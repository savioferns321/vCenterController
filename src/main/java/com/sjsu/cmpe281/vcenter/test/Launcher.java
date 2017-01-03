package com.sjsu.cmpe281.vcenter.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.sjsu.cmpe281.vcenter.main.VmCommandCenter;

public class Launcher {

	private static final Pattern PATTERN = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


	public static void main(String[] args) {
		System.out.println("CMPE HW2 from Savio Fernandes");
		VmCommandCenter commandCenter = new VmCommandCenter(args[0], args[1], args[2]);
		boolean flag = true;

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			while(flag){
				System.out.print("SavioFernandes-480> ");

				String inputCmd = bufferedReader.readLine();
				inputCmd = inputCmd.trim();
				switch (inputCmd) {
				case "exit":
					flag = false;
					break;

				case "help":
					commandCenter.printHelpCommands();
					break;

				case "host":
					commandCenter.printHostSystems();
					break;

				case "vm":
					commandCenter.printVirtualMachines();
					break;

				default:
					handleCustomCommand(inputCmd, commandCenter);
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Invalid command");
			e.printStackTrace();
		} finally {
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(commandCenter != null){
				commandCenter.closeConnection();
			}
		}
	}

	public static void handleCustomCommand(String inputCmd, VmCommandCenter commandCenter){

		String[] commandArray = inputCmd.split("\\s+");
		if(commandArray == null || commandArray.length == 0){
			//No Command entered
			System.out.println("No command entered");
			return;
		}
		if(commandArray.length != 3){
			printInvalidCommandHeader(inputCmd);
			return;
		}
		switch (commandArray[0]) {
		case "host":
			//Host related commands
			//Validate IP address
			if(!validate(commandArray[1])){
				System.out.println("Invalid IP address entered : "+commandArray[1]);
				return;
			}
			String hostName = commandArray[1];
			//Validating the command
			switch (commandArray[2]) {
			case "info":
				commandCenter.printHostInfo(hostName);
				break;

			case "datastore":
				commandCenter.printHostDatastoreInfo(hostName);
				break;	

			case "network":
				commandCenter.printHostNetworkInfo(hostName);
				break;
			default:
				printInvalidCommandHeader(inputCmd);
				break;
			}
			break;

		case "vm":
			//VM related commands
			String vmName = commandArray[1];
			switch (commandArray[2]) {
			case "info":
				commandCenter.printVmInfo(vmName);
				break;

			case "on":
				commandCenter.powerOnVm(vmName);
				break;

			case "off":
				commandCenter.powerOffVm(vmName);
				break;

			case "shutdown":
				commandCenter.shutDownVm(vmName);
				break;

			default:
				break;
			}

			break;


		default:
			printInvalidCommandHeader(inputCmd);
			break;
		}
	}

	/**
	 * Validates a string to check if it is a valid IP address.
	 * @param ip The IP address to be checked.
	 * @return True if IP address is valid, false otherwise.
	 */
	public static boolean validate(final String ip) {
		return PATTERN.matcher(ip).matches();
	}

	/**
	 * Prints statement for an invalid command.
	 * @param cmd The invalid command input.
	 */
	public static void printInvalidCommandHeader(String cmd){
		System.out.println("Invalid command entered : "+cmd);
	}
}
