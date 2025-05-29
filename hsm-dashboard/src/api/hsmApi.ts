import axios from 'axios';

// Axios instance
const api = axios.create({
    baseURL: '/api/v1', // Proxy handles this to hit Spring Boot
});

export const getSlots = () => api.get('/slots');
export const getKeys = (slotId: number) => api.get(`/slots/${slotId}/keys`);
export const generateKey = (vendorId: string) => api.post(`/keys/generate`, { vendorId });
export const signData = (keyId: string, payload: any) => api.post(`/data/sign/${keyId}`, payload);
export const verifySignature = (keyId: string, payload: any, signData: any) => api.post(`/data/verify/${keyId}`, payload, signData);
