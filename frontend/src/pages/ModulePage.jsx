import { useEffect, useMemo, useState } from "react";
import {
  ArrowUpRight,
  BookOpen,
  Building2,
  CalendarDays,
  CheckCircle2,
  ClipboardCheck,
  FileSpreadsheet,
  GraduationCap,
  Plus,
  Search,
} from "lucide-react";
import { apiRequest } from "../api/client";

const moduleData = {
  departments: {
    eyebrow: "ACADEMIC STRUCTURE",
    title: "Departments",
    description: "Organize the academic units that power your institution.",
    icon: Building2,
    endpoint: "/admin/departments",
    columns: ["Department", "Code", "Status"],
    mapRow: (item) => [item.name, item.code, item.active ? "Active" : "Inactive"],
  },
  programs: {
    eyebrow: "ACADEMIC STRUCTURE",
    title: "Academic Programs",
    description: "Manage degrees, program ownership, and enrollment capacity.",
    icon: GraduationCap,
    endpoint: "/admin/programs",
    columns: ["Program", "Department", "Students", "Status"],
    mapRow: (item) => [item.name, item.departmentName || "—", `${item.totalSemesters} semesters`, item.active ? "Active" : "Inactive"],
  },
  "academic-years": {
    eyebrow: "ACADEMIC CALENDAR",
    title: "Academic Years",
    description: "Keep academic cycles and their active status in one place.",
    icon: CalendarDays,
    endpoint: "/admin/academic-years",
    columns: ["Academic year", "Start date", "End date", "Status"],
    mapRow: (item) => [item.label, item.startDate, item.endDate, item.active ? "Active" : "Inactive"],
  },
  subjects: {
    eyebrow: "CURRICULUM",
    title: "Subjects",
    description: "Maintain the subject catalogue used across active programs.",
    icon: BookOpen,
    endpoint: "/admin/subjects",
    columns: ["Subject", "Code", "Program", "Status"],
    mapRow: (item) => [item.title, item.code, item.programName || "—", item.active ? "Active" : "Inactive"],
  },
  "my-subjects": {
    eyebrow: "TEACHING WORKSPACE",
    title: "My Subjects",
    description: "Your assigned teaching load for the current semester.",
    icon: BookOpen,
    endpoint: "/teacher/subject-offerings",
    columns: ["Subject", "Class", "Schedule", "Students"],
    mapRow: (item) => [item.subjectCode ? `${item.subjectCode} — ${item.subjectTitle}` : item.subjectTitle || item.title, item.programName || "—", item.semesterName || item.semesterLabel || "—", item.offeringStatus || item.status || "Assigned"],
  },
  attendance: {
    eyebrow: "STUDENT ENGAGEMENT",
    title: "Attendance",
    description: "Create sessions and monitor attendance across your classes.",
    icon: ClipboardCheck,
    endpoint: "/teacher/attendance/sessions",
    columns: ["Subject", "Session date", "Present", "Attendance"],
    mapRow: (item) => [item.subjectName || item.subjectTitle || "Attendance session", item.sessionDate || item.date, item.presentCount ?? "—", item.attendancePercentage ? `${item.attendancePercentage}%` : item.status || "Open"],
  },
  marks: {
    eyebrow: "ASSESSMENTS",
    title: "Enter Marks",
    description: "Record assessment performance and keep grading on track.",
    icon: FileSpreadsheet,
    endpoint: "/teacher/marks/assessments",
    columns: ["Assessment", "Subject", "Due date", "Progress"],
    mapRow: (item) => [item.name || item.title || `Assessment #${item.id}`, item.subjectName || item.subjectTitle || "—", item.assessmentDate || item.dueDate || "—", item.status || "Open"],
  },
  results: {
    eyebrow: "STUDENT ACADEMICS",
    title: "My Results",
    description: "Review your latest academic performance and progress.",
    icon: GraduationCap,
    endpoint: "/student/results",
    columns: ["Subject", "Assessment", "Score", "Grade"],
    mapRow: (item) => [`${item.subjectCode || ""} ${item.subjectTitle || "Subject"}`.trim(), item.assessmentTitle || `Assessment #${item.assessmentId}`, `${item.marksObtained ?? "—"} / ${item.maxMarks ?? "—"}`, item.grade || "Pending"],
  },
};

