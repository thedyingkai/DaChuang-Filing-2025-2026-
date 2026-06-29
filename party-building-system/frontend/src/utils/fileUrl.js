/** 存库/提交用：后端 files 目录下的默认封面 */
export const DEFAULT_ACTIVITY_COVER = '/file/download/dsjtjt.jpg';

/** 前端静态默认封面（public/img，不依赖后端与自签证书） */
export const DEFAULT_ACTIVITY_COVER_STATIC = '/img/default-activity-cover.jpg';

/** 无封面且所有默认图均不可用时的占位图 */
export const ACTIVITY_COVER_PLACEHOLDER =
    'data:image/svg+xml,' +
    encodeURIComponent(
        '<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180">' +
        '<rect fill="#f0f2f5" width="100%" height="100%"/>' +
        '<text x="50%" y="50%" fill="#909399" font-size="16" text-anchor="middle" dy=".3em">暂无封面</text>' +
        '</svg>'
    );

function isBlankCover(url) {
  if (url == null) return true;
  const s = String(url).trim();
  return !s || s === 'null' || s === 'undefined';
}

/**
 * 活动封面展示 URL（列表/详情用）
 */
export function activityCoverUrl(url) {
  if (isBlankCover(url)) {
    return DEFAULT_ACTIVITY_COVER_STATIC;
  }
  const resolved = resolveFileUrl(url);
  return resolved || DEFAULT_ACTIVITY_COVER_STATIC;
}

/** 图片加载失败时依次尝试的备用地址 */
export function activityCoverErrorFallbacks() {
  const list = [DEFAULT_ACTIVITY_COVER_STATIC];
  const backend = resolveFileUrl(DEFAULT_ACTIVITY_COVER);
  if (backend && backend !== DEFAULT_ACTIVITY_COVER_STATIC) {
    list.push(backend);
  }
  list.push(ACTIVITY_COVER_PLACEHOLDER);
  return list;
}

/**
 * 将后端返回的文件地址转为浏览器可访问的 URL。
 * - 生产（VUE_APP_BASEURL 为空）：同源 /file/download/...，由 Nginx 反代到后端
 * - 开发：使用相对路径走 devServer 的 /file 代理，避免直连 8081 自签证书导致图片失败
 */
export function resolveFileUrl(url) {
  if (!url || typeof url !== 'string') {
    return url;
  }
  const trimmed = url.trim();
  let path = trimmed;
  const match = trimmed.match(/\/file\/download\/(.+)$/);
  if (match) {
    path = '/file/download/' + match[1];
  } else if (!trimmed.startsWith('/file/download/')) {
    return trimmed;
  }
  if (process.env.NODE_ENV === 'development') {
    return path;
  }
  const base = (process.env.VUE_APP_BASEURL || '').replace(/\/$/, '');
  return base ? base + path : path;
}
