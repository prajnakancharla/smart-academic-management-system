
import { Bell, Search, UserRound, KeyRound } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { getRoles } from "../api/client";

export default function Topbar() {
  const navigate = useNavigate();
  const username = localStorage.getItem("username") || "User";
  const roles = getRoles().map((role) =>
    String(role).replace(/^ROLE_/, "").toUpperCase()
  );
  const role = roles[0] || "USER";

  return (
    <header className="topbar">
      <div className="topbar-heading">
        <span className="topbar-kicker">SMARTACADEMIC PORTAL</span>
        <span className="topbar-welcome">Academic workspace</span>
      </div>

      <div className="topbar-actions">
        <div className="topbar-search">
          <Search size={17} />
          <span>Academic management</span>
        </div>

        <button
          type="button"
          className="topbar-password-button"
          aria-label="Notifications"
          title="Notifications"
        >
          <Bell size={19} />
        </button>
        <button
          type="button"
          className="topbar-icon-button"
          aria-label="Change password"
          title="Change password"
          onClick={() => navigate("/change-password")}
        >
          <KeyRound size={17} />
          <span>Change password</span>
        </button>

        <div className="topbar-profile">
          <div className="topbar-avatar">
            <UserRound size={19} />
          </div>
          <div className="topbar-profile-text">
            <strong>{username}</strong>
            <span>{role}</span>
          </div>
        </div>
      </div>
    </header>
  );
}
