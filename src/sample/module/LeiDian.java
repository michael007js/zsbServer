package sample.module;

import sample.bean.LeiDianSimulatorBean;
import utils.LogUtils;
import utils.MichaelUtils;
import utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class LeiDian {
    private static LeiDian instance;
    private String installDirectory = "E:/leidian/LDPlayer4/";
    private String commandPath;

    public static LeiDian getInstance() {
        synchronized (LeiDian.class) {
            if (instance == null) {
                instance = new LeiDian();
            }
            return instance;
        }
    }

    /**
     * 设安装目录
     */
    public void setInstallDirectory(String installDirectory) {
        if (StringUtils.isEmpty(installDirectory)) {
            commandPath = this.installDirectory + "ldconsole.exe";
            return;
        }
        this.installDirectory = installDirectory;
        if (new File(this.installDirectory + "ldconsole.exe").exists()) {
            commandPath = this.installDirectory + "ldconsole.exe";
            return;
        }
        commandPath = this.installDirectory + "dnconsole.exe";
        LogUtils.e(installDirectory);
    }

    /**
     * 获取模拟器列表
     * 0,雷电模拟器,0,0,0,-1,-1
     * 索引，标题，顶层窗口句柄，绑定窗口句柄，是否进入android，进程PID，VBox进程PID
     */
    public ArrayList<LeiDianSimulatorBean> getSimulatorList() {
        String command = commandPath + " list2";
        LogUtils.e(command);
        String res = MichaelUtils.launchCmd(commandPath + " list2");
        ArrayList<LeiDianSimulatorBean> list = new ArrayList<>();
        String[] temp = res.split("\n");
        for (int i = 0; i < temp.length; i++) {
            String[] temp2 = temp[i].split(",");
            if (temp2.length == 7) {
                LeiDianSimulatorBean bean = new LeiDianSimulatorBean();
                bean.setPosition(Integer.parseInt(temp2[0]));
                bean.setName(temp2[1]);
                bean.setParentWindowHandle(Long.parseLong(temp2[2]));
                bean.setBindWindowHandle(Long.parseLong(temp2[3]));
                bean.setAndroid("1".equals(temp2[4]));
                bean.setPid(Long.parseLong(temp2[5]));
                bean.setvBoxPid(Long.parseLong(temp2[6]));
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 创建模拟器
     *
     * @param name 模拟器名
     */
    public String add(String name) {
        String command = commandPath + " add --name " + name;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 删除模拟器
     *
     * @param position 索引
     */
    public String removeByIndex(int position) {
        String command = commandPath + " remove --index " + position;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 删除模拟器
     * 无法删除第0个，不知是何原因
     *
     * @param name 模拟器名
     */
    public String removeByName(String name) {
        String command = commandPath + " remove --name " + name;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 启动模拟器
     *
     * @param position 索引
     */
    public String launch(int position) {
        String command = commandPath + " launch --index " + position;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 重启模拟器
     *
     * @param position 索引
     */
    public String reboot(int position) {
        String command = commandPath + " reboot --index " + position;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 退出模拟器
     *
     * @param position 索引 -1时退出所有模拟器
     */
    public String quit(int position) {
        String command = commandPath + " " + (position > -1 ? "quit" : "quitall") + " --index " + position;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改分辨率
     *
     * @param position 索引
     * @param width    宽
     * @param height   高
     * @param ppi      ppi
     */
    public String modifyDisplay(int position, int width, int height, int ppi) {
        String command = commandPath + " modify --index " + position + " --resolution " + width + "," + height + "," + ppi;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改CPU配置
     *
     * @param position 索引
     * @param core     CPU核心数
     */
    public String modifyCpu(int position, int core) {
        String command = commandPath + " modify --index " + position + " --cpu " + core;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改内存
     *
     * @param position 索引
     * @param memory   内存
     */
    public String modifyMemory(int position, int memory) {
        String command = commandPath + " modify --index " + position + " --memory " + memory;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改手机厂商
     *
     * @param position     索引
     * @param manufacturer 手机厂商
     */
    public String modifyManufacturer(int position, String manufacturer) {
        String command = commandPath + " modify --index " + position + " --manufacturer " + manufacturer;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改手机型号
     *
     * @param position 索引
     * @param model    手机型号
     */
    public String modifyModel(int position, String model) {
        String command = commandPath + " modify --index " + position + " --model " + model;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改IMSI
     *
     * @param position 索引
     * @param auto     自动获取
     * @param imsi     auto为true时随意
     */
    public String modifyImsi(int position, boolean auto, long imsi) {
        String command = commandPath + " modify --index " + position + " --imsi " + (auto ? "auto" : imsi);
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改SIM卡序列号
     *
     * @param position  索引
     * @param auto      自动获取
     * @param simSerial auto为true时随意
     */
    public String modifySimSerial(int position, boolean auto, long simSerial) {
        String command = commandPath + " modify --index " + position + " --imsi " + (auto ? "auto" : simSerial);
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改androidId
     *
     * @param position  索引
     * @param auto      自动获取
     * @param androidId auto为true时随意
     */
    public String modifyAndroidId(int position, boolean auto, long androidId) {
        String command = commandPath + " modify --index " + position + " --imsi " + (auto ? "auto" : androidId);
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改电话号码
     *
     * @param position 索引
     * @param pnumber  电话号码
     */
    public String modifyPhoneNumber(int position, String pnumber) {
        String command = commandPath + " modify --index " + position + " --pnumber " + pnumber;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 修改mac
     *
     * @param position 索引
     * @param mac      mac
     */
    public String modifyMac(int position, String mac) {
        String command = commandPath + " modify --index " + position + " --mac " + mac;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * CPU使用率
     *
     * @param position 索引
     * @param percent  CPU使用率 0-100
     */
    public String downCpu(int position, int percent) {
        String command = commandPath + " downcpu --index " + position + " --rate " + percent;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 设置FPS
     *
     * @param position 索引
     * @param fps      fps 0-60
     */
    public String fps(int position, int fps) {
        String command = commandPath + " globalsetting --index " + position + " --fps " + fps;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 设置音频
     *
     * @param position 索引
     * @param enable   1开器 0关闭
     */
    public String audio(int position, int enable) {
        String command = commandPath + " globalsetting --index " + position + " --audio " + enable;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 设置音频
     *
     * @param position 索引
     * @param enable   1开器 0关闭
     */
    public String fastpaly(int position, int enable) {
        String command = commandPath + " globalsetting --index " + position + " --fastpaly " + enable;
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }


    public static final String MANUFACTURER = "ro.product.manufacturer";//厂商
    public static final String MODEL = "ro.product.model";//型号
    public static final String IMEI = "phone.imei";//IMEI
    public static final String IMSI = "phone.imsi";//IMSI
    public static final String PHONE_NUMBER = "phone.number";//手机号码
    public static final String SIM_SERIAL = "phone.simserial";//sim卡序列号
    public static final String ANDROID_ID = "phone.androidid";//android id

    /**
     * 获取属性
     *
     * @param position 索引
     * @param type     类型
     * @return
     */
    public String getProParameter(int position, String type) {
        String command = commandPath + " getprop --index " + position + " --key \"" + type + "\"";
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }

    /**
     * 设置属性
     *
     * @param position 索引
     * @param type     类型字段
     * @param value    值
     */
    public String setProParameter(int position, String type, String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        String command = commandPath + " setprop --index " + position + " --key \"" + type + "\" --value \"" + value + "\"";
        LogUtils.e(command);
        return MichaelUtils.launchCmd(command);
    }


}
