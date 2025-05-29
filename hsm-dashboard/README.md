
# SoftHSM Dashboard

A React-based dashboard for interacting with HSM, allowing management and visualization of HSM-related operations.

## Project Overview

The HSM Dashboard provides a user interface for:
- Displaying HSM slots and tokens
- Managing cryptographic keys
- Performing signing and verification operations
- Interacting with the backend API through Swagger UI

## Development Setup

### Prerequisites
- Node.js (v14 or higher)
- npm (v6 or higher)
- HSM wrapper service running

### Installation

1. Install dependencies:
```
npm install
```

2. Create a `.env` file in the root directory with:
```
REACT_APP_API_URL=http://localhost:8080/api
```

## Available Scripts

### `npm start`

Runs the app in development mode at [http://localhost:3000](http://localhost:3000).

The page will automatically reload when you make changes to the code, and lint errors will appear in the console.

### `npm test`

Launches the test runner in interactive watch mode.

### `npm run build`

Builds the app for production to the `build` folder. The build is optimized and minified for the best performance.

## Connecting to the Backend

This dashboard connects to the HSM wrapper service, which should be running separately. Make sure the backend service is running before using the dashboard.

## Developer Mode

For developer mode:

1. Run the backend HSM service:
```
cd ../hsm-wrapper
mvn spring-boot:run
```

2. In a separate terminal, start the dashboard in development mode:
```
npm start
```

3. Access Swagger UI at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to interact with the API directly.

## Debugging

To debug the React application:
1. Use the browser's developer tools (F12 or Ctrl+Shift+I)
2. Check the console for errors and warnings
3. Use the React Developer Tools extension for deeper component inspection

## Project Structure

- `src/components`: React components for the dashboard UI
- `src/services`: API service clients for connecting to the backend
- `src/pages`: Main page layouts
- `src/utils`: Utility functions

## Building for Production

To create a production build that can be served by the backend:

1. Build the React app:
```
npm run build
```

2. Copy the build files to the backend's static resources:
```
cp -r build/* ../hsm-wrapper/src/main/resources/static/
```

3. Build and run the backend to serve the UI:
```
cd ../hsm-wrapper
mvn clean package
java -jar target/hsm-wrapper.jar
```
```