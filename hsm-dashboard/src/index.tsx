import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from "react-router-dom";
import App from './App';
import CssBaseline from '@mui/material/CssBaseline';
import ErrorBoundary from "./ErrorBoundary";

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
    <React.StrictMode>
        <BrowserRouter>
        <ErrorBoundary>
            <CssBaseline/>
            <App/>
        </ErrorBoundary>
            </BrowserRouter>
    </React.StrictMode>
);
