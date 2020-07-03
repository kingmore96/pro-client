package cn.net.icomp.protestdemo.jfdj;

import cn.net.icomp.hongyan.protocol_jfdj.command.Command0801;
import cn.net.icomp.hongyan.protocol_jfdj.command.Command0802;
import cn.net.icomp.hongyan.protocol_jfdj.command.Command0806;
import cn.net.icomp.hongyan.protocol_jfdj.command.Command0808;
import cn.net.icomp.hongyan.protocol_jfdj.enums.TimeType;
import cn.net.icomp.hongyan.protocol_jfdj.enums.UpdateType;
import cn.net.icomp.protestdemo.common.redis.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jfdj")
public class TestController {

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 控制修改刀具id上传服务器的业务流程走向
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/update0A03", method = RequestMethod.POST)
    public String update0A03(@RequestBody JSONObject params) {
        try {
            System.out.println("接收到参数：" + params.toJSONString());
            String operationType = params.getString("operation_type");
            redisUtil.set("0A03:result", operationType);
            return "ok";
        } catch (Exception e) {
            System.out.println("未知异常！" + e);
            return "fail";
        }
    }

    /**
     * 控制换刀原因上传服务器的业务流程走向
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/update0A05", method = RequestMethod.POST)
    public String update0A05(@RequestBody JSONObject params) {
        try {
            System.out.println("接收到参数：" + params.toJSONString());
            String operationType = params.getString("operation_type");
            redisUtil.set("0A05:result", operationType);
            return "ok";
        } catch (Exception e) {
            System.out.println("未知异常！" + e);
            return "fail";
        }
    }


    @RequestMapping(value = "/operate", method = RequestMethod.POST)
    public String operate(@RequestBody JSONObject params) {
        System.out.println("接收到参数：" + params.toJSONString());
        if (ClientHandler.ctx == null) {
            System.out.println("网络未连接，无法处理！");
            return "fail";
        }
        try {
            String allType = params.getString("all_type");
            switch (allType) {
                //设置时间参数
                case "set_time":
                    int setType = params.getIntValue("set_type");
                    int timeValue = params.getIntValue("time_value");
                    Command0801 command0801;
                    if (setType == TimeType.CENTER_INVETORY_TIME.value()) {
                        command0801 = new Command0801(TimeType.CENTER_INVETORY_TIME, timeValue, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0801.encode());
                        System.out.println("成功发送设置时间参数指令！");
                    } else if (setType == TimeType.CENTER_RESPONSE_TIMEOUT.value()) {
                        command0801 = new Command0801(TimeType.CENTER_RESPONSE_TIMEOUT, timeValue, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0801.encode());
                        System.out.println("成功发送设置时间参数指令！");
                    } else if (setType == TimeType.KEYBOARD_GREEN_HOLDTIME.value()) {
                        command0801 = new Command0801(TimeType.KEYBOARD_GREEN_HOLDTIME, timeValue, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0801.encode());
                        System.out.println("成功发送设置时间参数指令！");
                    } else if (setType == TimeType.KEYBOARD_LIGHT_FREQUENCY.value()) {
                        command0801 = new Command0801(TimeType.KEYBOARD_LIGHT_FREQUENCY, timeValue, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0801.encode());
                        System.out.println("成功发送设置时间参数指令！");
                    } else {
                        command0801 = new Command0801(TimeType.KEYBOARD_RESPONSE_TIMEOUT, timeValue, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0801.encode());
                        System.out.println("成功发送设置时间参数指令！");
                    }
                    break;
                case "get_time":
                    int getType = params.getIntValue("get_type");
                    Command0802 command0802;
                    if (getType == TimeType.CENTER_INVETORY_TIME.value()) {
                        command0802 = new Command0802(TimeType.CENTER_INVETORY_TIME, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0802.encode());
                        System.out.println("成功发送获取时间参数指令！");
                    } else if (getType == TimeType.CENTER_RESPONSE_TIMEOUT.value()) {
                        command0802 = new Command0802(TimeType.CENTER_RESPONSE_TIMEOUT, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0802.encode());
                        System.out.println("成功发送获取时间参数指令！！");
                    } else if (getType == TimeType.KEYBOARD_GREEN_HOLDTIME.value()) {
                        command0802 = new Command0802(TimeType.KEYBOARD_GREEN_HOLDTIME, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0802.encode());
                        System.out.println("成功发送获取时间参数指令！！");
                    } else if (getType == TimeType.KEYBOARD_LIGHT_FREQUENCY.value()) {
                        command0802 = new Command0802(TimeType.KEYBOARD_LIGHT_FREQUENCY, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0802.encode());
                        System.out.println("成功发送获取时间参数指令！！");
                    } else {
                        command0802 = new Command0802(TimeType.KEYBOARD_RESPONSE_TIMEOUT, 1);
                        ChannelHandlerContext ctx = ClientHandler.ctx;
                        ctx.writeAndFlush(command0802.encode());
                        System.out.println("成功发送获取时间参数指令！！");
                    }
                    break;
                case "set_net":
                    String ip = params.getString("ip");
                    String mask = params.getString("mask");
                    String gateway = params.getString("gateway");
                    String port = params.getString("port");
                    String mac = params.getString("mac");
                    Command0806 command0806 = new Command0806(ip, mask, gateway, port, mac);
                    ChannelHandlerContext ctx = ClientHandler.ctx;
                    ctx.writeAndFlush(command0806.encode());
                    System.out.println("成功发送设置网口参数指令！！");
                    break;
                case "get_firware":
                    int firewareType = params.getIntValue("fireware_type");
                    Command0808 command0808;
                    if (firewareType == UpdateType.UPDATE_KEYBOARD.value()) {
                        command0808 = new Command0808(UpdateType.UPDATE_KEYBOARD, 1);
                    } else {
                        command0808 = new Command0808(UpdateType.UPDATE_READER, 1);
                    }
                    ChannelHandlerContext ctx1 = ClientHandler.ctx;
                    ctx1.writeAndFlush(command0808.encode());
                    System.out.println("成功发送获取固件版本指令！！");
                    break;
                default:

            }
            return "ok";
        } catch (Exception e) {
            System.out.println("未知异常！" + e);
            return "fail";
        }
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public String connect(@RequestBody JSONObject params) {
        System.out.println("接收到参数：" + params.toJSONString());
        String ip = params.getString("ip");
        int port = params.getIntValue("port");
        NettyClient.connect(ip, port);
        return "ok";
    }
}
