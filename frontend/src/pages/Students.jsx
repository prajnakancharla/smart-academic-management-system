
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Users,
  Search,
  RefreshCw,
  Plus,
  Pencil,
  Power,
  X,
  AlertCircle,
  Mail,
  Phone,
  GraduationCap,
  CalendarDays,
  Hash,
  CheckCircle2,
} from "lucide-react";
import { apiRequest } from "../api/client";
import "./Students.css";

const EMPTY_FORM = {
  username: "",
  email: "",
  rollNumber: "",
  firstName: "",
  lastName: "",
  phone: "",
  programId: "",
  admissionDate: "",
  currentYear: "1",
};

function getErrorMessage(error) {
  return error?.message || "Something went wrong. Please try again.";
}

function getLocalDateString() {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

/**
 * Supports common API response formats:
 * - [ ... ]
 * - { data: [ ... ] }
 * - { content: [ ... ] }
 * - { items: [ ... ] }
 * - { students: [ ... ] }
 *
 * apiRequest normally unwraps ApiResponse.data already.
 */
function extractList(response, resourceName) {
  if (Array.isArray(response)) return response;

  if (!response || typeof response !== "object") {
    throw new Error(
      `The ${resourceName} API returned an unexpected response.`
    );
  }

  const possibleLists = [
    response.data,
    response.content,
    response.items,
    response[resourceName],
    response.records,
  ];

  const list = possibleLists.find(Array.isArray);

  if (list) return list;

  throw new Error(
    `Could not find the ${resourceName} list in the API response. Check the backend response format.`
  );
}

function toFormData(student) {
  return {
    username: student.username ?? "",
    email: student.email ?? "",
    rollNumber: student.rollNumber ?? "",
    firstName: student.firstName ?? "",
    lastName: student.lastName ?? "",
    phone: student.phone ?? "",
    programId: String(student.programId ?? ""),
    admissionDate: student.admissionDate ?? "",
    currentYear: String(student.currentYear ?? 1),
  };
}

function validateForm(form, selectedProgram) {
  const errors = {};

  if (!form.username.trim()) {
    errors.username = "Username is required.";
  } else if (form.username.trim().length > 100) {
    errors.username = "Username cannot exceed 100 characters.";
  }

  if (!form.email.trim()) {
    errors.email = "Email is required.";
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
    errors.email = "Enter a valid email address.";
  } else if (form.email.trim().length > 150) {
    errors.email = "Email cannot exceed 150 characters.";
  }

  if (!form.rollNumber.trim()) {
    errors.rollNumber = "Roll number is required.";
  } else if (form.rollNumber.trim().length > 30) {
    errors.rollNumber = "Roll number cannot exceed 30 characters.";
  }

  if (!form.firstName.trim()) errors.firstName = "First name is required.";
  if (!form.lastName.trim()) errors.lastName = "Last name is required.";

  if (
    form.phone.trim() &&
    !/^[0-9+() -]{7,20}$/.test(form.phone.trim())
  ) {
    errors.phone = "Enter a valid phone number.";
  }

  if (!form.programId) {
    errors.programId = "Select an academic program.";
  } else if (
    !Number.isInteger(Number(form.programId)) ||
    Number(form.programId) < 1
  ) {
    errors.programId = "Select a valid academic program.";
  } else if (!selectedProgram || selectedProgram.active === false) {
    errors.programId = "Select an available academic program.";
  }

  if (!form.admissionDate) {
    errors.admissionDate = "Admission date is required.";
  } else if (form.admissionDate > getLocalDateString()) {
    errors.admissionDate = "Admission date cannot be in the future.";
  }

  if (
    !form.currentYear ||
    !Number.isInteger(Number(form.currentYear)) ||
    Number(form.currentYear) < 1
  ) {
    errors.currentYear = "Current year must be at least 1.";
  } else if (
    selectedProgram &&
    Number(form.currentYear) > Number(selectedProgram.durationYears)
  ) {
    errors.currentYear = `This program lasts ${selectedProgram.durationYears} year(s).`;
  }

  return errors;
}

function Field({
  label,
  name,
  value,
  onChange,
  error,
  icon: Icon,
  required = true,
  ...props
}) {
  return (
    <div className="student-field">
      <label htmlFor={name}>
        {label} {required && <span>*</span>}
      </label>

      <div className={`student-input-wrap ${error ? "has-error" : ""}`}>
        {Icon && <Icon size={17} aria-hidden="true" />}
        <input
          id={name}
          name={name}
          value={value}
          onChange={onChange}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${name}-error` : undefined}
          {...props}
        />
      </div>

      {error && (
        <small id={`${name}-error`} className="field-error">
          {error}
        </small>
      )}
    </div>
  );
}

export default function Students() {
  const [students, setStudents] = useState([]);
  const [programs, setPrograms] = useState([]);

  const [loading, setLoading] = useState(true);
  const [programsLoading, setProgramsLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [deactivatingId, setDeactivatingId] = useState(null);

  const [error, setError] = useState("");
  const [programsError, setProgramsError] = useState("");
  const [success, setSuccess] = useState("");

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formErrors, setFormErrors] = useState({});

  const loadStudents = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await apiRequest("/admin/students");
      const list = extractList(response, "students");
      setStudents(list);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  const loadPrograms = useCallback(async () => {
    setProgramsLoading(true);
    setProgramsError("");

    try {
      const response = await apiRequest("/admin/programs/active");
      const list = extractList(response, "programs");
      setPrograms(list);
    } catch (err) {
      setProgramsError(getErrorMessage(err));
    } finally {
      setProgramsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStudents();
    loadPrograms();
  }, [loadStudents, loadPrograms]);

  const selectedProgram = useMemo(
    () =>
      programs.find(
        (program) => String(program.id) === String(form.programId)
      ) || null,
    [programs, form.programId]
  );

  const filteredStudents = useMemo(() => {
    const query = search.trim().toLowerCase();

    return students.filter((student) => {
      const matchesStatus =
        statusFilter === "all" ||
        (statusFilter === "active" && student.active) ||
        (statusFilter === "inactive" && !student.active);

      const searchableText = [
        student.username,
        student.email,
        student.rollNumber,
        student.firstName,
        student.lastName,
        student.phone,
        student.programName,
        student.departmentName,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      return matchesStatus && (!query || searchableText.includes(query));
    });
  }, [students, search, statusFilter]);

  const activeCount = students.filter((student) => student.active).length;
  const inactiveCount = students.length - activeCount;

  function handleRefresh() {
    setSearch("");
    setStatusFilter("all");
    setSuccess("");
    loadStudents();
    loadPrograms();
  }

  function openAddModal() {
    setEditingStudent(null);
    setForm(EMPTY_FORM);
    setFormErrors({});
    setError("");
    setSuccess("");
    setModalOpen(true);

    if (programs.length === 0) loadPrograms();
  }

  function openEditModal(student) {
    if (!student.active) {
      setError("Inactive students cannot be edited from this page.");
      return;
    }

    setEditingStudent(student);
    setForm(toFormData(student));
    setFormErrors({});
    setError("");
    setSuccess("");
    setModalOpen(true);

    if (programs.length === 0) loadPrograms();
  }

  function closeModal() {
    if (saving) return;

    setModalOpen(false);
    setEditingStudent(null);
    setForm(EMPTY_FORM);
    setFormErrors({});
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((previous) => ({ ...previous, [name]: value }));
    setFormErrors((previous) => ({ ...previous, [name]: "" }));
  }

  function handleProgramChange(event) {
    const programId = event.target.value;
    const nextProgram = programs.find(
      (program) => String(program.id) === programId
    );

    setForm((previous) => ({
      ...previous,
      programId,
      currentYear:
        nextProgram &&
        Number(previous.currentYear) > Number(nextProgram.durationYears)
          ? "1"
          : previous.currentYear,
    }));

    setFormErrors((previous) => ({
      ...previous,
      programId: "",
      currentYear: "",
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const validationErrors = validateForm(form, selectedProgram);
    setFormErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) return;

    const payload = {
      username: form.username.trim(),
      email: form.email.trim(),
      rollNumber: form.rollNumber.trim(),
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      phone: form.phone.trim(),
      programId: Number(form.programId),
      admissionDate: form.admissionDate,
      currentYear: Number(form.currentYear),
    };

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      if (editingStudent) {
        await apiRequest(`/admin/students/${editingStudent.id}`, {
          method: "PUT",
          body: JSON.stringify(payload),
        });

        setSuccess("Student updated successfully.");
      } else {
        await apiRequest("/admin/students", {
          method: "POST",
          body: JSON.stringify(payload),
        });

        setSuccess("Student added successfully. Initial password: s123");
      }

      setModalOpen(false);
      setEditingStudent(null);
      setForm(EMPTY_FORM);
      setFormErrors({});

      await loadStudents();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  async function handleDeactivate(student) {
    const confirmed = window.confirm(
      `Deactivate ${student.firstName} ${student.lastName} (${student.rollNumber})? They will no longer be able to use their account.`
    );

    if (!confirmed) return;

    setDeactivatingId(student.id);
    setError("");
    setSuccess("");

    try {
      await apiRequest(`/admin/students/${student.id}/deactivate`, {
        method: "PATCH",
      });

      setSuccess(
        `${student.firstName} ${student.lastName} was deactivated.`
      );

      await loadStudents();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setDeactivatingId(null);
    }
  }

  return (
    <main className="students-page">
      <header className="students-header">
        <div>
          <div className="students-eyebrow">
            <Users size={15} /> STUDENT MANAGEMENT
          </div>
          <h1>Students</h1>
          <p>Manage student profiles, academic details, and account status.</p>
        </div>

        <div className="students-header-actions">
          <button
            className="students-btn students-btn-secondary"
            onClick={handleRefresh}
            disabled={loading || programsLoading}
            type="button"
          >
            <RefreshCw
              size={17}
              className={
                loading || programsLoading ? "students-spin" : ""
              }
            />
            Refresh
          </button>

          <button
            className="students-btn students-btn-primary"
            onClick={openAddModal}
            type="button"
          >
            <Plus size={18} /> Add Student
          </button>
        </div>
      </header>

      <section className="students-stats">
        <article className="students-stat-card">
          <div className="students-stat-icon stat-icon-total">
            <Users size={20} />
          </div>
          <div>
            <span>Total students</span>
            <strong>{students.length}</strong>
          </div>
        </article>

        <article className="students-stat-card">
          <div className="students-stat-icon stat-icon-active">
            <CheckCircle2 size={20} />
          </div>
          <div>
            <span>Active</span>
            <strong>{activeCount}</strong>
          </div>
        </article>

        <article className="students-stat-card">
          <div className="students-stat-icon stat-icon-inactive">
            <Power size={20} />
          </div>
          <div>
            <span>Inactive</span>
            <strong>{inactiveCount}</strong>
          </div>
        </article>
      </section>

      {error && (
        <div className="students-alert students-alert-error" role="alert">
          <AlertCircle size={18} />
          <span>{error}</span>
          <button
            type="button"
            onClick={() => setError("")}
            aria-label="Dismiss error"
          >
            <X size={17} />
          </button>
        </div>
      )}

      {success && (
        <div className="students-alert students-alert-success" role="status">
          <CheckCircle2 size={18} />
          <span>{success}</span>
          <button
            type="button"
            onClick={() => setSuccess("")}
            aria-label="Dismiss message"
          >
            <X size={17} />
          </button>
        </div>
      )}

      <section className="students-panel">
        <div className="students-toolbar">
          <div className="students-search">
            <Search size={18} />
            <input
              type="search"
              placeholder="Search name, roll number, email, or program..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              aria-label="Search students"
            />

            {search && (
              <button
                type="button"
                onClick={() => setSearch("")}
                aria-label="Clear search"
              >
                <X size={16} />
              </button>
            )}
          </div>

          <div className="students-filter">
            <label htmlFor="student-status-filter">Status</label>
            <select
              id="student-status-filter"
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
            >
              <option value="all">All students</option>
              <option value="active">Active only</option>
              <option value="inactive">Inactive only</option>
            </select>
          </div>
        </div>

        <div className="students-table-heading">
          <div>
            <h2>Student records</h2>
            <p>
              {filteredStudents.length} record
              {filteredStudents.length === 1 ? "" : "s"} shown
            </p>
          </div>
        </div>

        <div className="students-table-scroll">
          <table className="students-table">
            <thead>
              <tr>
                <th>Student</th>
                <th>Roll number</th>
                <th>Program / Department</th>
                <th>Academic year</th>
                <th>Status</th>
                <th className="students-actions-heading">Actions</th>
              </tr>
            </thead>

            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="6" className="students-state-cell">
                    <RefreshCw className="students-spin" size={22} />
                    <span>Loading students...</span>
                  </td>
                </tr>
              ) : filteredStudents.length === 0 ? (
                <tr>
                  <td colSpan="6" className="students-state-cell">
                    <Users size={30} />
                    <strong>
                      {error ? "Unable to load student records" : "No students found"}
                    </strong>
                    <span>
                      {error
                        ? "Check the error message above and try refreshing."
                        : students.length === 0
                          ? "No student records were returned by the server."
                          : "Try changing your search or status filter."}
                    </span>

                    {!error && students.length === 0 && (
                      <button
                        className="students-btn students-btn-primary"
                        onClick={openAddModal}
                        type="button"
                      >
                        <Plus size={16} /> Add Student
                      </button>
                    )}
                  </td>
                </tr>
              ) : (
                filteredStudents.map((student) => (
                  <tr key={student.id}>
                    <td>
                      <div className="student-person">
                        <div className="student-avatar">
                          {(
                            student.firstName?.[0] ||
                            student.username?.[0] ||
                            "S"
                          ).toUpperCase()}
                        </div>

                        <div className="student-person-details">
                          <strong>
                            {student.firstName} {student.lastName}
                          </strong>
                          <span>@{student.username}</span>
                          <span className="student-mobile-email">
                            <Mail size={13} /> {student.email}
                          </span>
                        </div>
                      </div>
                    </td>

                    <td>
                      <span className="student-roll">
                        {student.rollNumber || "—"}
                      </span>
                    </td>

                    <td>
                      <div className="student-program">
                        <strong>
                          {student.programName ||
                            `Program #${student.programId ?? "—"}`}
                        </strong>
                        <span>
                          {student.departmentName ||
                            "Department not available"}
                        </span>
                      </div>
                    </td>

                    <td>
                      <div className="student-year">
                        <span>Year {student.currentYear}</span>
                        <small>
                          {student.admissionDate || "—"}
                          {student.expectedCompletionYear
                            ? ` · Ends ${student.expectedCompletionYear}`
                            : ""}
                        </small>
                      </div>
                    </td>

                    <td>
                      <span
                        className={`student-status ${
                          student.active ? "is-active" : "is-inactive"
                        }`}
                      >
                        <span />
                        {student.active ? "Active" : "Inactive"}
                      </span>
                    </td>

                    <td>
                      <div className="student-row-actions">
                        <button
                          type="button"
                          className="student-action-button"
                          onClick={() => openEditModal(student)}
                          disabled={
                            !student.active || deactivatingId === student.id
                          }
                          title={
                            student.active
                              ? "Edit student"
                              : "Inactive students cannot be edited"
                          }
                          aria-label={`Edit ${student.firstName} ${student.lastName}`}
                        >
                          <Pencil size={16} />
                        </button>

                        <button
                          type="button"
                          className="student-action-button student-deactivate-button"
                          onClick={() => handleDeactivate(student)}
                          disabled={
                            !student.active || deactivatingId === student.id
                          }
                          title={
                            student.active
                              ? "Deactivate student"
                              : "Already inactive"
                          }
                          aria-label={`Deactivate ${student.firstName} ${student.lastName}`}
                        >
                          {deactivatingId === student.id ? (
                            <RefreshCw
                              className="students-spin"
                              size={16}
                            />
                          ) : (
                            <Power size={16} />
                          )}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="students-table-footer">
          <span>
            Showing {filteredStudents.length} of {students.length} students
          </span>
          <span>Student Management</span>
        </div>
      </section>

      {modalOpen && (
        <div
          className="students-modal-backdrop"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) closeModal();
          }}
        >
          <section
            className="students-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="student-modal-title"
          >
            <header className="students-modal-header">
              <div>
                <div className="students-eyebrow">
                  {editingStudent ? <Pencil size={14} /> : <Plus size={14} />}
                  {editingStudent ? "EDIT RECORD" : "NEW RECORD"}
                </div>
                <h2 id="student-modal-title">
                  {editingStudent ? "Edit student" : "Add student"}
                </h2>
                <p>
                  {editingStudent
                    ? "Update profile and academic details."
                    : "Enter the student's account and academic details."}
                </p>
              </div>

              <button
                type="button"
                className="students-modal-close"
                onClick={closeModal}
                aria-label="Close modal"
                disabled={saving}
              >
                <X size={20} />
              </button>
            </header>

            {!editingStudent && (
              <div className="students-password-note">
                <AlertCircle size={16} />
                The initial password for new student accounts is{" "}
                <strong>s123</strong>.
              </div>
            )}

            <form onSubmit={handleSubmit} noValidate>
              <div className="students-form-section">
                <h3>Personal & account information</h3>

                <div className="students-form-grid">
                  <Field
                    label="First name"
                    name="firstName"
                    value={form.firstName}
                    onChange={handleChange}
                    error={formErrors.firstName}
                    autoComplete="given-name"
                  />

                  <Field
                    label="Last name"
                    name="lastName"
                    value={form.lastName}
                    onChange={handleChange}
                    error={formErrors.lastName}
                    autoComplete="family-name"
                  />

                  <Field
                    label="Username"
                    name="username"
                    value={form.username}
                    onChange={handleChange}
                    error={formErrors.username}
                    autoComplete="username"
                  />

                  <Field
                    label="Email address"
                    name="email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    error={formErrors.email}
                    icon={Mail}
                    autoComplete="email"
                  />

                  <Field
                    label="Roll number"
                    name="rollNumber"
                    value={form.rollNumber}
                    onChange={handleChange}
                    error={formErrors.rollNumber}
                    icon={Hash}
                  />

                  <Field
                    label="Phone number"
                    name="phone"
                    type="tel"
                    value={form.phone}
                    onChange={handleChange}
                    error={formErrors.phone}
                    icon={Phone}
                    placeholder="Optional"
                    required={false}
                  />
                </div>
              </div>

              <div className="students-form-section">
                <h3>
                  <GraduationCap size={17} /> Academic information
                </h3>

                {programsError && (
                  <div
                    className="students-alert students-alert-error"
                    role="alert"
                  >
                    <AlertCircle size={17} />
                    <span>{programsError}</span>
                    <button
                      type="button"
                      onClick={loadPrograms}
                      disabled={programsLoading}
                    >
                      Retry
                    </button>
                  </div>
                )}

                <div className="students-form-grid">
                  <div className="student-field">
                    <label htmlFor="programId">
                      Academic program <span>*</span>
                    </label>

                    <div
                      className={`student-input-wrap ${
                        formErrors.programId ? "has-error" : ""
                      }`}
                    >
                      <GraduationCap size={17} aria-hidden="true" />

                      <select
                        id="programId"
                        name="programId"
                        value={form.programId}
                        onChange={handleProgramChange}
                        disabled={
                          programsLoading ||
                          (programs.length === 0 && !programsError)
                        }
                        aria-invalid={Boolean(formErrors.programId)}
                        aria-describedby={
                          formErrors.programId ? "programId-error" : undefined
                        }
                        required
                      >
                        <option value="">
                          {programsLoading
                            ? "Loading programs..."
                            : programsError
                              ? "Could not load programs"
                              : programs.length === 0
                                ? "No active programs available"
                                : "Select an academic program"}
                        </option>

                        {programs.map((program) => (
                          <option
                            key={program.id}
                            value={String(program.id)}
                          >
                            {program.name} ({program.code})
                          </option>
                        ))}
                      </select>
                    </div>

                    {formErrors.programId && (
                      <small id="programId-error" className="field-error">
                        {formErrors.programId}
                      </small>
                    )}

                    {selectedProgram && (
                      <small className="students-form-help">
                        {selectedProgram.departmentName
                          ? `${selectedProgram.departmentName} · `
                          : ""}
                        {selectedProgram.durationYears} year(s) ·{" "}
                        {selectedProgram.totalSemesters ??
                          selectedProgram.durationYears *
                            selectedProgram.semestersPerYear}{" "}
                        semester(s)
                      </small>
                    )}
                  </div>

                  <Field
                    label="Admission date"
                    name="admissionDate"
                    type="date"
                    max={getLocalDateString()}
                    value={form.admissionDate}
                    onChange={handleChange}
                    error={formErrors.admissionDate}
                    icon={CalendarDays}
                  />

                  <Field
                    label="Current year"
                    name="currentYear"
                    type="number"
                    min="1"
                    max={selectedProgram?.durationYears || undefined}
                    value={form.currentYear}
                    onChange={handleChange}
                    error={formErrors.currentYear}
                  />
                </div>

                <p className="students-form-help">
                  Only active academic programs are listed. The selected
                  program's ID is submitted to the backend automatically.
                </p>
              </div>

              <footer className="students-modal-footer">
                <button
                  type="button"
                  className="students-btn students-btn-secondary"
                  onClick={closeModal}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="students-btn students-btn-primary"
                  disabled={
                    saving ||
                    programsLoading ||
                    programs.length === 0 ||
                    Boolean(programsError)
                  }
                >
                  {saving ? (
                    <>
                      <RefreshCw size={16} className="students-spin" />
                      Saving...
                    </>
                  ) : (
                    <>
                      {editingStudent ? (
                        <Pencil size={16} />
                      ) : (
                        <Plus size={17} />
                      )}
                      {editingStudent ? "Save changes" : "Create student"}
                    </>
                  )}
                </button>
              </footer>
            </form>
          </section>
        </div>
      )}
    </main>
  );
}
