const apiBase = '';

const loginForm = document.getElementById('loginForm');
const demoMentorBtn = document.getElementById('demoMentorBtn');
const demoMenteeBtn = document.getElementById('demoMenteeBtn');
const profileCreateForm = document.getElementById('profileCreateForm');

const mentorSection = document.getElementById('mentorSection');
const menteeSection = document.getElementById('menteeSection');
const adminSection = document.getElementById('adminSection');
const adminLoadProfilesBtn = document.getElementById('adminLoadProfilesBtn');
const adminProfilesTableBody = document.getElementById('adminProfilesTableBody');
const mentorProfileForm = document.getElementById('mentorProfileForm');
const mentorLoadSessionsBtn = document.getElementById('mentorLoadSessionsBtn');
const mentorSessionsTableBody = document.getElementById('mentorSessionsTableBody');

const menteeSearchForm = document.getElementById('menteeSearchForm');
const matchesTableBody = document.getElementById('matchesTableBody');
const sessionCreateForm = document.getElementById('sessionCreateForm');
const menteeLoadSessionsBtn = document.getElementById('menteeLoadSessionsBtn');
const menteeSessionsTableBody = document.getElementById('menteeSessionsTableBody');

const currentUserCard = document.getElementById('currentUserCard');
const navAuthBadge = document.getElementById('navAuthBadge');
const logoutBtn = document.getElementById('logoutBtn');
const screenFlash = document.getElementById('screenFlash');
const toastStack = document.getElementById('toastStack');
const roleWelcomeBanner = document.getElementById('roleWelcomeBanner');

const consoleOutput = document.getElementById('consoleOutput');
const clearConsoleBtn = document.getElementById('clearConsoleBtn');

let currentUser = null;
let roleBannerTimer = null;

function triggerScreenPulse() {
  screenFlash.classList.remove('active');
  // Force reflow to reliably restart animation.
  void screenFlash.offsetWidth;
  screenFlash.classList.add('active');
}

function showToast(type, title, message, roleTone = '') {
  const toast = document.createElement('div');
  toast.className = `toast ${type} ${roleTone}`.trim();
  toast.innerHTML = `
    <span class="toast-title">${title}</span>
    <span class="toast-message">${message}</span>
  `;
  toastStack.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('out');
    setTimeout(() => toast.remove(), 260);
  }, 2200);
}

function showRoleWelcomeBanner(user) {
  if (!user) {
    roleWelcomeBanner.classList.add('hidden');
    roleWelcomeBanner.classList.remove('show', 'hide', 'mentor', 'mentee');
    roleWelcomeBanner.textContent = '';
    return;
  }

  const tone = user.role === 'MENTOR' ? 'mentor' : 'mentee';
  roleWelcomeBanner.textContent = `Welcome ${user.fullName}. You are now in the ${user.role.toLowerCase()} interface.`;
  roleWelcomeBanner.classList.remove('hidden', 'hide', 'mentor', 'mentee');
  roleWelcomeBanner.classList.add('show', tone);

  if (roleBannerTimer) {
    clearTimeout(roleBannerTimer);
  }

  roleBannerTimer = setTimeout(() => {
    roleWelcomeBanner.classList.remove('show');
    roleWelcomeBanner.classList.add('hide');
    setTimeout(() => {
      roleWelcomeBanner.classList.add('hidden');
      roleWelcomeBanner.classList.remove('hide', 'mentor', 'mentee');
      roleWelcomeBanner.textContent = '';
    }, 320);
  }, 2100);
}

