/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samplegenerator;

import com.ft.otp.common.StrConstant;
import com.ft.otp.util.alg.AESUtil;
import com.ft.otp.util.crypto.ByteUtil;
import com.ft.otp.util.crypto.PBKDF2;
import com.ft.otp.util.crypto.PRF;
import com.securemetric.otp.OtpData;
import com.securemetric.otp.generator.CROTPGenerator;
import com.securemetric.otp.generator.TOTPGenerator;
import com.securemetric.otp.management.OtpManagement;
import com.securemetric.otp.verifier.CROTPVerifier;
import com.securemetric.otp.verifier.TOTPVerifier;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dom4j.Element;


/**
 *
 * @author jinyoo.auyong
 */
public class SampleGenerator {
    private int SERIAL_NUMBER_LENGTH = 15;
    private int SYNC_WINDOW = 0;
    private int TIMESTEP = 0;
    public static final BouncyCastleProvider BC_PROVIDER = new BouncyCastleProvider ();;
    private static final String IV = "F1A0D2C404D98E9B20A28B6D90F4FEDC";
    public static void main ( String[] args ) throws NoSuchAlgorithmException
    {
        OtpManagement otpManagement = new OtpManagement();
        SampleGenerator sampleGenerator = new SampleGenerator();
        //OtpData otpData = sampleGenerator.assignOTP();
       // sampleGenerator.encryptTokenSeed("f51759d9f1215805fbe2211d7e9464a1e90dc5ef".getBytes());
        sampleGenerator.decryptTokenSeed("IHK/V3u7V7TT9G1cgkXq8fTkiep+OCLsngWSfxO8XrpA8E8U/9jtr5APP9vKVUcCbOmwREmACIG3+7keU68mRhedZ85w7GxiVtfaBVrfxzc=");
        OtpData otpData;
        otpData = sampleGenerator.manualAssignOTP(
                "6bdOrw6UU+Z2221qYvETRYmDxrVjoo0QZzFgVht9+yIXYWUf4ZUQwijuFh7lMSb9",
                "GATP00614617",
                0,
                "HmacSHA1",
                "OCRA-1:HOTP-SHA1-6:QN12-T8S",
                Long.parseLong("8000"),
                6);
        Long time = Long.parseLong("1602219692000");
        Long validity1 = Long.parseLong("8000");
        Long validity = Long.parseLong ( "60" );
        validity *= 1000L;
        
        System.out.println("test 1 = "+time/validity);
        System.out.println("test 2 = "+time/validity1);
        
        
        String timeFactor = Long.toHexString (  time   / validity ).toUpperCase ();
        System.out.println("test 3 = "+timeFactor);
//        //8829ED2B03C8F44058AB8200FF55A2DA630FFD27AF036CAA14037C7F8B40A191
//        
//        String totp = sampleGenerator.generateTOTP(otpData, time + (1 * Long.parseLong("8000")));
//            System.out.println("generated time otp = " + totp);
//        String inputotp = "43077993";    
//            for (int i = 1; i <= 3600; i++) {
//    //        // generate time otp and verify time otp
//
//            totp = sampleGenerator.generateTOTP(otpData, time - (i * Long.parseLong("30000")));
//            System.out.println(time + (i * Long.parseLong("30000")));
//            if(totp.equals(inputotp)) {
//                System.out.println("generated time otp count " + i +" : "+ totp);
//                break;
//            }
//            
//    
//        }
//        int returnCode = 1;
//        // generate time otp and verify time otp
//        String totp = sampleGenerator.generateTOTP(otpData, time);
//        System.out.println("generated time otp = " + totp);
//        String totp2 = sampleGenerator.generateTOTP(otpData, time - (1 * Long.parseLong("30000")));
//        System.out.println("generated time otp = " + totp2);
//        String totp3 = sampleGenerator.generateTOTP(otpData, time - (2 * Long.parseLong("30000")));
//        System.out.println("generated time otp = " + totp3);
//        String totp4 = sampleGenerator.generateTOTP(otpData, time - (3 * Long.parseLong("30000")));
//        System.out.println("generated time otp = " + totp4);
//        
//        
//        int returnCode = sampleGenerator.verifyTOTP(otpData, totp);
//        if(returnCode == 0) {
//            System.out.println(otpManagement.getReturnMsg(returnCode));
//            System.out.println("Valid OTP");
//        }
//        else {
//              System.out.println(otpManagement.getReturnMsg(returnCode));
//            System.out.println("Invalid OTP");
//        }
//        
//        String challenge = sampleGenerator.requestCROTP(otpData);
        String challenge = "226270926553";
        System.out.println("generated challenge= " + challenge);
        if(challenge != null) {
            //generate otp using the challenge 
            String otp  = "422032";
            //String otp  = sampleGenerator.generateCROTP(otpData, challenge, time);
            System.out.println("input otp = " + otp);
            int returnCode = sampleGenerator.verifyCROTP(otpData, otp, challenge);
            if(returnCode == 0) {
                System.out.println(otpManagement.getReturnMsg(returnCode));
                System.out.println("Valid OTP");
            }
            else {
                System.out.println(otpManagement.getReturnMsg(returnCode));
                System.out.println("Invalid OTP");
            }
        }
        
    }


