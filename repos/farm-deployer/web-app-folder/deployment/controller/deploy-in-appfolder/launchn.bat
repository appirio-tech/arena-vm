set FARM_LAUNCH_PATH=C:\controller\launcher
set FARM_DEPLOY_PATH=C:\controller\deploy
:loop
mkdir %FARM_DEPLOY_PATH%
cd %FARM_DEPLOY_PATH%

for /F %%V in (%FARM_LAUNCH_PATH%\idfile) do java -XX:+UseConcMarkSweepGC -Xms128m -Xmx640m -cp %FARM_LAUNCH_PATH%\netx-0.5.2.jar netx.jnlp.runtime.Boot -basedir %FARM_LAUNCH_PATH% -verbose -noupdate -nosecurity -headless -jnlp http://localhost:8080/farm-deployer/launch.jnlp?type=controller^&id=%%V
sleep 3
goto loop