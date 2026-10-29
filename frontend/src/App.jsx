
import { useEffect } from "react";
import {
  Navigate,
  Route,
  Routes,
  useNavigate,
} from "react-router-dom";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Students from "./pages/Students";
import Layout from "./components/Layout";
import NotFound from "./pages/NotFound";

import { clearSession, getToken } from "./api/client";

import Teachers from "./pages/Teachers";
import ModulePage from "./pages/ModulePage";
import ChangePassword from "./pages/ChangePassword";
import Assignments from "./pages/Assignments";
import Attendance from "./pages/Attendance";
import Marks from "./pages/Marks";
import StudentAttendance from "./pages/StudentAttendance";
function ProtectedRoute({ children }) {
  return getToken() ? children : <Navigate to="/login" replace />;
}

const pages = [
  {
    path: "teachers",
    title: "Teachers",
    description: "Manage faculty records and teacher information.",
  },
  {
    path: "departments",
    title: "Departments",
    description: "Manage academic departments.",
  },
  {
    path: "programs",
    title: "Programs",
    description: "Manage academic programs.",
  },
  {
    path: "academic-years",
    title: "Academic Years",
    description: "Manage academic years and their active status.",
  },
  {
    path: "subjects",
    title: "Subjects",
    description: "Manage subjects offered by academic programs.",
  },
  {
    path: "my-subjects",
    title: "My Subjects",
    description: "View your assigned teaching subjects.",
  },
  {
    path: "attendance",
    title: "Attendance",
    description: "Manage attendance sessions and student attendance.",
  },
  {
    path: "marks",
    title: "Enter Marks",
    description: "Manage assessments and student marks.",
  },
  {
    path: "results",
    title: "My Results",
    description: "View academic results and assessment performance.",
  },
];

export default function App() {
  const navigate = useNavigate();

  useEffect(() => {
    function handleSessionExpired() {
      clearSession();
      navigate("/login", { replace: true });
    }

    window.addEventListener("session-expired", handleSessionExpired);

    return () => {
      window.removeEventListener("session-expired", handleSessionExpired);
    };
  }, [navigate]);

  return (
    <Routes>
      <Route
        path="/"
        element={
          getToken() ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <Navigate to="/login" replace />
          )
        }
      />

      <Route path="/login" element={<Login />} />
      <Route path="/change-password" element={<ProtectedRoute><ChangePassword /></ProtectedRoute>} />
      <Route path="/assignments" element={<ProtectedRoute><Layout><Assignments /></Layout></ProtectedRoute>} />
      <Route path="/student-attendance" element={<ProtectedRoute><Layout><StudentAttendance /></Layout></ProtectedRoute>} />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Layout>
              <Dashboard />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/students"
        element={
          <ProtectedRoute>
            <Layout>
              <Students />
            </Layout>
          </ProtectedRoute>
        }
      />

      {pages.map((page) => (
  <Route
    key={page.path}
    path={`/${page.path}`}
    element={
      <ProtectedRoute>
        <Layout>
          {page.path === "teachers" ? (
            <Teachers />
          ) : page.path === "attendance" ? (
            <Attendance />
          ) : page.path === "marks" ? (
            <Marks />
          ) : <ModulePage moduleKey={page.path} />}
        </Layout>
      </ProtectedRoute>
    }
  />
))}

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
