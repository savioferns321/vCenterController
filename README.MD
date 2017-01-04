This project is a Java-based program to interact with a VMWare vCenter server.



Description:

This is a Java-based command line application based on VMWare's vijava Driver with three command line arguments passed to a running instance ofx Launcher.java
IP login password
where IP is the IP of CMPE vCenter server, login and password are the ones you used to connect to CMPE vCenter
server. For example,
130.65.159.14 username password vmName

A user can enter one of the following commands: 

1. exit - exit the program

2. help - Print out the usage, e.g., the
entire list of commands

3. host - Enumerate all hosts. Eg: 
host[0]: Name = xxx.xx.xxx.xx1
host[1]: Name = xxx.xx.xxx.xx2
...	

4. host hname info - Show info of host hname, e.g., host xxx.xx.xxx.xx info
Name = xxx.xx.xxx.xx
ProductFullName = VMware ESXi 5.5.0 build-1623387
Cpu cores = 24
RAM = 255 GB

5. host hname datastore - Enumerate datastores of host hname, e.g., host xxx.xx.xxx.xx datastore
Datastore[0]: name=..., capacity = ... GB, FreeSpace = ... GB.
Datastore[1] = ...

6. Host hname network - Enumerate networks of host hname, e.g., host xxx.xx.xxx.xx network
Name = xxx.xx.xxx.xx
Network[0]: name=VM Network
Network[1]: name=...

7. vm - Enumerate all virtual machines, e.g., : vm 
vm[0]: Name = demo-centos7-123
vm[1]: Name = vm1
...

8. vm vname info - Show info of VM vname, e.g., vm demo-centos7-123 info
Name = demo-centos7-123
Guest full name = CentOS 4/5/6 (64-bit)
Guest state = notRunning
IP addr = xx.xxx.xxx.xx
Tool running status = guestToolsNotRunning
Power state = poweredOff

9. vm vname on - Power on VM vname and wait until task completes, e.g., vm demo-centos7-123 on
Name = demo-centos7-123
Power on VM: status = success, completion time = 08/19/2016
15:05:22

OR

Name = demo-centos7-123
Power on VM: status = The attempted operation cannot be
performed in the current state (Powered on)., completion time =
08/19/2016 15:06:31

10. vm vname off - Power off VM vname and wait until task completes, e.g., vm demo-centos7-123 off
Name = demo-centos7-123
Power off VM: status = success, completion time = 08/19/2016
15:07:25

OR 

Name = demo-centos7-123
Power off VM: status = The attempted operation cannot be
performed in the current state (Powered off)., completion time =
08/19/2016 15:09:46

11. vm vname shutdown - Shutdown guest of VM vname, e.g., vm demo-centos7-123 shutdown
Name = demo-centos7-123
Shutdown guest: completed, time = 08/19/2016 15:31:42

OR 

Name = demo-centos7-123
Shutdown guest: completed, time = 08/19/2016 15:31:42
Name = demo-centos7-123
Graceful shutdown failed. Now try a hard power off.
Power off VM: status = success, completion time = 08/19/2016
20:09:32

