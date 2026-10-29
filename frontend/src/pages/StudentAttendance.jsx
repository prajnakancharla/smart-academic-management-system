import { useEffect, useState } from "react";
import { apiRequest } from "../api/client";

export default function StudentAttendance() {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    apiRequest("/student/attendance")
      .then(setSummary)
      .catch((requestError) => setError(requestError.message || "Unable to load attendance."));
  }, []);

  return (
    <div className="records-page">
      <div className="section-heading">
        <div><span className="eyebrow">STUDENT ENGAGEMENT</span><h1>My Attendance</h1><p className="muted-text">Review attendance by subject and your overall attendance.</p></div>
      </div>
      {error && <div className="inline-notice">{error}</div>}
      <section className="attendance-subject-grid">
        {summary?.subjects?.map((subject) => (
          <article className="content-card attendance-subject-card" key={subject.subjectId}>
            <div className="content-card-heading"><div><span className="eyebrow">{subject.subjectCode}</span><h3>{subject.subjectTitle}</h3></div></div>
            <strong className="attendance-percentage">{subject.attendancePercentage}%</strong>
            <div className="attendance-breakdown">{subject.presentCount} present · {subject.absentCount} absent · {subject.totalSessions} sessions</div>
          </article>
        ))}
      </section>
      <section className="content-card attendance-overall-card">
        <div className="content-card-heading"><div><span className="eyebrow">OVERALL</span><h3>Overall attendance</h3></div></div>
        <strong className="attendance-percentage">{summary ? `${summary.attendancePercentage}%` : "—"}</strong>
        <div className="attendance-breakdown">{summary?.presentCount ?? "—"} present · {summary?.absentCount ?? "—"} absent · {summary?.totalSessions ?? "—"} sessions</div>
      </section>
    </div>
  );
}
