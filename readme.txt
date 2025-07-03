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
but for 1:n microservice :
    this would take a lot of time
    and if in between any service collapses the response would be stuck
    for this reason we use apache kafka

apache kafka :
    handle 1:n communication with ease
    doesn't collapse in between
    use pub/sub logic
    process:
        we create a kafka cluster and assign a broker to communicate with it
        in this we create a kafka topic managed by broker through which the producer and consumer communicate
        the producer pushes the evnet/data inside the topic and continues it execution
        it leaves the kafka topic to handle the data/event
        the consumer subscribes to the topic and listens on it
        based on the events given by the producer the consumer performs it tasks
        insides:
            multiple topics are created within a same cluster for different event/data
            within a topic their are partitions and offset
            partitions to handle the large incoming data to a particular topic and split them into different partition
            each data/event inside a partition is assigned a offset number to uniquely identify the data
            the large data coming from different partitions can't be handled by single consumer
            so we create multiple copies of the consumer and assign them to a single group for ease data handling
    code:
        KafkaProducer:
            we can either configure it for the rest or grpc request/response
            configuring grpc in patientService:
                create a .proto file in same format as billing for KafkaProducer called patientEvent
                create a message of type patientEvent
                compile the patientService to generate dto's and getters/setters for the patientEvent
                create a KafkaProducer @service inside the patientService:
                    which acts as client side of the grpc service
                use kafkaTemplate to send data/event to the topic:
                    specify the key and value types
                    key value:
                        is the name of the topic to which you send data
                    create a patientEvent object and pass it as a value inside the kafkaTemplate.send()
        the consumer who has subscribed to the topic will receive the message
        set the kafka key and value types in the application properties file
        inside the compose.yml set SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092:
            as kafka is the name of the container created

        KafkaConsumer:
            create a same protobuf's .proto for the patientEvent
            subscribe and listen to a topic using KafkaListener annotation on a method:
                provide <topic name, groupId of consumer/Listener>
            pass the value and its type(sent by producer)as arg to the method
            retrieve the value and convert it into patientEvent type using the protobuf generated method
            log the results

client communicates directly with the multiple services:
    this leads to dependency on the ports of the microservice:
        if port changes the entire uri of the client changes
    the port get exposed to the outside world which may lead to DDOS and various attacks
    solution:
        creation of apiGateway:
            the client communicates with the apiGateway which internally communicates with rest of the services
            only the apiGateway port is exposed to the outside world

apiGateway:
    include the reactor gateway dependency
    configure the application.properties/application.yml file:
        provide the source port
        configure the Spring.cloud.gateway routes to listen
        provide the id's, uri's, and predicate's to monitor
        uri:
            when the apiGateway port is hit which url to refer
        predicate and filter:
            use path arg in predicate to monitor
            filter to navigate if the path is called
    remove the exposed patientService port

authService:
    login :
        controller:
            create a authenticate method in the controller at /login route
        service:
            get the user details from the LoginRequestDto's email
            get passwordEncoder:
                use SecurityFilterChain method which intern uses HttpSecurity arg to:
                    permit all requests using authorizeHttpRequests method
                    crsf method for cross file resource sharing
                And then build the HttpSecurity
                compare the passwords using the passwordEncoder method from SecurityConfig which intern uses bcrypt passwordEncoder
            if password matches create a jwtToken form User model and jwtSecret
                using Base64.getDecoder.decode method to get byte array from the String using UTF-8 encoding
                then the byte array is converted to Key type using the hmacShaKeyFor method
                then the key is used for signing of the token created using JWTS framework
            note :
                keep the return type as Optional<String> because :
                    match password return password
                    unmatched password return null
    validate:
        send a header called Authorization with BearerToken as value
        check if the token is null or without Bearer prefix
        pass only the token to the auth Service to check and return boolean value
        use jwtUtils class to verify the token
        use JWTS.parser.verifywith((Secretkey) secretkey) method to verify token
            and parseSignedClaims() to verify signature of the token
            
Now we need to configure authService
    in such a way that it should be accessible only through api-gateway just like patientService

