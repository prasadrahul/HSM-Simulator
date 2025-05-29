# Image and container names
IMAGE_NAME := hsm-simulator-test
CONTAINER_NAME := hsm-simulator-container
FRONTEND_DIR := hsm-dashboard
BACKEND_DIR := hsm-wrapper
STATIC_DIR := $(BACKEND_DIR)/src/main/resources/static

# Build the React frontend and copy it to Spring Boot
frontend:
	cd $(FRONTEND_DIR) && npm install && npm run build
	rm -rf $(STATIC_DIR)/*
	mkdir -p $(STATIC_DIR)
	cp -r $(FRONTEND_DIR)/build/* $(STATIC_DIR)/

# Build the Spring Boot backend
backend:
	cd $(BACKEND_DIR) && mvn clean package -DskipTests

# Combine frontend + backend build, then Build the Docker image
build: frontend backend
	docker build -t $(IMAGE_NAME) .

# Run the container
run:
	docker run --rm -it -p 8080:8080 --name $(CONTAINER_NAME) $(IMAGE_NAME)

# Open a shell in the container
shell:
	docker run --rm -it --name $(CONTAINER_NAME) $(IMAGE_NAME) /bin/bash

# Clean up the Docker image
clean:
	docker image rm $(IMAGE_NAME) || true
