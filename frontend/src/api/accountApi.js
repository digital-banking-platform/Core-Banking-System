import { request } from './client';


const mapAccount = (a) => ({
  holderName: a.AccountHolderName ?? a.accountHolderName,
  accountNumber: a.accountnumber ?? a.accountNumber,
  phone: a.phonenumber,
  branch: a.branch,
  ifsc: a.ifsccode,
  type: a.accountType,
});

export async function getProfile() {
  return request('/profile/me'); // 404 → no profile yet
}
export const createProfile = (payload) => request('/profile/create-bank-Profile', { method: 'POST', body: payload });
export const updateName = (name) => request('/profile/name', { method: 'POST', body: { name } });
export const updateAddress = (payload) => request('/profile/address', { method: 'PUT', body: payload });

export async function getAccounts() {
  const data = await request('/accounts/me');
  return (data?.accountDetailsResponseList || []).map(mapAccount);
}
export const createAccount = ({ accountType, ifsc, pin, pinConfirm }) =>
  request('/accounts/create-bank-account', {
    method: 'POST',
    body: { accountType, IfscCode: ifsc, pin, pinConfirm },
  });
export const checkBalance = (accountnumber, pin) =>
  request('/accounts/check-balance', { method: 'POST', body: { accountnumber, pin } });
export const getBranches = (page = 0, size = 20) => request('/accounts/internals/branches', { query: { page, size } });
export const verifyReceiver = (accountNumber) =>
  request('/accounts/internals/verify-receiver', { method: 'POST', body: { accountNumber } });

/* Admin / manager */
export const listProfilesByKyc = (kycStatus, page = 0, size = 6) =>
  request('/admin/accounts/profiles/kycstatus', { query: { kycStatus, page, size } });
export const findProfileByAadhaar = (aadhaar) =>
  request('/admin/accounts/kyc/search', { method: 'POST', body: { AadhaarNumber: aadhaar } });
export const verifyKyc = (profileId) => request('/admin/accounts/kycstatus/verify', { method: 'PUT', body: { ProfileId: profileId } });
export const rejectKyc = (profileId) => request('/admin/accounts/kycstatus/reject', { method: 'PUT', body: { ProfileId: profileId } });
export const listAccounts = (accountType, page = 0, size = 6) => request('/admin/accounts', { query: { accountType, page, size } });
export const createBranch = (payload) => request('/admin/accounts/branches', { method: 'POST', body: payload });
