// ===== UTILITIES =====
const $ = (s) => document.querySelector(s);
const $$ = (s) => [...document.querySelectorAll(s)];

let me = null;
let page = 'dashboard';

const api = async (url, opt = {}) => {
    const r = await fetch(url, { credentials: 'same-origin', ...opt });
    const d = await r.json().catch(() => ({}));
    if (!r.ok) throw Error(d.message || 'Something went wrong');
    return d;
};

const toast = (m) => {
    const t = $('#toast');
    t.textContent = m;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 2800);
};

const esc = (s) =>
    String(s ?? '').replace(/[&<>"']/g, (c) => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;',
    }[c]));

const formBody = (form) => new URLSearchParams(new FormData(form)).toString();

// ===== AUTH VIEW =====
$$('.auth-tabs button').forEach((b) => {
    b.onclick = () => {
        $$('.auth-tabs button').forEach((x) => x.classList.remove('active'));
        b.classList.add('active');
        const reg = b.dataset.auth === 'register';
        $('#loginForm').classList.toggle('hidden', reg);
        $('#registerForm').classList.toggle('hidden', !reg);
    };
});

$('#loginForm').onsubmit = async (e) => {
    e.preventDefault();
    try {
        const d = await api('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formBody(e.target),
        });
        me = d.user;
        showApp();
        toast('Welcome back, ' + me.fullName.split(' ')[0] + '!');
    } catch (x) {
        toast(x.message);
    }
};

$('#registerForm').onsubmit = async (e) => {
    e.preventDefault();
    try {
        await api('/api/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formBody(e.target),
        });
        toast('Account created. You can now sign in.');
        document.querySelector('[data-auth=login]').click();
        e.target.reset();
    } catch (x) {
        toast(x.message);
    }
};

$('#logout').onclick = async () => {
    await api('/api/logout', { method: 'POST' });
    location.reload();
};

// ===== APP SHELL =====
function showApp() {
    $('#authView').classList.add('hidden');
    $('#appView').classList.remove('hidden');
    $('#sideName').textContent = me.fullName;
    $('#sideRole').textContent = me.role;
    $('#avatar').textContent = me.fullName.trim()[0]?.toUpperCase() || 'Q';
    buildNav();
    go('dashboard');
}

function buildNav() {
    const common = [['dashboard', '▦', 'Dashboard']];
    let items = common;

    if (me.role === 'STUDENT') {
        items = [
            ...common,
            ['queue', '⌁', 'Join Queue'],
            ['appointments', '◷', 'Appointments'],
            ['feedback', '★', 'Feedback'],
        ];
    }
    if (me.role === 'STAFF') {
        items = [
            ...common,
            ['staffqueue', '☷', 'Live Queue'],
            ['laptops', '▣', 'Laptop Inventory'],
        ];
    }
    if (me.role === 'ADMIN') {
        items = [...common, ['admin', '◉', 'Administration']];
    }

    $('#nav').innerHTML = items
        .map(
            (x) =>
                `<button class="nav-btn ${x[0] === page ? 'active' : ''}" data-page="${x[0]}">${x[1]} <span>${x[2]}</span></button>`
        )
        .join('');

    $$('.nav-btn').forEach((b) => (b.onclick = () => go(b.dataset.page)));
}

async function go(p) {
    page = p;
    buildNav();

    const titles = {
        dashboard: 'Dashboard',
        queue: 'Join Queue',
        appointments: 'Appointments',
        feedback: 'Feedback',
        staffqueue: 'Live Queue',
        laptops: 'Laptop Inventory',
        admin: 'Administration',
    };

    $('#pageTitle').textContent = titles[p] || 'Dashboard';
    $('#pageEyebrow').textContent = (me.role + ' • QUEUESMART').toUpperCase();

    try {
        if (p === 'dashboard') return renderDashboard();
        if (p === 'queue') return renderQueue();
        if (p === 'appointments') return renderAppointments();
        if (p === 'feedback') return renderFeedback();
        if (p === 'staffqueue') return renderStaffQueue();
        if (p === 'laptops') return renderLaptops();
        if (p === 'admin') return renderAdmin();
    } catch (x) {
        $('#content').innerHTML = `<div class="notice">${esc(x.message)}</div>`;
    }
}

