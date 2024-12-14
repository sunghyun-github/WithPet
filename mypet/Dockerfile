FROM jenkins/jenkins:lts

# Java 21 설치 (예시: Ubuntu 기반)
RUN apt-get update && apt-get install -y openjdk-21-jdk

# JAVA_HOME 환경 변수 설정
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Jenkins 실행
USER jenkins
