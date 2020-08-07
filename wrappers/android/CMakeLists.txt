cmake_minimum_required(VERSION 3.4.1)

set(JNI_LIBS_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs)
set(ANDROID_STL "c++_shared")

# add_library(gidcrypto-lib
#        SHARED)

# find_library(
#        log-lib
#         log)

# Include External Lib Headers
# include_directories("${JNI_LIBS_DIR}/include/universal/"
#         "${JNI_LIBS_DIR}/include/${ANDROID_ABI}/")

# LibIndy
add_library(indy
        SHARED
        IMPORTED)

set_target_properties(
        indy

        PROPERTIES IMPORTED_LOCATION

        ${JNI_LIBS_DIR}/lib/${ANDROID_ABI}/libindy.so)

# Target
# target_link_libraries(
#        libindy)

