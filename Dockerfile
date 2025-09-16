FROM eclipse-temurin:21-jdk

EXPOSE 8080

RUN rm -rf /app && \
    mkdir -p /app

WORKDIR /app/

COPY bh/*.jar app.jar

RUN chmod 755 app.jar

ENV LC_ALL="ru_RU.UTF-8" \
    LANG="ru_RU.UTF-8" \
    LANGUAGE="ru_RU.UTF-8"

ENV INTERNAL_ARGS="\
-Dfile.encoding=UTF-8 \
-Dsun.stderr.encoding=UTF-8 \
-Dsun.stdout.encoding=UTF-8 \
-Duser.timezone=Europe/Moscow \
-Djava.security.egd=file:/dev/./urandom \
-Djava.awt.headless=true \
-XX:+UseG1GC \
-XX:G1HeapRegionSize=4M \
-XX:MaxGCPauseMillis=20 \
-XX:+AlwaysActAsServerClassMachine \
-XX:+ExitOnOutOfMemoryError \
-XX:+UseContainerSupport \
--enable-preview \
"
ENV SPRING_PROFILES_ACTIVE="prom"

RUN adduser -u 1001 --no-create-home --shell /sbin/nologin appuser && \
    chown -R appuser:appuser /app/
USER appuser


ENTRYPOINT exec java $JAVA_OPTS $INTERNAL_ARGS -jar ./app.jar
