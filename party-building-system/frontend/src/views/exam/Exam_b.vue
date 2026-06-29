<template>
  <div id="app" class="container">
    <!-- 题目标题 -->
    <div class="header">
      <h1>{{ paper.paper_description || '在线测试' }}</h1>
      <p v-if="paper.test_paper_id != null" class="header-sub">试卷编号 {{ paper.test_paper_id }}</p>
    </div>
    <!--
    <div class="question-card">
      <el-collapse v-model="activeNames" @change="handleChange">
      <el-collapse-item title="试卷描述" >
        <div>{{paper.paper_description}}</div>
      </el-collapse-item>
      </el-collapse>
    </div>
    -->
    <!-- 答题卡区域 -->
    <div class="question-card" style="height: auto;">
      <!-- 显示当前题目 -->
      <div style="margin-bottom: 10px">
        <el-tag style="float: left; margin-right: 10px">
          {{ paper.paper_description }}
        </el-tag>
        <el-tag style="float: left;" type="warning">
          截至 {{ paper.deadline }}
        </el-tag>
      </div>
      <div style="height: 10px"></div>
      <el-divider content-position="right">
        <h2>第 {{ currentIndex + 1 }} 题 / 共 {{ questions.length }} 题</h2>
      </el-divider>

      <div style="margin-bottom: 60px; margin-top: 20px">
        <p class="question-text">{{ questions[currentIndex].text }}</p>
      </div>
      <!-- 答案选项 -->
      <!--      <div v-if="questions[currentIndex].type === 'single'" class="options">-->
      <!--        <el-radio-group v-model="userAnswers[currentIndex][0]">-->
      <!--          <el-radio-->
      <!--              style="display: flex"-->
      <!--              v-for="(option, index) in questions[currentIndex].options"-->
      <!--              :key="index"-->
      <!--              :label="option"-->
      <!--          >-->
      <!--            <div style="margin-bottom: 10px">{{ option }}</div>-->
      <!--          </el-radio>-->
      <!--        </el-radio-group>-->
      <!--      </div>-->
      <!--&lt;!&ndash;-->
      <!--      <div v-else-if="questions[currentIndex].type === 'multiple'" class="options">-->
      <!--        <el-checkbox-group v-model="userAnswers[currentIndex]">-->
      <!--          <el-checkbox-->
      <!--              style="display: flex"-->
      <!--              v-for="(option, index) in questions[currentIndex].options"-->
      <!--              :key="index"-->
      <!--              :label="option"-->
      <!--          >-->
      <!--            <div style="margin-bottom: 10px">{{ option }}</div>-->
      <!--          </el-checkbox>-->
      <!--        </el-checkbox-group>-->
      <!--      </div>-->
      <!--&ndash;&gt;-->
      <!--      <div v-else-if="questions[currentIndex].type === 'fill-in'" class="options">-->
      <!--        <div v-for="(blank, index) in questions[currentIndex].blanks" :key="index">-->
      <!--          <p>空 {{ index + 1 }}</p>-->
      <!--          <el-input-->
      <!--              v-model="userAnswers[currentIndex][index]"-->
      <!--              placeholder="请输入答案"-->
      <!--              style="margin-bottom: 10px"-->
      <!--          ></el-input>-->
      <!--        </div>-->
      <!--      </div>-->
      <div v-if="questions[currentIndex].type === 'single'" class="options">
        <el-radio-group v-model="userAnswers[currentIndex][0]">
          <el-radio
              v-for="(option, index) in questions[currentIndex].options"
              :key="index"
              :disabled="showResult"
              :label="option"
              style="display: flex"
          >
            <div style="margin-bottom: 10px">{{ option }}</div>
          </el-radio>
        </el-radio-group>
        <div v-if="showResult">
          <p>
            <el-tag type="success"> 正确答案</el-tag>
          <p> {{ questions[currentIndex].answer }} </p>
          </p>
          <!--          <p>-->
          <!--            <el-tag type="info">题目解析</el-tag> <p> {{ questions[currentIndex].anal}} </p>-->
          <!--          </p>-->
        </div>
      </div>
      <div v-else-if="questions[currentIndex].type === 'fill-in'" class="options">
        <div v-for="(blank, index) in questions[currentIndex].blanks" :key="index">
          <p>空 {{ index + 1 }}</p>
          <el-input
              v-model="userAnswers[currentIndex][index]"
              :disabled="showResult"
              placeholder="请输入答案"
              style="margin-bottom: 10px"
          ></el-input>
        </div>
        <div v-if="showResult">
          <p>
            <el-tag type="success"> 正确答案</el-tag>
          <p> {{ questions[currentIndex].answer }} </p>
          </p>
          <!--          <p>-->
          <!--            <el-tag type="info">题目解析</el-tag> <p> {{ questions[currentIndex].anal}} </p>-->
          <!--          </p>-->
        </div>
      </div>
    </div>


    <!-- 导航按钮 -->
    <div class="controls">
      <el-button
          :disabled="currentIndex === 0"
          type="primary"
          @click="prevQuestion"
      >
        上一题
      </el-button>
      <el-button
          v-if="currentIndex < questions.length - 1"
          type="success"
          @click="nextQuestion"
      >
        下一题
      </el-button>
      <!--      <el-button-->
      <!--          type="warning"-->
      <!--          v-if="currentIndex === questions.length - 1"-->
      <!--          @click="submitAnswers"-->
      <!--      >-->
      <!--        提交-->
      <!--      </el-button>-->
      <template v-if="currentIndex === questions.length - 1">
        <el-button v-if="!showResult"
                   type="warning"
                   @click="submitAnswers">
          提交
        </el-button>
        <el-button v-if="showResult"
                   type="warning"
                   @click="returnPapers">
          退出
        </el-button>
      </template>
    </div>

    <!-- 答题结果 -->
    <div v-if="showResult" class="result">
      <h2>答题完成！</h2>
      <p>您的得分是：<strong>{{ score }}</strong> 分</p>
      <p v-if="resultStats && resultStats.total > 0" class="result-sub">
        答对 {{ resultStats.correct }} / {{ resultStats.total }} 题，正确率 {{ resultStats.rate }}%
      </p>
      <p v-if="participationSaved === false" class="result-warn">本次成绩未能写入服务器，仅作本地展示。</p>
    </div>
  </div>
