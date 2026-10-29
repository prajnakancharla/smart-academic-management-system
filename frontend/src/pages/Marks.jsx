import { useEffect, useState } from "react";
import { apiRequest } from "../api/client";

const today = new Date().toISOString().slice(0, 10);
const types = ["ASSIGNMENT", "QUIZ", "INTERNAL_EXAM", "MIDTERM", "FINAL_EXAM", "PROJECT", "PRACTICAL"];
const list = (value) => Array.isArray(value) ? value : value?.content || value?.items || [];

export default function Marks() {
  const [offerings, setOfferings] = useState([]);
  const [assessments, setAssessments] = useState([]);
  const [students, setStudents] = useState([]);
  const [savedMarks, setSavedMarks] = useState({});
  const [selectedAssessment, setSelectedAssessment] = useState("");
  const [form, setForm] = useState({ offeringId: "", title: "", assessmentType: "ASSIGNMENT", assessmentDate: today, maxMarks: "100", passMarks: "40" });
  const [error, setError] = useState(""); const [message, setMessage] = useState(""); const [saving, setSaving] = useState(false);

  async function load() {
    const [offeringData, assessmentData] = await Promise.all([apiRequest("/teacher/subject-offerings"), apiRequest("/teacher/marks/assessments")]);
    setOfferings(list(offeringData)); setAssessments(list(assessmentData));
  }
  useEffect(() => { load().catch((err) => setError(err.message || "Unable to load marks.")); }, []);

  async function createAssessment(event) {
    event.preventDefault(); setSaving(true); setError(""); setMessage("");
    try {
      await apiRequest("/teacher/marks/assessments", { method: "POST", body: { ...form, offeringId: Number(form.offeringId), maxMarks: Number(form.maxMarks), passMarks: Number(form.passMarks) } });
      setMessage("Assessment created."); await load();
    } catch (err) { setError(err.message || "Unable to create assessment."); } finally { setSaving(false); }
  }

  async function selectAssessment(id) {
    setSelectedAssessment(id); setError("");
    try {
      const assessment = assessments.find((item) => String(item.id) === String(id));
      const [studentData, marksData] = await Promise.all([apiRequest(`/teacher/subject-offerings/${assessment.offeringId}/students`), apiRequest(`/teacher/marks/assessments/${id}/students`)]);
      setStudents(list(studentData)); setSavedMarks(Object.fromEntries(list(marksData).map((item) => [item.studentId, item])));
    } catch (err) { setError(err.message || "Unable to load the class roster."); }
  }

  async function saveMark(studentId, value) {
    if (value === "") return;
    setSaving(true); setError("");
    try {
      const saved = await apiRequest(`/teacher/marks/assessments/${selectedAssessment}/students`, { method: "POST", body: { studentId, marksObtained: Number(value) } });
      setSavedMarks((current) => ({ ...current, [studentId]: saved })); setMessage("Mark saved.");
    } catch (err) { setError(err.message || "Unable to save mark."); } finally { setSaving(false); }
  }

  return <div className="records-page">
    <div className="section-heading"><div><span className="eyebrow">ASSESSMENTS</span><h2>Enter Marks</h2><p className="muted-text">Create assessments and enter marks for students in that subject.</p></div></div>
    {error && <div className="inline-notice">{error}</div>}{message && <div className="records-notice records-notice-success">{message}</div>}
    <section className="content-card"><h3>Create assessment</h3><form className="record-form-grid" onSubmit={createAssessment}>
      <label>Subject<select required value={form.offeringId} onChange={(e) => setForm({ ...form, offeringId: e.target.value })}><option value="">Select subject</option>{offerings.map((item) => <option key={item.offeringId} value={item.offeringId}>{item.subjectCode} — {item.subjectTitle}</option>)}</select></label>
      <label>Title<input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></label>
      <label>Type<select value={form.assessmentType} onChange={(e) => setForm({ ...form, assessmentType: e.target.value })}>{types.map((type) => <option key={type}>{type}</option>)}</select></label>
      <label>Date<input type="date" required max={today} value={form.assessmentDate} onChange={(e) => setForm({ ...form, assessmentDate: e.target.value })} /></label>
      <label>Maximum marks<input type="number" min="0.01" step="0.01" required value={form.maxMarks} onChange={(e) => setForm({ ...form, maxMarks: e.target.value })} /></label>
      <label>Pass marks<input type="number" min="0" step="0.01" required value={form.passMarks} onChange={(e) => setForm({ ...form, passMarks: e.target.value })} /></label>
      <button className="primary-button" disabled={saving}>Create assessment</button>
    </form></section>
    <section className="content-card"><h3>Student marks</h3><select className="wide-select" value={selectedAssessment} onChange={(e) => selectAssessment(e.target.value)}><option value="">Select an assessment</option>{assessments.map((item) => <option key={item.id} value={item.id}>{item.title} · {item.assessmentDate}</option>)}</select>
      {selectedAssessment && <div className="records-table-wrap"><table className="records-table"><thead><tr><th>Roll number</th><th>Student</th><th>Marks</th><th>Result</th></tr></thead><tbody>{students.map((student) => { const saved = savedMarks[student.id]; return <tr key={student.id}><td>{student.rollNumber}</td><td>{student.firstName} {student.lastName}</td><td><input type="number" min="0" step="0.01" defaultValue={saved?.marksObtained ?? ""} onBlur={(e) => saveMark(student.id, e.target.value)} /></td><td>{saved ? `${saved.marksObtained} / ${saved.maxMarks} (${saved.grade})` : "Not entered"}</td></tr>; })}</tbody></table></div>}
    </section>
  </div>;
}
