import { request } from './client';

/* idempotencyKey is generated once per transfer attempt by the form, so a retry after a
   timeout re-sends the same key and cannot move the money twice. */
export const transferMoney = (payload) => request('/payment-transactions/transfer', { method: 'POST', body: payload });

export const getHistory = (accountNumber, page = 0, size = 10) =>
  request('/payment-transactions/history', { method: 'POST', query: { page, size }, body: { accountNumber } });

const transferAttempts = new Map();

function runTransferAttempt(attempt) {
  attempt.phase = 'loading';
  attempt.error = null;
  attempt.promise = transferMoney(attempt.payload).then(
    (result) => {
      attempt.phase = 'complete';
      attempt.result = result;
      attempt.payload = null;
      return attempt;
    },
    (error) => {
      attempt.phase = 'error';
      attempt.error = error;
      return attempt;
    },
  );
  return attempt.promise;
}

export function beginTransferAttempt(payload) {
  const attempt = { payload, promise: null, result: null, error: null, phase: 'loading' };
  transferAttempts.set(payload.idempotencyKey, attempt);
  runTransferAttempt(attempt);
  return attempt;
}

export function getTransferAttempt(idempotencyKey) {
  return transferAttempts.get(idempotencyKey) || null;
}

export function retryTransferAttempt(idempotencyKey) {
  const attempt = transferAttempts.get(idempotencyKey);
  if (!attempt?.payload) return null;
  runTransferAttempt(attempt);
  return attempt;
}

export function clearTransferAttempt(idempotencyKey) {
  transferAttempts.delete(idempotencyKey);
}
