set FARM_LAUNCH_PATH=C:\processor\launcher
set FARM_DEPLOY_PATH=C:\processor\deploy
:loop
mkdir %FARM_DEPLOY_PATH%
cd %FARM_DEPLOY_PATH%

for /F %%V in (%FARM_LAUNCH_PATH%\idfile) do java -Xmx128m -cp %FARM_LAUNCH_PATH%\netx-0.5.1.jar netx.jnlp.runtime.Boot -basedir %FARM_LAUNCH_PATH% -noupdate -nosecurity -headless -jnlp http://farm-controller.topcoder.com:8080/farm-deployer/launch.jnlp?type=processor^&id=%%V
sleep 3
goto loop
