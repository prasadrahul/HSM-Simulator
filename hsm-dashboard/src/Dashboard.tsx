import React, { useState } from "react";
import { Tabs, Tab, Box, Typography } from "@mui/material";
import SlotsPage from "./pages/SlotsPage";
import KeysPage from "./pages/KeysPage";
import SignPage from "./pages/SignPage";
import VerifyPage from "./pages/VerifyPage";
import SwaggerTab from "./components/SwaggerTab";

function Dashboard() {
    const [tabIndex, setTabIndex] = useState(0);

    const handleChange = (event: React.SyntheticEvent, newValue: number) => {
        setTabIndex(newValue);
    };

    return (
        <Box sx={{ width: "100%", typography: "body1", p: 2 }}>
            <Typography variant="h4" gutterBottom>
                HSM Dashboard
            </Typography>
            <Tabs value={tabIndex} onChange={handleChange} aria-label="HSM tabs">
                <Tab label="Slots" />
                <Tab label="Keys" />
                <Tab label="Sign" />
                <Tab label="Verify" />
                <Tab label="Swagger" />
            </Tabs>

            <Box sx={{ mt: 2 }}>
                {tabIndex === 0 && <SlotsPage />}
                {tabIndex === 1 && <KeysPage />}
                {tabIndex === 2 && <SignPage />}
                {tabIndex === 3 && <VerifyPage />}
                {tabIndex === 4 && <SwaggerTab />}
            </Box>
        </Box>
    );
}

export default Dashboard;