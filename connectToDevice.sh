DEVICEPORT=$(curl $FARM_IP:8000)
ssh -f pi@$FARM_IP -L $DEVICEPORT:localhost:$DEVICEPORT 
adb connect $FARM_IP:$DEVICEPORT
