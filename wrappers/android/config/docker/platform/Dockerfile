# ====================================================================== #
# Android SDK Emulator Docker Image for Jenkins
# ====================================================================== #

# Base image
# ---------------------------------------------------------------------- #
FROM kaminomobile/android-platform-30:1

# Author
# ---------------------------------------------------------------------- #
LABEL blazsolar "blaz@kamino.si"

ARG hostUid
ARG hostGid

USER root

RUN groupmod -g ${hostGid} jenkins && \
    usermod -u ${hostUid} jenkins

ENV CI=true

USER jenkins
WORKDIR /home/jenkins