function parseCsv(text) {
  return text
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

function logToConsole(title, data) {
  const stamp = new Date().toLocaleTimeString();
  consoleOutput.textContent = `[${stamp}] ${title}\n${JSON.stringify(data, null, 2)}`;
}

function extractErrorMessage(error, fallback) {
  if (!error) {
    return fallback;
  }

  if (typeof error === 'string') {
    return error;
  }

  if (error.error && typeof error.error === 'string') {
    return error.error;
  }

  if (error.message && typeof error.message === 'string') {
    return error.message;
  }

  if (error.details && typeof error.details === 'object') {
    const details = Object.entries(error.details)
      .map(([field, msg]) => `${field}: ${msg}`)
      .join(' | ');
    if (details) {
      return details;
    }
  }

  return fallback;
}

function setRoleView() {
  adminSection.classList.add('hidden');
  mentorSection.classList.add('hidden');
  menteeSection.classList.add('hidden');

  if (!currentUser) {
    return;
  }

  if (currentUser.role === 'ADMIN') {
    adminSection.classList.remove('hidden');
  }

  if (currentUser.role === 'MENTOR') {
    mentorSection.classList.remove('hidden');
    fillMentorProfileForm(currentUser);
  }

  if (currentUser.role === 'MENTEE') {
    menteeSection.classList.remove('hidden');
  }
}

function focusActiveRoleSection(role) {
  const target = role === 'ADMIN'
    ? adminSection
    : role === 'MENTOR'
      ? mentorSection
      : menteeSection;
  if (!target || target.classList.contains('hidden')) {
    return;
  }

  target.classList.remove('focused');
  void target.offsetWidth;
  target.classList.add('focused');

  target.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function setCurrentUser(user) {
  currentUser = user;

  if (!user) {
    currentUserCard.textContent = 'Not logged in.';
    navAuthBadge.textContent = 'Guest';
    logoutBtn.disabled = true;
    mentorProfileForm.reset();
    menteeSearchForm.reset();
    sessionCreateForm.reset();
    adminProfilesTableBody.innerHTML = '';
    clearRoleTables();
    setRoleView();
    showRoleWelcomeBanner(null);
    return;
  }

  currentUserCard.innerHTML = `
    <strong>${user.fullName}</strong><br>
    Email: ${user.email}<br>
    Role: ${user.role}<br>
    User ID: ${user.id}
  `;
  navAuthBadge.textContent = `${user.role} - ${user.fullName}`;
  logoutBtn.disabled = false;
  setRoleView();
  showRoleWelcomeBanner(user);
}

function fillMentorProfileForm(user) {
  mentorProfileForm.elements.fullName.value = user.fullName || '';
  mentorProfileForm.elements.yearsOfExperience.value = user.yearsOfExperience ?? 0;
  mentorProfileForm.elements.skills.value = (user.skills || []).join(', ');
  mentorProfileForm.elements.interests.value = (user.interests || []).join(', ');
  mentorProfileForm.elements.bio.value = user.bio || '';
  mentorProfileForm.elements.availableForMentorship.checked = !!user.availableForMentorship;
}

function clearRoleTables() {
  mentorSessionsTableBody.innerHTML = '';
  menteeSessionsTableBody.innerHTML = '';
  matchesTableBody.innerHTML = '';
}

function renderAdminProfiles(profiles) {
  adminProfilesTableBody.innerHTML = '';

  const filtered = profiles.filter((profile) => profile.role === 'MENTOR' || profile.role === 'MENTEE');

  if (!filtered.length) {
    adminProfilesTableBody.innerHTML = '<tr><td colspan="6">No mentor/mentee profiles found.</td></tr>';
    return;
  }

  filtered.forEach((profile) => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${profile.id}</td>
      <td>${profile.fullName}</td>
      <td>${profile.email}</td>
      <td>${profile.role}</td>
      <td>${profile.yearsOfExperience}</td>
      <td><button type="button" class="session-action-btn danger admin-delete-btn" data-profile-id="${profile.id}">Delete</button></td>
    `;
    adminProfilesTableBody.appendChild(row);
  });
}

function renderSessions(tableBody, sessions, allowMentorActions = false) {
  tableBody.innerHTML = '';

  if (!sessions.length) {
    tableBody.innerHTML = `<tr><td colspan="${allowMentorActions ? 6 : 5}">No sessions found.</td></tr>`;
    return;
  }

  sessions.forEach((session) => {
    let actionCell = '';
    if (allowMentorActions) {
      const actions = [];
      if (session.status === 'REQUESTED') {
        actions.push(`<button type="button" class="session-action-btn" data-session-id="${session.id}" data-status="CONFIRMED">Accept</button>`);
        actions.push(`<button type="button" class="session-action-btn danger" data-session-id="${session.id}" data-status="CANCELLED">Reject</button>`);
      } else if (session.status === 'CONFIRMED') {
        actions.push(`<button type="button" class="session-action-btn" data-session-id="${session.id}" data-status="COMPLETED">Mark Complete</button>`);
        actions.push(`<button type="button" class="session-action-btn danger" data-session-id="${session.id}" data-status="CANCELLED">Cancel</button>`);
      } else {
        actions.push('<span class="muted">No actions</span>');
      }
      actionCell = `<td>${actions.join('')}</td>`;
    }

    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${session.id}</td>
      <td>${session.topic}</td>
      <td>${session.status}</td>
      <td>${session.scheduledAt}</td>
      <td>${session.durationMinutes} min</td>
      ${actionCell}
    `;
    tableBody.appendChild(row);
  });
}

function renderMatches(matches) {
  matchesTableBody.innerHTML = '';

  if (!matches.length) {
    matchesTableBody.innerHTML = '<tr><td colspan="6">No suitable mentors found. Try broader preferred skill keywords.</td></tr>';
    return;
  }

  matches.forEach((match, index) => {
    const skills = Array.isArray(match.matchedSkills) ? match.matchedSkills : [];
    const interests = Array.isArray(match.matchedInterests) ? match.matchedInterests : [];
    const criteriaParts = [];
    if (skills.length) {
      criteriaParts.push(`Skills: ${skills.join(', ')}`);
    }
    if (interests.length) {
      criteriaParts.push(`Interests: ${interests.join(', ')}`);
    }
    if (!criteriaParts.length) {
      criteriaParts.push('Experience-based match');
    }

    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${index + 1}</td>
      <td>${match.mentorId}</td>
      <td>${match.mentorName}</td>
      <td>${match.matchScore}</td>
      <td>${criteriaParts.join(' | ')}</td>
      <td><button type="button" class="choose-mentor-btn ghost" data-mentor-id="${match.mentorId}">Choose</button></td>
    `;
    matchesTableBody.appendChild(row);
  });

  const firstMentor = matches[0];
  if (firstMentor) {
    sessionCreateForm.elements.mentorId.value = firstMentor.mentorId;
    showToast('success', 'Mentors Loaded', 'Click Choose on any mentor to book with that mentor.');
  }
}

async function apiCall(url, options = {}) {
  const response = await fetch(`${apiBase}${url}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });

  let payload;
  try {
    payload = await response.json();
  } catch (_) {
    payload = { message: 'No JSON response body' };
  }

  if (!response.ok) {
    throw payload;
  }

  return payload;
}

async function loginWithCredentials(email, password, role) {
  const data = await apiCall('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password, role }),
  });

  const profile = await apiCall(`/api/profiles/${data.id}`);
  setCurrentUser(profile);
  triggerScreenPulse();
  const roleTone = profile.role === 'MENTOR' ? 'mentor' : 'mentee';
  showToast('success', 'Login Successful', `Welcome, ${profile.fullName} (${profile.role}).`, roleTone);
  focusActiveRoleSection(profile.role);
  logToConsole('Login Successful', profile);
}

