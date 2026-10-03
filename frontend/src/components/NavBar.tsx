import { Link } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

export function NavBar() {
  const { logout } = useAuth()

  return (
    <nav className="border-b border-slate-200 px-4 py-3">
      <div className="mx-auto flex max-w-2xl items-center justify-between">
        <div className="flex gap-4">
          <Link to="/" className="text-sm font-medium text-slate-900">
            New session
          </Link>
          <Link to="/sessions" className="text-sm font-medium text-slate-900">
            My past preps
          </Link>
        </div>
        <button onClick={logout} className="text-sm text-slate-500 hover:text-slate-900">
          Log out
        </button>
      </div>
    </nav>
  )
}
