package com.wakoo.simplechat;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;

public final class ProfileCatalog {
    public static final ProfileCatalog SINGLETON = new ProfileCatalog();
    private ProfileCatalog()  {
        final MsgDisplay load_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно прочитать сведения из файла конфигурации");
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
            pairgen.initialize(2048);
            SettingsData.SINGLETON.my_keypair = pairgen.genKeyPair();
            SaveSettings();
        } catch (NoSuchAlgorithmException noalgoexcp) {
            save_display.DisplayMessage("У вас не поддерживается алгоритм RSA");
        }
    }

    private static Path settings_root;

    private static final MsgDisplay save_display = new ErrorDisplay("Ошибка при работе с файлом конфигурации", "Невозможно сохранить сведения в файл конфигурации");

    public String getNickname() { return SettingsData.SINGLETON.nickname;}
    public void   setNickname(String nickname) { SettingsData.SINGLETON.nickname = nickname;}

    public int getListenPort() {return SettingsData.SINGLETON.listen_port;}
    public void setListenPort(int port) {SettingsData.SINGLETON.listen_port = port;}

    public PrivateKey getClosedKey() {return SettingsData.SINGLETON.my_keypair.getPrivate();}
    public PublicKey getOpenKey() {return SettingsData.SINGLETON.my_keypair.getPublic();}

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
            try (FileInputStream map_fi = new FileInputStream(settings_root.resolve("settings").toFile())) {
                try (ObjectInputStream map_is = new ObjectInputStream(map_fi)) {
                   map = (HashMap<String, PublicKey>) map_is.readObject();
                } catch (IOException ioexcp) {
                    save_display.DisplayMessage("Невозможно записать объект настроек в файл");
                } catch (ClassNotFoundException e) {
                    save_display.DisplayMessage("Не найден класс");
                }
            } catch (FileNotFoundException e) {
                map = new HashMap<>();
            } catch (IOException e) {
                save_display.DisplayMessage("Ошибка ввода-вывода");
            }
        }

        public boolean CheckNicknameKeyMapping(final String nickname, final PublicKey okey) {
            if (map.containsKey(nickname)) {
                return map.get(nickname).equals(okey);
            } else {
                map.put(nickname, okey);
                SaveStorage();
                return true;
            }
        }

        private void SaveStorage() {
            try (FileOutputStream map_fo = new FileOutputStream(settings_root.resolve("settings").toFile(), false)) {
                try (ObjectOutputStream map_os = new ObjectOutputStream(map_fo)) {
                    map_os.writeObject(map);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
