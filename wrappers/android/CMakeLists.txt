cmake_minimum_required(VERSION 3.4.1)

set(JNI_LIBS_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs)
set(ANDROID_STL "c++_shared")

# LibIndy
add_library(indy
        SHARED
        IMPORTED)

set_target_properties(
        indy

        PROPERTIES IMPORTED_LOCATION

        ${JNI_LIBS_DIR}/lib/${ANDROID_ABI}/libindy.so)
