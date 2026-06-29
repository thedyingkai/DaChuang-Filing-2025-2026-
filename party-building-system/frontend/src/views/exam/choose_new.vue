<template>
  <div class="enterprise-container">
    <el-card class="form-card" shadow="never">
      <div class="card-header">
        <h2 class="enterprise-title">智能组卷</h2>
        <div class="header-subtitle">试卷参数配置中心</div>
      </div>

      <el-form ref="form" :model="form" label-position="top" class="compact-form">
        <!-- 模式选择 -->
        <div class="form-section">
          <h3 class="section-title"><i class="el-icon-s-operation"></i> 组卷策略</h3>
          <el-radio-group v-model="form.mode" class="strategy-radio">
            <el-radio-button label="newrandom">智能配题</el-radio-button>
            <el-radio-button label="random">随机组卷</el-radio-button>
            <el-radio-button label="byCategory">关键词组卷</el-radio-button>
          </el-radio-group>

          <el-select
              v-if="form.mode === 'byCategory'"
              v-model="form.keyword"
              class="keyword-select"
              placeholder="请选择关键词"
              filterable
          >
            <el-option
                v-for="(category, index) in categories"
                :key="index"
                :label="category.keyword_description"
                :value="category.keyword_description"
            />
          </el-select>
        </div>

        <!-- 题目配置 -->
        <div class="form-section">
          <h3 class="section-title"><i class="el-icon-document"></i> 题目配置</h3>
          <div class="question-row">
            <el-form-item label="判断题数量" class="inline-item">
              <el-input-number
                  v-model="form.judgmentCount"
                  :min="0"
                  controls-position="right"
                  class="compact-input"
              />
            </el-form-item>

            <el-form-item label="选择题数量" class="inline-item">
              <el-input-number
                  v-model="form.choiceCount"
                  :min="0"
                  controls-position="right"
                  class="compact-input"
              />
            </el-form-item>

            <el-form-item label="填空题数量" class="inline-item">
              <el-input-number
                  v-model="form.fillBlankCount"
                  :min="0"
                  controls-position="right"
                  class="compact-input"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 元数据配置 -->
        <div class="form-section">
          <h3 class="section-title"><i class="el-icon-setting"></i> 试卷属性</h3>
          <div class="metadata-grid">
            <el-form-item label="试卷标题" class="form-item-full">
              <el-input
                  v-model="form.filldescriptionCount"
                  placeholder="请输入试卷标题"
                  class="compact-input"
              />
            </el-form-item>

            <el-form-item label="生效时间" class="time-item">
              <el-input
                  type="datetime-local"
                  v-model="form.filltimeCount"
                  class="time-input"
              />
            </el-form-item>

            <el-form-item label="积分奖励" class="score-item">
              <el-tag type="info" class="score-tag">
                {{ fillrewardCount }} 积分
              </el-tag>
            </el-form-item>

            <el-form-item label="截止时间" class="time-item">
              <el-input
                  type="datetime-local"
                  v-model="form.filldeadlineCount"
                  class="time-input"
              />
            </el-form-item>


          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button
              type="primary"
              icon="el-icon-check"
              class="submit-btn"
              @click="handleSubmit"
          >
            生成试卷
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request";
import devLog from "@/utils/devLog";

