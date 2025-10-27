const baseUrl = 'http://localhost:8080';

const currentUser = {
  email: localStorage.getItem("email"),
  password: localStorage.getItem("password")
};

if (!currentUser.email || !currentUser.password) {
  window.location.href = "entry.html"; // force login
}

// Elements
const peopleListEl = document.getElementById('peopleList');
const refreshBtn = document.getElementById('refreshBtn');
const newUserBtn = document.getElementById('newUserBtn');
const modal = document.getElementById('modal');
const closeModalBtn = document.getElementById('closeModalBtn');
const createUserBtn = document.getElementById('createUserBtn');
const newName = document.getElementById('newUsername');
const newEmail = document.getElementById('newEmail');
const newPassword = document.getElementById('newPassword');
const newRole = document.getElementById('newRole');

const senderSelect = document.getElementById('senderSelect');
const receiverSelect = document.getElementById('receiverSelect');
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');

const messagesEl = document.getElementById('messages');
const chatTitle = document.getElementById('chatTitle');
const chatSub = document.getElementById('chatSub');
const openUser1 = document.getElementById('openUser1');
const openUser2 = document.getElementById('openUser2');
const openChatByIds = document.getElementById('openChatByIds');

const personSearch = document.getElementById('personSearch');
const searchBtn = document.getElementById('searchBtn');
const unreadOverviewBtn = document.getElementById('unreadOverviewBtn');

let people = [];
let currentChat = {user1: null, user2: null};

// ----------- Helpers ----------

function showToast(msg){
    console.log('[toast]', msg);
    alert(msg);
}

async function doFetch(path, options = {}){
    const res = await fetch(baseUrl + path, {
        ...options,
        credentials: "include"
    });
    return res;
}

// ---------- Person ----------
async function loadPeople() {
    try{
        const res = await doFetch(`/person/getAll`);
        if(!res.ok) throw new Error("Unauthorized or failed");
        people = await res.json();
        renderPeople();
        fillSelects();
    }catch(e){
        console.error(e);
        showToast('Failed to load people');
    }
}

function renderPeople(){
    peopleListEl.innerHTML = '';
    people.forEach(p => {
        const div = document.createElement('div');
        div.className = 'person';
        div.innerHTML = `
           <div class="avatar">${(p.name[0] || 'U').toUpperCase()}</div>
           <div class="meta">
               <div class="name">${p.name}</div>
               <div class="id">id: ${p.id}</div>
               <div class="muted">${p.email}</div>
            </div>
            <div style="margin-left:auto">
                <button class="btn small openChatBtn" data-id="${p.id}">Open</button>
            </div>`;

        peopleListEl.appendChild(div);

        // open chat click
        div.querySelector('.openChatBtn').addEventListener('click', () => {
            const activeSenderId = senderSelect.value || people[0]?.id;
            openChatByTwoIds(activeSenderId, p.id);
        });
    });
}

function fillSelects(){
    senderSelect.innerHTML = '';
    receiverSelect.innerHTML = '';
    people.forEach(p => {
        const opt1 = document.createElement('option');
        opt1.value = p.id;
        opt1.textContent = `${p.name} (id:${p.id})`;
        senderSelect.appendChild(opt1);

        const opt2 = opt1.cloneNode(true);
        receiverSelect.appendChild(opt2);
    });
}

// Search by Id
async function findPersonById() {
    const id = personSearch.value;
    if(!id) return showToast('Enter ID');    
    try{
       const res = await doFetch(`/person/getPerson/${id}`);
       if(res.status == 404) return showToast('Person not found');
       const p = await res.json();
       showToast(`Found: ${p.name} (id:${p.id})`);
    }catch(e){
        showToast('Search failed');
    }
}

// ----------- Messages ------------
async function openChatByTwoIds(user1, user2){
    if(!user1 || !user2){
        showToast('Select two user Ids');
        return;
    }
    currentChat.user1 = Number(user1);
    currentChat.user2 = Number(user2);

    try{
        const [user1Det, user2Det] = await Promise.all([
            doFetch(`/person/getPerson/${user1}`),
            doFetch(`/person/getPerson/${user2}`)
        ]);

        const user1Data = await user1Det.json();
        const user2Data = await user2Det.json();

        chatTitle.textContent = `Chat: ${user1Data.name} <-> ${user2Data.name}`;
        chatSub.textContent = `Showing conversation between ${user1Data.name} and ${user2Data.name}`;
    }catch(err){
        console.error("Error fetching user details:", err);
        showToast("Could not load user names, showing IDs instead");
        chatTitle.textContent = `Chat: ${user1} <-> ${user2}`;
        chatSub.textContent = `Showing conversation between ${user1} and ${user2}`;
    }
    await loadConversation(user1, user2);
}

async function loadConversation(user1, user2) {
    messagesEl.innerHTML = '<div class="muted">Loading messages...</div>';
    try{
        const res = await doFetch(`/api/chat/${user1}/${user2}`);
            
        const list = await res.json();
        const filtered = Array.isArray(list) ? list.filter(m => !m.isDeleted) : [];
        renderMessages(filtered);
    }catch(e){
        messagesEl.innerHTML = '<div class="muted">Failed to load</div>';
        console.error(e);
    }
}

