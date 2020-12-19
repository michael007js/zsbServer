package socket;

import utils.LogUtils;

/**
 * <p>
 * 客户服务端业务逻辑处理
 */
public class ClientBusinessProcessor extends AppBusinessProcessor {

    public void process(NettyMessage message) {
        NettyClientHelper helper = NettyClientHelper.getFirst();
        if (helper == null) {
            return;
        }
        LogUtils.e("客户端执行业务处理...");

        // TODO: biz goes here

        // 更新界面
        LogUtils.e("responseMessage", new Object[]{message.bodyToString()});
        LogUtils.e("statusMsg", new Object[]{"已收到响应..."});
    }

}