// ===== DASHBOARD =====
async function renderDashboard() {
    if (me.role === 'STUDENT') {
        const [q, s] = await Promise.all([api('/api/student/status'), api('/api/services')]);

        $('#content').innerHTML = `
            <div class="grid">
                <div class="card stat">
                    <div class="label">QUEUE STATUS</div>
                    <div class="value">${q.active ? '#' + q.active.queueNumber : '—'}</div>
                    <div class="trend">${q.active ? q.active.status : 'Not in a queue'}</div>
                </div>
                <div class="card stat">
                    <div class="label">POSITION</div>
                    <div class="value">${q.active && q.position ? q.position : '—'}</div>
                    <div class="trend">${q.active && q.position ? 'people ahead of you' : 'Ready when you are'}</div>
                </div>
                <div class="card stat">
                    <div class="label">SERVICES</div>
                    <div class="value">${s.services.length}</div>
                    <div class="trend">Available today</div>
                </div>
                <div class="card stat">
                    <div class="label">ACCOUNT</div>
                    <div class="value">STUDENT</div>
                    <div class="trend">${esc(me.username)}</div>
                </div>
            </div>
            <div class="section">
                <div class="section-head">
                    <h2>Your current queue</h2>
                    <button class="btn-small" onclick="go('queue')">Join queue</button>
                </div>
                <div class="card">
                    ${
                        q.active
                            ? queueSummary(q)
                            : `<div class="empty">You are not currently in a queue. Choose a service to get started.</div>`
                    }
                </div>
            </div>
            <div class="section">
                <div class="section-head"><h2>Popular IT services</h2></div>
                <div class="service-grid">${s.services.slice(0, 3).map(serviceCard).join('')}</div>
            </div>`;
    } else if (me.role === 'STAFF') {
        const q = await api('/api/staff/queue');
        const waiting = q.queue.filter((x) => x.status === 'WAITING').length;
        const serving = q.queue.filter((x) => x.status === 'SERVING').length;

        $('#content').innerHTML = `
            <div class="grid">
                <div class="card stat">
                    <div class="label">WAITING</div>
                    <div class="value">${waiting}</div>
                    <div class="trend">Students today</div>
                </div>
                <div class="card stat">
                    <div class="label">SERVING</div>
                    <div class="value">${serving}</div>
                    <div class="trend">Currently in service</div>
                </div>
                <div class="card stat">
                    <div class="label">TOTAL ACTIVE</div>
                    <div class="value">${q.queue.length}</div>
                    <div class="trend">Live queue</div>
                </div>
                <div class="card stat">
                    <div class="label">ROLE</div>
                    <div class="value">STAFF</div>
                    <div class="trend">${esc(me.fullName)}</div>
                </div>
            </div>
            <div class="section">
                <div class="section-head">
                    <h2>Live queue</h2>
                    <button class="btn-small" onclick="go('staffqueue')">Manage queue</button>
                </div>
                ${staffTable(q.queue)}
            </div>`;
    } else {
        const a = await api('/api/admin/summary');

        $('#content').innerHTML = `
            <div class="grid">
                <div class="card stat">
                    <div class="label">FEEDBACK RATING</div>
                    <div class="value">${a.average.toFixed(1)}</div>
                    <div class="trend">Average / 5</div>
                </div>
                <div class="card stat">
                    <div class="label">AUDIT EVENTS</div>
                    <div class="value">${a.logs.length}</div>
                    <div class="trend">Recent activity</div>
                </div>
                <div class="card stat">
                    <div class="label">DATABASE</div>
                    <div class="value">ONLINE</div>
                    <div class="trend">MySQL connected</div>
                </div>
                <div class="card stat">
                    <div class="label">ROLE</div>
                    <div class="value">ADMIN</div>
                    <div class="trend">${esc(me.fullName)}</div>
                </div>
            </div>
            <div class="section">
                <div class="section-head"><h2>Recent activity</h2></div>
                ${logTable(a.logs)}
            </div>`;
    }
}

