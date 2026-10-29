import { useEffect, useState } from "react";
import { apiRequest } from "../api/client";

const today = new Date().toISOString().slice(0, 10);
const list = (value) => Array.isArray(value) ? value : value?.content || value?.items || [];

export default function Attendance() {
  const [offerings, setOfferings] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [students, setStudents] = useState([]);
  const [attendance, setAttendance] = useState({});
  const [selectedSession, setSelectedSession] = useState("");
  const [form, setForm] = useState({ offeringId: "", sessionDate: today, classHour: "1", topic: "" });
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [saving, setSaving] = useState(false);

  async function load() {
    const [offeringData, sessionData] = await Promise.all([
      apiRequest("/teacher/subject-offerings"),
      apiRequest("/teacher/attendance/sessions"),
    ]);
    setOfferings(list(offeringData));
    setSessions(list(sessionData));
  }

  useEffect(() => {
    load().catch((err) => setError(err.message || "Unable to load attendance."));
  }, []);

  async function createSession(event) {
    event.preventDefault();
    setSaving(true); setError(""); setMessage("");
    try {
      await apiRequest("/teacher/attendance/sessions", {
        method: "POST",
        body: { ...form, offeringId: Number(form.offeringId), classHour: Number(form.classHour) },
      });
      setMessage("Attendance session created.");
      await load();
    } catch (err) {
      setError(err.message || "Unable to create attendance session.");
    } finally { setSaving(false); }
  }

  async function selectSession(sessionId) {
    setSelectedSession(sessionId);
    setError(""); setMessage("");
    try {
      const session = sessions.find((item) => String(item.id) === String(sessionId));
      const [studentData, attendanceData] = await Promise.all([
        apiRequest(`/teacher/subject-offerings/${session.offeringId}/students`),
        apiRequest(`/teacher/attendance/sessions/${sessionId}/students`),
      ]);
      setStudents(list(studentData));
      setAttendance(Object.fromEntries(list(attendanceData).map((item) => [item.studentId, item])));
    } catch (err) { setError(err.message || "Unable to load the class roster."); }
  }

  async function mark(studentId, status) {
    setSaving(true); setError(""); setMessage("");
    try {
      const saved = await apiRequest(`/teacher/attendance/sessions/${selectedSession}/students`, {
        method: "POST", body: { studentId, status },
      });
      setAttendance((current) => ({ ...current, [studentId]: saved }));
      setMessage("Attendance saved.");
    } catch (err) { setError(err.message || "Unable to save attendance."); }
    finally { setSaving(false); }
  }

  return (
    <div className="records-page">
      <div className="section-heading"><div><span className="eyebrow">STUDENT ENGAGEMENT</span><h2>Attendance</h2><p className="muted-text">Take attendance for every active student enrolled in the selected subject.</p></div></div>
      {error && <div className="inline-notice">{error}</div>}
      {message && <div className="records-notice records-notice-success">{message}</div>}
      <section className="content-card">
        <h3>Create attendance session</h3>
        <form className="record-form-grid" onSubmit={createSession}>
          <label>Subject<select required value={form.offeringId} onChange={(e) => setForm({ ...form, offeringId: e.target.value })}><option value="">Select subject</option>{offerings.map((item) => <option key={item.offeringId} value={item.offeringId}>{item.subjectCode} — {item.subjectTitle}</option>)}</select></label>
          <label>Date<input type="date" required value={form.sessionDate} max={today} onChange={(e) => setForm({ ...form, sessionDate: e.target.value })} /></label>
          <label>Class hour<input type="number" min="1" max="12" required value={form.classHour} onChange={(e) => setForm({ ...form, classHour: e.target.value })} /></label>
          <label>Topic<input value={form.topic} maxLength="255" onChange={(e) => setForm({ ...form, topic: e.target.value })} /></label>
          <button className="primary-button" disabled={saving}>Create session</button>
        </form>
      </section>
      <section className="content-card">
        <h3>Mark students</h3>
        <select className="wide-select" value={selectedSession} onChange={(e) => selectSession(e.target.value)}><option value="">Select a session</option>{sessions.map((item) => <option key={item.id} value={item.id}>{item.sessionDate} · Hour {item.classHour} · Session #{item.id}</option>)}</select>
        {selectedSession && <div className="records-table-wrap"><table className="records-table"><thead><tr><th>Roll number</th><th>Student</th><th>Status</th><th>Action</th></tr></thead><tbody>{students.map((student) => { const saved = attendance[student.id]; return <tr key={student.id}><td>{student.rollNumber}</td><td>{student.firstName} {student.lastName}</td><td>{saved?.status || "Not marked"}</td><td>{saved ? "Saved" : <div className="button-row">{["PRESENT", "ABSENT", "LATE", "EXCUSED"].map((status) => <button type="button" key={status} className="secondary-button" disabled={saving} onClick={() => mark(student.id, status)}>{status}</button>)}</div>}</td></tr>; })}</tbody></table></div>}
      </section>
    </div>
  );
}
