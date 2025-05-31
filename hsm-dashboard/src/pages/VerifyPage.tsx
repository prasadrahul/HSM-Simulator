import React, { useState, useEffect } from "react";
    import axios from "axios";
    import {
        Button,
        TextField,
        Typography,
        Select,
        MenuItem,
        FormControl,
        InputLabel,
    } from "@mui/material";

    interface Slot {
        slotIndex: number;
        slotHex: string;
        slotDecimal: number;
        label: string;
    }

    interface Key {
        id: number;
        label: string;
        type: string;
        subject: string;
        usage: string;
        access: string;
    }

    const VerifyPage = () => {
        const [slots, setSlots] = useState<Slot[]>([]);
        const [selectedSlotIndex, setSelectedSlotIndex] = useState<number | "">("");
        const [keys, setKeys] = useState<Key[]>([]);
        const [selectedKeyAlias, setSelectedKeyAlias] = useState("");
        const [data, setData] = useState("");
        const [base64Signature, setSignature] = useState("");
        const [verifyAlgo, setVerifyAlgo] = useState("SHA256withECDSA");
        const [verificationResult, setVerificationResult] = useState<string | null>(null);

        useEffect(() => {
            axios.get("/api/v1/slots").then((res) => {
                const slotsData = res.data.data.map((slot: any) => ({
                    slotIndex: parseInt(slot.slotHex, 16),
                    slotHex: slot.slotHex,
                    label: slot.label,
                }));
                setSlots(slotsData);
            });
        }, []);

        const handleSlotChange = async (slotIndex: number) => {
            setSelectedSlotIndex(slotIndex);
            setSelectedKeyAlias(""); // Clear the selected key alias
            setKeys([]); // Clear keys before fetching

            try {
                const response = await axios.get(`/api/v1/slots/${slotIndex}/keys`);
                const verificationKeys = response.data.data.filter((key: Key) =>
                    key.usage && key.usage.toLowerCase().includes("verify") &&
                    key.label.trim() !== ""
                );
                console.log("Filtered keys for slot", slotIndex, verificationKeys);
                setKeys(verificationKeys); // Update the keys state with filtered keys
            } catch (err) {
                console.error("Failed to fetch keys for slot", slotIndex, err);
                setKeys([]); // Ensure keys are cleared on error
            }
        };

        const handleVerify = async () => {
            if (!selectedKeyAlias || !data || !base64Signature || !verifyAlgo) return;
            try {
                const response = await axios.post(`/api/v1/data/verify/${selectedKeyAlias}`, {
                    message: data.trim(),
                    base64Signature: base64Signature.trim(),
                    verifyAlgo: verifyAlgo.trim(),
                });
                setVerificationResult(response.data.data ? "Verification Successful" : "Verification Failed");
            } catch (err) {
                console.error("Verification failed:", err);
                setVerificationResult("Verification failed");
            }
        };

        return (
            <div>
                <Typography variant="h6" gutterBottom>
                    Verify Data
                </Typography>

                {/* Slot Dropdown */}
                <FormControl fullWidth sx={{ mt: 2 }}>
                    <InputLabel id="slot-label">Select Slot</InputLabel>
                    <Select
                        labelId="slot-label"
                        value={selectedSlotIndex}
                        label="Select Slot"
                        onChange={(e) => handleSlotChange(Number(e.target.value))}
                    >
                        {slots.map((slot) => (
                            <MenuItem key={slot.slotIndex} value={slot.slotIndex}>
                                {slot.slotDecimal || `Slot ${slot.slotIndex}`}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                {/* Key Dropdown */}
                <FormControl fullWidth sx={{ mt: 2 }} disabled={!selectedSlotIndex || keys.length === 0}>
                    <InputLabel id="key-label">Select Verification Key</InputLabel>
                    <Select
                        labelId="key-label"
                        value={selectedKeyAlias}
                        label="Select Verification Key"
                        onChange={(e) => setSelectedKeyAlias(e.target.value)}
                    >
                        {keys.map((key) => (
                            <MenuItem key={key.id} value={key.label}>
                                {key.label}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                <FormControl fullWidth sx={{ mt: 2 }}>
                    <InputLabel id="verify-algo-label">Select Verification Algorithm</InputLabel>
                    <Select
                        labelId="verify-algo-label"
                        value={verifyAlgo}
                        label="Select Verification Algorithm"
                        onChange={(e) => setVerifyAlgo(e.target.value)}
                    >
                        <MenuItem value="SHA256withRSA">SHA256withRSA</MenuItem>
                        <MenuItem value="SHA256withECDSA">SHA256withECDSA</MenuItem>
                    </Select>
                </FormControl>

                {/* Data Input */}
                <TextField
                    label="Data to Verify"
                    fullWidth
                    multiline
                    rows={4}
                    value={data}
                    onChange={(e) => setData(e.target.value)}
                    sx={{ mt: 2 }}
                />

                {/* Signature Input */}
                <TextField
                    label="Signature (Base64)"
                    fullWidth
                    multiline
                    rows={2}
                    value={base64Signature}
                    onChange={(e) => setSignature(e.target.value)}
                    sx={{ mt: 2 }}
                />

                {/* Verify Button */}
                <Button
                    variant="contained"
                    onClick={handleVerify}
                    disabled={!selectedKeyAlias || !data || !base64Signature || !verifyAlgo}
                    sx={{ mt: 2 }}
                >
                    Verify
                </Button>

                {/* Verification Result Output */}
                {verificationResult && (
                    <Typography variant="body2" sx={{ mt: 2 }}>
                        {verificationResult}
                    </Typography>
                )}
            </div>
        );
    };

    export default VerifyPage;