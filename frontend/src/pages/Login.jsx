
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { GraduationCap, Eye, EyeOff, LockKeyhole, UserRound } from "lucide-react";
import { login } from "../api/client";

function getDashboardPath(roles = []) {
  const normalizedRoles = roles.map((role) =>
    String(role).replace(/^ROLE_/, "").toUpperCase()
  );

  if (normalizedRoles.includes("ADMIN")) return "/dashboard";
  if (normalizedRoles.includes("TEACHER")) return "/dashboard";
  if (normalizedRoles.includes("STUDENT")) return "/dashboard";

  return "/dashboard";
}

export default function Login() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    if (!username.trim() || !password) {
      setError("Please enter your username and password.");
      return;
    }

    setLoading(true);

    try {
      const response = await login(username.trim(), password);

      if (!response?.accessToken) {
        throw new Error("Login response did not contain an access token.");
      }

      if (response.mustChangePassword) {
        navigate("/change-password", { replace: true });
        return;
      }

      navigate(getDashboardPath(response.roles || []), {
        replace: true,
      });
    } catch (err) {
      setError(err.message || "Unable to sign in. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-brand-panel">
        <div className="login-brand">
          <div className="brand-icon">
            <GraduationCap size={32} />
          </div>
          <span>SmartAcademic</span>
        </div>

        <div className="login-brand-content">
          <span className="eyebrow">ACADEMIC MANAGEMENT SYSTEM</span>
          <h1>Manage education with clarity and confidence.</h1>
          <p>
            A central place to manage students, faculty, attendance,
            assessments and academic results.
          </p>
        </div>

        <p className="login-brand-footer">
          SmartAcademic · Academic Management Portal
        </p>
      </section>

      <section className="login-form-panel">
        <div className="login-form-container">
          <div className="login-mobile-brand">
            <GraduationCap size={30} />
            <span>SmartAcademic</span>
          </div>

          <span className="eyebrow">WELCOME BACK</span>
          <h2>Sign in to your account</h2>
          <p className="login-subtitle">
            Enter your credentials to access your dashboard.
          </p>

          <form onSubmit={handleSubmit} className="login-form">
            <label htmlFor="username">Username</label>
            <div className="login-input-wrapper">
              <UserRound size={19} aria-hidden="true" />
              <input
                id="username"
                name="username"
                type="text"
                autoComplete="username"
                placeholder="Enter your username"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                required
                disabled={loading}
              />
            </div>

            <label htmlFor="password">Password</label>
            <div className="login-input-wrapper">
              <LockKeyhole size={19} aria-hidden="true" />
              <input
                id="password"
                name="password"
                type={showPassword ? "text" : "password"}
                autoComplete="current-password"
                placeholder="Enter your password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
                disabled={loading}
              />
              <button
                className="password-toggle"
                type="button"
                onClick={() => setShowPassword((visible) => !visible)}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
              </button>
            </div>

            {error && (
              <div className="login-error" role="alert">
                {error}
              </div>
            )}

            <button
              className="login-submit"
              type="submit"
              disabled={loading}
            >
              {loading ? "Signing in..." : "Sign in"}
            </button>
          </form>

          <p className="login-help">
            Use the username and password provided by your administrator.
          </p>

          <p className="login-copyright">
            © {new Date().getFullYear()} SmartAcademic
          </p>
        </div>
      </section>
    </main>
  );
}
