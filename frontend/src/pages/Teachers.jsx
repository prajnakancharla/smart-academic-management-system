import { useCallback, useEffect, useMemo, useState } from "react";
import { apiRequest } from "../api/client";
import "./Teachers.css";

const emptyForm = {
username: "",
email: "",
employeeId: "",
firstName: "",
lastName: "",
phone: "",
departmentId: "",
};

function extractList(data) {
if (Array.isArray(data)) return data;
if (!data || typeof data !== "object") return [];

for (const key of ["content", "data", "teachers", "departments", "items", "results"]) {
if (Array.isArray(data[key])) return data[key];
}

return [];
}

function getErrorMessage(error) {
return error?.message || "Something went wrong. Please try again.";
}

export default function Teachers() {
const [teachers, setTeachers] = useState([]);
const [departments, setDepartments] = useState([]);
const [loading, setLoading] = useState(true);
const [saving, setSaving] = useState(false);
const [error, setError] = useState("");
const [success, setSuccess] = useState("");
const [search, setSearch] = useState("");
const [departmentFilter, setDepartmentFilter] = useState("all");
const [statusFilter, setStatusFilter] = useState("all");
const [modalOpen, setModalOpen] = useState(false);
const [editingTeacher, setEditingTeacher] = useState(null);
const [form, setForm] = useState(emptyForm);

const loadData = useCallback(async () => {
setLoading(true);
setError("");

try {
  const [teacherResponse, departmentResponse] = await Promise.all([
    apiRequest("/admin/teachers"),
    apiRequest("/admin/departments/active"),
  ]);

  setTeachers(extractList(teacherResponse));
  setDepartments(extractList(departmentResponse));
} catch (err) {
  setError(getErrorMessage(err));
} finally {
  setLoading(false);
}

}, []);

useEffect(() => {
loadData();
}, [loadData]);

const activeCount = teachers.filter((teacher) => teacher.active !== false).length;
const inactiveCount = teachers.length - activeCount;

const filteredTeachers = useMemo(() => {
const term = search.trim().toLowerCase();

return teachers.filter((teacher) => {
  const fullName = `${teacher.firstName || ""} ${teacher.lastName || ""}`.trim();

  const matchesSearch =
    !term ||
    [
      fullName,
      teacher.username,
      teacher.email,
      teacher.employeeId,
      teacher.phone,
      teacher.departmentName,
    ].some((value) => String(value || "").toLowerCase().includes(term));

  const matchesDepartment =
    departmentFilter === "all" ||
    String(teacher.departmentId ?? "") === departmentFilter;

  const isActive = teacher.active !== false;
  const matchesStatus =
    statusFilter === "all" ||
    (statusFilter === "active" && isActive) ||
    (statusFilter === "inactive" && !isActive);

  return matchesSearch && matchesDepartment && matchesStatus;
});

}, [teachers, search, departmentFilter, statusFilter]);

function openAddModal() {
setEditingTeacher(null);
setForm(emptyForm);
setError("");
setSuccess("");
setModalOpen(true);
}

function openEditModal(teacher) {
setEditingTeacher(teacher);
setForm({
username: teacher.username || "",
email: teacher.email || "",
employeeId: teacher.employeeId || "",
firstName: teacher.firstName || "",
lastName: teacher.lastName || "",
phone: teacher.phone || "",
departmentId: teacher.departmentId != null ? String(teacher.departmentId) : "",
});
setError("");
setSuccess("");
setModalOpen(true);
}

function closeModal() {
if (saving) return;
setModalOpen(false);
setEditingTeacher(null);
setForm(emptyForm);
}

function handleChange(event) {
const { name, value } = event.target;
setForm((previous) => ({ ...previous, [name]: value }));
}

async function handleSubmit(event) {
event.preventDefault();
setError("");
setSuccess("");

if (!form.username.trim() || !form.email.trim()) {
  setError("Username and email are required.");
  return;
}

if (!editingTeacher && !form.employeeId.trim()) {
  setError("Employee ID is required.");
  return;
}

if (!form.departmentId) {
  setError("Please select a department.");
  return;
}

const payload = {
  username: form.username.trim(),
  email: form.email.trim(),
  employeeId: form.employeeId.trim(),
  firstName: form.firstName.trim(),
  lastName: form.lastName.trim(),
  phone: form.phone.trim(),
  departmentId: Number(form.departmentId),
};

setSaving(true);

try {
  if (editingTeacher) {
    await apiRequest(`/admin/teachers/${editingTeacher.id}`, {
      method: "PUT",
      body: payload,
    });
    setSuccess("Teacher updated successfully.");
  } else {
    await apiRequest("/admin/teachers", {
      method: "POST",
      body: payload,
    });
    setSuccess("Teacher created successfully.");
  }

  setModalOpen(false);
  setEditingTeacher(null);
  setForm(emptyForm);
  await loadData();
} catch (err) {
  setError(getErrorMessage(err));
} finally {
  setSaving(false);
}

}

async function handleDeactivate(teacher) {
const fullName =
  `${teacher.firstName || ""} ${teacher.lastName || ""}`.trim() ||
  teacher.username ||
  "Unnamed Teacher";

if (!window.confirm(`Deactivate ${fullName}?`)) return;

setError("");
setSuccess("");

try {
  await apiRequest(`/admin/teachers/${teacher.id}/deactivate`, {
    method: "PATCH",
  });

  setSuccess("Teacher deactivated successfully.");
  await loadData();
} catch (err) {
  setError(getErrorMessage(err));
}

}

return (
<div className="teachers-page">
<div className="teachers-header">
<div>
<h1>Teachers</h1>
<p>Manage teachers, their departments, and account status.</p>
</div>

    <button
      type="button"
      className="teachers-btn teachers-btn-primary"
      onClick={openAddModal}
    >
      + Add Teacher
    </button>
  </div>

  {error && !modalOpen && (
    <div className="teachers-alert teachers-alert-error" role="alert">
      {error}
    </div>
  )}

  {success && (
    <div className="teachers-alert teachers-alert-success" role="status">
      {success}
    </div>
  )}

  <div className="teachers-stats">
    <div className="teachers-stat-card">
      <span className="teachers-stat-label">Total Teachers</span>
      <strong>{teachers.length}</strong>
    </div>

    <div className="teachers-stat-card">
      <span className="teachers-stat-label">Active Teachers</span>
      <strong>{activeCount}</strong>
    </div>

    <div className="teachers-stat-card">
      <span className="teachers-stat-label">Inactive Teachers</span>
      <strong>{inactiveCount}</strong>
    </div>

    <div className="teachers-stat-card">
      <span className="teachers-stat-label">Departments</span>
      <strong>{departments.length}</strong>
    </div>
  </div>

  <section className="teachers-panel">
    <div className="teachers-panel-header">
      <div>
        <h2>Teacher records</h2>
<p>{filteredTeachers.length} records shown</p>
      </div>
    </div>

    <div className="teachers-toolbar">
      <input
        type="search"
        className="teachers-search"
        placeholder="Search by name, email, ID, or department..."
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        aria-label="Search teachers"
      />

      <select
        className="teachers-filter"
        value={departmentFilter}
        onChange={(event) => setDepartmentFilter(event.target.value)}
        aria-label="Filter by department"
      >
        <option value="all">All Departments</option>
        {departments.map((department) => (
          <option key={department.id} value={String(department.id)}>
            {department.name || department.departmentName || `Department ${department.id}`}
          </option>
        ))}
      </select>

      <select
        className="teachers-filter"
        value={statusFilter}
        onChange={(event) => setStatusFilter(event.target.value)}
        aria-label="Filter by status"
      >
        <option value="all">All Statuses</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
      </select>

      <button
        type="button"
        className="teachers-btn teachers-btn-secondary"
        onClick={loadData}
        disabled={loading}
      >
        Refresh
      </button>
    </div>

    <div className="teachers-table-wrapper">
      <table className="teachers-table">
        <thead>
          <tr>
            <th>Teacher</th>
            <th>Employee ID</th>
            <th>Email</th>
            <th>Department</th>
            <th>Phone</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {loading ? (
            <tr>
              <td colSpan="7" className="teachers-empty">
                Loading teachers...
              </td>
            </tr>
          ) : filteredTeachers.length === 0 ? (
            <tr>
              <td colSpan="7" className="teachers-empty">
                No teachers found.
              </td>
            </tr>
          ) : (
            filteredTeachers.map((teacher) => {
              const fullName =
                `${teacher.firstName || ""} ${teacher.lastName || ""}`.trim() ||
                teacher.username ||
                "Unnamed Teacher";

              const isActive = teacher.active !== false;

              return (
                <tr key={teacher.id}>
                  <td>
                    <div className="teachers-person">
                      <div className="teachers-avatar">
                        {fullName.charAt(0).toUpperCase()}
                      </div>
                      <div className="teachers-person-info">
                        <strong>{fullName}</strong>
                        <span>{teacher.username || "—"}</span>
                      </div>
                    </div>
                  </td>

                  <td>{teacher.employeeId || "—"}</td>
                  <td>{teacher.email || "—"}</td>
                  <td>{teacher.departmentName || "—"}</td>
                  <td>{teacher.phone || "—"}</td>

                  <td>
                    <span
                      className={`teachers-status ${
                        isActive ? "teachers-status-active" : "teachers-status-inactive"
                      }`}
                    >
                      {isActive ? "Active" : "Inactive"}
                    </span>
                  </td>

                  <td>
                    <div className="teachers-actions">
                      <button
                        type="button"
                        className="teachers-btn teachers-btn-small teachers-btn-secondary"
                        onClick={() => openEditModal(teacher)}
                      >
                        Edit
                      </button>

                      {isActive && (
                        <button
                          type="button"
                          className="teachers-btn teachers-btn-small teachers-btn-danger"
                          onClick={() => handleDeactivate(teacher)}
                        >
                          Deactivate
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>

    <div className="teachers-table-footer">
      Showing {filteredTeachers.length} of {teachers.length} teachers
    </div>
  </section>

  {modalOpen && (
    <div
      className="teachers-modal-backdrop"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) closeModal();
      }}
    >
      <section
        className="teachers-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="teachers-modal-title"
      >
        <div className="teachers-modal-header">
          <div>
            <h2 id="teachers-modal-title">
              {editingTeacher ? "Edit Teacher" : "Add Teacher"}
            </h2>
            <p>
              {editingTeacher
                ? "Update the teacher's information."
                : "Enter the details to create a teacher."}
            </p>
          </div>

          <button
            type="button"
            className="teachers-modal-close"
            onClick={closeModal}
            disabled={saving}
            aria-label="Close dialog"
          >
            ×
          </button>
        </div>

        {error && (
          <div className="teachers-alert teachers-alert-error" role="alert">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="teachers-form-grid">
            <label className="teachers-form-field">
              First Name
              <input
                name="firstName"
                value={form.firstName}
                onChange={handleChange}
                placeholder="Enter first name"
              />
            </label>

            <label className="teachers-form-field">
              Last Name
              <input
                name="lastName"
                value={form.lastName}
                onChange={handleChange}
                placeholder="Enter last name"
              />
            </label>

            <label className="teachers-form-field">
              Username *
              <input
                name="username"
                value={form.username}
                onChange={handleChange}
                required
                autoComplete="username"
                placeholder="Enter username"
              />
            </label>

            <label className="teachers-form-field">
              Email *
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                required
                autoComplete="email"
                placeholder="Enter email address"
              />
            </label>

            <label className="teachers-form-field">
              Employee ID {!editingTeacher && "*"}
              <input
                name="employeeId"
                value={form.employeeId}
                onChange={handleChange}
                required={!editingTeacher}
                placeholder="Enter employee ID"
              />
            </label>

            <label className="teachers-form-field">
              Phone
              <input
                type="tel"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="Enter phone number"
              />
            </label>

            <label className="teachers-form-field teachers-form-field-full">
              Department *
              <select
                name="departmentId"
                value={form.departmentId}
                onChange={handleChange}
                required
              >
                <option value="">Select Department</option>
                {departments.map((department) => (
                  <option key={department.id} value={String(department.id)}>
                    {department.name ||
                      department.departmentName ||
                      `Department ${department.id}`}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {!editingTeacher && (
            <p className="teachers-form-note">
              Account creation and initial credentials are handled by the backend.
            </p>
          )}

          <div className="teachers-modal-footer">
            <button
              type="button"
              className="teachers-btn teachers-btn-secondary"
              onClick={closeModal}
              disabled={saving}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="teachers-btn teachers-btn-primary"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : editingTeacher
                  ? "Save Changes"
                  : "Create Teacher"}
            </button>
          </div>
        </form>
      </section>
    </div>
  )}
</div>

);
}