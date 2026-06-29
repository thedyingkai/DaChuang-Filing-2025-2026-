import axios from 'axios'
import router from "@/router";
import {decryptData, encryptData} from './sm4';
import devLog from './devLog';
import CryptoJS from 'crypto-js';
import { Message } from 'element-ui';

const request = axios.create({
    baseURL: process.env.VUE_APP_BASEURL,
    timeout: 600000
})

// 不需要 token 的白名单接口
const whiteListUrls = ['/login', '/register'];

// 请求拦截器
request.interceptors.request.use(async config => {
    // 判断是否为白名单接口（不需要 token）
    const isWhiteList = whiteListUrls.some(url => config.url.includes(url));

    if (!isWhiteList) {
        let userStr = localStorage.getItem("current-user");
        let user = null;
        try {
            user = JSON.parse(userStr);
        } catch (e) {
            user = null;
        }

        // 如果没有 token，强制跳转到登录页
        if (!user || !user.token) {
            router.push('/login');
            return Promise.reject(new Error('未登录，请先登录'));
        }

        config.headers['token'] = user.token;
    }

    const key = CryptoJS.lib.WordArray.random(16).toString(CryptoJS.enc.Hex).slice(0, 16);
    const iv = CryptoJS.lib.WordArray.random(16).toString(CryptoJS.enc.Hex).slice(0, 16);
    // 使用Base64编码
    const encodedKey = btoa(key);
    const encodedIv = btoa(iv);
    config.headers['SM4-Key'] = encodedKey;
    config.headers['SM4-IV'] = encodedIv;

    if (!(config.data instanceof FormData)) {
        config.data = encryptData(config.data, key, iv);
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
    }
    return config;
}, error => {
    console.error('request error:' + error);
    return Promise.reject(error);
});

// 响应拦截器
request.interceptors.response.use(
    async response => {
        const contentType = response.headers['content-type'];
        let key = response.headers['sm4-key'];
        let iv = response.headers['sm4-iv'];
        // 使用Base64解码
        let decodedKey = null;
        let decodedIv = null;
        if (key) {
            const binaryKey = atob(key);
            decodedKey = new Uint8Array(binaryKey.length);
            for (let i = 0; i < binaryKey.length; i++) {
                decodedKey[i] = binaryKey.charCodeAt(i);
            }
            // 将 Uint8Array 转换为字符串
            decodedKey = String.fromCharCode.apply(null, decodedKey);
        }
        if (iv) {
            const binaryIv = atob(iv);
            decodedIv = new Uint8Array(binaryIv.length);
            for (let i = 0; i < binaryIv.length; i++) {
                decodedIv[i] = binaryIv.charCodeAt(i);
            }
            // 将 Uint8Array 转换为字符串
            decodedIv = String.fromCharCode.apply(null, decodedIv);
        }

        if (contentType && contentType.includes('application/octet-stream')) {
            const arrayBuffer = await response.data.arrayBuffer();
            const encryptedData = Array.from(new Uint8Array(arrayBuffer));
            const decryptedData = decryptData(encryptedData, decodedKey, decodedIv);
            response.data = new Blob([decryptedData], {type: contentType});
        } else {
            let res = response.data;
            if (typeof res ==='string') {
                try {
                    res = decryptData(res, decodedKey, decodedIv);
                    res = JSON.parse(res);
                    devLog('response:', res);
                } catch (e) {
                    res = null;
                }
            }
            if (res && (res.code === '401' || res.code === 401)) {
                localStorage.removeItem('current-user');
                Message.warning((res.msg) || '登录已失效，请重新登录');
                router.push('/login');
            }
            response.data = res;
        }
        return response.data;
    },
    error => {
        const status = error.response && error.response.status;
        const data = error.response && error.response.data;
        const biz401 = data && (data.code === '401' || data.code === 401);
        if (status === 401 || biz401) {
            localStorage.removeItem('current-user');
            Message.warning((data && data.msg) || '登录已失效，请重新登录');
            router.push('/login');
        } else if (status === 403) {
            Message.warning((data && data.msg) || '权限不足，无法访问该功能');
        }
        console.error('response error' + error);
        return Promise.reject(error);
    }
);

export default request;