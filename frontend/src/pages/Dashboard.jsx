
import { useEffect, useState } from "react";
import {
  Users,
  UserRound,
  Building2,
  BookOpen,
  ArrowUpRight,
  GraduationCap,
  CalendarDays,
  Activity,
  ClipboardCheck,
} from "lucide-react";
import { apiRequest, getRoles } from "../api/client";
import { useNavigate } from "react-router-dom";

const statsConfig = [
  {
    key: "students",
    title: "Total Students",
    endpoint: "/admin/students",
    icon: Users,
    color: "blue",
  },
  {
    key: "teachers",
    title: "Total Teachers",
    endpoint: "/admin/teachers",
    icon: UserRound,
    color: "purple",
  },
  {
    key: "departments",
    title: "Departments",
    endpoint: "/admin/departments",
    icon: Building2,
    color: "orange",
  },
  {
    key: "programs",
    title: "Academic Programs",
    endpoint: "/admin/programs",
    icon: BookOpen,
    color: "green",
  },
  {
    key: "subjects",
    title: "Subjects",
    endpoint: "/admin/subjects",
    icon: BookOpen,
    color: "blue",
  },
  {
    key: "semesters",
    title: "Active Semesters",
    endpoint: "/admin/semesters",
    icon: CalendarDays,
    color: "orange",
  },
];

function getArray(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.items)) return data.items;
  return null;
}

function normalizeRole(role) {
  return String(role).replace(/^ROLE_/, "").toUpperCase();
}

