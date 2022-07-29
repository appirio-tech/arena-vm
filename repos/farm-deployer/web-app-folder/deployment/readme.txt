This folder is used by the deployer to provide 
personalized configuration and files to processors and controllers.

Default controller files and configuration should be in 
controller folder.
Default processor files and configuration should be in 
processor folder.

Specific controller files and configuration should be in
a folder named controller-{CONTROLLER_NAME} without the braces.

Specific processor files and configuration should be in
a folder named processor-{PROCESSOR_NAME} without the braces.

Inside each folder there must exist one file named config.xml that
should contain configuration information for the node. 
A deployment.properties properties file with the following properties:

version: containing the version number of the generated resource jar. This number must 
		be increased each time a file is changed/added to one of the deploy-* folders.
jars: a command separated list of jars that should be deployed in the node.


A file named system-properties.properties will define system properties for the node. The deployer will set
as system properties for the node the result of overlapping the system properties defined 
in the default folder with the system properties defined in the specific node folder.


Additionally, there must exist two folders: deploy-in-appfolder and deploy-in-classpath.
These folders are the deployable folders. They allow to dinamically change/add resources used 
by the application node.
The deploy-in-classpath folder must containg all classpath resources as log4j.xml configuration, etc.
The deploy-in-appfolder folder must contain files that are deployed in the application node directory and
can be accessed by other applications as compilers, or that could be modified by the application itself. 
There are special files in the deploy-in-appfolder folder,
	install.(bat|sh) files are post install scripts, that will be executed after deployment, in Linux is required
		that all files that will be executed from within the application must be set permission +x in this script
	run.(bat|sh) this are script to execute the application, after first installed, application should be run using this script.


A jar will be generated containing all files and folders in deploy-in-classpath 
directory and it also will hold a file named deploy.zip containing all files and folders in
deploy-in-appfolder. This file, deploy.zip, will be expanded as the application node 
directory and will be available for the node. When this is done, the previous contents of the 
application folder are removed.

The node will reconfigure log using the file log4j.xml in the deploy-in-classpath folder if exists.



