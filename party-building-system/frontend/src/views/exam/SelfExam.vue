<template>
  <div id="app" class="container">
    <div class="header">
      <h1>在线自测</h1>
      <p v-if="ready" class="header-sub">每次随机抽题；提交后成绩与正确率会写入参与记录（含管理员自测）。</p>
    </div>

    <div v-if="!ready" v-loading="true" class="loading-box" element-loading-text="加载题目中…"/>

    <template v-else-if="questions.length">
      <div class="question-card" style="height: auto;">
        <el-divider content-position="right">
          <h2>第 {{ currentIndex + 1 }} 题 / 共 {{ questions.length }} 题</h2>
        </el-divider>
        <div style="margin-bottom: 60px; margin-top: 20px">
          <p class="question-text" style="margin-top: 40px">{{ questions[currentIndex].text }}</p>
        </div>
        <div v-if="questions[currentIndex].type === 'single'" class="options">
          <el-radio-group v-model="userAnswers[currentIndex][0]">
            <el-radio
                v-for="(option, index) in questions[currentIndex].options"
                :key="index"
                :disabled="done"
                :label="option"
                style="display: flex"
            >
              <div style="margin-bottom: 10px">{{ option }}</div>
            </el-radio>
          </el-radio-group>
          <div v-if="done">
            <p><el-tag type="success">正确答案</el-tag></p>
            <p>{{ questions[currentIndex].answer }}</p>
            <p><el-tag type="info">题目解析</el-tag></p>
            <p>{{ questions[currentIndex].anal }}</p>
          </div>
        </div>
        <div v-else-if="questions[currentIndex].type === 'fill-in'" class="options">
          <div v-for="(blank, index) in questions[currentIndex].blanks" :key="index">
            <p>空 {{ index + 1 }}</p>
            <el-input
                v-model="userAnswers[currentIndex][index]"
                :disabled="done"
                placeholder="请输入答案"
                style="margin-bottom: 10px"
            />
          </div>
          <div v-if="done">
            <p><el-tag type="success">正确答案</el-tag></p>
            <p>{{ questions[currentIndex].answer }}</p>
            <p><el-tag type="info">题目解析</el-tag></p>
            <p>{{ questions[currentIndex].anal }}</p>
          </div>
        </div>
      </div>

      <div class="controls">
        <el-button :disabled="currentIndex === 0" type="primary" @click="prevQuestion">上一题</el-button>
        <el-button v-if="currentIndex < questions.length - 1" type="success" @click="nextQuestion">下一题</el-button>
        <template v-if="currentIndex === questions.length - 1">
          <el-button v-if="!done" type="warning" @click="submitAnswers">提交</el-button>
          <el-button v-if="done" type="warning" @click="returnHome">返回</el-button>
        </template>
      </div>

      <div v-if="showResult" class="result">
        <h2>答题完成！</h2>
        <p>您的得分是：<strong>{{ score }}</strong> 分</p>
        <p v-if="resultStats && resultStats.total > 0" class="result-sub">
          答对 {{ resultStats.correct }} / {{ resultStats.total }} 题，正确率 {{ resultStats.rate }}%
        </p>
        <p v-if="participationSaved === false" class="result-warn">本次成绩未能写入服务器，仅作本地展示。</p>
      </div>
    </template>

    <el-empty v-else description="未能加载题目，请稍后重试"/>
  </div>
</template>

<script>
import request from '@/utils/request';
import devLog from '@/utils/devLog';

function isResultOk(res) {
  return res && (res.code === '200' || res.code === 200);
}

