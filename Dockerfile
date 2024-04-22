FROM postgres:latest as gearhead_account_db
ENV POSTGRES_USER account
ENV POSTGRES_PASSWORD account123
ENV POSTGRES_DB account_service
ENV POSTGRES_PORT=5433
# Create directory for PostgreSQL configuration if it doesn't exist
RUN mkdir -p /etc/postgresql/

# Update PostgreSQL configuration to use the specified port
RUN echo "port = $POSTGRES_PORT" >> /etc/postgresql/postgresql.conf

# Expose the specified port
EXPOSE $POSTGRES_PORT

FROM openjdk:17 as gearhead_identity

# Information of owner or maintainer of image
MAINTAINER gearhead

# Add the application's jar to the container
COPY target/identity-service-0.0.1-SNAPSHOT.jar identity-service-0.0.1-SNAPSHOT.jar

#Execute the application
ENTRYPOINT ["java", "-jar","/identity-service-0.0.1-SNAPSHOT.jar"]