async function loadSessionsForCurrentUser() {
  if (!currentUser) {
    throw { error: 'Please login first.' };
  }

  const sessions = await apiCall(`/api/sessions/user/${currentUser.id}`);

  if (currentUser.role === 'MENTOR') {
    renderSessions(mentorSessionsTableBody, sessions, true);
  } else {
    renderSessions(menteeSessionsTableBody, sessions, false);
  }

  logToConsole('Sessions Loaded', sessions);
}

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(loginForm);

  try {
    await loginWithCredentials(
      formData.get('email'),
      formData.get('password'),
      formData.get('role')
    );
  } catch (error) {
    setCurrentUser(null);
    showToast('error', 'Login Failed', extractErrorMessage(error, 'Invalid credentials or role mismatch.'));
    logToConsole('Login Failed', error);
  }
});

demoMentorBtn.addEventListener('click', async () => {
  try {
    await loginWithCredentials('riya.mentor@example.com', 'Mentor@123', 'MENTOR');
  } catch (error) {
    logToConsole('Demo Mentor Login Failed', error);
  }
});

demoMenteeBtn.addEventListener('click', async () => {
  try {
    await loginWithCredentials('aman.mentee@example.com', 'Mentee@123', 'MENTEE');
  } catch (error) {
    logToConsole('Demo Mentee Login Failed', error);
  }
});

profileCreateForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(profileCreateForm);

  const body = {
    fullName: formData.get('fullName'),
    email: formData.get('email'),
    password: formData.get('password'),
    role: formData.get('role'),
    yearsOfExperience: Number(formData.get('yearsOfExperience')),
    bio: formData.get('bio') || null,
    skills: parseCsv(formData.get('skills') || ''),
    interests: parseCsv(formData.get('interests') || ''),
    availableForMentorship: formData.get('availableForMentorship') === 'on',
  };

  try {
    const data = await apiCall('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(body),
    });

    showToast('success', 'Registration Successful', `${data.fullName} can now login as ${data.role}.`);
    logToConsole('Registration Successful', {
      fullName: data.fullName,
      email: data.email,
      role: data.role,
      message: data.message,
    });
    profileCreateForm.reset();
  } catch (error) {
    showToast('error', 'Registration Failed', extractErrorMessage(error, 'Please check details and try again.'));
    logToConsole('Registration Failed', error);
  }
});

adminLoadProfilesBtn.addEventListener('click', async () => {
  if (!currentUser || currentUser.role !== 'ADMIN') {
    showToast('error', 'Access Denied', 'Login as admin to view all profiles.');
    return;
  }

  try {
    const profiles = await apiCall('/api/profiles');
    renderAdminProfiles(profiles);
    showToast('success', 'Profiles Loaded', 'Admin profile list refreshed.');
    logToConsole('Admin Profiles Loaded', profiles);
  } catch (error) {
    showToast('error', 'Load Failed', extractErrorMessage(error, 'Could not load profiles.'));
    logToConsole('Admin Load Profiles Failed', error);
  }
});

adminProfilesTableBody.addEventListener('click', async (event) => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) {
    return;
  }

  if (!target.classList.contains('admin-delete-btn')) {
    return;
  }

  const profileId = target.dataset.profileId;
  if (!profileId) {
    return;
  }

  if (!currentUser || currentUser.role !== 'ADMIN') {
    showToast('error', 'Access Denied', 'Only admin can delete profiles.');
    return;
  }

  try {
    await apiCall(`/api/profiles/${profileId}`, { method: 'DELETE' });
    showToast('success', 'Profile Deleted', `Profile ID ${profileId} deleted successfully.`);
    const profiles = await apiCall('/api/profiles');
    renderAdminProfiles(profiles);
    logToConsole('Admin Profile Deleted', { profileId });
  } catch (error) {
    showToast('error', 'Delete Failed', extractErrorMessage(error, 'Could not delete profile.'));
    logToConsole('Admin Delete Profile Failed', error);
  }
});

