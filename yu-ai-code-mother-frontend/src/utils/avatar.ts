// 统一头像地址生成工具函数
// 使用百度图片代理并对原始 URL 进行 encodeURIComponent 处理；
// 当不存在头像地址时，使用默认头像或传入的备用地址。
import aiAvatar from '@/assets/aiAvatar.png'

export const getAvatarSrc = (url?: string, fallback?: string) => {
  const hasUrl = typeof url === 'string' && url.trim().length > 0
  if (hasUrl) {
    return `https://image.baidu.com/search/down?url=${encodeURIComponent(url!)}`
  }
  return fallback || aiAvatar
}