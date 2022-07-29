for %%f in (%1/*.jar) do jarsigner -J-Xmx512m -keystore farm.keystore -storepass changeit %1/%%f deployer