export default {
  name: "ExamComposer",
  data() {
    return {
      // 表单数据
      form: {
        mode: "", // 试卷组成方式: "random" 或 "byCategory"
        categories: [], // 关键词选择
        judgmentCount: 0, // 判断题数量
        choiceCount: 0, // 选择题数量
        fillBlankCount: 0, // 填空题数量
        fillidCount: null,
        filldescriptionCount: '',//试卷标题
        filltimeCount: '',//试卷开始日期
        filldeadlineCount: '',//试卷截至日期
        fillrewardCount: 0,
        testid: [],
        test: [],
        keyword: '',//当前选择关键词
        keyword_description: [],//所有关键词
        column_id: [],//关键词所属栏目id
        keyword_id: [],//关键词id
      },
      //选择题元素
      choicequestion: {
        answer: [],//答案
        cmid: [],//选择题号
        analysis: [],//解析
        description: [],//题干
        all: [],
        selectids: [],

      },
      //填空题元素
      fiquestion: {
        answer: [],//答案
        fiid: [],//选择题号
        analysis: [],//解析
        description: [],//题干
        all: [],
        selectids: []
      },
      //判断题元素
      tfquestion: {
        answer: [],//答案
        tfid: [],//选择题号
        analysis: [],//解析
        description: [],//题干
        all: [],
        selectids: []
      },
      //关键词选择题元素
      flexmcquestion: {
        answer: [],//答案
        cmid: [],//选择题号
        analysis: [],//解析
        description: [],//题干
        all: [],
        selectids: [],

      },
      //关键词填空题元素
      flexfiquestion: {
        answer: [],//答案
        fiid: [],//选择题号
        analysis: [],//解析
        description: [],//题干
        all: [],
        selectids: []
      },
      //关键词判断题元素
      flextfquestion: {
        answer: [],//答案
        tfid: [],//选择题号
        analysis: [],//解析
        description: [],//题干
        all: [],
        selectids: []
      },
    };
  },
  created() {
    // 在组件加载时获取栏目数据
    this.fetchCategories();
    this.fetchgetid();
    this.fetchgetchoice();
    this.fetchgetfi();
    this.fetchgettf();

  },
  computed: {
    formArray() {
      const result = {};
      result.grouping_method = this.form.mode;
      result.paper_description = this.form.filldescriptionCount;
      result.points_reward = this.fillrewardCount;
      const c = this.formatDatetime(this.form.filltimeCount);
      const d = this.formatDatetime(this.form.filldeadlineCount);
      if (c) result.create_date = c;
      if (d) result.deadline = d;
      return result;
    },
    fillrewardCount() {
      // 根据题目数量计算积分奖励
      return 10 * this.form.judgmentCount + 10 * this.form.choiceCount + 10 * this.form.fillBlankCount;
    },
    cmformArray() {
      // 创建一个对象来存储键值对
      let result = {};
      // 为每个元素添加键值对，键为自定义的字符串，值为原数组元素
      result['correct_answer'] = this.choicequestion.answer;
      result['question_analysis'] = this.choicequestion.analysis;
      result['question_description'] = this.choicequestion.description;
      result['multiple_choice_question_id'] = this.choicequestion.cmid;
      return result;
    },
    tfformArray() {
      // 创建一个对象来存储键值对
      let result = {};
      // 为每个元素添加键值对，键为自定义的字符串，值为原数组元素
      result['correct_answer'] = this.tfquestion.answer;
      result['question_analysis'] = this.tfquestion.analysis;
      result['question_description'] = this.tfquestion.description;
      result['true_false_question_id'] = this.tfquestion.tfid;
      return result;
    },
    fiformArray() {
      // 创建一个对象来存储键值对
      let result = {};
      // 为每个元素添加键值对，键为自定义的字符串，值为原数组元素
      result['correct_answer'] = this.fiquestion.answer;
      result['question_analysis'] = this.fiquestion.analysis;
      result['question_description'] = this.fiquestion.description;
      result['fill_in_the_blank_question_id'] = this.fiquestion.fiid;
      return result;
    },
    keywordsArray() {
      // 创建一个对象来存储键值对
      let result = {};
      // 为每个元素添加键值对，键为自定义的字符串，值为原数组元素
      result['keyword_id'] = this.form.keyword_id;
      result['column_id'] = this.form.column_id;
      result['keyword_description'] = this.form.keyword;
      return result;
    }
  },
  methods: {
    //
    handlequestionnumber()
    {
      if(this.form.mode==='byCategory')
      {
        if(this.form.choiceCount > 0 && this.form.choiceCount>this.flexmcquestion.all.length)
        {
          this.$message.error("题库中选择题没有这么多！");
        }
        if(this.form.judgmentCount > 0 && this.form.judgmentCount>this.flextfquestion.all.length)
        {
          this.$message.error("题库中判断题没有这么多！");
        }
        if(this.form.fillBlankCount > 0 && this.form.fillBlankCount>this.flexfiquestion.all.length)
        {
          this.$message.error("题库中填空题没有这么多！");
        }
      }
      if(this.form.choiceCount > 0 && this.form.choiceCount>this.choicequestion.all.length)
      {
        this.$message.error("题库中选择题没有这么多！");
      }
      if(this.form.judgmentCount > 0 && this.form.judgmentCount>this.tfquestion.all.length)
      {
        this.$message.error("题库中判断题题没有这么多！");
      }
      if(this.form.fillBlankCount > 0 && this.form.fillBlankCount>this.fiquestion.all.length)
      {
        this.$message.error("题库中填空题没有这么多！");
      }

    },
    // 切换模式时重置相关选项
    handleModeChange() {
      if (this.form.mode === "random") {
        this.form.categories = []; // 清空栏目选择
      }
    },
    handlechange() {
      this.test1()
      this.fetchgetflexmc()
      this.fetchgetflextf()
      this.fetchgetflexfi()
    },
    test1() {
      devLog(this.form.keyword);
    },

    formatDatetime(datetime) {
      if (!datetime) return '';
      const s = String(datetime).replace('T', ' ').split('.')[0];
      return s.length === 16 ? `${s}:00` : s;
    },
    //获取填空题数据
    fetchgetfi() {
      request.get("/questions/fi") // 后台接口路径
          .then((response) => {
            // 假设返回的数据是全部选择题的数据
            this.fi = response.data;
            this.fiquestion.all = this.fi;
          })
          .catch((error) => {
            this.$message.error("获取填空题数据失败！");
            console.error("获取填空题失败:", error);
          });
    },
    //获取判断题数据
    fetchgettf() {
      request.get("/questions/tf") // 后台接口路径
          .then((response) => {
            // 假设返回的数据是全部选择题的数据
            this.tf = response.data;
            this.tfquestion.all = this.tf
          })
          .catch((error) => {
            this.$message.error("获取判断题数据失败！");
            console.error("获取判断题失败:", error);
          });
    },
    //获取选择题数据
    fetchgetchoice() {
      request.get("/questions/mc") // 后台接口路径
          .then((response) => {
            // 假设返回的数据是全部选择题的数据
            this.choice = response.data;
            this.choicequestion.all = this.choice;
            devLog(this.choicequestion.all);
          })
          .catch((error) => {
            this.$message.error("获取选择题数据失败！");
            console.error("获取选择题失败:", error);
          });
    },
    //获取满足关键词选择题数据
    fetchgetflexmc() {
      request.get("/questions/mc/search/keyword/" + this.form.keyword) // 后台接口路径
          .then((response) => {
            // 假设返回的数据是全部选择题的数据
            this.choice = response.data;
            this.flexmcquestion.all = this.choice;
            devLog(this.flexmcquestion.all);
          })
          .catch((error) => {
            this.$message.error("获取关键词选择题数据失败！");
            console.error("获取关键词选择题失败:", error);
          });
    },
    //获取满足关键词判断题数据
    fetchgetflextf() {
      request.get("/questions/tf/search/keyword/" + this.form.keyword) // 后台接口路径
          .then((response) => {
            // 假设返回的数据是全部判断题的数据
            this.tf = response.data;
            this.flextfquestion.all = this.tf
          })
          .catch((error) => {
            this.$message.error("获取关键词判断题数据失败！");
            console.error("获取关键词判断题失败:", error);
          });
    },
    //获取满足关键词填空题数据
    fetchgetflexfi() {
      request.get("/questions/fi/search/keyword/" + this.form.keyword) // 后台接口路径
          .then((response) => {
            // 假设返回的数据是全部填空题的数据
            this.fi = response.data;
            this.flexfiquestion.all = this.fi;
          })
          .catch((error) => {
            this.$message.error("获取关键词填空题数据失败！");
            console.error("获取关键词填空题失败:", error);
          });
    },
    //获取现有试卷数据
    fetchgetid() {
      request.get("/papers")
          .then((response) => {
            this.test = response.data;
            this.form.test = this.test;
          })
          .catch((error) => {
            this.$message.error("获取试卷数据失败！");
            devLog("获取试卷失败:", error);
          });
    },
    // 获取关键词数据
    fetchCategories() {
      request.get('/keywords')
          .then((res) => {
            if (res && (res.code === '200' || res.code === 200)) {
              this.categories = Array.isArray(res.data) ? res.data : [];
              if (this.categories.length === 0) {
                this.$message.warning('关键词库为空，请先在「试题编辑」中添加关键词');
              }
            } else {
              this.categories = [];
              this.$message.error((res && res.msg) || '获取关键词失败');
            }
            devLog(this.categories);
          })
          .catch((error) => {
            this.categories = [];
            const st = error.response && error.response.status;
            if (!st || st === 404) {
              this.$message.error('无法访问 /keywords，请检查服务器 Nginx 反代配置');
            } else if (st === 403) {
              this.$message.error('无题库权限，无法加载关键词');
            } else {
              this.$message.error('获取关键词数据失败');
            }
            console.error('获取关键词失败:', error);
          });
    },
    async _assertResultOk(promise) {
      const r = await promise;
      const code = r && r.code;
      const ok = code === '200' || code === 200;
      if (!r || !ok) {
        throw new Error((r && r.msg) || '请求失败');
      }
      return r;
    },
    handleSubmit() {
      this.handleSubmitAsync().catch((e) => {
        this.$message.error(e.message || '组卷失败，请稍后再试');
        devLog(e);
      });
    },
    async handleSubmitAsync() {
      if (!this.form.mode) {
        throw new Error('请选择组卷策略');
      }
      if (this.form.mode === 'byCategory' && !this.form.keyword) {
        throw new Error('关键词组卷请选择关键词');
      }
      if (
          this.form.judgmentCount <= 0 &&
          this.form.choiceCount <= 0 &&
          this.form.fillBlankCount <= 0
      ) {
        throw new Error('请至少设置一道题目的数量（三种题型合计须大于 0）');
      }
      const title = (this.form.filldescriptionCount || '').trim();
      if (!title) {
        throw new Error('请填写试卷标题');
      }
      if (!this.form.filltimeCount || !this.form.filldeadlineCount) {
        throw new Error('请填写生效时间与截止时间');
      }
      this._assertPoolSizes();

      devLog('papers/create payload', this.formArray);
      const res = await this._assertResultOk(request.post('/papers/create', this.formArray));
      const paper = res.data;
      const newId =
          paper &&
          (paper.test_paper_id != null
              ? paper.test_paper_id
              : paper.testPaperId != null
                  ? paper.testPaperId
                  : null);
      if (newId == null) {
        throw new Error('创建试卷失败：未返回试卷编号');
      }
      this.form.fillidCount = Number(newId);

      if (this.form.mode === 'random' || this.form.mode === 'newrandom') {
        await this.submitAsync();
      } else if (this.form.mode === 'byCategory') {
        await this.fsubmitAsync();
      }

      this.$message.success('组卷成功');
      this.$router.push({path: '/papers'});
    },
    _assertPoolSizes() {
      const mc = this.form.mode === 'byCategory' ? this.flexmcquestion.all : this.choicequestion.all;
      const tf = this.form.mode === 'byCategory' ? this.flextfquestion.all : this.tfquestion.all;
      const fi = this.form.mode === 'byCategory' ? this.flexfiquestion.all : this.fiquestion.all;
      const needMc = Number(this.form.choiceCount) || 0;
      const needTf = Number(this.form.judgmentCount) || 0;
      const needFi = Number(this.form.fillBlankCount) || 0;
      if (needMc > 0 && (!mc || mc.length < needMc)) {
        throw new Error('题库中选择题数量不足');
      }
      if (needTf > 0 && (!tf || tf.length < needTf)) {
        throw new Error('题库中判断题数量不足');
      }
      if (needFi > 0 && (!fi || fi.length < needFi)) {
        throw new Error('题库中填空题数量不足');
      }
    },
    async submitAsync() {
      const nc = Number(this.form.choiceCount) || 0;
      const nt = Number(this.form.judgmentCount) || 0;
      const nf = Number(this.form.fillBlankCount) || 0;
      if (nc > 0) await this.randomcmid(nc);
      if (nt > 0) await this.randomtfid(nt);
      if (nf > 0) await this.randomfiid(nf);
    },
    async fsubmitAsync() {
      const nc = Number(this.form.choiceCount) || 0;
      const nt = Number(this.form.judgmentCount) || 0;
      const nf = Number(this.form.fillBlankCount) || 0;
      if (nc > 0) await this.flexrandomcmid(nc);
      if (nt > 0) await this.flexrandomtfid(nt);
      if (nf > 0) await this.flexrandomfiid(nf);
    },
    async randomcmid(count) {
      this.getchid();
      if (!this.choicequestion.cmid || this.choicequestion.cmid.length < count) {
        throw new Error('选择题题库不足');
      }
      const randomIds = [];
      const selectedIndices = [];
      for (let i = 0; i < count; i++) {
        let randomIndex;
        do {
          randomIndex = Math.floor(Math.random() * this.choicequestion.cmid.length);
        } while (selectedIndices.includes(randomIndex));
        selectedIndices.push(randomIndex);
        randomIds.push(this.choicequestion.cmid[randomIndex]);
      }
      this.choicequestion.selectids = randomIds;
      await this.cmsubmitAsync(count);
    },
    async randomtfid(count) {
      this.gettfid();
      if (!this.tfquestion.tfid || this.tfquestion.tfid.length < count) {
        throw new Error('判断题题库不足');
      }
      const randomIds = [];
      const selectedIndices = [];
      for (let i = 0; i < count; i++) {
        let randomIndex;
        do {
          randomIndex = Math.floor(Math.random() * this.tfquestion.tfid.length);
        } while (selectedIndices.includes(randomIndex));
        selectedIndices.push(randomIndex);
        randomIds.push(this.tfquestion.tfid[randomIndex]);
      }
      this.tfquestion.selectids = randomIds;
      await this.tfsubmitAsync(count);
    },
    async randomfiid(count) {
      this.getfiid();
      if (!this.fiquestion.fiid || this.fiquestion.fiid.length < count) {
        throw new Error('填空题题库不足');
      }
      const randomIds = [];
      const selectedIndices = [];
      for (let i = 0; i < count; i++) {
        let randomIndex;
        do {
          randomIndex = Math.floor(Math.random() * this.fiquestion.fiid.length);
        } while (selectedIndices.includes(randomIndex));
        selectedIndices.push(randomIndex);
        randomIds.push(this.fiquestion.fiid[randomIndex]);
      }
      this.fiquestion.selectids = randomIds;
      await this.fisubmitAsync(count);
    },
    async flexrandomcmid(count) {
      this.getflchid();
      if (!this.flexmcquestion.cmid || this.flexmcquestion.cmid.length < count) {
        throw new Error('关键词选择题不足');
      }
      const randomIds = [];
      const selectedIndices = [];
      for (let i = 0; i < count; i++) {
        let randomIndex;
        do {
          randomIndex = Math.floor(Math.random() * this.flexmcquestion.cmid.length);
        } while (selectedIndices.includes(randomIndex));
        selectedIndices.push(randomIndex);
        randomIds.push(this.flexmcquestion.cmid[randomIndex]);
      }
      this.flexmcquestion.selectids = randomIds;
      await this.flexcmsubmitAsync(count);
    },
    async flexrandomtfid(count) {
      this.getfltfid();
      if (!this.flextfquestion.tfid || this.flextfquestion.tfid.length < count) {
        throw new Error('关键词判断题不足');
      }
      const randomIds = [];
      const selectedIndices = [];
      for (let i = 0; i < count; i++) {
        let randomIndex;
        do {
          randomIndex = Math.floor(Math.random() * this.flextfquestion.tfid.length);
        } while (selectedIndices.includes(randomIndex));
        selectedIndices.push(randomIndex);
        randomIds.push(this.flextfquestion.tfid[randomIndex]);
      }
      this.flextfquestion.selectids = randomIds;
      await this.flextfsubmitAsync(count);
    },
    async flexrandomfiid(count) {
      this.getflfiid();
      if (!this.flexfiquestion.fiid || this.flexfiquestion.fiid.length < count) {
        throw new Error('关键词填空题不足');
      }
      const randomIds = [];
      const selectedIndices = [];
      for (let i = 0; i < count; i++) {
        let randomIndex;
        do {
          randomIndex = Math.floor(Math.random() * this.flexfiquestion.fiid.length);
        } while (selectedIndices.includes(randomIndex));
        selectedIndices.push(randomIndex);
        randomIds.push(this.flexfiquestion.fiid[randomIndex]);
      }
      this.flexfiquestion.selectids = randomIds;
      await this.flexfisubmitAsync(count);
    },
    async cmsubmitAsync(count) {
      const id = this.form.fillidCount;
      for (let i = 0; i < count; i++) {
        const temple = this.choicequestion.selectids[i];
        await this._assertResultOk(
            request.post('/papers/add/mc', {
              test_paper_id: id,
              multiple_choice_question_id: temple,
              question_order_in_paper: i + 1,
            })
        );
      }
    },
    async tfsubmitAsync(count) {
      const id = this.form.fillidCount;
      for (let i = 0; i < count; i++) {
        const temple = this.tfquestion.selectids[i];
        await this._assertResultOk(
            request.post('/papers/add/tf', {
              test_paper_id: id,
              question_order_in_paper: i + 1 + Number(this.form.choiceCount),
              true_false_question_id: temple,
            })
        );
      }
    },
    async fisubmitAsync(count) {
      const id = this.form.fillidCount;
      for (let i = 0; i < count; i++) {
        const temple = this.fiquestion.selectids[i];
        await this._assertResultOk(
            request.post('/papers/add/fi', {
              test_paper_id: id,
              question_order_in_paper:
                  i + 1 + Number(this.form.choiceCount) + Number(this.form.judgmentCount),
              fill_in_the_blank_question_id: temple,
            })
        );
      }
    },
    async flexcmsubmitAsync(count) {
      const id = this.form.fillidCount;
      for (let i = 0; i < count; i++) {
        const temple = this.flexmcquestion.selectids[i];
        await this._assertResultOk(
            request.post('/papers/add/mc', {
              test_paper_id: id,
              multiple_choice_question_id: temple,
              question_order_in_paper: i + 1,
            })
        );
      }
    },
    async flextfsubmitAsync(count) {
      const id = this.form.fillidCount;
      for (let i = 0; i < count; i++) {
        const temple = this.flextfquestion.selectids[i];
        await this._assertResultOk(
            request.post('/papers/add/tf', {
              test_paper_id: id,
              question_order_in_paper: i + 1 + Number(this.form.choiceCount),
              true_false_question_id: temple,
            })
        );
      }
    },
    async flexfisubmitAsync(count) {
      const id = this.form.fillidCount;
      for (let i = 0; i < count; i++) {
        const temple = this.flexfiquestion.selectids[i];
        await this._assertResultOk(
            request.post('/papers/add/fi', {
              test_paper_id: id,
              question_order_in_paper:
                  i + 1 + Number(this.form.choiceCount) + Number(this.form.judgmentCount),
              fill_in_the_blank_question_id: temple,
            })
        );
      }
    },
    getchid() {
      const existingIds = this.choicequestion.all.map(category => category.multiple_choice_question_id);
      this.choicequestion.cmid = existingIds;
    },
    //获取选择题题目id
    gettfid() {
      const existingIds = this.tfquestion.all.map(category => category.true_false_question_id);
      this.tfquestion.tfid = existingIds;
    },
    //获取题目id
    getfiid() {
      const existingIds = this.fiquestion.all.map(category => category.fill_in_the_blank_question_id);
      this.fiquestion.fiid = existingIds;
      devLog(this.fiquestion.fiid);
    },
    getflchid() {
      const existingIds = this.flexmcquestion.all.map(category => category.multiple_choice_question_id);
      this.flexmcquestion.cmid = existingIds;
    },
    //获取题目id
    getfltfid() {
      const existingIds = this.flextfquestion.all.map(category => category.true_false_question_id);
      this.flextfquestion.tfid = existingIds;
    },
    //获取题目id
    getflfiid() {
      const existingIds = this.flexfiquestion.all.map(category => category.fill_in_the_blank_question_id);
      this.flexfiquestion.fiid = existingIds;
      devLog(this.flexfiquestion.fiid);
    },
  },
};
</script>

