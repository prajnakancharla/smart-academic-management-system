
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export function getToken() {
  return localStorage.getItem("accessToken");
}

export function saveSession(loginResponse) {
  if (loginResponse?.accessToken) {
    localStorage.setItem("accessToken", loginResponse.accessToken);
  }

  if (loginResponse?.username) {
    localStorage.setItem("username", loginResponse.username);
  }

  if (loginResponse?.roles) {
    localStorage.setItem("roles", JSON.stringify(loginResponse.roles));
  }

  if (loginResponse?.mustChangePassword !== undefined) {
    localStorage.setItem(
      "mustChangePassword",
      String(loginResponse.mustChangePassword)
    );
  }
}

export function clearSession() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("username");
  localStorage.removeItem("roles");
  localStorage.removeItem("mustChangePassword");
}

export function getRoles() {
  try {
    return JSON.parse(localStorage.getItem("roles") || "[]");
  } catch {
    return [];
  }
}

export async function apiRequest(path, options = {}) {
  const token = getToken();
  const requestBody =
    options.body &&
    typeof options.body === "object" &&
    !(options.body instanceof FormData)
      ? JSON.stringify(options.body)
      : options.body;

  const headers = {
    ...(options.body && !(options.body instanceof FormData)
      ? { "Content-Type": "application/json" }
      : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    body: requestBody,
    headers,
  });

  const text = await response.text();
  let result = null;

  if (text) {
    try {
      result = JSON.parse(text);
    } catch {
      result = text;
    }
  }

  if (!response.ok) {
    if (response.status === 401) {
      clearSession();
      window.dispatchEvent(new Event("session-expired"));
    }

    const message =
      typeof result === "object" && result !== null
        ? result.message || result.error || "Request failed"
        : result || "Request failed";

    throw new Error(message);
  }

  // Unwrap Spring Boot ApiResponse objects when present.
  if (
    result &&
    typeof result === "object" &&
    Object.prototype.hasOwnProperty.call(result, "success") &&
    Object.prototype.hasOwnProperty.call(result, "data")
  ) {
    if (result.success === false) {
      throw new Error(result.message || "Request failed");
    }

    return result.data;
  }

  return result;
}

export async function login(username, password) {
  const response = await apiRequest("/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });

  saveSession(response);
  return response;
}

export async function changePassword(currentPassword, newPassword) {
  return apiRequest("/auth/change-password", {
    method: "POST",
    body: JSON.stringify({ currentPassword, newPassword }),
  });
}
