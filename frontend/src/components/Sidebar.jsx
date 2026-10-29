
import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  GraduationCap,
  LayoutDashboard,
  Users,
  UserRound,
  Building2,
  BookOpen,
  CalendarDays,
  ClipboardCheck,
  FileSpreadsheet,
  ChartNoAxesColumn,
  PanelLeftClose,
  PanelLeftOpen,
  LogOut,
} from "lucide-react";
import { clearSession, getRoles } from "../api/client";

const menuByRole = {
  ADMIN: [
    { to: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
    { to: "/students", label: "Students", icon: Users },
    { to: "/teachers", label: "Teachers", icon: UserRound },
    { to: "/departments", label: "Departments", icon: Building2 },
    { to: "/programs", label: "Programs", icon: GraduationCap },
    { to: "/academic-years", label: "Academic Years", icon: CalendarDays },
    { to: "/subjects", label: "Subjects", icon: BookOpen },
    { to: "/assignments", label: "Teacher Assignments", icon: ClipboardCheck },
  ],
  TEACHER: [
    { to: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
    { to: "/my-subjects", label: "My Subjects", icon: BookOpen },
    { to: "/attendance", label: "Attendance", icon: ClipboardCheck },
    { to: "/marks", label: "Enter Marks", icon: FileSpreadsheet },
  ],
  STUDENT: [
    { to: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
    { to: "/student-attendance", label: "My Attendance", icon: ClipboardCheck },
    { to: "/results", label: "My Results", icon: ChartNoAxesColumn },
  ],
};

function normalizedRole(role) {
  return String(role).replace(/^ROLE_/, "").toUpperCase();
}

export default function Sidebar() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const roles = getRoles().map(normalizedRole);
  const role = roles.find((item) => menuByRole[item]) || "STUDENT";
  const menu = menuByRole[role];

  function handleLogout() {
    clearSession();
    navigate("/login", { replace: true });
  }

  return (
    <aside className={`sidebar ${collapsed ? "sidebar-collapsed" : ""}`}>
      <div className="sidebar-brand">
        <div className="brand-icon">
          <GraduationCap size={25} />
        </div>
        {!collapsed && (
          <div className="sidebar-brand-text">
            <strong>SmartAcademic</strong>
            <span>Management Portal</span>
          </div>
        )}
      </div>

      <div className="sidebar-section-label">
        {!collapsed && "WORKSPACE"}
      </div>

      <nav className="sidebar-nav" aria-label="Main navigation">
        {menu.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            title={collapsed ? label : undefined}
            className={({ isActive }) =>
              `nav-item ${isActive ? "nav-item-active" : ""}`
            }
          >
            <Icon size={19} />
            {!collapsed && <span>{label}</span>}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-bottom">
        <button
          className="nav-item sidebar-button"
          onClick={() => setCollapsed((value) => !value)}
          title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
        >
          {collapsed ? <PanelLeftOpen size={19} /> : <PanelLeftClose size={19} />}
          {!collapsed && <span>Collapse sidebar</span>}
        </button>

        <button className="nav-item sidebar-button logout-button" onClick={handleLogout}>
          <LogOut size={19} />
          {!collapsed && <span>Sign out</span>}
        </button>
      </div>

      {!collapsed && (
        <div className="sidebar-role">
          <span className="role-dot" />
          <span>{role} ACCOUNT</span>
        </div>
      )}
    </aside>
  );
}