<style scoped>
.enterprise-container {
  max-width: 1000px;
  margin: 20px auto; /* 原40px → 20px */
  padding: 0 20px;
  min-height: 80vh; /* 原100vh → 80vh */
}

.form-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #ffffff;
}

.card-header {
  padding: 24px 32px;
  border-bottom: 1px solid #ebeef5;
}

.enterprise-title {
  font-size: 20px;
  color: #303133;
  margin: 0;
  font-weight: 600;
}

.header-subtitle {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}

.compact-form {
  padding: 16px 24px;
}

.form-section {
  margin-bottom: 32px;
  padding: 20px;
  background: #f8f9fc;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.section-title {
  font-size: 14px; /* 原16px → 14px */
  margin: 0 0 16px 0; /* 原20px → 16px */
  color: #303133;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #409EFF;
}

.strategy-radio {
  margin-bottom: 16px;
}

.keyword-select {
  width: 300px;
}

.grid-layout {
  display: grid;
  grid-template-columns: 160px 1fr;
  gap: 24px;
}

.question-config,
.metadata-config {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.compact-item {
  margin-bottom: 0;
}

.compact-item >>> .el-form-item__label {
  font-size: 13px;
  color: #606266;
  padding-bottom: 6px;
}

.compact-input >>> .el-input__inner {
  height: 32px; /* 原36px → 32px */
  line-height: 32px;
  font-size: 13px;
}

.compact-date >>> .el-input__inner {
  height: 36px;
}

.score-tag {
  font-size: 12px; /* 原14px → 12px */
  padding: 6px 12px; /* 原8px 16px → 6px 12px */
}

.time-group {
  display: grid;
  gap: 16px;
}

.form-actions {
  margin-top: 32px;
  text-align: center;
}

.submit-btn {
  padding: 8px 32px; /* 原12px 40px → 8px 32px */
  font-size: 13px; /* 原14px → 13px */
  letter-spacing: 1px;
}
.question-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.inline-item {
  margin-bottom: 0;
  display: flex;
  align-items: center;
}

.inline-item >>> .el-form-item__label {
  padding-right: 12px;
  line-height: 36px;
  margin-bottom: 0;
}

/* 时间输入组件样式 */
.time-input >>> .el-input__inner {
  height: 32px; /* 原36px → 32px */
  width: 200px; /* 原240px → 200px */
}

/* 元数据栅格布局 */
.metadata-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 12px !important;
}

.form-item-full {
  margin-bottom: 8px !important;
}

.time-item {
  margin-bottom: 8px !important;
}

.score-item {
  margin-bottom: 8px !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .question-row {
    flex-direction: column;
    gap: 16px;
  }

  .inline-item >>> .el-form-item__content {
    width: 100%;
  }

  .time-input >>> .el-input__inner {
    width: 100%;
  }


  .metadata-grid {
    grid-template-columns: 1fr;
  }
}
</style>