Every request to patientService should pass through custom JwtValidationGatewayFilterFactory in the apiGateway:
    Create and initialize a webClient using the webClient.Builder and authServiceUrl :
        webClient can be used to send/receive request/response using the uri
    extend a AbstractGatewayFilterFactory<Object> and add override the natural GatewayFilter method using your own:
        return a function to this method which consist of 2 arg:
            exchange:
                it is used for :
                    Altering the received request entity
                    Altering the response entity to be sent
                through exchange we get the token from Authorization header
                if token is null or doesn't have Bearer Key:
                    add unauthorized statusCode to the response
                    complete the response
            chain:
                it used to add our middleware code inBetween the chain and pass the chain
            use webClient to :
                run a getRequest to the uri(webClient uri + /validate)
                add the token to the Auth header
                retrieve the response if ok
                    pass the chain.filter with exchange as arg
                else :
                    throws a JwtException handled by the custom class JwtValidationException:
                        handles only the webClient's unauthorized Exception class
                        use the exchange passed as arg to alter the response to desired one
                        complete the response
    add a JwtValidation Filter to the request path to patientService in apiGateway application.yml

provide a path through apiGateway to auth-docs similar to patient-docs

testing :
    unit testing: testing a particular method
    integration testing:
        how different Entities/classes interact with each other
        how different services interact with each other
    end to end testing:
        how client communicates with the services using UI

integrationTests:
    uses rest-assured and junit-jupyter dependencies
    remove the main folder and default test class
    Create AuthIntegrationTest:
        use BeforeAll annotation and setup() to specify the RestAssured.baseUri
        create a method with Test annotation for checking token is valid for correct input
            create a payload with email and password
            use given().contentType().body().when().post().then().statusCode().body().extract().response()
                and specify required args to get the response and store in the RestAssured Response
        create a method with Test annotation for checking return unauthorized for incorrect payload
            create payload
            check given().contentType().body().when().post().then().statusCode(unauthorized)
    Create PatientIntegrationTest:
        extract RestAssured.Response from the /auth/login uri and get the token
        pass the token as header to the get /api/patient request to get the patient
            in the same format as above

deployment:
    since aws services cost even in the free tier we going to use localStack
    localStack:
        An archestration of aws services within the host computer
        connect your docker desktop to the localStack using
    aws services :
        the microservices code in stored in VM called Ec2 instances
        the database is stored inside the RdS
        All the database and microservices are stored inside a vpc network
            which prevents from public access
        the services and database inside are accessed through a application load balancer
            which acts as a bridge b/w frontend and apiGateway
        IAC(Infrastructure as code):
            we the services with the configurations to be used in the form of any programming language
            then the code get converted into a cloud formation template 
                which is then fed into the aws or localStack
    download aws cli setup from the official aws.docs
        use aws configure to set id and name
        get the list of functions if needed

