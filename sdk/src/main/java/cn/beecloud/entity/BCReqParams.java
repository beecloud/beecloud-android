/**
 * BCReqParams.java
 *
 * Created by xuanzhui on 2015/7/29.
 * Copyright (c) 2015 BeeCloud. All rights reserved.
 */
package cn.beecloud.entity;

import java.util.Date;

import cn.beecloud.BCCache;
import cn.beecloud.BCException;
import cn.beecloud.BCMD5Util;

/**
 * 向服务端请求的基类
 * 包含请求的公用参数
 */
public class BCReqParams {

    //BeeCloud应用APPID
    //BeeCloud的唯一标识
    private String appId;

    //签名生成时间
    //时间戳, 毫秒数
    private Long timestamp;

    //加密签名
    //算法: md5(app_id+timestamp+app_secret), 32位16进制格式, 不区分大小写
    private String appSign;

    /**
     * 渠道类型
     * 根据不同场景选择不同的支付方式
     */
    public BCChannelTypes channel;

    /**
     * 请求类型
     */
    public enum ReqType{PAY, QUERY, QRCODE}

    /**
     * 渠道支付类型
     */
    public enum BCChannelTypes {
        /**
         * 所有渠道
         * 仅用于查询订单
         */
        ALL,

        /**
         * 微信所有渠道
         * 仅用于查询订单
         */
        WX,

        /**
         * 微信公众号二维码支付
         * 用于生成扫码和查询订单
         */
        WX_NATIVE,

        /**
         * 微信公众号支付
         * 仅用于查询订单
         */
        WX_JSAPI,

        /**
         * 微信手机原生APP支付
         */
        WX_APP,

        /**
         * 支付宝所有渠道
         * 仅用于查询订单
         */
        ALI,

        /**
         * 支付宝手机原生APP支付
         */
        ALI_APP,

        /**
         * 支付宝PC网页支付
         * 仅用于查询订单
         */
        ALI_WEB,

        /**
         * 支付宝内嵌二维码支付
         * 用于生成扫码和查询订单
         */
        ALI_QRCODE,

        /**
         * 支付宝线下二维码支付
         * 用于线下实体店扫码和查询订单
         */
        ALI_OFFLINE_QRCODE,

        /**
         * 支付宝移动网页支付
         * 仅用于查询订单
         */
        ALI_WAP,

        /**
         * 银联所有渠道
         * 仅用于查询订单
         */
        UN,

        /**
         * 银联手机原生APP支付
         */
        UN_APP,

        /**
         * 银联PC网页支付
         * 仅用于查询订单
         */
        UN_WEB;

        /**
         * 判断是否为有效的app端支付渠道类型
         *
         * @param channel 支付渠道类型
         * @return true表示有效
         */
        public static boolean isValidAPPPaymentChannelType(BCChannelTypes channel) {
            return channel == WX_APP ||
                    channel == ALI_APP ||
                    channel == UN_APP;
        }

        /**
         * 判断是否为有效的生成扫码的支付渠道类型
         *
         * @param channel 支付渠道类型
         * @return true表示有效
         */
        public static boolean isValidQRCodeReqChannelType(BCChannelTypes channel) {
            return channel == WX_NATIVE ||
                    channel == ALI_QRCODE ||
                    channel == ALI_OFFLINE_QRCODE;
        }

        /**
         * @param channel 支付渠道类型
         * @return 实际的渠道支付名
         */
        public static String getTranslatedChannelName(String channel) {
            if (channel.equals(WX.name()))
                return "微信所有渠道";
            else if (channel.equals(WX_NATIVE.name()))
                return "微信公众号二维码支付";
            else if (channel.equals(WX_JSAPI.name()))
                return "微信公众号支付";
            else if (channel.equals(WX_APP.name()))
                return "微信手机原生APP支付";
            else if (channel.equals(ALI.name()))
                return "支付宝所有渠道";
            else if (channel.equals(ALI_APP.name()))
                return "支付宝手机原生APP支付";
            else if (channel.equals(ALI_WEB.name()))
                return "支付宝PC网页支付";
            else if (channel.equals(ALI_QRCODE.name()))
                return "支付宝内嵌二维码支付";
            else if (channel.equals(ALI_OFFLINE_QRCODE.name()))
                return "支付宝线下二维码支付";
            else if (channel.equals(ALI_WAP.name()))
                return "支付宝移动网页支付";
            else if (channel.equals(UN.name()))
                return "银联所有渠道";
            else if (channel.equals(UN_APP.name()))
                return "银联手机原生APP支付";
            else if (channel.equals(UN_WEB.name()))
                return "银联PC网页支付";
            else
                return "非法的支付类型";
        }
    }

    /**
     * BeeCloud的唯一标识
     * @return  BeeCloud应用APPID
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 时间戳, 毫秒数
     * @return  签名生成时间
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * 算法: md5(app_id+timestamp+app_secret), 32位16进制格式, 不区分大小写
     * @return  加密签名
     */
    public String getAppSign() {
        return appSign;
    }

    /**
     * 初始化参数
     * @param channel   渠道类型
     * @param reqType   请求类型
     */
    public BCReqParams(BCChannelTypes channel, ReqType reqType) throws BCException{

        if (reqType == ReqType.PAY && (channel == null ||
                !BCChannelTypes.isValidAPPPaymentChannelType(channel)))
            throw new BCException("非法APP支付渠道");

        if (reqType == ReqType.QRCODE && !BCChannelTypes.isValidQRCodeReqChannelType(channel))
            throw new BCException("非法二维码请求支付渠道");

        BCCache mCache = BCCache.getInstance();

        if (mCache.appId == null || mCache.appSecret == null) {
            throw new BCException("parameters: 请通过BeeCloud初始化appId和appSecret");
        } else {
            appId = mCache.appId;
            timestamp = (new Date()).getTime();
            appSign = BCMD5Util.getMessageDigest(appId +
                    timestamp + mCache.appSecret);
            this.channel = channel;
        }
    }
}