    public OtpData assignOTP() {
        Long systemTime = System.currentTimeMillis();
        String serialNumber = "01"; //01 is my sample company code  
        
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("ddMMyy");   
        serialNumber = serialNumber.concat(simpleDateFormatter.format(new Date(systemTime)));
        
        //get random number to ensure serial is unique enough
        String randomNumber = generateRandomCharacters("AbBCcD1u2345d6s7t8e9fgh0ijYkQtzm9kEp10uQmqpFGpHIJKqLrMvzNwxO90214PaQRSlTmUzVynoWXYZ", SERIAL_NUMBER_LENGTH - serialNumber.length ());
        
        serialNumber = serialNumber.concat(randomNumber);
        
        
        OtpManagement otpManagement = new OtpManagement();
        
        if(otpManagement.getCRTokenData(serialNumber) != 0) {
	//OTP generation failed;
	// your own code handling
           return null;
        } 
         
        OtpData otpDataReturn = otpManagement.getOtpData();
        
        //need to store otpDataReturn data 
//        System.out.println(otpDataReturn.getOtpType());
//        System.out.println(otpDataReturn.getAlgId());
//        System.out.println(otpDataReturn.getAlgName());
//        System.out.println(otpDataReturn.getCrSuite());
//        System.out.println(otpDataReturn.getCvsSuite());
//        System.out.println(otpDataReturn.getOtpLen());
//        System.out.println(otpDataReturn.getOtpSeedInfo());
//        System.out.println(otpDataReturn.getSerialNumber());
//        System.out.println(otpDataReturn.getValidity());
//        System.out.println(otpDataReturn.getSignSuite());
//        System.out.println(otpDataReturn.getSeed());
        
        return otpDataReturn;
    }
    
    public OtpData manualAssignOTP(String seed, String serialNumber, int algId, String algName, String crSuite, Long validity, int otpLength) {

        OtpData otpDataReturn = new OtpData();
        otpDataReturn.setAlgId(algId);
        otpDataReturn.setAlgName(algName);
        otpDataReturn.setOtpLen(otpLength);
        otpDataReturn.setCrSuite(crSuite);
        otpDataReturn.setValidity(validity);
        otpDataReturn.setSeed(decryptTokenSeed(seed));
        otpDataReturn.setSerialNumber(serialNumber);
        
        
        //need to store otpDataReturn data 
//        System.out.println(otpDataReturn.getOtpType());
//        System.out.println(otpDataReturn.getAlgId());
//        System.out.println(otpDataReturn.getAlgName());
//        System.out.println(otpDataReturn.getCrSuite());
//        System.out.println(otpDataReturn.getCvsSuite());
//        System.out.println(otpDataReturn.getOtpLen());
//        System.out.println(otpDataReturn.getOtpSeedInfo());
//        System.out.println(otpDataReturn.getSerialNumber());
//        System.out.println(otpDataReturn.getValidity());
//        System.out.println(otpDataReturn.getSignSuite());
//        System.out.println(otpDataReturn.getSeed());
        
        return otpDataReturn;
    }
    
    
    public String generateTOTP(OtpData otpData, long time) {
        TOTPGenerator tgenerator = new TOTPGenerator ();               
        tgenerator.setAlgorithm(otpData.getAlgName());
        tgenerator.setCodeDigits(otpData.getOtpLen());
        tgenerator.setDriftCount(0);
        tgenerator.setKey(otpData.getSeed());
        tgenerator.setValidity(otpData.getValidity());
        tgenerator.setTime ( time );
        return tgenerator.generate();
    }
    
    public int verifyTOTP(OtpData otpData, String otp) {
        TOTPVerifier verifier = new TOTPVerifier();
        verifier.setAlgorithm(otpData.getAlgName());
        verifier.setCodeDigits(otpData.getOtpLen());
        verifier.setDriftCount(0);
        verifier.setKey(otpData.getSeed());
        verifier.setValidity(otpData.getValidity());
        verifier.setTimeStep(4);
        verifier.setOtp(otp);
        return verifier.verify();
    }
    
    public String requestCROTP(OtpData otpData) {
        
        CROTPGenerator generator = new CROTPGenerator();
        generator.setDriftCount(0);
        generator.setSuite(otpData.getCrSuite());

        return generator.generateChallenge();
    }
    
    public String generateCROTP(OtpData otpData,  String challenge, long time) {
        
        CROTPGenerator generator = new CROTPGenerator ();
        generator.setKey ( otpData.getSeed() );
        generator.setChallenge (new BigInteger(challenge, 10).toString(16).toUpperCase()  );
        generator.setSuite ( otpData.getCrSuite());
        generator.setTime ( time );
        generator.setDriftCount (0 );
        generator.setCodeDigits(  otpData.getOtpLen ());
        return generator.generate();
    }
    
