import { Route, Routes } from 'react-router-dom'
import { RequireAuth } from './auth/RequireAuth'
import { useAuth } from './auth/useAuth'
import { NavBar } from './components/NavBar'
import { LoginPage } from './pages/LoginPage'
import { OAuthRedirectPage } from './pages/OAuthRedirectPage'
import { NewSessionPage } from './pages/NewSessionPage'
import { SessionsListPage } from './pages/SessionsListPage'
import { SessionDetailPage } from './pages/SessionDetailPage'

function App() {
  const { isAuthenticated } = useAuth()

  return (
    <>
      {isAuthenticated && <NavBar />}
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/oauth2/redirect" element={<OAuthRedirectPage />} />
        <Route
          path="/"
          element={
            <RequireAuth>
              <NewSessionPage />
            </RequireAuth>
          }
        />
        <Route
          path="/sessions"
          element={
            <RequireAuth>
              <SessionsListPage />
            </RequireAuth>
          }
        />
        <Route
          path="/sessions/:id"
          element={
            <RequireAuth>
              <SessionDetailPage />
            </RequireAuth>
          }
        />
      </Routes>
    </>
  )
}

export default App
