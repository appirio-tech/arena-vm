while true; do
        ID=$(cat idfile)
        echo $ID
        javaws -wait http\://localhost\:8080/farm-deployer/launch.jnlp\?type=processor\&id=$ID
done
