#!/bin/bash
sleep 10 && adb connect 10.0.0.3:5555

#!/bin/bash
echo "Wait for emulator"
adb devices
adb wait-for-device

echo "Wait for boot"
while [ "`adb shell getprop sys.boot_completed | tr -d '\r' `" != "1" ] ;
do
  adb devices
  echo "Wait for boot..."
  sleep 1;
done

# Is this required?
echo "Unlock device"
adb shell input keyevent 82


./gradlew  connectedAndroidTest --console=verbose --no-daemon -Pandroid.builder.sdkDownload=false