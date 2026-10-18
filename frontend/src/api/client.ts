interface ApiErrorBody {
  error?: string
}

const TOKEN_KEY = 'authToken'

export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = localStorage.getItem(TOKEN_KEY)
  const isFormData = init?.body instanceof FormData

  const res = await fetch(`/api${path}`, {
    headers: {
      // FormData bodies must NOT have Content-Type set manually - the browser
      // needs to compute the multipart boundary itself when serializing it.
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...init,
  })

  if (res.status === 401) {
    localStorage.removeItem(TOKEN_KEY)
    window.location.href = '/login'
    throw new Error('Session expired, please log in again')
  }

  if (!res.ok) {
    const body: ApiErrorBody = await res.json().catch(() => ({}))
    throw new Error(body.error ?? `Request failed with status ${res.status}`)
  }

  return res.json() as Promise<T>
}
