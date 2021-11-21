package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public abstract class MessageProcessor {
    protected Signature sign;
    protected PublicKey okey;
    protected String nickname;
    protected ByteBuffer remain;
    protected boolean sign_ok;

    String marker = "";

    MessageProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in, ByteBuffer check_it) {
        remain = remain_in;
        nickname = GetString();
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(okey_arr);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            try {
                okey = kf.generatePublic(x509);
                sign = Signature.getInstance("SHA256withRSA");
                sign.initVerify(this.okey);
                sign.update(check_it);
                sign_ok = sign.verify(sign_arr) && ProfileCatalog.OpenKeyStorage.SINGLETON.CheckNicknameKeyMapping(nickname, okey);
            } catch (InvalidKeySpecException e) {
                disp.DisplayMessage("Не получается сгенерировать открытый ключ");
            } catch (InvalidKeyException e) {
                disp.DisplayMessage("Ключ не проходит для проверки ЭЦП");
            } catch (SignatureException e) {
                disp.DisplayMessage("Не получается проверить ЭЦП");
            }
        } catch (NoSuchAlgorithmException e) {
            disp.DisplayMessage("Нет такого алгоритма");
        }
    }

    protected static final MsgDisplay disp = new ErrorDisplay("Ошибка при разборе сообщения");

    protected String GetString() {
        final int len = remain.getInt();
        ByteBuffer bb = ByteBuffer.allocate(len);
        for (int i=0;i < len;i++) bb.put(remain.get());
        bb.flip();
        return StandardCharsets.UTF_8.decode(bb).toString();
    }

    public boolean GetSignOk() {return sign_ok;}
}
