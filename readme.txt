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

