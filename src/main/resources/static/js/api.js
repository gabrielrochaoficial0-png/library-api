const API_BASE = "/api";

async function apiRequest(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (res.status === 204) return null;

  const isJson = res.headers.get("content-type")?.includes("application/json");
  const body = isJson ? await res.json() : null;

  if (!res.ok) {
    const error = new Error(body?.message || "Erro na requisição");
    error.details = body?.details || [];
    error.status = res.status;
    throw error;
  }

  return body;
}

const BooksAPI = {
  list: () => apiRequest("/books"),
  get: (id) => apiRequest(`/books/${id}`),
  create: (dto) => apiRequest("/books", { method: "POST", body: JSON.stringify(dto) }),
  update: (id, dto) => apiRequest(`/books/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
  remove: (id) => apiRequest(`/books/${id}`, { method: "DELETE" }),
};

const MembersAPI = {
  list: () => apiRequest("/members"),
  get: (id) => apiRequest(`/members/${id}`),
  create: (dto) => apiRequest("/members", { method: "POST", body: JSON.stringify(dto) }),
  update: (id, dto) => apiRequest(`/members/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
  remove: (id) => apiRequest(`/members/${id}`, { method: "DELETE" }),
};

const LoansAPI = {
  list: () => apiRequest("/loans"),
  get: (id) => apiRequest(`/loans/${id}`),
  borrow: (dto) => apiRequest("/loans", { method: "POST", body: JSON.stringify(dto) }),
  returnBook: (id) => apiRequest(`/loans/${id}/return`, { method: "PATCH" }),
};
