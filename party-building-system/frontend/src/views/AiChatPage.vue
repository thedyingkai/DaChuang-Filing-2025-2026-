<template>
  <div class="ai-chat-page">
    <!-- 顶栏 -->
    <div class="chat-topbar">
      <el-button class="back-btn" type="text" icon="el-icon-arrow-left" @click="$router.push('/home')">
        返回首页
      </el-button>
      <span class="topbar-title">
        <i class="el-icon-cpu"></i> AI 助手
      </span>
      <span class="topbar-hint">
        由 Dify 提供智能服务
      </span>
      <!-- 操作按钮组 -->
      <el-button
        class="action-btn"
        type="text"
        icon="el-icon-plus"
        @click="startNewChat"
      >
        新对话
      </el-button>
    </div>

    <!-- 消息列表区 -->
    <div class="chat-body" ref="chatBody" @scroll="onScroll">
      <!-- 欢迎页（无消息时） -->
      <div v-if="messages.length === 0" class="welcome-panel">
        <div class="welcome-icon">
          <i class="el-icon-cpu"></i>
        </div>
        <h2>你好，我是党务 AI 助手</h2>
        <p>我可以帮您：</p>
        <div class="quick-actions">
          <div
            v-for="item in quickActions"
            :key="item.text"
            class="quick-action-item"
            @click="sendQuickAction(item.text)"
          >
            {{ item.label }}
          </div>
        </div>
      </div>

      <!-- 历史消息 -->
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        class="chat-message"
        :class="msg.role === 'user' ? 'msg-user' : 'msg-ai'"
      >
        <!-- AI 头像 -->
        <div v-if="msg.role === 'ai'" class="msg-avatar msg-avatar-ai">
          <i class="el-icon-cpu"></i>
        </div>

        <!-- 消息气泡 -->
        <div class="msg-bubble">
          <div class="msg-content" v-html="renderMarkdown(msg.content)"></div>
          <div class="msg-meta">
            <span class="msg-time">{{ formatTime(msg.time) }}</span>
            <!-- 流式输出进行中指示 -->
            <i v-if="msg.streaming" class="el-icon-loading streaming-dot"></i>
          </div>
        </div>

        <!-- 用户头像 -->
        <div v-if="msg.role === 'user'" class="msg-avatar msg-avatar-user">
          <i class="el-icon-user"></i>
        </div>
      </div>

      <!-- 打字指示器 -->
      <div v-if="aiTyping && !isStreaming" class="chat-message msg-ai">
        <div class="msg-avatar msg-avatar-ai">
          <i class="el-icon-cpu"></i>
        </div>
        <div class="msg-bubble typing-bubble">
          <span class="typing-dot"></span>
          <span class="typing-dot"></span>
          <span class="typing-dot"></span>
        </div>
      </div>

      <!-- 回到底部按钮 -->
      <div
        v-if="showScrollToBottom"
        class="scroll-to-bottom"
        @click="scrollToBottom(true)"
      >
        <i class="el-icon-bottom"></i>
      </div>
    </div>

    <!-- 输入区 -->
    <div class="chat-input-bar">
      <!-- 语音模式指示 -->
      <div v-if="voiceActive" class="voice-indicator">
        <i class="el-icon-microphone voice-pulse"></i>
        <span>正在聆听… 请说话</span>
        <el-button size="mini" type="text" @click="stopVoice">取消</el-button>
      </div>

      <!-- 文本输入 -->
      <div class="input-row">
        <el-input
          ref="inputBox"
          v-model="inputText"
          class="text-input"
          type="textarea"
          :rows="1"
          :autosize="{ minRows: 1, maxRows: 4 }"
          placeholder="输入您的问题或指令，按 Enter 发送"
          :disabled="aiTyping || voiceActive"
          @keydown.enter.exact.prevent="sendMessage"
        />
        <div class="input-actions">
          <!-- 语音按钮 -->
          <el-button
            class="voice-btn"
            :type="voiceActive ? 'danger' : 'default'"
            :icon="voiceActive ? 'el-icon-close' : 'el-icon-microphone'"
            circle
            size="small"
            :title="voiceSupported ? '语音输入 (仅支持 Chrome/Edge)' : '浏览器不支持语音识别'"
            @click="toggleVoice"
          />
          <!-- 发送按钮 -->
          <el-button
            class="send-btn"
            type="primary"
            icon="el-icon-promotion"
            :disabled="!inputText.trim() || aiTyping"
            @click="sendMessage"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'AiChatPage',
  data() {
    return {
      messages: [],
      inputText: '',
      conversationId: null, // Dify 多轮对话 ID
      aiTyping: false,
      isStreaming: false, // 是否正在接收 SSE 流
      showScrollToBottom: false,
      voiceActive: false,
      voiceSupported: false,
      // SSE 取消失控
      abortController: null,
      // 快捷指令
      quickActions: [
        { label: '📝 发布支部活动通知', text: '我想发布一条支部活动通知' },
        { label: '🔍 上个月主题党日活动', text: '上个月我们支部举办了几次主题党日活动？' },
        { label: '👥 今年发展党员人数', text: '今年发展党员多少人？' },
        { label: '📊 查看活动统计', text: '帮我统计一下最近的活动情况' },
        { label: '✅ 检查文本规范性', text: '帮我审核以下文本是否合规：' },
      ],
    };
  },
  mounted() {
    this.voiceSupported =
      'webkitSpeechRecognition' in window || 'SpeechRecognition' in window;
    this.loadChatHistory();
  },
  beforeUnmount() {
    this.abortSSE();
  },
  watch: {
    aiTyping(val) {
      if (!val) this.$nextTick(() => this.scrollToBottom());
    },
  },
  methods: {
    // ============ 消息管理 ============

    /** 加载本地存储的对话历史 */
    loadChatHistory() {
      try {
        const saved = localStorage.getItem('ai_chat_history');
        if (saved) {
          const data = JSON.parse(saved);
          this.messages = data.messages || [];
          this.conversationId = data.conversationId || null;
        }
      } catch (e) {
        // ignore
      }
      this.$nextTick(() => this.scrollToBottom(false));
    },

    /** 持久化对话历史 */
    saveChatHistory() {
      try {
        const data = {
          messages: this.messages.slice(-50), // 最多保留最近 50 条
          conversationId: this.conversationId,
        };
        localStorage.setItem('ai_chat_history', JSON.stringify(data));
      } catch (e) {
        // ignore
      }
    },

    /** 开始新对话 */
    startNewChat() {
      this.messages = [];
      this.conversationId = null;
      this.inputText = '';
      this.abortSSE();
      localStorage.removeItem('ai_chat_history');
    },

    /** 添加快捷指令消息 */
    sendQuickAction(text) {
      this.inputText = text;
      this.sendMessage();
    },

    /** 发送用户消息 */
    async sendMessage() {
      const text = this.inputText.trim();
      if (!text || this.aiTyping) return;
      this.inputText = '';

      // 添加用户消息
      const userMsg = {
        role: 'user',
        content: text,
        time: Date.now(),
      };
      this.messages.push(userMsg);
      this.$nextTick(() => this.scrollToBottom(true));

      // 调用后端代理
      this.aiTyping = true;
      this.isStreaming = false;

      try {
        // 从 current-user 中读取 token（与 request.js 拦截器保持一致）
        let token = null;
        try {
          const user = JSON.parse(localStorage.getItem('current-user'));
          token = user?.token || null;
        } catch (e) {
          token = null;
        }
        const headers = {
          Authorization: token ? `Bearer ${token}` : '',
        };

        const payload = {
          query: text,
        };
        if (this.conversationId) {
          payload.conversationId = this.conversationId;
        }

        // 使用 fetch + ReadableStream 处理 SSE 流
        this.abortController = new AbortController();
        const response = await fetch('/api/dify/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...headers,
          },
          body: JSON.stringify(payload),
          signal: this.abortController.signal,
          credentials: 'same-origin',
        });

        if (!response.ok) {
          const errData = await response.json().catch(() => ({}));
          throw new Error(errData.error || `请求失败 (${response.status})`);
        }

        // 处理 SSE 流
        this.isStreaming = true;
        const aiMsg = {
          role: 'ai',
          content: '',
          time: Date.now(),
          streaming: true,
        };
        this.messages.push(aiMsg);
        const aiMsgIndex = this.messages.length - 1;

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        // eslint-disable-next-line no-constant-condition
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          buffer += decoder.decode(value, { stream: true });
          const lines = buffer.split('\n');
          buffer = lines.pop() || '';

          for (const line of lines) {
            if (line.startsWith('data: ')) {
              const dataStr = line.slice(6).trim();
              if (!dataStr) continue;
              try {
                const event = JSON.parse(dataStr);
                if (event.event === 'message' || event.event === 'agent_message') {
                  const chunk = event.answer || '';
                  this.messages[aiMsgIndex].content += chunk;
                  this.$nextTick(() => this.scrollToBottom(true));
                } else if (event.event === 'message_end') {
                  this.conversationId = event.conversation_id || null;
                } else if (event.event === 'error') {
                  throw new Error(event.message || 'Dify 处理失败');
                }
              } catch (parseErr) {
                // 忽略解析错误
              }
            }
          }
        }

        this.messages[aiMsgIndex].streaming = false;
      } catch (err) {
        if (err.name === 'AbortError') {
          // 用户主动取消，不显示错误
        } else {
          this.messages.push({
            role: 'ai',
            content: `❌ 抱歉，请求失败：${err.message || '网络错误，请稍后重试'}`,
            time: Date.now(),
            streaming: false,
            error: true,
          });
        }
      } finally {
        this.aiTyping = false;
        this.isStreaming = false;
        this.abortController = null;
        this.saveChatHistory();
        this.$nextTick(() => this.scrollToBottom(true));
      }
    },

    /** 中止 SSE 流 */
    abortSSE() {
      if (this.abortController) {
        this.abortController.abort();
        this.abortController = null;
      }
    },

    // ============ 语音 ============

    toggleVoice() {
      if (this.voiceActive) {
        this.stopVoice();
      } else {
        this.startVoice();
      }
    },

    startVoice() {
      if (!this.voiceSupported) {
        this.$message.warning('当前浏览器不支持语音识别，请使用 Chrome 或 Edge');
        return;
      }
      this.voiceActive = true;
      const SpeechRecognition =
        window.SpeechRecognition || window.webkitSpeechRecognition;
      this.recognition = new SpeechRecognition();
      this.recognition.lang = 'zh-CN';
      this.recognition.interimResults = false;
      this.recognition.continuous = false;

      this.recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        this.inputText = transcript;
        this.stopVoice();
        // 语音输入后自动发送
        this.$nextTick(() => {
          if (this.inputText.trim()) this.sendMessage();
        });
      };

      this.recognition.onerror = (event) => {
        this.$message.error(`语音识别错误: ${event.error}`);
        this.stopVoice();
      };

      this.recognition.start();
    },

    stopVoice() {
      if (this.recognition) {
        this.recognition.abort();
        this.recognition = null;
      }
      this.voiceActive = false;
    },

    // ============ 滚动 ============

    scrollToBottom(smooth) {
      const el = this.$refs.chatBody;
      if (!el) return;
      if (smooth) {
        el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
      } else {
        el.scrollTop = el.scrollHeight;
      }
    },

    onScroll() {
      const el = this.$refs.chatBody;
      if (!el) return;
      const threshold = 100;
      this.showScrollToBottom =
        el.scrollHeight - el.scrollTop - el.clientHeight > threshold;
    },

    // ============ 工具 ============

    formatTime(ts) {
      const d = new Date(ts);
      const pad = (n) => String(n).padStart(2, '0');
      return `${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },

    /** 简单 Markdown 渲染（**加粗**、`代码`、换行） */
    renderMarkdown(text) {
      if (!text) return '';
      return text
        .replace(/&/g, '&')
        .replace(/</g, '<')
        .replace(/>/g, '>')
        .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
        .replace(/`(.+?)`/g, '<code>$1</code>')
        .replace(/\n/g, '<br>');
    },
  },
};
</script>

<style scoped>
/* ========== 页面容器 ========== */
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f6fa;
  overflow: hidden;
}

/* ========== 顶栏 ========== */
.chat-topbar {
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 48px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  gap: 12px;
}
.back-btn {
  color: #666;
  font-size: 14px;
  padding: 4px 8px;
}
.back-btn:hover {
  color: #d21300;
  background: #fde8e8;
}
.topbar-title {
  font-size: 15px;
  font-weight: 600;
  color: #d21300;
}
.topbar-title i {
  margin-right: 4px;
}
.topbar-hint {
  flex: 1;
  font-size: 12px;
  color: #bbb;
}
.action-btn {
  color: #666;
  font-size: 13px;
}

/* ========== 聊天中间区域 ========== */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  scroll-behavior: smooth;
  position: relative;
}
.chat-body::-webkit-scrollbar {
  width: 5px;
}
.chat-body::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

/* 欢迎面板 */
.welcome-panel {
  text-align: center;
  padding: 40px 16px 20px;
}
.welcome-icon {
  width: 64px;
  height: 64px;
  line-height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #d21300, #ff6b5b);
  border-radius: 50%;
  color: #fff;
  font-size: 30px;
}
.welcome-panel h2 {
  font-size: 18px;
  color: #333;
  margin: 0 0 8px;
}
.welcome-panel p {
  font-size: 14px;
  color: #999;
  margin: 0 0 20px;
}
.quick-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  max-width: 500px;
  margin: 0 auto;
}
.quick-action-item {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
}
.quick-action-item:hover {
  background: #fff5f5;
  border-color: #d21300;
  color: #d21300;
}

/* ========== 消息 ========== */
.chat-message {
  display: flex;
  margin-bottom: 16px;
}
.msg-user {
  flex-direction: row-reverse;
}
.msg-ai {
  flex-direction: row;
}
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.msg-avatar-ai {
  background: linear-gradient(135deg, #d21300, #ff6b5b);
  color: #fff;
  margin-right: 10px;
}
.msg-avatar-user {
  background: #e8e8e8;
  color: #666;
  margin-left: 10px;
}
.msg-bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 12px;
  position: relative;
  word-break: break-word;
}
.msg-user .msg-bubble {
  background: #d21300;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.msg-ai .msg-bubble {
  background: #fff;
  color: #333;
  border: 1px solid #e8e8e8;
  border-bottom-left-radius: 4px;
}
.msg-content {
  font-size: 14px;
  line-height: 1.6;
}
.msg-content :deep(code) {
  background: rgba(0,0,0,0.06);
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 12px;
}
.msg-user .msg-content :deep(code) {
  background: rgba(255,255,255,0.2);
}
.msg-content :deep(strong) {
  font-weight: 600;
}
.msg-meta {
  display: flex;
  align-items: center;
  margin-top: 4px;
  font-size: 11px;
  gap: 6px;
}
.msg-user .msg-meta {
  color: rgba(255,255,255,0.7);
  justify-content: flex-end;
}
.msg-ai .msg-meta {
  color: #bbb;
}
.streaming-dot {
  color: #d21300;
  font-size: 12px;
}

/* 打字指示器 */
.typing-bubble {
  min-width: 50px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 14px 16px;
}
.typing-dot {
  width: 6px;
  height: 6px;
  background: #bbb;
  border-radius: 50%;
  animation: typing-bounce 1.4s infinite ease-in-out both;
}
.typing-dot:nth-child(1) {
  animation-delay: -0.32s;
}
.typing-dot:nth-child(2) {
  animation-delay: -0.16s;
}
@keyframes typing-bounce {
  0%, 80%, 100% {
    transform: scale(0.6);
  }
  40% {
    transform: scale(1);
  }
}

/* 回到底部 */
.scroll-to-bottom {
  position: sticky;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  width: 36px;
  height: 36px;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  z-index: 5;
  color: #666;
  transition: all 0.2s;
}
.scroll-to-bottom:hover {
  background: #d21300;
  color: #fff;
  border-color: #d21300;
}

/* ========== 输入区 ========== */
.chat-input-bar {
  background: #fff;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px 14px;
  flex-shrink: 0;
}
.voice-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  margin-bottom: 8px;
  background: #fff5f5;
  border: 1px solid #ffd6d6;
  border-radius: 8px;
  font-size: 13px;
  color: #d21300;
}
.voice-pulse {
  animation: voice-pulse 1.2s ease-in-out infinite;
}
@keyframes voice-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}
.input-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}
.text-input {
  flex: 1;
}
.text-input :deep(textarea) {
  resize: none;
  border-radius: 8px;
}
.input-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}
.send-btn {
  background: #d21300;
  border-color: #d21300;
}
.send-btn:hover {
  background: #b51000;
  border-color: #b51000;
}
.voice-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  font-size: 14px;
}
</style>