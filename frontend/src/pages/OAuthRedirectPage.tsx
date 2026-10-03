import { useEffect, useState } from 'react'
import { Navigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

export function OAuthRedirectPage() {
  const [searchParams] = useSearchParams()
  const { login } = useAuth()
  const token = searchParams.get('token')
  const [ready, setReady] = useState(false)

  useEffect(() => {
    if (token) {
      login(token)
      setReady(true)
    }
  }, [token, login])

  if (!token) {
    return <Navigate to="/login" replace />
  }

  if (!ready) {
    return null
  }

  return <Navigate to="/" replace />
}
