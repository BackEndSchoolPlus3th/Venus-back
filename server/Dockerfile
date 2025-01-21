FROM openjdk:21-jdk-slim

WORKDIR /app

# COPY는 Docker Compose 파일의 위치를 기반으로 작동함
COPY . .

# 개행문자 오류 해결 (Windows에서 Unix 시스템으로 변환)
RUN sed -i 's/\r$//' gradlew

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# Gradle 빌드 실행
RUN ./gradlew clean build

# 빌드된 JAR 파일의 위치를 지정하고, 해당 파일을 /app/app.jar로 이동
ENV JAR_PATH=/app/build/libs
RUN mv ${JAR_PATH}/*.jar /app/app.jar

# 애플리케이션을 실행하는 명령어
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
