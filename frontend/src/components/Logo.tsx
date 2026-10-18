export function Logo() {
  return (
    <div className="flex items-center gap-2">
      <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-blue-600">
        <svg viewBox="0 0 24 24" className="h-4 w-4 text-white" fill="currentColor">
          <path d="M12 2l1.8 5.2L19 9l-5.2 1.8L12 16l-1.8-5.2L5 9l5.2-1.8L12 2z" />
        </svg>
      </div>
      <span className="text-lg font-semibold tracking-tight text-slate-900">
        Interview<span className="text-blue-600">Prep</span>AI
      </span>
    </div>
  )
}