// ===== SHARED COMPONENTS =====
function serviceCard(s) {
    return `
        <div class="service-card">
            <div class="service-icon">✦</div>
            <h3>${esc(s.serviceName)}</h3>
            <p>${esc(s.description)}</p>
            <button class="btn-small" onclick="joinService(${s.serviceId})">Join this queue</button>
        </div>`;
}

function queueSummary(q) {
    return `
        <div class="queue-card">
            <div class="queue-number">${q.active.queueNumber}</div>
            <div>
                <span class="status-pill">${esc(q.active.status)}</span>
                <h2>${esc(q.active.serviceName)}</h2>
                <p class="muted">${
                    q.active.status === 'WAITING'
                        ? `You are #${q.position} in line.`
                        : 'You are currently being served.'
                }</p>
            </div>
        </div>`;
}

// ===== STUDENT: QUEUE =====
async function renderQueue() {
    const [s, q] = await Promise.all([api('/api/services'), api('/api/student/status')]);

    $('#content').innerHTML = `
        <div class="section">
            <div class="notice">
                You can have one active queue entry at a time. Your number is generated automatically for today's selected service.
            </div>
        </div>
        <div class="section">
            <div class="section-head"><h2>Choose a service</h2></div>
            <div class="service-grid">${s.services.map(serviceCard).join('')}</div>
        </div>
        ${
            q.active
                ? `<div class="section">
                    <div class="section-head"><h2>Current queue</h2></div>
                    <div class="card">${queueSummary(q)}</div>
                </div>`
                : ''
        }`;
}

async function joinService(id) {
    try {
        const d = await api('/api/queue/join', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'serviceId=' + id,
        });
        toast('You joined queue #' + d.queueNumber);
        go('queue');
    } catch (x) {
        toast(x.message);
    }
}

// ===== STUDENT: APPOINTMENTS =====
async function renderAppointments() {
    const [s, a] = await Promise.all([api('/api/services'), api('/api/student/appointments')]);

    $('#content').innerHTML = `
        <div class="card">
            <div class="section-head"><h2>Book an appointment</h2></div>
            <form id="apptForm" class="form-grid">
                <label>Service
                    <select name="serviceId">
                        ${s.services
                            .map(
                                (x) =>
                                    `<option value="${x.serviceId}">${esc(x.serviceName)}</option>`
                            )
                            .join('')}
                    </select>
                </label>
                <label>Date<input class="field" type="date" name="date" required></label>
                <label>Time<input class="field" type="time" name="time" required></label>
                <div class="full"><button class="primary">Book appointment</button></div>
            </form>
        </div>
        <div class="section">
            <div class="section-head"><h2>My appointments</h2></div>
            ${
                a.appointments.length
                    ? `<div class="table-wrap">
                        <table class="table">
                            <thead>
                                <tr><th>Service</th><th>Date</th><th>Time</th><th>Status</th><th></th></tr>
                            </thead>
                            <tbody>
                                ${a.appointments
                                    .map(
                                        (x) => `
                                    <tr>
                                        <td>${esc(x.serviceName)}</td>
                                        <td>${x.date}</td>
                                        <td>${x.time}</td>
                                        <td><span class="status-pill">${x.status}</span></td>
                                        <td>${
                                            x.status === 'BOOKED'
                                                ? `<button class="danger" onclick="cancelAppt(${x.appointmentId})">Cancel</button>`
                                                : ''
                                        }</td>
                                    </tr>`
                                    )
                                    .join('')}
                            </tbody>
                        </table>
                    </div>`
                    : '<div class="card empty">No appointments yet.</div>'
            }
        </div>`;

    $('#apptForm').onsubmit = async (e) => {
        e.preventDefault();
        try {
            await api('/api/appointment/book', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formBody(e.target),
            });
            toast('Appointment booked');
            go('appointments');
        } catch (x) {
            toast(x.message);
        }
    };
}

async function cancelAppt(id) {
    try {
        await api('/api/appointment/cancel', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'appointmentId=' + id,
        });
        toast('Appointment cancelled');
        go('appointments');
    } catch (x) {
        toast(x.message);
    }
}

// ===== STUDENT: FEEDBACK =====
async function renderFeedback() {
    const q = await api('/api/student/status');

    $('#content').innerHTML = `
        <div class="card">
            <h2>How was your service?</h2>
            <p class="muted">Your feedback helps the IT department improve the student experience.</p>
            <form id="feedbackForm">
                <div class="stars">
                    ${[1, 2, 3, 4, 5]
                        .map((n) => `<button type="button" class="star" data-rating="${n}">★</button>`)
                        .join('')}
                </div>
                <input type="hidden" name="rating" id="rating" value="0">
                <textarea name="comment" rows="5" placeholder="Tell us about your experience..."></textarea>
                <div class="actions" style="margin-top:14px">
                    <button class="primary">Submit feedback</button>
                </div>
            </form>
        </div>`;

    $$('.star').forEach((b) => {
        b.onclick = () => {
            $('#rating').value = b.dataset.rating;
            $$('.star').forEach((x) =>
                x.classList.toggle('selected', x.dataset.rating <= b.dataset.rating)
            );
        };
    });

    $('#feedbackForm').onsubmit = async (e) => {
        e.preventDefault();
        try {
            await api('/api/feedback', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formBody(e.target) + (q.active ? '&queueId=' + q.active.queueId : ''),
            });
            toast('Thanks for your feedback!');
            e.target.reset();
            $$('.star').forEach((x) => x.classList.remove('selected'));
        } catch (x) {
            toast(x.message);
        }
    };
}

// ===== STAFF: QUEUE =====
async function renderStaffQueue() {
    const q = await api('/api/staff/queue');

    $('#content').innerHTML = `
        <div class="section">
            <div class="section-head">
                <h2>Today's queue</h2>
                <button class="btn-small" onclick="renderStaffQueue()">Refresh</button>
            </div>
            ${staffTable(q.queue)}
        </div>`;
}

function staffTable(rows) {
    if (!rows.length) return '<div class="card empty">No active queue entries right now.</div>';

    return `
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr><th>#</th><th>Student</th><th>Service</th><th>Status</th><th>Joined</th><th>Action</th></tr>
                </thead>
                <tbody>
                    ${rows
                        .map(
                            (x) => `
                        <tr>
                            <td><b>${x.queueNumber}</b></td>
                            <td>${esc(x.studentName)}</td>
                            <td>${esc(x.serviceName)}</td>
                            <td><span class="status-pill">${x.status}</span></td>
                            <td>${x.joinedAt ? x.joinedAt.replace('T', ' ').slice(0, 16) : ''}</td>
                            <td>${
                                x.status === 'WAITING'
                                    ? `<button class="btn-small" onclick="callNext(${x.queueId})">Call next</button>`
                                    : `<button class="primary" style="padding:8px 12px" onclick="complete(${x.queueId})">Complete</button>`
                            }</td>
                        </tr>`
                        )
                        .join('')}
                </tbody>
            </table>
        </div>`;
}

async function callNext(id) {
    try {
        await api('/api/staff/call', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'queueId=' + id,
        });
        toast('Student called');
        go('staffqueue');
    } catch (x) {
        toast(x.message);
    }
}

async function complete(id) {
    try {
        await api('/api/staff/complete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'queueId=' + id,
        });
        toast('Service completed');
        go('staffqueue');
    } catch (x) {
        toast(x.message);
    }
}

// ===== STAFF: LAPTOPS =====
async function renderLaptops() {
    const [d, q] = await Promise.all([api('/api/laptops'), api('/api/staff/queue')]);
    const students = q.queue.filter((x) => x.status === 'SERVING');

    const select = students.length
        ? `<select class="field" id="allocationStudent">
                <option value="">Choose a student currently being served</option>
                ${students
                    .map(
                        (x) =>
                            `<option value="${x.studentId}">#${x.queueNumber} — ${esc(x.studentName)} — ${esc(x.serviceName)}</option>`
                    )
                    .join('')}
            </select>`
        : '<div class="notice">No student is currently being served. Call a student from the live queue first.</div>';

    $('#content').innerHTML = `
        <div class="card">
            <div class="section-head"><h2>Allocate a laptop</h2></div>
            ${select}
        </div>
        <div class="section">
            <div class="section-head">
                <h2>Available laptops</h2>
                <button class="btn-small" onclick="renderLaptops()">Refresh</button>
            </div>
            <div class="service-grid">
                ${
                    d.laptops
                        .map(
                            (x) => `
                        <div class="card">
                            <div class="service-icon">▣</div>
                            <h3>${esc(x.assetNumber)}</h3>
                            <p>Available for allocation.</p>
                            <button class="btn-small" onclick="allocate(${x.laptopId})" ${
                                students.length ? '' : 'disabled'
                            }>Allocate to selected student</button>
                        </div>`
                        )
                        .join('') || '<div class="card empty">No laptops currently available.</div>'
                }
            </div>
        </div>`;
}

async function allocate(id) {
    const opt = $('#allocationStudent')?.selectedOptions?.[0];
    if (!opt || !opt.value) {
        toast('Choose a student first.');
        return;
    }

    const q = await api('/api/staff/queue');
    const selected = q.queue.find((x) => String(x.studentId) === opt.value);
    if (!selected) {
        toast('Student is no longer in service. Refresh the page.');
        return;
    }

    try {
        await api('/api/laptop/allocate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'laptopId=' + id + '&studentId=' + opt.value,
        });
        toast('Laptop allocated to ' + selected.studentName);
        renderLaptops();
    } catch (x) {
        toast(x.message);
    }
}

// ===== ADMIN =====
async function renderAdmin() {
    const d = await api('/api/admin/summary');

    $('#content').innerHTML = `
        <div class="card">
            <div class="section-head"><h2>Register staff member</h2></div>
            <form id="staffForm" class="form-grid">
                <label>Full name<input class="field" name="fullName" required></label>
                <label>Staff number<input class="field" name="staffNumber" required></label>
                <label>Email<input class="field" type="email" name="email" required></label>
                <label>Username<input class="field" name="username" required></label>
                <label>Password<input class="field" type="password" name="password" minlength="8" required></label>
                <label>Position<input class="field" name="position" value="IT Support Technician"></label>
                <div class="full"><button class="primary">Create staff account</button></div>
            </form>
        </div>
        <div class="section">
            <div class="section-head"><h2>Audit log</h2></div>
            ${logTable(d.logs)}
        </div>`;

    $('#staffForm').onsubmit = async (e) => {
        e.preventDefault();
        try {
            await api('/api/admin/staff', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formBody(e.target),
            });
            toast('Staff account created');
            e.target.reset();
        } catch (x) {
            toast(x.message);
        }
    };
}

function logTable(rows) {
    if (!rows.length) return '<div class="card empty">No audit events yet.</div>';

    return `
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr><th>Time</th><th>Action</th><th>Details</th></tr>
                </thead>
                <tbody>
                    ${rows
                        .map(
                            (x) => `
                        <tr>
                            <td>${x.createdAt ? x.createdAt.replace('T', ' ').slice(0, 16) : ''}</td>
                            <td><b>${esc(x.action)}</b></td>
                            <td>${esc(x.details)}</td>
                        </tr>`
                        )
                        .join('')}
                </tbody>
            </table>
        </div>`;
}

// ===== BOOT =====
(async () => {
    try {
        const d = await api('/api/me');
        if (d.user) {
            me = d.user;
            showApp();
        }
    } catch (e) {
        // Not logged in — do nothing
    }
})();