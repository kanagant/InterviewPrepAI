import { useQuery } from '@tanstack/react-query'
import { useParams } from 'react-router-dom'
import { getSession, listArtifacts } from '../api/sessions'
import { ArtifactResultView } from '../components/ArtifactResultView'

export function SessionDetailPage() {
  const { id } = useParams<{ id: string }>()
  const sessionId = Number(id)

  const sessionQuery = useQuery({
    queryKey: ['session', sessionId],
    queryFn: () => getSession(sessionId),
  })

  const artifactsQuery = useQuery({
    queryKey: ['session', sessionId, 'artifacts'],
    queryFn: () => listArtifacts(sessionId),
  })

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      {sessionQuery.data && (
        <>
          <h1 className="text-2xl font-semibold text-slate-900">
            {sessionQuery.data.role ?? 'Untitled role'}
            {sessionQuery.data.company ? ` @ ${sessionQuery.data.company}` : ''}
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            {new Date(sessionQuery.data.createdAt).toLocaleString()}
          </p>
        </>
      )}

      {artifactsQuery.isLoading && <p className="mt-6 text-slate-500">Loading...</p>}
      {artifactsQuery.isError && (
        <p className="mt-6 text-sm text-red-600">Failed to load artifacts.</p>
      )}

      {artifactsQuery.data?.map((artifact) => (
        <ArtifactResultView key={artifact.id} artifact={artifact} />
      ))}
    </div>
  )
}
