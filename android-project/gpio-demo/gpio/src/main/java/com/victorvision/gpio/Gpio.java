package com.victorvision.gpio;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Gpio {

    private static String _gpioBasePath = "/sys/class/gpio/";

    private IoPin Pin;
    public IoPin getPin(){
        return Pin;
    }
    private void setPin(IoPin pin){
        Pin = pin;
    }

    private IoDirection Direction = IoDirection.In;
    public IoDirection getDirection(){
        return Direction;
    }
    public void setDirection(IoDirection direction){
        Direction = direction;
        switch (Direction){
            case In:
                setGpioIn(Pin.getValue());
                break;

            case Out:
                setGpioOut(Pin.getValue());
                setValue(Value);
                break;
        }
    }

    private IoValue Value = IoValue.Low;
    public IoValue getValue() {
        switch (Direction){
            case In:
                String currentValue = getGpioValue(Pin.getValue());
                return IoValue.fromIntString(currentValue);

            case Out:
            default:
                return Value;
        }
    }
    public void setValue(IoValue value){
        Value = value;
        switch (Direction) {
            case In:
                /* Do nothing */
                break;

            case Out:
                switch (value){
                    case High:
                        setGpioHigh(Pin.getValue());
                        break;

                    case Low:
                        setGpioLow(Pin.getValue());
                        break;
                }
                break;
        }


    }

    public Gpio(IoPin pin) {
        setPin(pin);
        initGpio(pin.getValue());
        setDirection(IoDirection.In);
    }


    public Gpio(IoPin pin, IoDirection direction) {
        this(pin);
        setDirection(direction);
    }

    /**
     * 创建GPIO操作实例
     * @param gpio gpio引脚编号
     * **/
    public static void initGpio(String gpio) {
        String path = "echo " + gpio + " > " + _gpioBasePath + "export";
        execRootCmd(path);
    }

    /**
     * GPIO 设置 out
     * @param gpio gpio引脚编号
     * **/
    public static void setGpioOut(String gpio) {
        execRootCmd("echo out > " + getGpioPath(gpio) + "/direction");
    }

    /**
     * GPIO 设置 in
     * @param gpio gpio引脚编号
     * **/
    public static void setGpioIn(String gpio) {
        execRootCmd("echo in > " + getGpioPath(gpio) + "/direction");
    }

    /**
     * GPIO 设置 高电位
     * @param gpio gpio引脚编号
     * **/
    public static void setGpioHigh(String gpio) {
        execRootCmd("echo 1 > " + getGpioPath(gpio) + "/value");
    }

    /**
     * GPIO 设置 低电位
     * @param gpio gpio引脚编号
     * **/
    public static void setGpioLow(String gpio) {
        execRootCmd("echo 0 > " + getGpioPath(gpio) + "/value");
    }

    /**
     * GPIO 获取当前电位
     * @param gpio gpio引脚编号
     * **/
    public static String getGpioValue(String gpio) {
        return getGpioString("" + getGpioPath(gpio) + "/value");
    }

    private static String getGpioName(String gpio) {
        return "gpio" + gpio;
    }

    private static String getGpioPath(String gpio) {
        return _gpioBasePath + getGpioName(gpio);
    }

    private static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            for(String line = null; (line = dis.readLine()) != null; result = result
                    + line) {
            }
            p.waitFor();
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String getGpioString(String path) {
        String defString = "0";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            defString = reader.readLine();
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        return defString;
    }



    public enum IoPin{
        Pin1("13"),
        Pin2("12"),
        Pin3("11"),
        Pin4("75"),
        Pin5("74"),
        Pin6("73");

        private String _pinId;

        IoPin(String pinId) {
            this._pinId = pinId;
        }

        public String getValue() {
            return _pinId;
        }
    }

    public enum IoDirection{
        In("in"),
        Out("out");

        private String _value;

        IoDirection(String value) {
            this._value = value;
        }

        public String getValue() {
            return _value;
        }
    }

    public enum IoValue{
        High(1),
        Low(0),
        Undefined(-1);

        private int _value;

        IoValue(int value) {
            this._value = value;
        }

        public int getValue() {
            return _value;
        }

        public static IoValue fromInt(int intValue){
            switch (intValue){
                case 0:
                    return Low;

                case 1:
                    return High;

                default:
                    return Undefined;
            }
        }

        public static IoValue fromIntString(String intValue){
            switch (intValue){
                case "0":
                    return Low;

                case "1":
                    return High;

                default:
                    return Undefined;
            }
        }
    }
}