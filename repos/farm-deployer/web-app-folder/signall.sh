for i in $(find $1 -name '*.jar' -and -type f); do
jarsigner -J-Xmx512m -keystore farm.keystore -storepass changeit $i deployer
done
