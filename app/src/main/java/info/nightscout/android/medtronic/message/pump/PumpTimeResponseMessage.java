package info.nightscout.android.medtronic.message.pump;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import info.nightscout.android.BuildConfig;
import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.message.MessageUtils;
import info.nightscout.android.utils.HexDump;

/**
 * Created by lgoedhart on 27/03/2016.
 */
public class PumpTimeResponseMessage extends MedtronicSendMessageResponseMessage {
    private static final String TAG = PumpTimeResponseMessage.class.getSimpleName();

    private Date pumpTime;

    protected PumpTimeResponseMessage(MedtronicCnlSession pumpSession, byte[] payload) throws EncryptionException, ChecksumException {
        super(pumpSession, payload);

        /**
         2017-03-11 11:50:29 +0000 UTC I ContourNextLinkMessage  :0    READ:
         2017-03-11 11:50:29 +0000 UTC I ContourNextLinkMessage  :0               ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?A ?B ?C ?D ?E ?F
         2017-03-11 11:50:29 +0000 UTC I ContourNextLinkMessage  :0    0x00000000 51 03 30 30 30 30 30 30 00 00 00 00 00 00 00 00 Q.000000........
         2017-03-11 11:50:29 +0000 UTC I ContourNextLinkMessage  :0    0x00000010 00 00 81 05 00 00 00 00 00 00 00 00 0F 00 00 00 ................
         2017-03-11 11:50:29 +0000 UTC I ContourNextLinkMessage  :0    0x00000020 D2 55 0D 00 04 00 00 00 00 03 00 01 02 02 58 03 .U............X.

         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0    READ:
         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0               ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?A ?B ?C ?D ?E ?F
         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0    0x00000000 51 03 30 30 30 30 30 30 00 00 00 00 00 00 00 00 Q.000000........
         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0    0x00000010 00 00 80 D0 8E 00 80 00 00 00 00 00 28 00 00 00 ............(...
         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0    0x00000020 D8 55 26 00 06 EB 2F 11 EE 45 F7 23 00 01 0B 10 .U&.../..E.#....
         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0    0x00000030 82 06 F7 23 00 02 0C 04 0E A9 BF 51 60 15 80 A7 ...#.......Q`...
         2017-03-11 11:50:31 +0000 UTC I ContourNextLinkMessage  :0    0x00000040 20 65 39 1A EC 5A 4E 1D 29                      .e9..ZN.)

         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0    DECRYPTED:
         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0               ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?A ?B ?C ?D ?E ?F
         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0    0x00000000 51 03 30 30 30 30 30 30 00 00 00 00 00 00 00 00 Q.000000........
         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0    0x00000010 00 00 80 D0 8E 00 80 00 00 00 00 00 28 00 00 00 ............(...
         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0    0x00000020 D8 55 26 00 06 EB 2F 11 EE 45 F7 23 00 01 0B 10 .U&.../..E.#....
         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0    0x00000030 82 06 F7 23 00 02 0C 04 0E 01 04 07 01 80 8D DF ...#............
         2017-03-11 11:50:31 +0000 UTC I MedtronicResponseMessage  :0    0x00000040 24 9F C8 D0 7F 7A 1D 1D 29                      $....z..)
         2017-03-11 11:50:31 +0000 UTC I PumpTimeResponseMessage  :0    PAYLOAD:
         2017-03-11 11:50:31 +0000 UTC I PumpTimeResponseMessage  :0               ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7
         2017-03-11 11:50:31 +0000 UTC I PumpTimeResponseMessage  :0    0x00000000 80 8D DF 24 9F C8 D0 7F                         ...$....

         Steam with wrong (old) key
         2017-03-11 11:48:04 +0000 UTC I ContourNextLinkMessage  :0    READ:
         2017-03-11 11:48:04 +0000 UTC I ContourNextLinkMessage  :0               ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?A ?B ?C ?D ?E ?F
         2017-03-11 11:48:04 +0000 UTC I ContourNextLinkMessage  :0    0x00000000 51 03 30 30 30 30 30 30 00 00 00 00 00 00 00 00 Q.000000........
         2017-03-11 11:48:04 +0000 UTC I ContourNextLinkMessage  :0    0x00000010 00 00 81 04 00 00 00 00 00 00 00 00 0F 00 00 00 ................
         2017-03-11 11:48:04 +0000 UTC I ContourNextLinkMessage  :0    0x00000020 D1 55 0D 00 04 00 00 00 00 03 00 01 02 02 58 03 .U............X.
         2017-03-11 11:48:07 +0000 UTC I ContourNextLinkMessage  :0    READ:
         2017-03-11 11:48:07 +0000 UTC I ContourNextLinkMessage  :0               ?0 ?1 ?2 ?3 ?4 ?5 ?6 ?7 ?8 ?9 ?A ?B ?C ?D ?E ?F
         2017-03-11 11:48:07 +0000 UTC I ContourNextLinkMessage  :0    0x00000000 51 03 30 30 30 30 30 30 00 00 00 00 00 00 00 00 Q.000000........
         2017-03-11 11:48:07 +0000 UTC I ContourNextLinkMessage  :0    0x00000010 00 00 80 CE 8E 00 80 00 00 00 00 00 0D 00 00 00 ................
         2017-03-11 11:48:07 +0000 UTC I ContourNextLinkMessage  :0    0x00000020 24 55 0B 00 00 00 02 00 00 03 00 00 7D 65       $U..........}e
         2017-03-11 11:48:07 +0000 UTC W PumpTimeResponseMessage  :0    Invalid message received for getPumpTime
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    Unexpected Message
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    info.nightscout.android.medtronic.exception.UnexpectedMessageException: Invalid message received for getPumpTime
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.message.PumpTimeResponseMessage.<init>(PumpTimeResponseMessage.java:31)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.message.PumpTimeRequestMessage.getResponse(PumpTimeRequestMessage.java:49)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.message.PumpTimeRequestMessage.send(PumpTimeRequestMessage.java:39)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.message.PumpTimeRequestMessage.send(PumpTimeRequestMessage.java:15)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.message.ContourNextLinkRequestMessage.send(ContourNextLinkRequestMessage.java:26)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.MedtronicCnlReader.getPumpTime(MedtronicCnlReader.java:177)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at info.nightscout.android.medtronic.service.MedtronicCnlIntentService.onHandleIntent(MedtronicCnlIntentService.java:236)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at android.app.IntentService$ServiceHandler.handleMessage(IntentService.java:66)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at android.os.Handler.dispatchMessage(Handler.java:102)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at android.os.Looper.loop(Looper.java:148)
         2017-03-11 11:48:07 +0000 UTC W MedtronicCnlReader  :0    	at android.os.HandlerThread.run(HandlerThread.java:61)
         2017-03-11 11:48:07 +0000 UTC I UsbHidDriver  :0    Wrote amt=64 attempted=64

         */
        if (this.encode().length < (61 + 8)) {
            // Invalid message. Return an invalid date.
            // TODO - deal with this more elegantly
            Log.e(TAG, "Invalid message received for getPumpTime");
            pumpTime = new Date();
        } else {
            ByteBuffer dateBuffer = ByteBuffer.allocate(8);
            dateBuffer.order(ByteOrder.BIG_ENDIAN);
            dateBuffer.put(this.encode(), 0x3d, 8);

            if (BuildConfig.DEBUG) {
                String outputString = HexDump.dumpHexString(dateBuffer.array());
                Log.d(TAG, "PAYLOAD: " + outputString);
            }

            long rtc = dateBuffer.getInt(0) & 0x00000000ffffffffL;
            long offset = dateBuffer.getInt(4);
            pumpTime = MessageUtils.decodeDateTime(rtc, offset);
        }
    }

    public Date getPumpTime() {
        return pumpTime;
    }
}
