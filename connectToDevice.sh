FARM_IP=192.168.192.227
DEVICEPORT=$(curl $FARM_IP:8000)
ps xu | grep @$FARM_IP | grep -v grep | awk '{ print $2 }' | xargs kill -9
ssh pi@$FARM_IP -L $DEVICEPORT:localhost:$DEVICEPORT -f -N
adb connect 127.0.0.1:$DEVICEPORT