mentorProfileForm.addEventListener('submit', async (event) => {
  event.preventDefault();

  if (!currentUser || currentUser.role !== 'MENTOR') {
    logToConsole('Mentor Update Failed', { error: 'Login as mentor first.' });
    return;
  }

  const formData = new FormData(mentorProfileForm);
  const body = {
    fullName: formData.get('fullName'),
    email: currentUser.email,
    role: 'MENTOR',
    yearsOfExperience: Number(formData.get('yearsOfExperience')),
    bio: formData.get('bio') || null,
    skills: parseCsv(formData.get('skills') || ''),
    interests: parseCsv(formData.get('interests') || ''),
    availableForMentorship: formData.get('availableForMentorship') === 'on',
  };

  try {
    const updated = await apiCall(`/api/profiles/${currentUser.id}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
    setCurrentUser(updated);
    showToast('success', 'Profile Saved', 'Mentor profile updated successfully.');
    logToConsole('Mentor Profile Updated', updated);
  } catch (error) {
    showToast('error', 'Update Failed', 'Mentor profile could not be updated.');
    logToConsole('Mentor Update Failed', error);
  }
});

menteeSearchForm.addEventListener('submit', async (event) => {
  event.preventDefault();

  if (!currentUser || currentUser.role !== 'MENTEE') {
    logToConsole('Search Failed', { error: 'Login as mentee first.' });
    return;
  }

  const searchForm = new FormData(menteeSearchForm);
  const limit = Number(searchForm.get('limit') || 5);
  const preferredSkill = String(searchForm.get('preferredSkill') || '').trim();
  const skillQuery = preferredSkill ? `&skill=${encodeURIComponent(preferredSkill)}` : '';

  try {
    const matches = await apiCall(`/api/matches/mentee/${currentUser.id}?limit=${limit}${skillQuery}`);
    renderMatches(matches);
    if (preferredSkill) {
      showToast('success', 'Skill-Based Results', `Showing mentors for preferred skill: ${preferredSkill}.`);
    }
    logToConsole('Mentor Matches Loaded', matches);
  } catch (error) {
    showToast('error', 'Search Failed', 'Could not load mentor matches.');
    logToConsole('Search Failed', error);
  }
});

matchesTableBody.addEventListener('click', (event) => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) {
    return;
  }

  if (!target.classList.contains('choose-mentor-btn')) {
    return;
  }

  const mentorId = target.dataset.mentorId;
  if (!mentorId) {
    return;
  }

  sessionCreateForm.elements.mentorId.value = mentorId;
  sessionCreateForm.scrollIntoView({ behavior: 'smooth', block: 'center' });
  showToast('success', 'Mentor Selected', `Mentor ID ${mentorId} selected. Complete date and click Create Session Request.`);
});

sessionCreateForm.addEventListener('submit', async (event) => {
  event.preventDefault();

  if (!currentUser || currentUser.role !== 'MENTEE') {
    logToConsole('Session Create Failed', { error: 'Login as mentee first.' });
    return;
  }

  const formData = new FormData(sessionCreateForm);
  const body = {
    mentorId: Number(formData.get('mentorId')),
    menteeId: currentUser.id,
    topic: formData.get('topic'),
    scheduledAt: new Date(formData.get('scheduledAt')).toISOString().slice(0, 19),
    durationMinutes: Number(formData.get('durationMinutes')),
  };

  try {
    const data = await apiCall('/api/sessions', {
      method: 'POST',
      body: JSON.stringify(body),
    });
    const createdMentorId = data.mentor?.id ?? body.mentorId;
    showToast('success', 'Session Requested', `Session created with mentor ID ${createdMentorId}.`);
    logToConsole('Session Created', data);
    sessionCreateForm.reset();
  } catch (error) {
    showToast('error', 'Session Failed', 'Could not create session request.');
    logToConsole('Session Create Failed', error);
  }
});

mentorLoadSessionsBtn.addEventListener('click', async () => {
  try {
    await loadSessionsForCurrentUser();
    showToast('success', 'Sessions Loaded', 'Mentor sessions loaded. Use Accept/Reject/Complete actions.');
  } catch (error) {
    showToast('error', 'Load Failed', extractErrorMessage(error, 'Could not load mentor sessions.'));
    logToConsole('Load Mentor Sessions Failed', error);
  }
});

mentorSessionsTableBody.addEventListener('click', async (event) => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) {
    return;
  }

  if (!target.classList.contains('session-action-btn')) {
    return;
  }

  const sessionId = target.dataset.sessionId;
  const status = target.dataset.status;
  if (!sessionId || !status) {
    return;
  }

  try {
    await apiCall(`/api/sessions/${sessionId}/status?status=${status}`, {
      method: 'PATCH',
    });
    showToast('success', 'Session Updated', `Session ${sessionId} updated to ${status}.`);
    await loadSessionsForCurrentUser();
  } catch (error) {
    showToast('error', 'Update Failed', extractErrorMessage(error, 'Could not update session status.'));
    logToConsole('Session Status Update Failed', error);
  }
});

menteeLoadSessionsBtn.addEventListener('click', async () => {
  try {
    await loadSessionsForCurrentUser();
    showToast('success', 'Sessions Loaded', 'Mentee sessions loaded.');
  } catch (error) {
    showToast('error', 'Load Failed', extractErrorMessage(error, 'Could not load mentee sessions.'));
    logToConsole('Load Mentee Sessions Failed', error);
  }
});

logoutBtn.addEventListener('click', () => {
  const name = currentUser ? currentUser.fullName : 'User';
  setCurrentUser(null);
  loginForm.reset();
  triggerScreenPulse();
  showToast('success', 'Logout Successful', `Goodbye, ${name}. You are now logged out.`);
  logToConsole('Logged Out', { status: 'ok' });
});

clearConsoleBtn.addEventListener('click', () => {
  consoleOutput.textContent = 'Cleared.';
});

setCurrentUser(null);
