const backendOrigin = import.meta.env.VITE_BACKEND_ORIGIN as string

export function LoginPage() {
  return (
    <div className="mx-auto flex max-w-md flex-col items-center px-4 py-24 text-center">
      <h1 className="text-2xl font-semibold text-slate-900">InterviewPrepAI</h1>
      <p className="mt-2 text-slate-500">
        Sign in to paste a job description and resume and get tailored interview prep.
      </p>
      <a
        href={`${backendOrigin}/oauth2/authorization/google`}
        className="mt-6 rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white"
      >
        Sign in with Google
      </a>
    </div>
  )
}
