#! /bin/bash
jarsigner -J-Xmx512m -keystore farm.keystore -storepass changeit $1 deployer 
