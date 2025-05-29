import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [useSSL, setUseSSL] = useState(false);
    const [keyStoreFile, setKeyStoreFile] = useState<File | null>(null);
    const [keyStorePassword, setKeyStorePassword] = useState("");
    const [trustStoreFile, setTrustStoreFile] = useState<File | null>(null);
    const [trustStorePassword, setTrustStorePassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const validateForm = () => {
        if (!username.trim()) return "Username is required.";
        if (!password) return "Password is required.";
        if (useSSL) {
            if (!keyStoreFile) return "Client KeyStore file is required for SSL.";
            if (!keyStorePassword) return "Client KeyStore password is required.";
            if (!trustStoreFile) return "TrustStore file is required for SSL.";
            if (!trustStorePassword) return "TrustStore password is required.";
        }
        return "";
    };

    const handleLogin = async () => {
        const validationError = validateForm();
        if (validationError) {
            setErrorMsg(validationError);
            return;
        }

        setErrorMsg("");
        setLoading(true);

        try {
            let response;

            if (useSSL) {
                const formData = new FormData();
                formData.append("username", username);
                formData.append("password", password);
                if (keyStoreFile) formData.append("keyStore", keyStoreFile);
                formData.append("keyStorePassword", keyStorePassword);
                if (trustStoreFile) formData.append("trustStore", trustStoreFile);
                formData.append("trustStorePassword", trustStorePassword);

                response = await axios.post("/api/v1/auth/login", formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });
            } else {
                const formData = new FormData();
                formData.append("username", username);
                formData.append("password", password);

                response = await axios.post("/api/v1/auth/login", formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });
            }

            if (response.status >= 200 && response.status < 300) {
                sessionStorage.setItem("auth", "true");
                // alert("Login successful");
                navigate("/dashboard"); // Navigate to dashboard after successful login
            } else {
                setErrorMsg(`Login failed: ${response.status} ${response.data?.message || ""}`);
                alert("Invalid credentials");
            }
        } catch (err: any) {
            setErrorMsg(
                "Login failed: " +
                (err.response?.data?.message || err.message || "Unknown error")
            );
            setPassword("");
            setKeyStorePassword("");
            setTrustStorePassword("");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{padding: "16px", maxWidth: "400px", margin: "0 auto"}}>
            <h1>Login {useSSL && "(Mutual TLS)"}</h1>
            {errorMsg && (
                <div style={{color: "red", marginBottom: "16px"}}>{errorMsg}</div>
            )}

            <fieldset disabled={loading} style={{border: "none", padding: "0"}}>
                <label style={{display: "block", marginBottom: "8px"}}>
                    Username:
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        style={{display: "block", width: "100%", marginTop: "4px"}}
                    />
                </label>

                <label style={{display: "block", marginBottom: "8px"}}>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={{display: "block", width: "100%", marginTop: "4px"}}
                    />
                </label>

                <label style={{display: "block", marginBottom: "8px"}}>
                    <input
                        type="checkbox"
                        checked={useSSL}
                        onChange={(e) => setUseSSL(e.target.checked)}
                        style={{marginRight: "8px"}}
                    />
                    Use SSL (Mutual TLS)
                </label>

                {useSSL && (
                    <>
                        <label style={{display: "block", marginBottom: "8px"}}>
                            Client KeyStore File:
                            <input
                                type="file"
                                accept=".jks,.p12"
                                onChange={(e) =>
                                    setKeyStoreFile(e.target.files?.[0] || null)
                                }
                                style={{display: "block", marginTop: "4px"}}
                            />
                            {keyStoreFile && (
                                <div style={{fontSize: "12px", marginTop: "4px"}}>
                                    Selected: {keyStoreFile.name}
                                </div>
                            )}
                        </label>

                        <label style={{display: "block", marginBottom: "8px"}}>
                            KeyStore Password:
                            <input
                                type="password"
                                value={keyStorePassword}
                                onChange={(e) => setKeyStorePassword(e.target.value)}
                                style={{display: "block", width: "100%", marginTop: "4px"}}
                            />
                        </label>

                        <label style={{display: "block", marginBottom: "8px"}}>
                            TrustStore File:
                            <input
                                type="file"
                                accept=".jks,.p12"
                                onChange={(e) =>
                                    setTrustStoreFile(e.target.files?.[0] || null)
                                }
                                style={{display: "block", marginTop: "4px"}}
                            />
                            {trustStoreFile && (
                                <div style={{fontSize: "12px", marginTop: "4px"}}>
                                    Selected: {trustStoreFile.name}
                                </div>
                            )}
                        </label>

                        <label style={{display: "block", marginBottom: "8px"}}>
                            TrustStore Password:
                            <input
                                type="password"
                                value={trustStorePassword}
                                onChange={(e) => setTrustStorePassword(e.target.value)}
                                style={{display: "block", width: "100%", marginTop: "4px"}}
                            />
                        </label>
                    </>
                )}

                <button
                    type="button"
                    onClick={handleLogin}
                    style={{
                        display: "block",
                        width: "100%",
                        padding: "8px",
                        backgroundColor: "#007BFF",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                        marginTop: "16px",
                    }}
                >
                    {loading ? "Logging in..." : "Login"}
                </button>
            </fieldset>
        </div>
    );
}