document.addEventListener('DOMContentLoaded', () => {
    const exitBtn = document.getElementById('exit-btn');
    if (exitBtn) {
        exitBtn.addEventListener('click', () => {
            window.location.href = 'index.html';
        });
    }

    const chatMessages = document.getElementById('chat-messages');
    const chatInput = document.getElementById('chat-input');
    const sendBtn = document.getElementById('send-btn');
    const languageSelector = document.getElementById('language-selector');
    const suggestionChips = document.querySelectorAll('.chip');
    const quickLinks = document.querySelectorAll('.chat-sidebar nav a');
    
    let currentLanguage = 'EN';
    let isTyping = false;
    let currentStadiumId = null;

    // Fetch the stadium so we can pass it to the chat request
    async function fetchStadiums() {
        try {
            const res = await fetch('/api/v1/stadiums', { headers: window.appAuth?.getAuthHeaders() || {} });
            if (res.ok) {
                const stadiums = await res.json();
                if (stadiums && stadiums.length > 0) {
                    currentStadiumId = stadiums[0].id;
                }
            }
        } catch (error) {
            console.error('Failed to fetch stadiums', error);
        }
    }
    fetchStadiums();

    // Scroll to bottom
    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    languageSelector.addEventListener('change', (e) => {
        currentLanguage = e.target.value;
        // In a real app, we might want to notify the backend or switch language context
    });

    // Handle suggestion chips
    suggestionChips.forEach(chip => {
        chip.addEventListener('click', () => {
            chatInput.value = chip.textContent;
            sendMessage();
        });
    });

    // Handle quick links
    quickLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            chatInput.value = link.textContent.trim();
            sendMessage();
        });
    });

    // Handle send button
    sendBtn.addEventListener('click', sendMessage);

    // Handle Enter key
    chatInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    async function sendMessage() {
        const text = chatInput.value.trim();
        if (!text || isTyping) return;

        // Add User Message
        appendMessage(text, 'user');
        chatInput.value = '';
        
        // Show typing indicator
        showTypingIndicator();
        
        try {
            // API Call
            const res = await fetch('/api/v1/assistant/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    query: text,
                    language: currentLanguage,
                    stadiumId: currentStadiumId
                })
            });

            removeTypingIndicator();

            if (!res.ok) {
                const text = await res.text();
                throw new Error(`API Error: ${res.status} ${text}`);
            }
            const data = await res.json();
            
            appendMessage(data.answer || data.reply || data.message || "Sorry, I couldn't understand that.", 'bot');
        } catch (error) {
            console.error('Chat error:', error);
            removeTypingIndicator();
            appendMessage(`Error: ${error.message}. Please try again.`, 'bot');
        }
    }

    function appendMessage(text, sender) {
        const bubble = document.createElement('div');
        bubble.className = `chat-bubble chat-bubble-${sender}`;
        
        const timeString = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        
        bubble.innerHTML = `
            <div>${escapeHTML(text)}</div>
            <div class="chat-timestamp">${timeString}</div>
        `;
        
        chatMessages.appendChild(bubble);
        scrollToBottom();
    }

    function showTypingIndicator() {
        isTyping = true;
        const indicator = document.createElement('div');
        indicator.id = 'typing-indicator';
        indicator.className = 'typing-indicator';
        indicator.innerHTML = `
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
        `;
        chatMessages.appendChild(indicator);
        scrollToBottom();
    }

    function removeTypingIndicator() {
        isTyping = false;
        const indicator = document.getElementById('typing-indicator');
        if (indicator) {
            indicator.remove();
        }
    }

    function escapeHTML(str) {
        return str.replace(/[&<>'"]/g, 
            tag => ({
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                "'": '&#39;',
                '"': '&quot;'
            }[tag])
        );
    }
});
