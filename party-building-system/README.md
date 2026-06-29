# 智慧党建平台系统

## SpringBoot配置
自行配置Maven仓库，执行mvn install下载依赖项，依赖项下载完成后pox.xml没有爆红即可启动。
当前用Spring AI框架通过ollma对本地LLM进行调用，用WebMagic框架对指定网站进行爬虫获取最新信息实现伪联网搜索功能。如需开发或调试相关功能请自行安装ollama并进行配置。
连接数据库和调用ollama的参数均在src/resources/application.properties中。


## Vue配置（脚手架自动生成文档，如果npm下载较慢请自行换源）
Vue项目代码位于fronted文件夹中
## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
