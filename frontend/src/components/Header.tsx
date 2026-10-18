import { Link } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import { Logo } from './Logo'

export function Header() {
  const { isAuthenticated, logout } = useAuth()

  return (
    <header className="border-b border-slate-200 bg-white px-4 py-3">
      <div className="mx-auto flex max-w-2xl items-center justify-between">
        <Link to={isAuthenticated ? '/' : '/login'} className="transition-opacity hover:opacity-80">
          <Logo />
        </Link>
        {isAuthenticated && (
          <div className="flex items-center gap-4">
            <Link
              to="/"
              className="text-sm font-medium text-slate-600 transition-colors hover:text-blue-600"
            >
              New session
            </Link>
            <Link
              to="/sessions"
              className="text-sm font-medium text-slate-600 transition-colors hover:text-blue-600"
            >
              My past preps
            </Link>
            <button
              onClick={logout}
              className="text-sm text-slate-400 transition-colors hover:text-slate-900"
            >
              Log out
            </button>
          </div>
        )}
      </div>
    </header>
  )
}
