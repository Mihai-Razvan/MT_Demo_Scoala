FROM openjdk:17
COPY .target/classes/com/example/demo_scoala/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","DemoScoalaApplication"]
