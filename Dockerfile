FROM gradle:8.11.1-jdk17

USER root

ENV ANDROID_CMDLINE_TOOLS_VERSION=15859902
ENV ANDROID_SDK_URL=https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS_VERSION}_latest.zip

ENV ANDROID_HOME=/usr/local/android-sdk-linux

ENV ANDROID_API_LEVEL=android-36
ENV ANDROID_VERSION=36
ENV ANDROID_BUILD_TOOLS_VERSION=36.0.0

ENV ANDROID_NDK_VERSION=27.2.12479018
ENV ANDROID_NDK_HOME=${ANDROID_HOME}/ndk/${ANDROID_NDK_VERSION}

ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/build-tools/${ANDROID_BUILD_TOOLS_VERSION}:${ANDROID_HOME}/platform-tools
ENV PATH=${PATH}:${ANDROID_NDK_HOME}
ENV PATH=${PATH}:${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin

RUN mkdir -p "${ANDROID_HOME}/cmdline-tools" /home/gradle/.android && \
    cd /tmp && \
    curl -fL "${ANDROID_SDK_URL}" -o commandlinetools.zip && \
    unzip -q commandlinetools.zip && \
    mv cmdline-tools "${ANDROID_HOME}/cmdline-tools/latest" && \
    rm commandlinetools.zip

RUN yes | sdkmanager --sdk_root="${ANDROID_HOME}" --licenses
RUN sdkmanager \
    --sdk_root="${ANDROID_HOME}" \
    "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
    "platforms;android-${ANDROID_VERSION}" \
    "platform-tools" \
    "ndk;${ANDROID_NDK_VERSION}" \
    "cmake;3.22.1"

# Ensure Android SDK, NDK, and tools are readable/executable by any UID, and /home/gradle is accessible
RUN chmod -R a+rX "${ANDROID_HOME}" && \
    chmod 777 /home/gradle

WORKDIR /workspace

CMD ["./gradlew", ":TMessagesProj_App:assembleAfatRelease", ":TMessagesProj_App:bundleAfatRelease"]
