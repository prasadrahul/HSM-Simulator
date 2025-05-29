import React, { useState, useEffect } from "react";
import axios from "axios";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Typography,
} from "@mui/material";

interface Key {
    id: number;
    label: string;
    type: string;
    subject: string;
    usage: string;
    access: string;
    slotIndex: number;
    slotHex: string;
}

interface Slot {
    slotIndex: number;
    slotHex: string;
    slotDecimal: number;
    label: string;
    manufacturer: string;
    model: string;
    flags: string;
    hwVersion: string;
    fwVersion: string;
    serial: string;
    pinMinMax: string;
    initialized: boolean;
}

const KeysPage = () => {
    const [slots, setSlots] = useState<Slot[]>([]);
    const [keysBySlot, setKeysBySlot] = useState<{ [slotIndex: number]: Key[] }>({});

    useEffect(() => {
        axios.get("/api/v1/slots").then((res) => {
            const slots: Slot[] = res.data.data;
            setSlots(slots);

            const allKeysPromises = slots.map((slot) => {
                const slotIndex = slot.slotDecimal;
                return axios
                    .get(`/api/v1/slots/${slotIndex}/keys`)
                    .then((response) => ({
                        slotIndex: slot.slotIndex,
                        keys: response.data.data.map((key: Key) => ({
                            ...key,
                            slotIndex: slot.slotIndex,
                            slotHex: slot.slotHex,
                        })),
                    }))
                    .catch(() => ({
                        slotIndex: slot.slotIndex,
                        keys: [],
                    }));
            });

            Promise.all(allKeysPromises).then((results) => {
                const grouped: { [slotIndex: number]: Key[] } = {};
                results.forEach(({ slotIndex, keys }) => {
                    grouped[slotIndex] = keys;
                });
                setKeysBySlot(grouped);
            });
        });
    }, []);

    return (
        <div>
            {slots.map((slot) => {
                const slotKeys = keysBySlot[slot.slotIndex] || [];

                return (
                    <div key={slot.slotIndex}>
                        <Typography variant="h6" gutterBottom>
                            Keys in Slot {slot.slotIndex} ({parseInt(slot.slotHex, 16)})
                        </Typography>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>ID</TableCell>
                                    <TableCell>Label</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Subject</TableCell>
                                    <TableCell>Usage</TableCell>
                                    <TableCell>Access</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {slotKeys.length > 0 ? (
                                    slotKeys.map((key) => (
                                        <TableRow key={key.id}>
                                            <TableCell>{key.id}</TableCell>
                                            <TableCell>{key.label || "No Label"}</TableCell>
                                            <TableCell>{key.type}</TableCell>
                                            <TableCell>{key.subject}</TableCell>
                                            <TableCell>{key.usage}</TableCell>
                                            <TableCell>{key.access}</TableCell>
                                        </TableRow>
                                    ))
                                ) : (
                                    <TableRow>
                                        <TableCell colSpan={6} align="center">
                                            No keys found in this slot
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </div>
                );
            })}
        </div>
    );
};

export default KeysPage;