import React from 'react';

const SwaggerTab: React.FC = () => {
    return (
        <iframe
            title="Swagger UI"
            src="http://localhost:8080/swagger-ui/index.html"
            style={{ width: '100%', height: '100vh', border: 'none' }}
        />
    );
};

export default SwaggerTab;