import { useEffect, useState } from "react";
import { ClipboardCheck, Plus } from "lucide-react";
import { apiRequest } from "../api/client";

function list(value) {
  return Array.isArray(value) ? value : value?.content || value?.items || [];
}

export default function Assignments() {
  const [teachers, setTeachers] = useState([]);
  const [offerings, setOfferings] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [semesters, setSemesters] = useState([]);
  const [teacherId, setTeacherId] = useState("");
  const [offeringId, setOfferingId] = useState("");
  const [subjectId, setSubjectId] = useState("");
  const [semesterId, setSemesterId] = useState("");
  const [credits, setCredits] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  async function load() {
    const [teacherData, offeringData, assignmentData, subjectData, semesterData] = await Promise.all([
      apiRequest("/admin/teachers/active"),
      apiRequest("/admin/subject-offerings"),
      apiRequest("/admin/teacher-assignments"),
      apiRequest("/admin/subjects/active"),
      apiRequest("/admin/semesters"),
    ]);
    setTeachers(list(teacherData));
    setOfferings(list(offeringData));
    setAssignments(list(assignmentData));
    setSubjects(list(subjectData));
    setSemesters(list(semesterData));
  }

  useEffect(() => {
    load().catch((requestError) => setError(requestError.message || "Unable to load assignments."));
  }, []);

  async function createOffering(event) {
    event.preventDefault();
    setSaving(true);
    setError("");
    setMessage("");
    try {
      await apiRequest("/admin/subject-offerings", {
        method: "POST",
        body: { subjectId: Number(subjectId), semesterId: Number(semesterId), credits: Number(credits) },
      });
      setSubjectId("");
      setSemesterId("");
      setCredits("");
      setMessage("Subject offering created successfully.");
      await load();
    } catch (requestError) {
      setError(requestError.message || "Unable to create subject offering.");
    } finally {
      setSaving(false);
    }
  }

  async function submit(event) {
    event.preventDefault();
    setSaving(true);
    setError("");
    setMessage("");
    try {
      await apiRequest("/admin/teacher-assignments", {
        method: "POST",
        body: { teacherId: Number(teacherId), offeringId: Number(offeringId) },
      });
      setTeacherId("");
      setOfferingId("");
      setMessage("Teacher assigned to subject offering successfully.");
      await load();
    } catch (requestError) {
      setError(requestError.message || "Unable to create assignment.");
    } finally {
      setSaving(false);
    }

  }

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div><span className="eyebrow">ACADEMIC OPERATIONS</span><h1>Teacher Assignments</h1><p>Assign teachers to subject offerings for a specific semester.</p></div>
      </div>
      <section className="assignment-layout">
        <div className="assignment-stack">
        <form className="content-card assignment-form" onSubmit={createOffering}>
          <div className="content-card-heading"><div><span className="eyebrow">STEP 1</span><h3>Create offering</h3></div><ClipboardCheck size={22} /></div>
          <label>Subject<select value={subjectId} onChange={(event) => setSubjectId(event.target.value)} required><option value="">Select subject</option>{subjects.map((subject) => <option key={subject.id} value={subject.id}>{subject.code} · {subject.title}</option>)}</select></label>
          <label>Semester<select value={semesterId} onChange={(event) => setSemesterId(event.target.value)} required><option value="">Select semester</option>{semesters.map((semester) => <option key={semester.id} value={semester.id}>Sem {semester.semesterNumber} · {semester.programName || `Program ${semester.programId}`} · {semester.academicYearLabel || semester.academicYearId}</option>)}</select></label>
          <label>Credits<input type="number" min="0.01" step="0.01" value={credits} onChange={(event) => setCredits(event.target.value)} required /></label>
          <button className="primary-button" type="submit" disabled={saving}><Plus size={16} />Create offering</button>
        </form>
        <form className="content-card assignment-form" onSubmit={submit}>
          <div className="content-card-heading"><div><span className="eyebrow">STEP 2</span><h3>Assign subject</h3></div><ClipboardCheck size={22} /></div>
          <label>Teacher<select value={teacherId} onChange={(event) => setTeacherId(event.target.value)} required><option value="">Select teacher</option>{teachers.map((teacher) => <option key={teacher.id} value={teacher.id}>{teacher.firstName} {teacher.lastName} · {teacher.employeeId}</option>)}</select></label>
          <label>Subject offering<select value={offeringId} onChange={(event) => setOfferingId(event.target.value)} required><option value="">Select offering</option>{offerings.map((offering) => <option key={offering.id} value={offering.id}>{offering.subjectCode} · {offering.subjectTitle} · {offering.programName} · Sem {offering.semesterNumber}</option>)}</select></label>
          {error && <div className="login-error">{error}</div>}
          {message && <div className="records-notice records-notice-success">{message}</div>}
          <button className="primary-button" type="submit" disabled={saving}><Plus size={16} />{saving ? "Assigning…" : "Assign teacher"}</button>
        </form>
        </div>
        <section className="module-table-card">
          <div className="module-table-heading"><div><h2>Current assignments</h2><p>{assignments.length} assignment(s)</p></div></div>
          <div className="module-table-scroll"><table><thead><tr><th>Teacher</th><th>Subject offering</th><th>Program</th></tr></thead><tbody>{assignments.map((item) => <tr key={item.id}><td>{item.teacherName || item.teacherUsername || item.teacherId}</td><td>{item.subjectTitle || item.subjectCode || item.offeringId}</td><td>{item.programName || "—"}</td></tr>)}{!assignments.length && <tr><td className="module-empty" colSpan="3">No assignments found.</td></tr>}</tbody></table></div>
        </section>
      </section>
    </div>
  );
}