function renderMessages(list) {
    messagesEl.innerHTML = '';
    if(!list || list.length === 0){
        messagesEl.innerHTML = '<div class="muted">No messages yet</div>';
        return;
    }

    list.forEach(m => {
        const el = document.createElement('div');
        const amISender = Number(senderSelect.value) === (m.sender?.id || m.senderId);
        el.className = 'msg ' + (amISender ? 'me' : '');
        const readMark = m.isRead ? '• read' : '• unread';

        el.innerHTML = `
        <div class="meta">
            <strong>${m.sname || (m.sender?.name || 'User')}</strong>
            <span class="muted">id:${m.sender?.id || (m.senderId || '')}</span>
            <span class="muted">${m.sentDate} ${m.sentTime || ''}</span>
            <span class="muted">${readMark}</span>
        </div>
        <div class="content">${escapeHtml(m.content || '')}</div>
        <div class="actions">
            <button class="action-btn mark-read" data-id="${m.id}">Read</button>
            ${amISender ? `<button class="action-btn edit" data-id="${m.id}">Edit</button>
            <button class="action-btn del" data-id="${m.id}">Delete</button>` : ''}
        </div>`;
        messagesEl.appendChild(el);
            
        // Action Listeners
        el.querySelector('.mark-read').addEventListener('click', () => markAsRead(m.id));
        if(amISender){
            el.querySelector('.edit').addEventListener('click', () => {
                const newText = prompt('Edit your message', m.content);
                if(newText !== null){
                    editMessage(m.id, m.sender.id, newText);
                }
            });
            el.querySelector('.del').addEventListener('click', () => {
                if(confirm('Delete this message?')) deleteMessage(m.id, m.sender.id);
            });
        }
    });

    // Scroll Down
    messagesEl.scrollTop = messagesEl.scrollHeight;
}

function escapeHtml(s){
    return String(s).replace(/[&<>"']/g, (m) => (
        {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[m]));
}

// Send Message
async function sendMessage() {
    const sid = senderSelect.value;
    const rid = receiverSelect.value;
    const content = messageInput.value.trim();
    if(!sid || !rid || !content){
        showToast('select sender, receiver and type message');
        return;
    }

    // Encode content for URL path
    const encoded = encodeURIComponent(content);
    try{
        const res = await doFetch(`/api/chat/send/${sid}/${rid}/${encoded}`, {
            method: 'POST'
        });
        if(!res.ok) throw new Error('send failed');
        messageInput.value = '';
        await loadConversation(sid, rid);
    }catch(err){
        showToast('Send failed: ' + err.message);
    }
}

// Mark as read
async function markAsRead(msgId) {
    try{
        await doFetch(`/api/chat/read/${msgId}`, {
            method: 'PUT'
        });
        if(currentChat.user1 && currentChat.user2){
            await loadConversation(currentChat.user1, currentChat.user2);
        }
    }catch(e){
        console.error(e);
        showToast('Mark read failed');
    }
}

// Edit Message
async function editMessage(msgId, senderId, newText) {
    try{
        const res = await doFetch(`/api/chat/edit/${msgId}/${senderId}`,{
            method: 'PUT',
            headers: {'Content-Type': 'text/plain'},
            body: newText
        });

        if(!res.ok) throw new Error('edit failed');
        await loadConversation(currentChat.user1, currentChat.user2);
    }catch(e){
        showToast('Edit failed: ' + e.message);
    }  
}

// Delete (Soft)
async function deleteMessage(msgId, senderId) {
    try{
        const res = await doFetch(`/api/chat/delete/${msgId}/${senderId}`, {
            method: 'DELETE',
        });
        if(!res.ok) throw new Error('delete failed');
        await loadConversation(currentChat.user1, currentChat.user2);
    } catch(e){ 
        showToast('Delete failed'); 
    }
}

// Get Unread Message for a user (shows in alert)
async function showUnreadFor(receiverId){
  try {
    const res = await doFetch(`/api/chat/unread/${receiverId}`);
    const list = await res.json();
    if(!list.length) return showToast('No unread messages');
    const summary = list.map(m => `${m.sname||m.sender?.name||'User'}: ${m.content}`).join('\n---\n');
    alert('Unread messages:\n\n' + summary);
  } catch(e){
     showToast('Failed to fetch unread'); 
  }
}

// ---------- Event Bindings ----------
refreshBtn.addEventListener('click', loadPeople);
searchBtn.addEventListener('click', findPersonById);
sendBtn.addEventListener('click', sendMessage);
openChatByIds.addEventListener('click', () => {
  openChatByTwoIds(openUser1.value, openUser2.value);
});

unreadOverviewBtn.addEventListener('click', () => {
  const rid = receiverSelect.value;
  if(!rid) return showToast('Select a receiver first');
  showUnreadFor(rid);
});

// init
loadPeople();