function statusClass(value) {
  return String(value).toLowerCase().includes("complete") ||
    String(value).toLowerCase().includes("active")
    ? "module-status module-status-success"
    : "module-status module-status-neutral";
}

export default function ModulePage({ moduleKey }) {
  const config = moduleData[moduleKey] || moduleData.departments;
  const isAdmin = String(localStorage.getItem("roles") || "")
    .toUpperCase()
    .includes("ADMIN");
  const Icon = config.icon;
  const [query, setQuery] = useState("");
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({});
  const [options, setOptions] = useState({ departments: [], programs: [] });

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError("");

    apiRequest(config.endpoint)
      .then((data) => {
        if (!cancelled) {
          const list = Array.isArray(data) ? data : data?.content || data?.items || [];
          setRecords(list);
        }
      })
      .catch((requestError) => {
        if (!cancelled) setError(requestError.message || "Unable to load records.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => { cancelled = true; };
  }, [config.endpoint]);

  useEffect(() => {
    if (!modalOpen || !["programs", "subjects"].includes(moduleKey)) return;
    Promise.all([
      moduleKey === "programs"
        ? apiRequest("/admin/departments/active")
        : apiRequest("/admin/programs/active"),
    ]).then(([data]) => {
      const list = Array.isArray(data) ? data : [];
      setOptions(moduleKey === "programs" ? { departments: list, programs: [] } : { departments: [], programs: list });
    }).catch((requestError) => setError(requestError.message || "Unable to load form options."));
  }, [modalOpen, moduleKey]);

  function openAddModal() {
    setError("");
    setForm(moduleKey === "departments"
      ? { name: "", code: "" }
      : moduleKey === "programs"
        ? { departmentId: "", name: "", code: "", durationYears: "4", semestersPerYear: "2" }
        : moduleKey === "academic-years"
          ? { label: "", startDate: "", endDate: "" }
          : moduleKey === "subjects"
            ? { programId: "", code: "", title: "", description: "", subjectType: "THEORY", defaultCredits: "3" }
            : {});
    setModalOpen(true);
  }

  function closeAddModal() {
    if (!saving) setModalOpen(false);
  }

  function handleFormChange(event) {
    setForm((previous) => ({ ...previous, [event.target.name]: event.target.value }));
  }

  async function handleCreate(event) {
    event.preventDefault();
    setSaving(true);
    setError("");
    const payload = moduleKey === "departments"
      ? { name: form.name.trim(), code: form.code.trim() }
      : moduleKey === "programs"
        ? { departmentId: Number(form.departmentId), name: form.name.trim(), code: form.code.trim(), durationYears: Number(form.durationYears), semestersPerYear: Number(form.semestersPerYear) }
        : moduleKey === "academic-years"
          ? { label: form.label.trim(), startDate: form.startDate, endDate: form.endDate }
          : { programId: Number(form.programId), code: form.code.trim(), title: form.title.trim(), description: form.description.trim(), subjectType: form.subjectType, defaultCredits: Number(form.defaultCredits) };

    try {
      await apiRequest(config.endpoint, { method: "POST", body: payload });
      setModalOpen(false);
      setRecords((current) => current);
      const data = await apiRequest(config.endpoint);
      setRecords(Array.isArray(data) ? data : data?.content || data?.items || []);
    } catch (requestError) {
      setError(requestError.message || "Unable to create record.");
    } finally {
      setSaving(false);
    }
  }

  const fieldConfig = {
    departments: [["name", "Department name", "text"], ["code", "Department code", "text"]],
    programs: [["name", "Program name", "text"], ["code", "Program code", "text"], ["durationYears", "Duration in years", "number"], ["semestersPerYear", "Semesters per year", "number"]],
    "academic-years": [["label", "Academic year label", "text"], ["startDate", "Start date", "date"], ["endDate", "End date", "date"]],
    subjects: [["code", "Subject code", "text"], ["title", "Subject title", "text"], ["description", "Description", "text"], ["defaultCredits", "Credits", "number"]],
  };

  const rows = useMemo(
    () =>
      records.map(config.mapRow).filter((row) =>
        row.join(" ").toLowerCase().includes(query.toLowerCase())
      ),
    [config.mapRow, records, query]
  );

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div>
          <span className="eyebrow">{config.eyebrow}</span>
          <h1>{config.title}</h1>
          <p>{config.description}</p>
        </div>
        {isAdmin && ["departments", "programs", "academic-years", "subjects"].includes(moduleKey) && (
          <button className="primary-button" type="button" onClick={openAddModal}>
            <Plus size={17} />
            Add {config.title.replace("My ", "").replace("Enter ", "")}
          </button>
        )}
      </div>

      <div className="module-summary-grid">
        <article className="module-summary-card">
          <div className="module-summary-icon"><Icon size={19} /></div>
          <span>Records in view</span>
          <strong>{loading ? "—" : records.length}</strong>
        </article>
        <article className="module-summary-card">
          <div className="module-summary-icon module-summary-icon-soft"><CheckCircle2 size={19} /></div>
          <span>On track</span>
          <strong>{loading ? "—" : records.filter((item) => item.active !== false).length}</strong>
        </article>
        <article className="module-summary-card">
          <div className="module-summary-icon module-summary-icon-light"><ArrowUpRight size={19} /></div>
          <span>Last updated</span>
          <strong>Today</strong>
        </article>
      </div>

      <section className="module-table-card">
        <div className="module-table-heading">
          <div>
            <h2>Directory</h2>
            <p>Browse and manage your academic records.</p>
          </div>
          <label className="module-search">
            <Search size={17} />
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Search records"
              aria-label={`Search ${config.title}`}
            />
          </label>
        </div>
        {error && <div className="records-notice records-notice-error">{error}</div>}
        <div className="module-table-scroll">
          <table>
            <thead>
              <tr>
                {config.columns.map((column) => <th key={column}>{column}</th>)}
              </tr>
            </thead>
            <tbody>
              {loading && <tr><td className="module-empty" colSpan={config.columns.length}>Loading records…</td></tr>}
              {!loading && rows.map((row) => (
                <tr key={row.join("-")}>
                  {row.map((cell, index) => (
                    <td key={`${row[0]}-${cell}`}>
                      {index === row.length - 1 && ["Active", "Completed", "Review", "Draft"].includes(cell) ? (
                        <span className={statusClass(cell)}>{cell}</span>
                      ) : cell}
                    </td>
                  ))}
                </tr>
              ))}
              {!loading && !rows.length && (
                <tr><td className="module-empty" colSpan={config.columns.length}>No matching records found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </section>

      {modalOpen && (
        <div className="module-modal-backdrop" role="presentation" onMouseDown={closeAddModal}>
          <form className="module-modal" onSubmit={handleCreate} onMouseDown={(event) => event.stopPropagation()}>
            <div className="module-modal-heading">
              <div><span className="eyebrow">NEW RECORD</span><h2>Add {config.title}</h2></div>
              <button type="button" className="module-modal-close" onClick={closeAddModal}>×</button>
            </div>
            {moduleKey === "programs" && (
              <label>Department<select name="departmentId" value={form.departmentId} onChange={handleFormChange} required><option value="">Select department</option>{options.departments.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
            )}
            {moduleKey === "subjects" && (
              <label>Program<select name="programId" value={form.programId} onChange={handleFormChange} required><option value="">Select program</option>{options.programs.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
            )}
            {fieldConfig[moduleKey]?.map(([name, label, type]) => (
              <label key={name}>{label}<input name={name} type={type} value={form[name] || ""} onChange={handleFormChange} required={name !== "description"} min={type === "number" ? (["durationYears", "semestersPerYear"].includes(name) ? "1" : "0.01") : undefined} step={type === "number" ? (["durationYears", "semestersPerYear"].includes(name) ? "1" : "0.01") : undefined} /></label>
            ))}
            {moduleKey === "subjects" && <label>Subject type<select name="subjectType" value={form.subjectType} onChange={handleFormChange}><option>THEORY</option><option>LABORATORY</option></select></label>}
            {error && <div className="login-error">{error}</div>}
            <button className="primary-button" type="submit" disabled={saving}>{saving ? "Saving…" : "Create record"}</button>
          </form>
        </div>
      )}
    </div>
  );
}
