import type { ReactNode } from 'react'

export function LabeledNote({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="mt-3 rounded-md bg-slate-50 p-3">
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">{label}</p>
      <p className="mt-1 whitespace-pre-wrap text-sm text-slate-700">{children}</p>
    </div>
  )
}