    public int verifyCROTP(OtpData otpData, String otp, String challenge) {
        CROTPVerifier verifier = new CROTPVerifier();
        
        //driftcount also from the system table
        verifier.setDriftCount(0);
        verifier.setKey(otpData.getSeed());
        verifier.setOtp(otp);
        
        System.out.println(new BigInteger(challenge, 10).toString(16).toUpperCase());
        //sync window is from the system config
        verifier.setTimeStep(0);
        verifier.setChallenge(new BigInteger(challenge, 10).toString(16).toUpperCase());
        verifier.setCrSuite(otpData.getCrSuite());
        
        //sync window is from the system config
        verifier.setSyncWindow(SYNC_WINDOW);
        verifier.setValidity(otpData.getValidity());
        return verifier.verify();
    }
     
    public static String generateRandomCharacters ( String charset , int length )
    {
        
        SecureRandom generator = new SecureRandom ();

        StringBuilder builder = new StringBuilder ();

        int limit = charset.length ();

        for ( int i = 0 ; i < length ; i ++ )
            builder.append ( String.valueOf ( charset.charAt ( generator.nextInt ( limit ) ) ) );

        return builder.toString ();
    }
    
    
    private synchronized String decryptTokenSeed(String encryptedSeed) {
        String decryptedPublicKey = "";

        SecretKey encKey = null;
        Provider provider = BC_PROVIDER;
        encKey = new SecretKeySpec("LUBFYvmo+LShW30WEETYYUcZbJ6mWxn3".getBytes(), "AES");

        try {
            String algorithm = "AES";
            final IvParameterSpec ivSpec = new IvParameterSpec(hexToBytes(IV));

            final Cipher cipher = Cipher.getInstance(algorithm, provider);
            cipher.init(Cipher.DECRYPT_MODE, encKey, ivSpec);

            byte[] paddedDecryptedPublicKey = cipher.doFinal(Base64.decodeBase64(encryptedSeed));
            decryptedPublicKey = new String(paddedDecryptedPublicKey).toUpperCase();

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        System.out.println("decryptedPublicKey = "+decryptedPublicKey);
        return decryptedPublicKey;
    }
    //f51759d9f1215805fbe2211d7e9464a1e90dc5ef

    private String encryptTokenSeed(byte[] plainPublicKey) {
        String encryptedPublicKey = "";


        SecretKey encKey = null;


        Provider provider = BC_PROVIDER;


         encKey = new SecretKeySpec("LUBFYvmo+LShW30WEETYYUcZbJ6mWxn3".getBytes(), "AES");


        try {
            String algorithm = "AES";
            final IvParameterSpec ivSpec = new IvParameterSpec(hexToBytes(IV));

            final Cipher cipher = Cipher.getInstance(algorithm, provider);
            cipher.init(Cipher.ENCRYPT_MODE, encKey, ivSpec);

            encryptedPublicKey = Base64.encodeBase64String(cipher.doFinal(plainPublicKey));
        } catch (Exception ex) {
            ex.printStackTrace();
        }  
        System.out.println(encryptedPublicKey);
        return encryptedPublicKey;
    }
    
    
    public static byte[] hexToBytes(String string) {
        int length = string.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character
                    .digit(string.charAt(i + 1), 16));
        }
        return data;
    }
    
    
    public byte[] getPBEKey(byte[] deskey, Element derivedKeyNode) throws IOException, InvalidKeyException {
        byte[] saltVal = null;
        int counter = 0;
        int keyLength = 0;
        if (null != derivedKeyNode) {
            
            Element keyDerivationMethodNode = derivedKeyNode.element("KeyDerivationMethod");
            if (null != keyDerivationMethodNode) {
                Element PBKDF2Params = keyDerivationMethodNode.element("PBKDF2-params");
                
                Element saltEle = PBKDF2Params.element("Salt");
                if (null != saltEle) {
                    Element specifiedEle = saltEle.element("Specified");
                    saltVal = Base64.decodeBase64(specifiedEle.getText());
                }
                Element countEle = PBKDF2Params.element("IterationCount");
                if (null != countEle) {
                    counter = Integer.parseInt(countEle.getText());
                }
                Element keylengthEle = PBKDF2Params.element("KeyLength");
                if (null != keylengthEle) {
                    keyLength = Integer.parseInt(keylengthEle.getText());
            }
            }
        }

        if (saltVal == null || counter <= 0 || keyLength <= 0) {
            return null;
        }
        PBKDF2 pkcs5 = new PBKDF2(PRF.HMAC_SHA1, saltVal);
        
        final byte[] key = pkcs5.deriveKey( ByteUtil.bytesToHex(deskey).toUpperCase(), counter, keyLength);
 
        return key;

    }
    
//    public void getKeySecret(){
//        String cipherValue = null;
//        String derivedKeyNode = null;
//        byte[] key = null;
//        if (null != derivedKeyNode) {
//            byte[] pinkey = getPBEKey(key, derivedKeyNode);
//            byte[] mackey = Base64.decodeBase64(cipherValue);
//            //keySecret = AlgConvert.bytes2HexString(AESUtil.PBEDescrypty(pinkey, mackey,StrConstant.FILTYPE_PKCS5PADDING));
//            keySecret = AESUtil.AESDecrypt(StrConstant.FILTYPE_PKCS5PADDING, cipherValue, pinkey);
//        }
//    }
//    
}



