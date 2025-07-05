// Chat Application
class ChatApp {
    constructor() {
        this.sessionId = document.getElementById('sessionId').value;
        this.stompClient = null;
        this.connected = false;
        
        this.initializeElements();
        this.setupEventListeners();
        this.connect();
        this.loadChatHistory();
        this.loadCvList();
    }
    
    initializeElements() {
        this.chatMessages = document.getElementById('chatMessages');
        this.messageInput = document.getElementById('messageInput');
        this.sendButton = document.getElementById('sendButton');
        this.fileInput = document.getElementById('fileInput');
        this.uploadProgress = document.getElementById('uploadProgress');
        this.cvList = document.getElementById('cvList');
    }
    
    setupEventListeners() {
        // Send message
        this.sendButton.addEventListener('click', () => this.sendMessage());
        this.messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.sendMessage();
        });
        
        // File upload
        this.fileInput.addEventListener('change', (e) => this.handleFileUpload(e));
        
        // Drag and drop
        const uploadZone = document.querySelector('.upload-zone');
        uploadZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadZone.classList.add('dragover');
        });
        
        uploadZone.addEventListener('dragleave', () => {
            uploadZone.classList.remove('dragover');
        });
        
        uploadZone.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadZone.classList.remove('dragover');
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                this.uploadFile(files[0]);
            }
        });
    }
    
    connect() {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.connected = true;
            
            // Subscribe to chat messages
            this.stompClient.subscribe(`/topic/chat/${this.sessionId}`, (message) => {
                const chatMessage = JSON.parse(message.body);
                this.displayMessage(chatMessage);
            });
            
            // Add user to chat
            this.stompClient.send('/app/chat.addUser', {}, JSON.stringify({
                sessionId: this.sessionId
            }));
        });
    }
    
    sendMessage() {
        const messageText = this.messageInput.value.trim();
        if (messageText && this.connected) {
            this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
                sessionId: this.sessionId,
                message: messageText
            }));
            
            // Display user message immediately
            this.displayMessage({
                senderType: 'USER',
                message: messageText,
                createdAt: new Date().toISOString()
            });
            
            this.messageInput.value = '';
        }
    }
    
    displayMessage(message) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message message-${message.senderType.toLowerCase()}`;
        
        const bubbleDiv = document.createElement('div');
        bubbleDiv.className = 'message-bubble';
        bubbleDiv.textContent = message.message;
        
        const timeDiv = document.createElement('div');
        timeDiv.className = 'message-time';
        timeDiv.textContent = this.formatTime(message.createdAt);
        
        messageDiv.appendChild(bubbleDiv);
        messageDiv.appendChild(timeDiv);
        
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }
    
    handleFileUpload(event) {
        const file = event.target.files[0];
        if (file) {
            this.uploadFile(file);
        }
    }
    
    uploadFile(file) {
        // Validate file
        const allowedTypes = ['application/pdf', 'application/msword', 
                             'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
        
        if (!allowedTypes.includes(file.type)) {
            this.showError('Please upload a PDF, DOC, or DOCX file.');
            return;
        }
        
        if (file.size > 10 * 1024 * 1024) { // 10MB
            this.showError('File size must be less than 10MB.');
            return;
        }
        
        // Show progress
        this.showProgress(0);
        
        const formData = new FormData();
        formData.append('file', file);
        
        fetch('/api/cv/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            this.hideProgress();
            
            if (data.success) {
                this.showSuccess(`File "${data.filename}" uploaded successfully!`);
                this.loadCvList();
                
                // Send notification to chat
                if (this.connected) {
                    this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
                        sessionId: this.sessionId,
                        message: `I've uploaded my CV: ${data.filename}`
                    }));
                }
            } else {
                this.showError(data.message);
            }
        })
        .catch(error => {
            this.hideProgress();
            this.showError('Error uploading file: ' + error.message);
        });
    }
    
    loadChatHistory() {
        fetch('/api/chat/history')
        .then(response => response.json())
        .then(messages => {
            messages.forEach(message => this.displayMessage(message));
        })
        .catch(error => console.error('Error loading chat history:', error));
    }
    
    loadCvList() {
        fetch('/api/cv/list')
        .then(response => response.json())
        .then(cvs => {
            this.displayCvList(cvs);
        })
        .catch(error => console.error('Error loading CV list:', error));
    }
    
    displayCvList(cvs) {
        this.cvList.innerHTML = '';
        
        if (cvs.length === 0) {
            this.cvList.innerHTML = '<p class="text-muted small">No CVs uploaded yet</p>';
            return;
        }
        
        cvs.forEach(cv => {
            const cvDiv = document.createElement('div');
            cvDiv.className = 'cv-item';
            
            cvDiv.innerHTML = `
                <div class="d-flex justify-content-between align-items-start">
                    <div class="flex-grow-1">
                        <h6 class="mb-1">${cv.originalFilename}</h6>
                        <small class="text-muted">${this.formatFileSize(cv.fileSize)}</small>
                    </div>
                    <span class="cv-status status-${cv.processingStatus.toLowerCase()}">
                        ${cv.processingStatus}
                    </span>
                </div>
                <div class="mt-2">
                    <small class="text-muted">${this.formatTime(cv.createdAt)}</small>
                </div>
            `;
            
            this.cvList.appendChild(cvDiv);
        });
    }
    
    showProgress(percent) {
        this.uploadProgress.classList.remove('d-none');
        const progressBar = this.uploadProgress.querySelector('.progress-bar');
        progressBar.style.width = percent + '%';
    }
    
    hideProgress() {
        this.uploadProgress.classList.add('d-none');
    }
    
    showSuccess(message) {
        this.showNotification(message, 'success');
    }
    
    showError(message) {
        this.showNotification(message, 'danger');
    }
    
    showNotification(message, type) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.insertBefore(alertDiv, document.body.firstChild);
        
        // Auto dismiss after 5 seconds
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }
    
    formatTime(timestamp) {
        const date = new Date(timestamp);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    
    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
    
    scrollToBottom() {
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }
}

// Initialize chat app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ChatApp();
});
