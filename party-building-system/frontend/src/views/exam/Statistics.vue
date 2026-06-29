<template>
  <div id="app" v-loading="loading" class="container">
    <!-- 页面标题 -->
    <div class="header">
      <h1>测评情况查看</h1>
      <el-button type="text" icon="el-icon-refresh" @click="loadStatistics">刷新数据</el-button>
    </div>

    <el-alert
        v-if="!loading && questions.length && participantTotal === 0"
        title="暂无答卷记录"
        type="info"
        :closable="false"
        show-icon
        style="width: 100%; margin-bottom: 12px"
        description="可能尚未有人提交，或接口异常。项目使用表 participation_o（与 init_sqls/xxfb_mysql.sql 一致）；若曾误建 participation2，请把数据迁回 participation_o。"
    />

    <!-- 题目展示 -->
    <el-scrollbar class="custom-scrollbar" style="height: 80%; overflow-y: auto;">
    <div v-for="(question, index) in questions" :key="index" class="question-card">
      <!-- 题目标题 -->
      <div style="margin-bottom: 20px">
        <h2>题目 {{ index + 1 }}: {{ question.text }}</h2>
        <el-tag v-if="question.type === 'single'" type="success">单选题</el-tag>
        <el-tag v-if="question.type === 'fill-in'" type="warning">填空题</el-tag>
      </div>

      <!-- 单选题和判断题 -->
      <div v-if="question.type === 'single'" class="answer-container">
        <div v-for="(option, idx) in question.options" :key="idx" class="answer-item">
          <!-- 左边文字内容 -->
          <div class="answer-left">
            {{ option.text }}（{{ option.count }}人选择）
            <!-- 点击选项时显示选择该选项的用户 -->
            <el-button @click="showUsers(option)" size="mini">查看选择用户</el-button>
          </div>
          <!-- 中间进度条 -->
          <div class="answer-middle">
            <el-progress
                :percentage="optionPercent(option.count)"
                :text-inside="false"
                stroke-width="10"
            ></el-progress>
          </div>
          <!-- 右边留空 -->
          <div class="answer-right"></div>
        </div>
      </div>

      <!-- 填空题的显示 -->
      <div v-else-if="question.type === 'fill-in'" class="answer-container">
        <!-- 明细功能 -->
        <el-collapse v-model="question.expanded">
          <el-collapse-item title="查看明细" :name="index.toString()">
            <div v-for="(user, idx) in question.detailedAnswers" :key="idx">
              <p>{{ user.user_name }}: {{ user.user_answer }}</p>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
    </el-scrollbar>

    <!-- 用户选择列表浮现窗口 -->
    <el-dialog :visible.sync="showUserDialog" title="选择该选项的用户">
      <el-table :data="selectedUsers" style="width: 100%">
        <el-table-column label="用户名" prop="user_name"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showUserDialog = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>


<script>
import request from "@/utils/request";

