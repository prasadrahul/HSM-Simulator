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

const SignPage = () => {
    const [slots, setSlots] = useState<Slot[]>([]);
    const [selectedSlotIndex, setSelectedSlotIndex] = useState<number | "">("");
    const [keys, setKeys] = useState<Key[]>([]);
    const [selectedKeyAlias, setSelectedKeyAlias] = useState("");
    const [data, setData] = useState("");
    const [signature, setSignature] = useState("");
    const [signAlgo, setSignAlgo] = useState("SHA256withECDSA");

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
            const signingKeys = response.data.data.filter((key: Key) =>
                key.usage && key.usage.toLowerCase().includes("sign") &&
                key.label.trim() !== ""
            );
            console.log("Filtered keys for slot", slotIndex, signingKeys);
            setKeys(signingKeys); // Update the keys state with filtered keys
        } catch (err) {
            console.error("Failed to fetch keys for slot", slotIndex, err);
            setKeys([]); // Ensure keys are cleared on error
        }
    };

    const handleSign = async () => {
        if (!selectedKeyAlias || !data || !signAlgo) return;
        try {
            const response = await axios.post(`/api/v1/data/sign/${selectedKeyAlias}`, {
                message: data,
                signAlgo: signAlgo,
            });
            setSignature(response.data.data);
        } catch (err) {
            console.error("Signing failed:", err);
            setSignature("Signing failed");
        }
    };

    return (
        <div>
            <Typography variant="h6" gutterBottom>
                Sign Data
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
                <InputLabel id="key-label">Select Signing Key</InputLabel>
                <Select
                    labelId="key-label"
                    value={selectedKeyAlias}
                    label="Select Signing Key"
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
                <InputLabel id="sign-algo-label">Select Signing Algorithm</InputLabel>
                <Select
                    labelId="sign-algo-label"
                    value={signAlgo}
                    label="Select Signing Algorithm"
                    onChange={(e) => setSignAlgo(e.target.value)}
                >
                    <MenuItem value="SHA256withRSA">SHA256withRSA</MenuItem>
                    <MenuItem value="SHA256withECDSA">SHA256withECDSA</MenuItem>
                </Select>
            </FormControl>

            {/* Data Input */}
            <TextField
                label="Data to Sign"
                fullWidth
                multiline
                rows={4}
                value={data}
                onChange={(e) => setData(e.target.value)}
                sx={{ mt: 2 }}
            />

            {/* Sign Button */}
            <Button
                variant="contained"
                onClick={handleSign}
                disabled={!selectedKeyAlias || !data || !signAlgo}
                sx={{ mt: 2 }}
            >
                Sign
            </Button>

            {/* Signature Output */}
            {signature && (
                <Typography variant="body2" sx={{ mt: 2 }}>
                    Signature (Base64): {signature}
                </Typography>
            )}
        </div>
    );
};

export default SignPage;
