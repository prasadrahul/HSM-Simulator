import React, { useEffect, useState } from "react";
import axios from "axios";
import { Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";


interface Slot {
    slotIndex: number;
    slotHex: String;
    slotDecimal: number;
    label: String;
    manufacturer: String;
    model: String;
    flags: String;
    hwVersion: String;
    fwVersion: String;
    serial: String;
    pinMinMax: String;
    initialized: boolean;
}

const SlotsPage = () => {
    const [slots, setSlots] = useState<Slot[]>([]);

    useEffect(() => {
        axios.get("/api/v1/slots").then((res) => {
            setSlots(res.data.data);
        });
    }, []);

    return (
        <div>
            <Typography variant="h6">Available Slots</Typography>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Index</TableCell>
                        <TableCell>Hex Value</TableCell>
                        <TableCell>Decimal Value</TableCell>
                        <TableCell>Label</TableCell>
                        <TableCell>Manufacturer</TableCell>
                        <TableCell>Model</TableCell>
                        <TableCell>Flags</TableCell>
                        <TableCell>HW Version</TableCell>
                        <TableCell>FW Version</TableCell>
                        <TableCell>Serial</TableCell>
                        <TableCell>PIN Min/Max</TableCell>
                        <TableCell>Initialized</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {slots.map((slot) => (
                        <TableRow key={slot.slotIndex}>
                            <TableCell>{slot.slotIndex}</TableCell>
                            <TableCell>{slot.slotHex}</TableCell>
                            <TableCell>{slot.slotDecimal}</TableCell>
                            <TableCell>{slot.label}</TableCell>
                            <TableCell>{slot.manufacturer}</TableCell>
                            <TableCell>{slot.model}</TableCell>
                            <TableCell>{slot.flags}</TableCell>
                            <TableCell>{slot.hwVersion}</TableCell>
                            <TableCell>{slot.fwVersion}</TableCell>
                            <TableCell>{slot.serial}</TableCell>
                            <TableCell>{slot.pinMinMax}</TableCell>
                            <TableCell>{slot.initialized ? "Yes" : "No"}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
};

export default SlotsPage;
