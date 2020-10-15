## Indy SDK for Android

This Android wrapper currently API level 21 or higher.

Pull requests welcome!

### How to install
1. Add maven repository
    maven {
        url = "https://repo.sovrin.org/repository/maven-public"
    }

2. Add dependency
    
    implementation("org.hyperledger.android:indy:1.15.0")

Note that before you can use java wrapper you must install  c-callable SDK. 
See the section "Installing the SDK" in the [Indy SDK documentation](../../README.md#installing-the-sdk)
### How to build

First, build the native "indy" library at https://github.com/hyperledger/indy-sdk:

	cargo build

Then copy the resulting `libindy.so` to `./lib/`.

Then run

    ./gradlew assambleDebug

### Example use
For the main workflow examples check test folder: https://github.com/hyperledger/indy-sdk/tree/master/wrappers/android/src/androidTest/java/org/hyperledger/indy/sdk

#### Logging
The Android wrapper uses slf4j as a facade for various logging frameworks, such as java.util.logging, logback and log4j.
