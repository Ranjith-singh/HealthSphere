Architecture :
    frontend :
        the user communicates with the frontend
    controller :
        the frontend sends and receives data from the controller through http requests
        some of the data validition and constraints can be done at this level
    service :
        the controller communicates with service layer by sending and receiving dto's
        the dto filters the data to be sent and received
        the logic for incoming and outcoming data is filter
    repository :
        the service layer communicates with the repository layer to retrieve and send data
        the repository layer internally uses the jpa persistent to perform ETL ops on the database
        the data is in the form of models
    models :
        represents the schema of the database tables as entity

h2Database :
    url : http://localhost:4000/h2-console
    datasource : jdbc:h2:mem:testdb
    it uses in memory ram to store the data so data is volatile

patientService :
    note :
        you can't use autowired inside a method or to a entity not managed by Spring
    mvn project
    dependencies :
        Spring web, data jpa, validation, lombok
    database :
        set the java h2 database using /h2-console path
    models :
        create patient model
        run the springboot app server:
            it will automatically create the entity/tables if not present using jpa persistent dependency
    resources :
        the springboot application scans the resources folder which consist of application.yml file
        application.yml file:
            used for configuring database connections
            setting keys like tokens etc
        if resources consist of any other files like data.sql it will execute those files as well
    urls :
        getPatients :
            patient repository communicates to database with the help of patient model
            the retrieved data from the patient repository is convert to patientResponseDto using mapper
            then in the ResponseEntity body we pass the List of patientResponseDto
        createpatients :
            the requestBody passed is cross validated with patientRequestDto and errors are handled using errorHandler for methods arg
            errorHandler :
                handles errors occurs in the fields of the requestBody when compared with patientRequestDto
            mapper from dto to patient model is created
            EmailAlreadyExistsCheck :
                throw Exception EmailAlreadyExists and handle it
                add boolean function in repo that return value based on if email Exists
        updatePatients :
            patientId can be fetched from the path and patientRequestDto is passed
            search for patient and diff patient with patientRequestDto.email and handle the Exception
            change field registeredDate for class CreateUser
        deletePatients :
            check if patient exists in the database
            get the patient's data and delete the patient using patientId
            return the patientResponseDto

springdoc :
    add the dependency
    use tags and operation with name and summary to give info
    url : http://localhost:4000/v3/api-docs provides an openApi of your project
    pass the json to Swagger editor to get UI/UX of the cred operations

docker :
    create a image of software tools, database, redisCache and tier 3 microservices(your code)
    run the image to create and run the container which consist of all the configured software in the image
    avoids version compatible errors and ease of software import
    we can also clone the images present in the dockerhub ex :
        postgresDB
        python
    In our springboot project :
        create a image consisting of :
            java runtime(jdk)
            config files : application.properties and pom.xml
            microservices
        the image is stored in local dockerImageRegistry which can be run using docker cmds to create a container
        the image can be moved to cloud using dockerhub where it can be cloned and run on cloud servers or any other machines
        Dockerfile:
            docker images can be build from a docker file when we run the .jar files
            builder and runner are used to create and run the .jar files resp
            working :
                build:
                    in our case we use the maven:3.9.9-eclipse-temurin-21 as builder which consist of both jdk-21 and maven
                    specify the working directory as /app
                    copy the pom.xml file into to the app directory
                    download/prefetch the maven dependencies into the app directory for faster execution
                    copy the /src repository into the app/src directory
                    run mvn clean package :
                        To clean the /target repo for any previous created files
                        package the code into a executable .jar file and place it in /target
                    note :
                        the .jar file that is generated will be based on the artifactId of the project
                run :
                    we use openjdk:21-jdk as runner
                    specify the working directory as /app
                    copy the .jar file from /app/target/<filename> to /app.jar
                    specify the port as 4000
                    specify the entrypoint as ['java','-jar','app.jar']:
                        which executes java -jar app.jar cmd when we build cmd to build image from the docker file
        docker-compose.yml :
            configure and run multiple services
            working :
                in our case we configure and run 2 services from images :
                    postgres:latest(database)
                    patient-service:latest(generated from Dockerfile)
                patient-service-db:
                    uses the postgres:latest image to build container
                    specify the local and container ports resp
                    configure the env variables like:
                        POSTGRES_USER: admin
                        POSTGRES_PASSWORD: password
                        POSTGRES_DB: patientServiceDb
                    set the network as internal for ease of communication b/w the services
                    And store the data /var/lib/postgres/data in local patientServiceData :
                        for easy retrieval
                        data remains even if the container is destroyed
                patientService :
                    depends on patient-service-db for the database
                    builds the image from the Dockerfile and adds it to the cur dir
                    image tag :
                        names and uses the image generated from the Dockerfile to build container
                    specify the local and container ports resp
                    note : make sure to add postgres dependency to communicate with patientServiceDb
                    specify env variables :
                        SPRING_DATASOURCE_URL: jdbc:postgresql://patient-service-db:5432/patientServiceDb
                        SPRING_DATASOURCE_USERNAME: admin
                        SPRING_DATASOURCE_PASSWORD: password
                        SPRING_JPA_HIBERNATE_DDL_AUTO: update //Updates the database schema on startup
                        SPRING_SQL_INIT_MODE: always // update the database with the .sql file in the application properties
                    add this service to the same network as patient-service-db
                docker compose up -d builds and run the containers

BillingService :
    This service is based on the grpc(grpc remote procedural calls)
    the grpc intern uses http/2 which is faster then the http/1.1 used for restApi's
    the grpc dependencies and build/plugin can be found in its documentation itself
    grpc :
        the protobuf(protocol buffer) file is used to specify the grpc client and server
        in our case client is patientService and server is BillingService
        the .proto file :
            you specify the package name, rpc method through which they communicate and the message(request/response) types
        compile or clean install the project
        the .proto file gets converted into .java file turns file names from pascel casing into camel casing
        it also generates the dto's for request and response and well as getters and setters
    we create a grpc service that extends the base BillingServiceImpl class
    in which we override the rpc method with our own method
    we can send stream of response/multiple response
    mainly used for :
        live dashboards
        live data stream
        gaming etc.
    make sure to complete the response after the business logic

grpc client :
    we need to configure the grpc client which is patientService using protobuf
    so copy the same .proto fie into patientService and make sure all the dependencies and builds are available
    then compile the project to get BillingServiceGrpc and the request/response dto's and getters/setters.
    create a BillingServiceGrpcClient.java file:
        since it is a client it is a general service not grpcService
        create a billingStub which helps in client side communication using Blockingstub of BillingServiceGrpc
        get serverAddress and serverPort for billing
        configure a managedChannel from the serverAddress and serverPort
        assign value to the billingStub using the managedChannel
        create a method which takes patientId, username & email and creates billingAccount using billingStub and returns response
    pass env variables in the compose.yml for BILLING_SERVICE_ADDRESS:BillingService because the service name created in the shared container
    and pass the port : 9001(grpc port)
    
network :
    create a new network called shared-internal
    provide a name to e used among services
    specify external:true to inform docker that the network already Exists

after this we would have created a 1:1 service communication


                    


                




