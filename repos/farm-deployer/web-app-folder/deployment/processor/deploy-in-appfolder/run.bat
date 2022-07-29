:loop
for /F %%V in (idfile) do launcher.exe PR-LAUNCH javaws -wait http://localhost:8080/farm-deployer/launch.jnlp?type=processor^&id=%%V
goto loop