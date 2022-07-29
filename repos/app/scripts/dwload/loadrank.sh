nohup java com.topcoder.utilities.dwload.TCLoadUtility -xmlfile loadrank.xml > ./nohup.out 2>&1 &
#nohup java com.topcoder.utilities.dwload.TCLoadUtility -xmlfile loadallrank.xml > ./nohup.out 2>&1 &
tail -f nohup.out