export default {
  name: 'SelfExam',
  data() {
    return {
      ready: false,
      done: false,
      selfExamPaperId: null,
      questions: [],
      userAnswers: [],
      currentIndex: 0,
      showResult: false,
      score: 0,
      resultStats: null,
      participationSaved: null,
      user: JSON.parse(localStorage.getItem('current-user') || '{}'),
    };
  },
  created() {
    this.bootstrap().catch((e) => {
      devLog(e);
      this.$message.error(e.message || '加载失败');
      this.ready = true;
    });
  },
  watch: {
    questions: {
      handler(newVal, oldVal) {
        const n = (newVal && newVal.length) || 0;
        const o = (oldVal && oldVal.length) || 0;
        if (n !== o) {
          this.updateAnswer();
        }
      },
    },
  },
  methods: {
    returnHome() {
      this.$router.push({ path: '/home' });
    },
    getRandomElements(arr, num) {
      const shuffled = arr.slice();
      for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        const t = shuffled[i];
        shuffled[i] = shuffled[j];
        shuffled[j] = t;
      }
      return shuffled.slice(0, num);
    },
    _normalizeTfAnswer(raw) {
      const s = raw == null ? '' : String(raw).trim();
      if (s === '1' || /^true$/i.test(s) || /^t$/i.test(s) || s === '正确') return '正确';
      if (s === '0' || /^false$/i.test(s) || /^f$/i.test(s) || s === '错误') return '错误';
      return s;
    },
    async bootstrap() {
      const paperRes = await request.get('/papers/self-exam');
      if (!isResultOk(paperRes) || !paperRes.data) {
        throw new Error((paperRes && paperRes.msg) || '无法获取自测试卷');
      }
      const pid = paperRes.data.test_paper_id;
      if (pid == null) {
        throw new Error('自测试卷编号无效');
      }
      this.selfExamPaperId = pid;

      const built = [];

      const tfRes = await request.get('/questions/tf');
      const tfPool = Array.isArray(tfRes.data) ? tfRes.data : [];
      this.getRandomElements(tfPool, 3).forEach((q) => {
        built.push({
          text: q.question_description,
          type: 'single',
          options: ['正确', '错误'],
          answer: this._normalizeTfAnswer(q.correct_answer),
          anal: q.question_analysis || '',
        });
      });

      const mcRes = await request.get('/questions/mc');
      const mcPool = Array.isArray(mcRes.data) ? mcRes.data : [];
      await Promise.all(
          this.getRandomElements(mcPool, 4).map(async (q) => {
            const optRes = await request.get('/options/in_question/' + q.multiple_choice_question_id);
            const question = {
              text: q.question_description,
              type: 'single',
              options: [],
              answer: q.correct_answer,
              anal: q.question_analysis || '',
            };
            const options_data = Array.isArray(optRes.data) ? optRes.data : [];
            options_data.forEach((op) => {
              question.options.push(op.option_description);
              if (q.correct_answer === op.order_in_question) {
                question.answer = op.option_description;
              }
            });
            built.push(question);
          })
      );

      const fiRes = await request.get('/questions/fi');
      const fiPool = Array.isArray(fiRes.data) ? fiRes.data : [];
      this.getRandomElements(fiPool, 3).forEach((q) => {
        built.push({
          text: q.question_description,
          type: 'fill-in',
          blanks: [''],
          answer: q.correct_answer,
          anal: q.question_analysis || '',
        });
      });

      this.questions = built;
      this.ready = true;
      this.$nextTick(() => this.updateAnswer());
    },
    updateAnswer() {
      this.userAnswers = this.questions.map((question) => {
        if (question.type === 'single') {
          return [''];
        }
        if (question.type === 'multiple') {
          return [];
        }
        if (question.type === 'fill-in') {
          return new Array(question.blanks.length).fill('');
        }
        return [''];
      });
    },
    prevQuestion() {
      if (this.currentIndex > 0) {
        this.currentIndex -= 1;
      }
    },
    nextQuestion() {
      if (this.currentIndex < this.questions.length - 1) {
        this.currentIndex += 1;
      }
    },
    _resolveUserId() {
      const u = this.user;
      if (u.uid != null) return u.uid;
      if (u.user_id != null) return u.user_id;
      if (u.id != null) return u.id;
      return null;
    },
    _flattenAnswersForRecord() {
      return this.userAnswers.map((slot) => {
        if (Array.isArray(slot)) {
          return slot.map((x) => (x == null ? '' : String(x))).join(',');
        }
        return slot == null ? '' : String(slot);
      });
    },
    _buildTestRecordWithStats(correct, total, score) {
      const body = this._flattenAnswersForRecord().join('#');
      return body + '#__END__STATS:correct=' + correct + '&total=' + total + '&score=' + score;
    },
    getFormattedDate() {
      const currentDate = new Date();
      const year = currentDate.getFullYear();
      const month = (currentDate.getMonth() + 1).toString().padStart(2, '0');
      const day = currentDate.getDate().toString().padStart(2, '0');
      const hours = currentDate.getHours().toString().padStart(2, '0');
      const minutes = currentDate.getMinutes().toString().padStart(2, '0');
      const seconds = currentDate.getSeconds().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    _answerMatches(question, userSlot) {
      const expected = question.answer;
      if (question.type === 'fill-in') {
        const u = userSlot && userSlot[0] != null ? String(userSlot[0]).trim() : '';
        const e = expected == null ? '' : String(expected).trim();
        return u === e;
      }
      const u = userSlot && userSlot[0] != null ? String(userSlot[0]).trim() : '';
      const e = expected == null ? '' : String(expected).trim();
      return u === e;
    },
    submitAnswers() {
      const total = this.questions.length;
      let correct = 0;
      for (let i = 0; i < total; i++) {
        if (this._answerMatches(this.questions[i], this.userAnswers[i])) {
          correct++;
        }
      }
      this.score = correct * 10;
      const rate = total > 0 ? Math.round((correct / total) * 100) : 0;
      this.resultStats = { correct, total, rate };

      const uid = this._resolveUserId();
      if (uid == null || !Number.isFinite(uid) || this.selfExamPaperId == null) {
        this.participationSaved = false;
        this.$message.warning('未识别到用户或自测试卷，成绩未保存');
      } else {
        const payload = {
          user_id: uid,
          test_paper_id: this.selfExamPaperId,
          participation_time: this.getFormattedDate(),
          test_record: this._buildTestRecordWithStats(correct, total, this.score),
        };
        devLog('self-exam participation', payload);
        request
            .post('/participation/add', payload)
            .then((res) => {
              const ok = res && (res.code === '200' || res.code === 200);
              this.participationSaved = ok;
              if (!ok) {
                this.$message.warning((res && res.msg) || '成绩未保存到服务器');
              } else {
                this.$message.success((res && res.msg) || '自测成绩已保存');
              }
            })
            .catch(() => {
              this.participationSaved = false;
              this.$message.error('成绩提交失败，请检查网络后重试');
            });
      }

      this.done = true;
      this.showResult = true;
    },
  },
};
</script>

<style>
.loading-box {
  min-height: 200px;
  width: 100%;
  max-width: 800px;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-family: Arial, sans-serif;
  background: #f5f7fa;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header {
  margin-bottom: 20px;
  text-align: center;
}

.header h1 {
  font-size: 2em;
  color: #409eff;
}

.header-sub {
  margin: 8px 0 0;
  font-size: 13px;
  color: #909399;
}

.question-card {
  width: 100%;
  background: #ffffff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.question-text {
  font-size: 1.2em;
  margin-bottom: 20px;
}

.options {
  margin-bottom: 20px;
}

.controls {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.result {
  text-align: center;
  margin-top: 20px;
}

.result h2 {
  font-size: 1.5em;
  color: #67c23a;
}

.result-sub {
  font-size: 15px;
  color: #606266;
  margin-top: 8px;
}

.result-warn {
  font-size: 13px;
  color: #e6a23c;
  margin-top: 10px;
}
</style>
