package com.wakoo.simplechat;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public final class SettingsStorage {
    public static final SettingsStorage SINGLETON = new SettingsStorage();
    private SettingsStorage()  {
        final MsgDisplay load_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно прочитать сведения из файла конфигурации");
        SettingsData.SINGLETON = new SettingsData();
        settings_root = Path.of(System.getProperty("user.home") + File.separator + "simplechat");
        if (Files.isDirectory(settings_root)) {
            try (FileInputStream settings_fi = new FileInputStream(settings_root.resolve("settings").toFile())) {
                try (ObjectInputStream settings_in = new ObjectInputStream(settings_fi)) {
                    SettingsData.SINGLETON = (SettingsData) settings_in.readObject();
                } catch (ClassNotFoundException classnfexcp) {
                    load_display.DisplayMessage("У вас нет класса для объекта настроек");
                }
            } catch (FileNotFoundException fnfexcp) {
                load_display.DisplayMessage("Не удалось найти файл с объектом настроек");
            } catch (IOException ioexcp) {
                load_display.DisplayMessage("Не удалось прочитать файл с объектом настроек");
            }
        } else {
            try {
                Files.createDirectory(settings_root);
            } catch (IOException ioexcp) {
                save_display.DisplayMessage("Невозможно создать каталог для хранения настроек");
            }
            NewKeyPair();
        }
    }
    public void SaveSettings() {
        try (FileOutputStream settings_fw = new FileOutputStream(settings_root.resolve("settings").toFile(), false)) {
            try (ObjectOutputStream settings_wr = new ObjectOutputStream(settings_fw)) {
                settings_wr.writeObject(SettingsData.SINGLETON);
            } catch (IOException ioexcp) {
                save_display.DisplayMessage("Невозможно записать объект настроек в файл");
            }
        } catch (IOException ioexcp) {
            save_display.DisplayMessage("Невозможно записать пару ключей в файл");
        }
    }

    public void NewKeyPair() {
        try {
            KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
            SettingsData.SINGLETON.my_keypair = pairgen.genKeyPair();
            SaveSettings();
        } catch (NoSuchAlgorithmException noalgoexcp) {
            save_display.DisplayMessage("У вас не поддерживается алгоритм RSA");
        }
    }

    private final Path settings_root;

    private final MsgDisplay save_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно сохранить сведения в файл конфигурации");

    public String getNickname() { return SettingsData.SINGLETON.nickname;}
    public void   setNickname(String nickname) { SettingsData.SINGLETON.nickname = nickname;}

    public int getListenPort() {return SettingsData.SINGLETON.listen_port;}
    public void setListenPort(int port) {SettingsData.SINGLETON.listen_port = port;}

    private static final class SettingsData implements Serializable {
        public static transient SettingsData SINGLETON;

        public String nickname;
        public int listen_port;
        public KeyPair my_keypair;

        public SettingsData() {
            nickname = "user";
            listen_port = 27600;
        }
    }
}
