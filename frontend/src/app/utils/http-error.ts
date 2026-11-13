import { HttpErrorResponse } from '@angular/common/http';

/** Turns any HTTP error into a short, human-readable explanation. */
export function describeError(err: unknown, action: string): string {
  const e = err as HttpErrorResponse;
  if (!e || e.status === undefined) {
    return `${action}: unexpected error.`;
  }
  if (e.status === 0) {
    return `${action}: cannot reach the backend at the configured API URL. Is Spring Boot running on port 8080?`;
  }
  const serverMsg = (e.error && (e.error.message || e.error.error)) || e.statusText;
  return `${action} (HTTP ${e.status}): ${serverMsg}`;
}
