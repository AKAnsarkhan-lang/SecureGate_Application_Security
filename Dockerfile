
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY src ./src

RUN mkdir -p out && javac -d out src/main/java/com/securegate/SecureGate.java

EXPOSE 8080

CMD ["sh", "-c", "java -cp out com.securegate.SecureGate"]

