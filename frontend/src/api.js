const baseFromEnv = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
const API_BASE_URL = baseFromEnv.replace(/\/$/, '');

class ApiError extends Error {
  constructor(message, status, payload) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.payload = payload;
  }
}

async function request(path, options = {}) {
  const url = `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`;
  const response = await fetch(url, {
    headers: {
      Accept: 'application/json',
      ...(options.body ? { 'Content-Type': 'application/json' } : {})
    },
    ...options
  });

  const isJson = response.headers.get('content-type')?.includes('application/json');
  const payload = isJson ? await response.json() : null;

  if (!response.ok) {
    const message = payload?.message || payload?.error || response.statusText || 'Request failed';
    throw new ApiError(message, response.status, payload);
  }

  if (response.status === 204) {
    return null;
  }

  return payload;
}

export const api = {
  get: (path) => request(path),
  post: (path, data) => request(path, { method: 'POST', body: JSON.stringify(data) }),
  put: (path, data) => request(path, { method: 'PUT', body: JSON.stringify(data) }),
  patch: (path, data) => request(path, { method: 'PATCH', body: JSON.stringify(data) }),
  del: (path) => request(path, { method: 'DELETE' })
};

export { ApiError, API_BASE_URL };
