## Running SoftHSM2 on Docker

 This document provides instructions for running SoftHSM2 on Docker.

## Prerequisites
- Docker installed on your system.
- Basic knowledge of Docker commands.

## Steps to Run SoftHSM2 on Docker

    
### 1. Pull the HSM Simulator Docker Image
Build the HSM Simulator Docker image.:
Go to the root directory of the project and run the following command:
```bash
  make build
```

### 2. Running the build docker image in above step
```bash
  make run
``` 

### 3. Verify the Docker Container
After running the container, you can verify that SoftHSM2 is running by executing the following command:
```bash
  docker exec -it hsm-simulator-container softhsm2-util --version
```

### 4. Validate the REST endpoint exposed on swagger ui
Open your web browser and navigate to `http://localhost:8080/swagger-ui/index.html` to access the Swagger UI.
This will allow you to interact with the HSM Simulator based REST API.

### 5. Stopping the Docker Container
 To stop the running Docker container, use the following command:
```bash
  Crtl + C ( If you ran with make run command)
```
### or
```bash
  docker stop hsm-simulator-container
```

### 6. Removing the Docker Image
If you want to remove the Docker image, use the following command:
```bash
  make clean
```