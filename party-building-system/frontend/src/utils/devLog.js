/** 仅开发环境输出，避免生产控制台噪声与敏感信息泄露。 */
export function devLog(...args) {
  if (process.env.NODE_ENV === 'development') {
    console.debug(...args);
  }
}

export default devLog;