</template>


<script>
import request from "@/utils/request";
import devLog from "@/utils/devLog";

export default {
  name: "Exam",
  data() {
    return {
      paper: {},
      questions: [],
      // 用户答案（二维数组）
      userAnswers: [],
      // 当前题目索引
      currentIndex: 0,
      // 是否显示结果
      showResult: false,
      // 总分
      score: 0,
      resultStats: null,
      participationSaved: null,

      user: JSON.parse(localStorage.getItem("current-user") || '{}'),
    };
  },
  created() {
    this.loadPaperAndQuestions().catch((e) => {
      devLog(e);
      this.$message.error('加载试卷失败，请返回重试');
    });
  },
  updated() {
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
    _paperOrderNum(o) {
      const v = o != null ? Number(o) : NaN;
      return Number.isFinite(v) ? v : 1e9;
    },
    async loadPaperAndQuestions() {
      const id = parseInt(this.$route.query.id, 10);
      if (!Number.isFinite(id)) {
        throw new Error('无效的试卷');
      }
      const papersRes = await request.get('/papers');
      const list = Array.isArray(papersRes.data) ? papersRes.data : [];
      const papers = list.slice().sort((a, b) => new Date(b.create_date) - new Date(a.create_date));
      const paper = papers.find((p) => p.test_paper_id === id);
      if (!paper) {
        throw new Error('未找到试卷');
      }
      this.paper = paper;
      const pid = paper.test_paper_id;

      const built = [];

      const tfRes = await request.get('/questions/tf/in_paper/' + pid);
      const tfList = Array.isArray(tfRes.data) ? tfRes.data : [];
      tfList.forEach((q) => {
        built.push({
          text: q.question_description,
          type: 'single',
          options: ['正确', '错误'],
          answer: this._normalizeTfAnswer(q.correct_answer),
          order: q.question_order_in_paper,
        });
      });

      const mcRes = await request.get('/questions/mc/in_paper/' + pid);
      const mcList = Array.isArray(mcRes.data) ? mcRes.data : [];
      await Promise.all(
          mcList.map(async (q) => {
            const optRes = await request.get('/options/in_question/' + q.multiple_choice_question_id);
            const question = {
              text: q.question_description,
              type: 'single',
              options: [],
              answer: q.correct_answer,
              order: q.question_order_in_paper,
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

      const fiRes = await request.get('/questions/fi/in_paper/' + pid);
      const fiList = Array.isArray(fiRes.data) ? fiRes.data : [];
      fiList.forEach((q) => {
        built.push({
          text: q.question_description,
          type: 'fill-in',
          blanks: [''],
          answer: q.correct_answer,
          order: q.question_order_in_paper,
        });
      });

      built.sort((a, b) => this._paperOrderNum(a.order) - this._paperOrderNum(b.order));
      this.questions = built;
      this.$nextTick(() => this.updateAnswer());
    },
    _normalizeTfAnswer(raw) {
      const s = raw == null ? '' : String(raw).trim();
      if (s === '1' || /^true$/i.test(s) || /^t$/i.test(s) || s === '正确') return '正确';
      if (s === '0' || /^false$/i.test(s) || /^f$/i.test(s) || s === '错误') return '错误';
      return s;
    },
    getFormattedDate() {
      const currentDate = new Date();

      const year = currentDate.getFullYear(); // 获取年份
      const month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 获取月份（注意：月份是从0开始的，所以要加1）
      const day = currentDate.getDate().toString().padStart(2, '0'); // 获取日期
      const hours = currentDate.getHours().toString().padStart(2, '0'); // 获取小时
      const minutes = currentDate.getMinutes().toString().padStart(2, '0'); // 获取分钟
      const seconds = currentDate.getSeconds().toString().padStart(2, '0'); // 获取秒钟

      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`; // 返回格式化后的日期和时间
    },
    returnPapers() {
      this.$router.back()
    },
    updateAnswer() {
      this.userAnswers = this.questions.map((question) => {
        if (question.type === 'single') {
          return ['']; // 单选题初始值为单一字符串
        } else if (question.type === 'multiple') {
          return []; // 多选题初始值为空数组
        } else if (question.type === 'fill-in') {
          return new Array(question.blanks.length).fill(''); // 填空题初始值为与空数量匹配的数组
        }
      });
    },
    // 上一题
    prevQuestion() {
      if (this.currentIndex > 0) {
        this.currentIndex -= 1;
      }
    },
    // 下一题
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
    buildParticipationPayload(correct, total, score) {
      return {
        user_id: this._resolveUserId(),
        test_paper_id: parseInt(this.$route.query.id, 10),
        participation_time: this.getFormattedDate(),
        test_record: this._buildTestRecordWithStats(correct, total, score),
      };
    },
    // 提交答案（先算分再写入 participation，含正确率元数据）
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
      if (uid == null || !Number.isFinite(uid)) {
        this.participationSaved = false;
        this.$message.warning('未识别到用户编号，成绩未保存');
      } else {
        const payload = this.buildParticipationPayload(correct, total, this.score);
        devLog('participation payload', payload);
        request
            .post('/participation/add', payload)
            .then((res) => {
              const ok = res && (res.code === '200' || res.code === 200);
              this.participationSaved = ok;
              if (!ok) {
                this.$message.warning((res && res.msg) || '成绩未保存到服务器');
              } else {
                this.$message.success((res && res.msg) || '成绩已保存，统计页可点「刷新数据」查看最新');
              }
            })
            .catch(() => {
              this.participationSaved = false;
              this.$message.error('成绩提交失败，请检查网络后重试');
            });
      }

      this.showResult = true;
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

  },
};
</script>


<style>
/* 页面布局 */
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
}

.header h1 {
  font-size: 2em;
  color: #409eff;
}

.header-sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: #909399;
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