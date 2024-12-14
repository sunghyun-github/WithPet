# 1. Java 21 Base 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. 애플리케이션 JAR 파일 이름 정의
ARG JAR_FILE=build/libs/mypet-0.0.1-SNAPSHOT.jar

# 3. JAR 파일을 Docker 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 4. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
