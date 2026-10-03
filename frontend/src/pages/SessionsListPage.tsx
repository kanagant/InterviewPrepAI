import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { listSessions } from '../api/sessions'

export function SessionsListPage() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['sessions'],
    queryFn: listSessions,
  })

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="text-2xl font-semibold text-slate-900">My past preps</h1>

      {isLoading && <p className="mt-4 text-slate-500">Loading...</p>}
      {isError && <p className="mt-4 text-sm text-red-600">Failed to load sessions.</p>}

      {data && data.length === 0 && (
        <p className="mt-4 text-slate-500">
          No sessions yet. <Link to="/" className="underline">Create one</Link>.
        </p>
      )}

      <ul className="mt-4 space-y-3">
        {data?.map((session) => (
          <li key={session.id}>
            <Link
              to={`/sessions/${session.id}`}
              className="block rounded-lg border border-slate-200 p-4 hover:bg-slate-50"
            >
              <p className="font-medium text-slate-900">
                {session.role ?? 'Untitled role'}
                {session.company ? ` @ ${session.company}` : ''}
              </p>
              <p className="mt-1 text-sm text-slate-500">
                {new Date(session.createdAt).toLocaleString()}
              </p>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  )
}
