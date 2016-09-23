#!/usr/bin/env bash

LIB_VERSION="b96f9ca"
LIB_ZIP_URL="https://github.com/mik3y/usb-serial-for-android/archive/b96f9ca7a25f44e997e1b5cb5746eb8082716168.zip"
LIB_PATH="./libs/com/hoho/usb-serial-for-android"

# Use android command to install the following dependencies
# Required Tools:
#  - Android SDK Build-tools: 19.1
#  - Android SDK Build-tools: 22.0.1
# Required SDK Platforms:
#  - SDK Platform: 22
#  - SDK Platform: 19

cd ${LIB_PATH}
mkdir ${LIB_VERSION}
cd ${LIB_VERSION}
rm -fr usb-serial-for-android-*
curl -L -o ${LIB_VERSION}.zip ${LIB_ZIP_URL}
unzip ${LIB_VERSION}.zip
cd usb-serial-for-android-*
./gradlew build
cp -f usbSerialForAndroid/build/outputs/aar/usbSerialForAndroid-release.aar \
  ../usb-serial-for-android-${LIB_VERSION}.aar
cp -f ../../usb-serial-for-android-template.pom ../usb-serial-for-android-${LIB_VERSION}.pom
sed -i -e "s/%LIB_VERSION%/${LIB_VERSION//\//\\/}/g" ../usb-serial-for-android-${LIB_VERSION}.pom
cd ..
jar xf usb-serial-for-android-${LIB_VERSION}.aar classes.jar
mv classes.jar usb-serial-for-android-${LIB_VERSION}.jar