export default {
  name: "TeacherView",
  data() {
    return {
      loading: false,
      paper: {},
      questions: [],
      AlluserAnswers: [],
      total: 0,
      showUserDialog: false, // 控制用户选择列表浮现
      selectedUsers: [], // 存储选择该选项的用户列表
    };
  },
  created() {
    this.loadStatistics();
  },
  watch: {
    '$route.query.id'() {
      this.loadStatistics();
    },
  },
  computed: {
    /** 参与人次（用于选项占比，避免 total 为 0 时出现 NaN） */
    participantTotal() {
      const n = Number(this.total);
      return Number.isFinite(n) && n > 0 ? n : 0;
    },
  },
  methods: {
    optionPercent(count) {
      const c = Number(count);
      const t = this.participantTotal;
      if (!Number.isFinite(c) || t <= 0) {
        return 0;
      }
      const p = Math.round((c / t) * 100);
      return Number.isFinite(p) ? Math.min(100, Math.max(0, p)) : 0;
    },
    async loadStatistics() {
      const id = parseInt(this.$route.query.id, 10);
      if (!Number.isFinite(id)) {
        return;
      }
      this.loading = true;
      this.questions = [];
      this.AlluserAnswers = [];
      this.total = 0;
      this.paper = {};

      try {
        const papersRes = await request.get('/papers');
        const list = Array.isArray(papersRes.data) ? papersRes.data : [];
        const papers = list.slice().sort((a, b) => new Date(b.create_date) - new Date(a.create_date));
        const paper = papers.find((p) => p.test_paper_id === id);
        if (!paper) {
          this.$message.warning('未找到该试卷');
          return;
        }
        this.paper = paper;

        const tfRes = await request.get('/questions/tf/in_paper/' + this.paper.test_paper_id);
        const tfList = Array.isArray(tfRes.data) ? tfRes.data : [];
        tfList.forEach((q) => {
          this.questions.push({
            text: q.question_description,
            type: 'single',
            options: [
              { text: '正确', count: 0, users: [] },
              { text: '错误', count: 0, users: [] },
            ],
            answer: q.correct_answer,
            rate: 0,
            order: q.question_order_in_paper,
            expanded: false,
            detailedAnswers: [],
          });
        });

        const mcRes = await request.get('/questions/mc/in_paper/' + this.paper.test_paper_id);
        const mcList = Array.isArray(mcRes.data) ? mcRes.data : [];
        await Promise.all(
            mcList.map(async (q) => {
              const optionsRes = await request.get('/options/in_question/' + q.multiple_choice_question_id);
              const question = {
                text: q.question_description,
                type: 'single',
                options: [],
                answer: q.correct_answer,
                rate: 0,
                order: q.question_order_in_paper,
                expanded: false,
                detailedAnswers: [],
              };
              const opts = Array.isArray(optionsRes.data) ? optionsRes.data : [];
              opts.forEach((op) => {
                question.options.push({
                  text: op.option_description,
                  count: 0,
                  users: [],
                });
                if (q.correct_answer === op.order_in_question) {
                  question.answer = op.option_description;
                }
              });
              this.questions.push(question);
            })
        );

        const fiRes = await request.get('/questions/fi/in_paper/' + this.paper.test_paper_id);
        const fiList = Array.isArray(fiRes.data) ? fiRes.data : [];
        fiList.forEach((q) => {
          this.questions.push({
            text: q.question_description,
            type: 'fill-in',
            blanks: [''],
            answer: q.correct_answer,
            rate: 0,
            order: q.question_order_in_paper,
            expanded: false,
            detailedAnswers: q.answers || [],
          });
        });

        this.questions.sort((a, b) => (Number(a.order) || 0) - (Number(b.order) || 0));

        const participationRes = await request.get('/participation/paperwithname/' + this.paper.test_paper_id);
        const participation = Array.isArray(participationRes.data) ? participationRes.data : [];
        participation.forEach((q) => {
          this.total += 1;
          this.AlluserAnswers.push({
            userid: q.user_id,
            testrecord: q.test_record,
            username: q.user_name,
          });
        });
        this.countAnswers();
      } catch (error) {
        console.error('Error fetching data:', error);
        this.$message.error('加载统计数据失败');
      } finally {
        this.loading = false;
      }
    },
    /** 去掉答卷末尾的正确率元数据，再按 # 拆成与题目顺序一致的答案数组 */
    participationAnswersArray(testrecord) {
      if (!testrecord || typeof testrecord !== 'string') return [];
      const marker = '#__END__STATS:';
      const idx = testrecord.indexOf(marker);
      const raw = idx >= 0 ? testrecord.substring(0, idx) : testrecord;
      return raw.split('#');
    },
    // 显示选择该选项的用户
    showUsers(option) {
      this.selectedUsers = [];
      this.AlluserAnswers.forEach(answer => {
        const answers = this.participationAnswersArray(answer.testrecord);
        if (answers.includes(option.text)) {
          this.selectedUsers.push({ user_name: answer.username }); // 确保推送选项的用户
        }
      });
      this.showUserDialog = true;
    },

    displayFillInUsers() {
      this.questions.forEach((question) => {
        if (question.type === 'fill-in') {
          question.detailedAnswers = [];
          this.AlluserAnswers.forEach(answer => {
            const answers = this.participationAnswersArray(answer.testrecord);
            const questionIndex = this.questions.indexOf(question); // 获取该题在 questions 数组中的索引
            if (answers[questionIndex] !== undefined) {
              // 保存该题作答的用户名和答案
              question.detailedAnswers.push({
                user_name: answer.username,
                user_answer: answers[questionIndex], // 显示作答用户的答案
              });
            }
          });
        }
      });
    },
    // 统计单选题的选项情况
    countSingleChoiceAnswers() {
      this.AlluserAnswers.forEach(userAnswer => {
        const answers = this.participationAnswersArray(userAnswer.testrecord);
        this.questions.forEach((question, index) => {
          if (question.type === 'single' && answers[index]) {
            question.options.forEach(option => {
              if (answers[index] === option.text) {
                option.count += 1;
                if (!option.users.includes(userAnswer.username)) {  // 确保没有重复用户
                  option.users.push(userAnswer.username);
                }
              }
            });
          }
        });
      });
    },

    // 解析所有用户的答题记录并统计
    countAnswers() {
      this.countSingleChoiceAnswers();
      this.displayFillInUsers();
    },
  },
};
</script>



<style scoped>
/* 页面样式 */
.container {
  max-width: 900px; /* 保持宽度 */
  height: 90vh; /* 高度设置为占满整个视口 */
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  font-family: Arial, sans-serif;
  background: #f5f7fa;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow-x: hidden; /* 去掉水平滚动条 */
}

.custom-scrollbar {
  height: 80%; /* 使 el-scrollbar 占满父容器的高度 */
  overflow-y: auto; /* 只允许竖直滚动 */
}

.header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.header h1 {
  font-size: 2em;
  color: #409eff;
  text-align: center;
}

.question-card {
  width: 100%;
  background: #ffffff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.question-card h2 {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.answer-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.answer-item {
  display: flex;
  align-items: center;
}

.answer-left {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.answer-middle {
  flex: 2;
  margin: 0 10px;
}

.answer-right {
  flex: 1;
}
</style>