export default function Dashboard() {
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [attendance, setAttendance] = useState(null);
  const [teacherStats, setTeacherStats] = useState({});
  const navigate = useNavigate();

  const roles = getRoles().map(normalizeRole);
  const role = roles.find((item) => ["ADMIN", "TEACHER", "STUDENT"].includes(item)) || "USER";
  const isAdmin = role === "ADMIN";

  useEffect(() => {
    let cancelled = false;

    async function loadStats() {
      if (!isAdmin && role === "STUDENT") {
        try {
          const [attendanceData, resultsData] = await Promise.all([
            apiRequest("/student/attendance"),
            apiRequest("/student/results"),
          ]);
          if (!cancelled) {
            setAttendance(attendanceData);
            const results = getArray(resultsData) || [];
            setStats({
              attendance: attendanceData?.attendancePercentage ?? 0,
              results: results.length,
              subjects: new Set(results.map((item) => item.subjectId || item.subjectCode || item.subjectTitle).filter(Boolean)).size,
            });
          }
        } catch {
          if (!cancelled) setMessage("Unable to load your attendance summary.");
        }
        setLoading(false);
        return;
      }

      if (role === "TEACHER") {
        const endpoints = [
          ["subjects", "/teacher/subject-offerings"],
          ["attendance", "/teacher/attendance/sessions"],
          ["marks", "/teacher/marks/assessments"],
        ];
        const results = await Promise.all(endpoints.map(async ([key, endpoint]) => {
          try {
            const data = await apiRequest(endpoint);
            return [key, getArray(data)?.length ?? 0];
          } catch { return [key, 0]; }
        }));
        if (!cancelled) setTeacherStats(Object.fromEntries(results));
        setLoading(false);
        return;
      }

      if (!isAdmin) {
        setLoading(false);
        return;
      }

      const results = await Promise.all(
        statsConfig.map(async (item) => {
          try {
            const data = await apiRequest(item.endpoint);
            const array = getArray(data);

            return [item.key, array ? array.length : null];
          } catch {
            return [item.key, null];
          }
        })
      );

      if (!cancelled) {
        setStats(Object.fromEntries(results));
        setMessage(
          results.every(([, value]) => value === null)
            ? "Unable to load statistics. Check that the backend is running and your account has administrator access."
            : ""
        );
        setLoading(false);
      }
    }

    loadStats();

    return () => {
      cancelled = true;
    };
  }, [isAdmin, role]);

  const cards = isAdmin
    ? statsConfig
    : role === "TEACHER"
      ? [
          { key: "subjects", title: "My Subjects", icon: BookOpen, color: "blue" },
          { key: "attendance", title: "Attendance", icon: CalendarDays, color: "green" },
          { key: "marks", title: "Assessments", icon: Activity, color: "purple" },
          { key: "reports", title: "Reports", icon: GraduationCap, color: "orange" },
        ]
      : [
          { key: "attendance", title: "Attendance", icon: ClipboardCheck, color: "green" },
          { key: "results", title: "My Results", icon: GraduationCap, color: "blue" },
          { key: "subjects", title: "My Subjects", icon: BookOpen, color: "purple" },
        ];

  return (
    <div className="dashboard-page">
      <section className="dashboard-hero">
        <div className="dashboard-hero-copy">
          <span className="hero-eyebrow">YOUR ACADEMIC OVERVIEW</span>
          <h1>Welcome back, {localStorage.getItem("username") || "User"}!</h1>
          <p>
            {isAdmin
              ? "Manage your institution and keep track of key academic information."
              : role === "TEACHER"
                ? "Your teaching workspace is ready. Manage your subjects, attendance and assessments."
                : "Your student workspace is ready. Keep track of your academic progress and results."}
          </p>
          <span className="hero-role">{role} WORKSPACE</span>
        </div>

        <div className="hero-decoration">
          <GraduationCap size={90} strokeWidth={1.15} />
        </div>
      </section>

      <div className="section-heading">
        <div>
          <span className="eyebrow">AT A GLANCE</span>
          <h2>{isAdmin ? "Institution overview" : "Your workspace"}</h2>
        </div>
        <span className="live-indicator">
          <span /> Overview
        </span>
      </div>

      {message && <div className="inline-notice">{message}</div>}

      <section className="stats-grid">
        {cards.map((card) => {
          const Icon = card.icon;
          const value = isAdmin ? stats[card.key] : role === "TEACHER" ? teacherStats[card.key] : stats[card.key];

          return (
            <article className="stat-card stat-card-clickable" key={card.key} onClick={() => {
              const routes = { subjects: role === "TEACHER" ? "/my-subjects" : "/results", attendance: role === "TEACHER" ? "/attendance" : "/student-attendance", marks: "/marks", results: "/results", reports: "/attendance" };
              if (!isAdmin && routes[card.key]) navigate(routes[card.key]);
            }}>
              <div className={`stat-icon stat-icon-${card.color}`}>
                <Icon size={22} />
              </div>
              <div className="stat-label">{card.title}</div>
              <div className="stat-value">
                {loading ? "…" : value === undefined || value === null ? "—" : value}
              </div>
              <div className="stat-footer">
                <span>{isAdmin ? "Current records" : "Academic workspace"}</span>
                <ArrowUpRight size={16} />
              </div>
            </article>
          );
        })}
      </section>

      {role === "STUDENT" && (
        <section className="content-card attendance-summary-card">
          <div className="content-card-heading">
            <div><span className="eyebrow">STUDENT ENGAGEMENT</span><h3>My attendance</h3></div>
            <div className="soft-icon"><ClipboardCheck size={21} /></div>
          </div>
          <div className="account-row"><span>Attendance percentage</span><strong>{attendance ? `${attendance.attendancePercentage}%` : "—"}</strong></div>
          <div className="account-row"><span>Present</span><strong>{attendance?.presentCount ?? "—"} / {attendance?.totalSessions ?? "—"} sessions</strong></div>
          <div className="account-row"><span>Absent</span><strong>{attendance?.absentCount ?? "—"}</strong></div>
        </section>
      )}

      <section className="dashboard-bottom-grid">
        <article className="content-card">
          <div className="content-card-heading">
            <div>
              <span className="eyebrow">GETTING STARTED</span>
              <h3>Your workspace</h3>
            </div>
            <div className="soft-icon">
              <GraduationCap size={21} />
            </div>
          </div>

          <p className="muted-text">
            Use the navigation menu to access the tools available to your account.
          </p>

          <div className="getting-started-list">
            <div><span className="step-number">01</span><span>Open a section from the sidebar.</span></div>
            <div><span className="step-number">02</span><span>Review or update academic records.</span></div>
            <div><span className="step-number">03</span><span>Sign out when you finish your work.</span></div>
          </div>
        </article>

        <article className="content-card account-card">
          <div className="content-card-heading">
            <div>
              <span className="eyebrow">ACCOUNT</span>
              <h3>Session information</h3>
            </div>
            <div className="soft-icon"><UserRound size={21} /></div>
          </div>

          <div className="account-details">
            <div className="account-avatar"><UserRound size={26} /></div>
            <div>
              <strong>{localStorage.getItem("username") || "User"}</strong>
              <span>Signed in to SmartAcademic</span>
            </div>
          </div>

          <div className="account-row">
            <span>Account type</span>
            <strong>{role}</strong>
          </div>
          <div className="account-row">
            <span>Authentication</span>
            <strong className="authenticated-label">Token session</strong>
          </div>
        </article>
      </section>
    </div>
  );
}