infrastructure:
    process:
        configure the databases inside the Rds service of aws with the required credentials
        configure a MSK(amazon managed streaming for apache kafka):
            create a cluster and brokers which helps in communication with the cluster
        Assign a Ecs service for each service:
            Assign ASG's,LoadBalancers,computeEngines and AZ's
            It creates a ECSTask that run the microservice:
                it creates a multiple ECSTask to provide fault tolerance
        All the services are configured inside a Ecs cluster which helps in ease of inter communication
        All the Services, database and MSK are configured inside a vpc:
            it creates private network that protects the services from the public access
            we can create an ApplicationLoadBalancer that:
                communicates with the vpc
                it acts as bridge b/w the frontend client and our vpc
    working:
        create a maven project called infrastructure
            add amazon-cdk-lib and aws-java-sdk dependencies
            then create a class under infrastructureApplicationTests.java
                which extends the Stack class from aws.cdk(cloud development kit)
                create a localStack constructor which takes:
                    App scope:
                        defines that the scope of application comes under App which is the root tree
                        the localStack acts as a child of App Class
                        so all the cdk resources specified under the App are added to the localStack
                    String id:
                        Assign a particular id to the Stack which acts as identifier for the aws cdk
                    StackProps props:
                        these are used to assign configurations for the stack like
                            accountId, regions, IAmRules for resources
                it calls the super constructor which takes all the args passed to the localStack constructor

                Vpc createVpc():
                    create vpc using the vpc.Builder
                        create() which takes scope of this stack and id for the vpc
                        additional methods:
                            name and maxAzs
                            note : maxAzs should be min of 2 in us-east-1
                create a class variable for vpc
                    as it is used later in different methods
                    assign value to it using the createVpc()

                DatabaseInstance createDatabase():
                    takes id and dbname as args
                    create a Database using DatabaseInstance.Builder.create() pass the stack and id
                    define engine using DatabaseInstanceEngine and build a postgres engine
                    assign the vpc in which it'll be present
                    provide the InstanceType which consist of InstanceClass and Size for running the database
                    allocate storage(Mb)
                    use Credentials.fromGeneratedSecret():
                        to generate credentials from String "admin" and assign it to the service
                    add databaseName and RemovalPolicy to destroy when the stack is destroyed
                    build
                create 2 DatabaseInstance from this:
                    patientServiceDb and authServiceDb

                CfnHealthCheck checkDbHealthCheck():
                    pass id and DatabaseInstance as args
                    use CfnHealthCheck.healthCheckConfig() to provide configurations for thr health check
                        which uses the CfnHealthCheck.HealthCheckConfigProperty.builder()
                        specify the type of request and port, ipAddress of the DatabaseInstance
                        to get the port number use the Token.asNumber() 
                            to get number from the DatabaseInstance.getDbInstanceEndpointPort()
                            otherwise returns error "expected number got token"
                        provide requestInterval and failureThreshold for the HealthCheckConfigProperty
                        build the HealthCheckConfigProperty
                    build the CfnHealthCheck
                create the healthChecks for the databases
                    patientServiceDb and authServiceDb

                CfnCluster createMskCluster():
                    use CfnCluster.Builder.create to create a Cfn Cluster
                    provide clusterName, kafkaVersion and numberOfBrokerNodes
                    note: numberOfBrokerNodes%maxAzs==0
                    use the CfnCluster.BrokerNodeGroupInfoProperty.builder() to specify brokerNodeGroupInfo:
                        specify the instanceType, clientSubnets and brokerAzDistribution("DEFAULT")
                        clientSubnets are fetched through the getPrivateSubnets
                            and maps each subnet to getSubnetId and return list.of subnets
                        build the BrokerNodeGroupInfoProperty
                    build the CfnCluster
                create the mskCluster and Assign value in the constructor

                Cluster createEcsCluster():
                    use Cluster.Builder.create to create Cluster
                    provide vpc and defaultCloudMapNamespace
                    use CloudMapNameSpaceOptions.builder() to build defaultCloudMapNamespace
                        specify the name for the CloudMapNameSpaceOptions
                        build the CloudMapNameSpaceOptions
                    build the Cluster
                create a class variable for Cluster and assign value in constructor
                    because it is used in ecsService methods

                FargateService createFargateService():
                    pass id, imageName, list.of(ports), DatabaseInstance, additionalEnvVars as args
                    Build a FargateTaskDefinition:
                        use FargateTaskDefinition.Builder.create and provide stack and "task"+id
                            as it needs to be unique for each task
                        provide cpu and memoryLimitMiB
                        build the FargateTaskDefinition
                    Build the ContainerDefinitionOptions:
                        use ContainerDefinitionOptions.builder()
                        provide the image using ContainerImage.fromRegistry and pass the imageName
                            it is fetched from the docker image formed
                        provide the portMappings using the ports.stream()
                            and for each port use the PortMapping.Builder to specify configurations
                                specify the containerPort and hostPort and also the protocol
                                build the PortMapping
                            pass each PortMapping as a List to portMappings()
                        use LogDriver.awsLogs to provide logging arg
                            use AwsLogDriverProps.builder() to provide to awsLogs
                                specify the logGroup and streamPrefix(imageName)
                                Build the logGroup using the LogGroup.Builder.create
                                    pass the stack and id+"LogGroup" as args
                                        since id needs to be unique for each service
                                    specify the logGroupName("/ecs/"+imageName) and
                                        removalPolicy and retention
                                    build the LogGroup
                                build the AwsLogDriverProps
                    create a map of envVars and put
                        "SPRING_KAFKA_BOOTSTRAP_SERVERS" using the localhost.localStack.cloud
                            provide the ports on which it'll run.
                        if their are any additionalEnvVars add them to envVars using the putAll()
                        if the DatabaseInstance is notNull:
                            put the "SPRING_DATASOURCE_URL" and pass the "jdbc:postgresql://address:port/db"
                                the address and port are fetched from the DatabaseInstance
                                the db is the imageName
                            put the "SPRING_DATASOURCE_USERNAME" to provide username "admin"
                            put the "SPRING_DATASOURCE_PASSWORD" to provide password 
                                using DatabaseInstance.getSecrets().secretValueFromJson("password").toString()
                                    since the password is stored in the cdk
                            put the "SPRING_JPA_HIBERNATE_DDL_AUTO" and "SPRING_SQL_INIT_MODE"
                            put the "SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAILED_TIMEOUT" in milliseconds
                    pass the envVars as containerOptions variable of ContainerDefinitionOptions
                        in the .environment()
                    add the container inside the FargateTaskDefinition.addContainer()
                        specify the id as imageName+"Container" and containerOptions.build as args
                    Build a FargateService using the FargateService,Builder.create
                        pass the cluster, taskDefination and serviceName(imageName)
                        use assignPublicIp(False) from exposing the publicIp
                    return the created FargateService
                create the FargateService authService:
                    pass id, imageName(auth-service), List.of(ports) and DatabaseInstance of authService
                    in envVars pass the "JWT_SECRET" using the Map.of()
                    add dependencies on the authServiceDb and authHealthCheck using the
                        authService.getNode().addDependency()
                in similar manner
                create the FargateService billingService:
                    specify both the http and grpc ports
                    null for database and envVars
                create the FargateService analyticsService:
                    add dependency to mskCluster
                create the FargateService patientService:
                    add DatabaseInstance and envVars
                    in envVars specify the "BILLING_SERVICE_GRPC_PORT" and "BILLING_SERVICE_ADDRESS"
                    use domain related routing to get the address
                        host.docker.internal because all services are placed inside the same network
                    add dependencies on the patientServiceDb, patientServiceHealthCheck, BillingService and mskCluster
                createApiGatewayService():
                    create a FargateTaskDefinition using FargateTaskDefinition.Builder.create method
                    specify the id, imageName, db, ports and envs in the method itself
                        because we only create one service through ALB FargateService
                    create a ContainerDefinitionOptions using ContainerDefinitionOptions.builder():
                        in the .environment() method specify the env's in Map format
                            specify "SPRING_PROFILES_ACTIVE" : "prod"
                                to consider application-prod.yml instead of application.yml
                    create a ApplicationLoadBalancedFargateService using the
                        ApplicationLoadBalancedFargateService.Builder.create():
                        because we need to create a ALB for the apiGateway to communicate
                        specify the cluster, serviceName and taskDefination
                        specify the count using desiredCount(1) and healthCheckGracePeriod() of 60 seconds
                            desiredCount specifies the number of EcsTask to be running inside the EcsService
        after create a new image without any errors for the Services
            patientService, BillingService, analyticsService, authService and apiGateway
                to be used by the cloudFormation template

        create a shell script file to run the setOfCmds:
            specify it is a shell script by #!bin/bash
            set -e:
                stops the script if any cmd fails
            note: you can communicate with aws using the aws cmds
                but since your aws is not configured on the cloud rather in the host computer using localStack
                    you communicate with cli using the --endpoint-url=http://localhost:4566
                        since localStack uses port 4566 by default
            use aws --entrypoint-url=http://localhost:4566 cloudformation delete stack \
                    --stack-name patient-management to delete any previous stack with that name
                    note: \ is used to indicate next line is a continuation of the same line cmd
            use aws --entrypoint-url=http://localhost:4566 cloudformation deploy --stack-name patient-management \
                    --template-file "cloudformation template json file"
                    to deploy a stack with name using the provided json file
            use aws --entrypoint-url=http://localhost:4566 eblv2 describe-load-balancers \
                    --query "LoadBalancers[0].DnsName" --output text
                    to get the output of LoadBalancers[0].DnsName in text format
                    the query is used for perform ops on the resources 
            

                             

                

                
                

                


                

    




        




    


                    


                




