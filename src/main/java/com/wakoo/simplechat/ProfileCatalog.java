package com.wakoo.simplechat;

import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.HashMap;

public final class ProfileCatalog {
    public static final ProfileCatalog SINGLETON = new ProfileCatalog();

    private ProfileCatalog() {
        settings_root = Path.of(System.getProperty("user.home") + File.separator + "simplechat");
        if (Files.isDirectory(settings_root)) {
            try (FileInputStream settings_fi = new FileInputStream(settings_root.resolve("settings").toFile())) {
                try (ObjectInputStream settings_in = new ObjectInputStream(settings_fi)) {
                    SettingsData.SINGLETON = (SettingsData) settings_in.readObject();
                } catch (ClassNotFoundException classnfexcp) {
                    load_display.displayMessage(classnfexcp, "У вас нет класса для объекта настроек");
                }
            } catch (FileNotFoundException fnfexcp) {
                load_display.displayMessage(fnfexcp, "Не удалось найти файл с объектом настроек");
            } catch (IOException ioexcp) {
                load_display.displayMessage(ioexcp, "Не удалось прочитать файл с объектом настроек");
            }
        } else {
            try {
                Files.createDirectory(settings_root);
            } catch (IOException ioexcp) {
                save_display.displayMessage(ioexcp, "Невозможно создать каталог для хранения настроек");
            }
            newKeyPair();
        }
    }

    public void saveSettings() {
        try (FileOutputStream settings_fw = new FileOutputStream(settings_root.resolve("settings").toFile(), false)) {
            try (ObjectOutputStream settings_wr = new ObjectOutputStream(settings_fw)) {
                settings_wr.writeObject(SettingsData.SINGLETON);
            }
        } catch (IOException ioexcp) {
            save_display.displayMessage(ioexcp, "Невозможно записать пару ключей в файл");
        }
    }

    public void newKeyPair() {
        try {
            KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
            pairgen.initialize(2048);
            SettingsData.SINGLETON.my_keypair = pairgen.genKeyPair();
            saveSettings();
        } catch (NoSuchAlgorithmException noalgoexcp) {
            save_display.displayMessage(noalgoexcp, "У вас не поддерживается алгоритм RSA");
        }
    }

    private static Path settings_root;
    private static final MsgDisplay save_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно сохранить сведения в файл конфигурации");
    private static final MsgDisplay load_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно загрузить сведения из файла конфигурации");

    public String getNickname() {
        return SettingsData.SINGLETON.nickname;
    }

    public void setNickname(String nickname) {
        SettingsData.SINGLETON.nickname = nickname;
    }

    public int getListenPort() {
        return SettingsData.SINGLETON.listen_port;
    }

    public void setListenPort(int port) {
        SettingsData.SINGLETON.listen_port = port;
    }

    public PrivateKey getClosedKey() {
        return SettingsData.SINGLETON.my_keypair.getPrivate();
    }

    public PublicKey getOpenKey() {
        return SettingsData.SINGLETON.my_keypair.getPublic();
    }

    private static final class SettingsData implements Serializable {
        public static transient SettingsData SINGLETON = new SettingsData();

        public String nickname;
        public int listen_port;
        public KeyPair my_keypair;

        private SettingsData() {
            nickname = "user";
            listen_port = 27600;
        }
    }

    public static final class OpenKeyStorage {
        public static final OpenKeyStorage SINGLETON = new OpenKeyStorage();

        HashMap<String, PublicKey> map;

        private OpenKeyStorage() {
            try (FileInputStream map_fi = new FileInputStream(settings_root.resolve("keys").toFile())) {
                try (ObjectInputStream map_is = new ObjectInputStream(map_fi)) {
                    map = (HashMap<String, PublicKey>) map_is.readObject();
                } catch (ClassNotFoundException cnfexcp) {
                    load_display.displayMessage(cnfexcp, "Не найден класс хранилища открытых ключей");
                }
            } catch (FileNotFoundException fnfexcp) {
                map = new HashMap<>();
            } catch (IOException ioexcp) {
                load_display.displayMessage(ioexcp, "Невозможно прочитать хранилище открытых ключей из файла");
            }
        }

        public boolean checkNicknameKeyMapping(final String nickname, final PublicKey okey) {
            if (map.containsKey(nickname)) {
                return map.get(nickname).equals(okey);
            } else {
                map.put(nickname, okey);
                saveStorage();
                return true;
            }
        }

        private void saveStorage() {
            try (FileOutputStream map_fo = new FileOutputStream(settings_root.resolve("keys").toFile(), false)) {
                try (ObjectOutputStream map_os = new ObjectOutputStream(map_fo)) {
                    map_os.writeObject(map);
                }
            } catch (FileNotFoundException fnfexcp) {
                save_display.displayMessage(fnfexcp, "Не найден каталог для сохранения хранилища открытых ключей");
            } catch (IOException ioexcp) {
                save_display.displayMessage(ioexcp, "Невозможно записать хранилище открытых ключей в файл");
            }
        }
    }
}
