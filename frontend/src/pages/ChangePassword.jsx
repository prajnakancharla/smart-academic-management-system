import { useState } from "react";
import { LockKeyhole, ShieldCheck } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { changePassword } from "../api/client";

export default function ChangePassword() {
  const navigate = useNavigate();
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmation, setConfirmation] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setMessage("");

    if (newPassword !== confirmation) {
      setError("New password and confirmation must match.");
      return;
    }

    setLoading(true);
    try {
      await changePassword(currentPassword, newPassword);
      localStorage.removeItem("mustChangePassword");
      setMessage("Password changed successfully. Redirecting to your dashboard…");
      window.setTimeout(() => navigate("/dashboard", { replace: true }), 900);
    } catch (requestError) {
      setError(requestError.message || "Unable to change password.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page change-password-page">
      <section className="login-brand-panel">
        <div className="login-brand">
          <div className="brand-icon"><ShieldCheck size={30} /></div>
          <span>SmartAcademic</span>
        </div>
        <div className="login-brand-content">
          <span className="eyebrow">ACCOUNT SECURITY</span>
          <h1>Protect your academic workspace.</h1>
          <p>Your administrator issued a temporary password. Set a private password before continuing.</p>
        </div>
        <p className="login-brand-footer">SmartAcademic · Secure access</p>
      </section>

      <section className="login-form-panel">
        <div className="login-form-container">
          <span className="eyebrow">FIRST LOGIN</span>
          <h2>Change your password</h2>
          <p className="login-subtitle">Use at least 12 characters for your new password.</p>
          <form onSubmit={handleSubmit} className="login-form">
            <label htmlFor="current-password">Current password</label>
            <div className="login-input-wrapper">
              <LockKeyhole size={19} aria-hidden="true" />
              <input id="current-password" type="password" value={currentPassword} onChange={(event) => setCurrentPassword(event.target.value)} required />
            </div>
            <label htmlFor="new-password">New password</label>
            <div className="login-input-wrapper">
              <LockKeyhole size={19} aria-hidden="true" />
              <input id="new-password" type="password" minLength={12} value={newPassword} onChange={(event) => setNewPassword(event.target.value)} required />
            </div>
            <label htmlFor="confirm-password">Confirm new password</label>
            <div className="login-input-wrapper">
              <LockKeyhole size={19} aria-hidden="true" />
              <input id="confirm-password" type="password" minLength={12} value={confirmation} onChange={(event) => setConfirmation(event.target.value)} required />
            </div>
            {error && <div className="login-error" role="alert">{error}</div>}
            {message && <div className="records-notice records-notice-success">{message}</div>}
            <button className="login-submit" type="submit" disabled={loading}>
              {loading ? "Updating…" : "Update password"}
            </button>
          </form>
        </div>
      </section>
    </main>
  );
}
