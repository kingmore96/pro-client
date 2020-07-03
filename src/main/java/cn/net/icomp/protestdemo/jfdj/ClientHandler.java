package cn.net.icomp.protestdemo.jfdj;

import cn.net.icomp.hongyan.protocol_core.base.DeviceMessage;
import cn.net.icomp.hongyan.protocol_core.base.Packet;
import cn.net.icomp.hongyan.protocol_core.base.ProtocolService;
import cn.net.icomp.hongyan.protocol_core.util.HexStringUtils;
import cn.net.icomp.hongyan.protocol_jfdj.ProtocolServiceImpl;
import cn.net.icomp.hongyan.protocol_jfdj.command.Command0B03;
import cn.net.icomp.hongyan.protocol_jfdj.command.Command0B05;
import cn.net.icomp.hongyan.protocol_jfdj.command.Command0C04;
import cn.net.icomp.hongyan.protocol_jfdj.enums.OperateResult;
import cn.net.icomp.hongyan.protocol_jfdj.message.*;
import cn.net.icomp.protestdemo.common.redis.RedisUtil;
import cn.net.icomp.protestdemo.common.utils.ApplicationContextHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.ApplicationContext;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler extends SimpleChannelInboundHandler<byte[]> {

    private AtomicInteger count = new AtomicInteger(1);

    public static ChannelHandlerContext ctx;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //发送心跳指令
        if (count.intValue() > 150) {
            count.set(1);
        }
        Command0C04 command0C04 = new Command0C04(count.intValue());
        ctx.writeAndFlush(command0C04.encode());
        System.out.println("成功发送心跳指令" + count.intValue());
        count.getAndIncrement();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] msg) throws Exception {
        System.out.println("接收到原始字节码" + HexStringUtils.toHexString(msg));
        //先解码
        ProtocolService protocolService = new ProtocolServiceImpl();
        Packet packet = protocolService.decode(msg);
        if (!packet.isSuccess()) {
            System.out.println("解码失败！" + packet.getThrowable().getMessage());
            return;
        }
        //判断
        DeviceMessage deviceMessage = packet.getDeviceMessage();

        if (deviceMessage instanceof Message0D04) {
            System.out.println("接收到心跳响应！");
            //接收到响应后间隔5秒后再次发送
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //发送心跳指令
                    if (count.intValue() > 150) {
                        count.set(1);
                    }
                    Command0C04 command0C04 = new Command0C04(count.intValue());
                    channelHandlerContext.writeAndFlush(command0C04.encode());
                    System.out.println("成功发送心跳指令" + count.intValue());
                    count.getAndIncrement();
                }
            }, 5000);
            return;
        }

        //刀具id上传服务器指令
        if (deviceMessage instanceof Message0A03) {
            String epc = ((Message0A03) deviceMessage).getEpc();
            String desc = ((Message0A03) deviceMessage).getOperationType().desc();
            System.out.println("接受到刀具id上传服务器指令" + epc + desc);
            //需要测试三种不同的业务
            ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
            RedisUtil redisUtil = (RedisUtil) applicationContext.getBean("redisUtil");
            Object o = redisUtil.get("0A03:" + "result");
            if (o == null) {
                System.out.println("redis中不包含对应业务指令数据0A03Result，无法处理，抛弃！");
            }
            String result = (String) o;
            Command0B03 command0B03;
            switch (result) {
                case "OK":
                    command0B03 = new Command0B03(OperateResult.RESP_OK, 1);
                    channelHandlerContext.writeAndFlush(command0B03.encode());
                    System.out.println("刀具id上传服务器，发送成功响应！");
                    break;
                case "FAIL":
                    command0B03 = new Command0B03(OperateResult.RESP_FAIL, 1);
                    channelHandlerContext.writeAndFlush(command0B03.encode());
                    System.out.println("刀具id上传服务器，发送失败响应！");
                    break;
                case "TIMEOUT":
                    System.out.println("刀具id上传服务器，模拟超时不响应数据");
                    break;
                default:

            }
        } else if (deviceMessage instanceof Message0A05) {
            //换刀原因上传
            System.out.println("接受到换刀原因上传服务器指令" + ((Message0A05) deviceMessage).getChangeCause().desc());
            //需要测试三种不同的业务
            ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
            RedisUtil redisUtil = (RedisUtil) applicationContext.getBean("redisUtil");
            Object o = redisUtil.get("0A05:" + "result");
            if (o == null) {
                System.out.println("redis中不包含对应业务指令数据0A05Result，无法处理，抛弃！");
            }
            String result = (String) o;
            Command0B05 command0B05;
            switch (result) {
                case "OK":
                    command0B05 = new Command0B05(OperateResult.RESP_OK, 1);
                    channelHandlerContext.writeAndFlush(command0B05.encode());
                    System.out.println("换刀原因，发送成功响应！");
                    break;
                case "FAIL":
                    command0B05 = new Command0B05(OperateResult.RESP_FAIL, 1);
                    channelHandlerContext.writeAndFlush(command0B05.encode());
                    System.out.println("换刀原因，发送失败响应！");
                    break;
                case "TIMEOUT":
                    System.out.println("换刀原因，模拟超时不响应数据");
                    break;
                default:

            }
        } else if (deviceMessage instanceof Message0901) {
            System.out.println("接收到设置时间参数指令帧响应！" + ((Message0901) deviceMessage).getOperateResult().desc());
        } else if (deviceMessage instanceof Message0902) {
            System.out.println("接收到获取时间参数指令帧响应！" + ((Message0902) deviceMessage).getTimeParamer());
        } else if (deviceMessage instanceof Message0906) {
            System.out.println("接收到设置网口参数指令帧响应！" + ((Message0906) deviceMessage).getOperateResult().desc());
        } else if (deviceMessage instanceof Message0907) {
            System.out.println("接收到固件升级响应！" + ((Message0907) deviceMessage).getOperateResult().desc());
        } else if (deviceMessage instanceof Message0908) {
            System.out.println("接收到获取固件升级响应！" + deviceMessage.toString());
        } else {
            System.out.println("不支持的响应类型！");
        }
    }
}
