LAUNCH=$HOME/controller/launcher
DEPLOY=$HOME/controller/deploy
while true; do
        ID=$(cat $LAUNCH/idfile)
        mkdir $DEPLOY
        cd $DEPLOY
        java -XX:+UseConcMarkSweepGC -Xms128m -Xmx640m -cp $LAUNCH/netx-0.5.1.jar netx.jnlp.runtime.Boot -verbose -basedir $LAUNCH -nosecurity -noupdate -headless -jnlp http\://farm-controller.topcoder.com\:8080/farm-deployer/launch.jnlp\?type=controller\&id=$ID
	sleep 3
done
