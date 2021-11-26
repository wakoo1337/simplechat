package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.messages.MessageTypes;
import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.messages.Message;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;

public abstract class MessageProcessor implements Message {
    protected Signature sign;
    protected PublicKey okey;
    protected String nickname;
    protected ByteBuffer remain;
    protected boolean sign_ok;

    public String marker = "";

    public MessageProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in, ByteBuffer check_it) {
        remain = remain_in;
        nickname = getString();
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(okey_arr);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            try {
                okey = kf.generatePublic(x509);
                sign = Signature.getInstance("SHA256withRSA");
                sign.initVerify(this.okey);
                sign.update(check_it);
                sign_ok = sign.verify(sign_arr) && ProfileCatalog.OpenKeyStorage.SINGLETON.checkNicknameKeyMapping(nickname, okey);
            } catch (InvalidKeySpecException invkeyspecexcp) {
                disp.displayMessage(invkeyspecexcp,"Не получается сгенерировать открытый ключ");
            } catch (InvalidKeyException invkeyexcp) {
                disp.displayMessage(invkeyexcp,"Ключ не проходит для проверки ЭЦП");
            } catch (SignatureException sigexcp) {
                disp.displayMessage(sigexcp,"Не получается проверить ЭЦП");
            }
        } catch (NoSuchAlgorithmException noalgoexcp) {
            disp.displayMessage(noalgoexcp, "Нет такого алгоритма");
        }
    }

    protected static final MsgDisplay disp = new ErrorDisplay("Ошибка при разборе сообщения");

    protected String getString() {
        final int len = remain.getInt();
        ByteBuffer bb = ByteBuffer.allocate(len);
        for (int i=0;i < len;i++) bb.put(remain.get());
        bb.flip();
        return StandardCharsets.UTF_8.decode(bb).toString();
    }

    public boolean getSignOk() {return sign_ok;}

    public List<ByteBuffer> export() {
        ByteBuffer[] bba = new ByteBuffer[2];
        bba[0] = ByteBuffer.allocate(8);
        bba[0].order(ByteOrder.LITTLE_ENDIAN);
        bba[0].putInt(MessageTypes.magic);
        bba[0].putInt(remain.limit());
        bba[1] = remain.asReadOnlyBuffer();
        return Arrays.asList(bba);
    }
}
