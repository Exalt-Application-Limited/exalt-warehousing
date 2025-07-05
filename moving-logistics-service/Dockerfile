FROM openjdk:17-jre-slim
WORKDIR /app
RUN groupadd -r appuser && useradd -r -g appuser appuser
COPY target/moving-logistics-service-*.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser
EXPOSE 8088
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8088/moving-logistics/actuator/health || exit 1
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
