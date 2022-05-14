package com.miaxis.face.util;

import android.graphics.Bitmap;

import com.zkteco.android.IDReader.WLTService;

public  class IdCardParser {


    public static String getName(byte[] idCardData){
        try{
            byte[] id_Name = new byte[30]; // 姓名
            System.arraycopy(idCardData, 0, id_Name, 0, id_Name.length);
            return unicode2String(id_Name).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEnglishName(byte[] idCardData){
        try {
            byte[] englishname=new byte[120];
            System.arraycopy(idCardData,0,englishname,0,englishname.length);
            return unicode2String(englishname);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEnglishGender(byte[] idCardData){
        try {
            byte[] englishgender=new byte[2];
            System.arraycopy(idCardData,120,englishgender,0,englishgender.length);
            return unicode2String(englishgender);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getGender(byte[] idCardData){
        try{
            byte[] gender = new byte[2];
            System.arraycopy(idCardData, 30, gender, 0, gender.length);
            return unicode2String(gender);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getNation(byte[] idCardData){
        try{
            byte[] nation = new byte[4];
            System.arraycopy(idCardData, 32, nation, 0, nation.length);
            return unicode2String(nation).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getBirthDay(byte[] idCardData){
        try{
            byte[] brithday = new byte[4];
            System.arraycopy(idCardData, 48, brithday, 0, brithday.length);
            return unicode2String(brithday).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getBirthYear(byte[] idCardData){
        try{
            byte[] brithday = new byte[8];
            System.arraycopy(idCardData, 36, brithday, 0, brithday.length);
            return unicode2String(brithday).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getBirthMonth(byte[] idCardData){
        try{
            byte[] brithday = new byte[4];
            System.arraycopy(idCardData, 44, brithday, 0, brithday.length);
            return unicode2String(brithday).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getBirthday(byte[] idCardData){
        try{
            byte[] brithday = new byte[16];
            System.arraycopy(idCardData, 36, brithday, 0, brithday.length);
            return unicode2String(brithday).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getAddress(byte[] idCardData){
        try{
            byte[] address = new byte[70];
            System.arraycopy(idCardData, 52, address, 0, address.length);
            return unicode2String(address).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getCardNum(byte[] idCardData){
        try{
            byte[] cardnum = new byte[36];
            System.arraycopy(idCardData, 122, cardnum, 0, cardnum.length);
            return unicode2String(cardnum).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getNationality(byte[] idCardData){
        try {
            byte[] nationality=new byte[6];
            System.arraycopy(idCardData,152,nationality,0,nationality.length);
            return unicode2String(nationality);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getChineseName(byte[] idCardData){
        try {
            byte[] chinesename=new byte[30];
            System.arraycopy(idCardData,158,chinesename,0,chinesename.length);
            return unicode2String(chinesename);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getIssuingAuthority(byte[] idCardData){
        try{
            byte[] IssuingAuthority = new byte[30];
            System.arraycopy(idCardData, 158, IssuingAuthority, 0, IssuingAuthority.length);
            return unicode2String(IssuingAuthority).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getStartYear(byte[] idCardData){
        try{
            byte[] starttime = new byte[8];
            System.arraycopy(idCardData, 188, starttime, 0, starttime.length);
            return unicode2String(starttime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getStartMonth(byte[] idCardData){
        try{
            byte[] starttime = new byte[4];
            System.arraycopy(idCardData, 196, starttime, 0, starttime.length);
            return unicode2String(starttime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getStartDay(byte[] idCardData){
        try{
            byte[] starttime = new byte[4];
            System.arraycopy(idCardData, 200, starttime, 0, starttime.length);
            return unicode2String(starttime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getStartTime(byte[] idCardData){
        try{
            byte[] starttime = new byte[16];
            System.arraycopy(idCardData, 188, starttime, 0, starttime.length);
            return unicode2String(starttime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEndYear(byte[] idCardData){
        try{
            byte[] endtime = new byte[8];
            System.arraycopy(idCardData, 204, endtime, 0, endtime.length);
            return unicode2String(endtime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEndMonth(byte[] idCardData){
        try{
            byte[] endtime = new byte[4];
            System.arraycopy(idCardData, 212, endtime, 0, endtime.length);
            return unicode2String(endtime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEndDay(byte[] idCardData){
        try{
            byte[] endtime = new byte[4];
            System.arraycopy(idCardData, 216, endtime, 0, endtime.length);
            return unicode2String(endtime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEndTime(byte[] idCardData){
        try{
            byte[] endtime = new byte[16];
            System.arraycopy(idCardData, 204, endtime, 0, endtime.length);
            return unicode2String(endtime).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static String getEnglishY(byte[] idCardData){
        try {
            byte[] englishbir=new byte[8];
            System.arraycopy(idCardData,220,englishbir,0,englishbir.length);
            return unicode2String(englishbir);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEnglishM(byte[] idCardData){
        try {
            byte[] englishbir=new byte[4];
            System.arraycopy(idCardData,228,englishbir,0,englishbir.length);
            return unicode2String(englishbir);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEnglishD(byte[] idCardData){
        try {
            byte[] englishbir=new byte[4];
            System.arraycopy(idCardData,232,englishbir,0,englishbir.length);
            return unicode2String(englishbir);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String getEnglishBir(byte[] idCardData){
        try {
            byte[] englishbir=new byte[16];
            System.arraycopy(idCardData,220,englishbir,0,englishbir.length);
            return unicode2String(englishbir);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersion(byte[] idCardData){
        try {
            byte[] version=new byte[4];
            System.arraycopy(idCardData,236,version,0,version.length);
            return unicode2String(version);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getPassNum(byte[] idCardData){
        try{
            byte[] passnumber = new byte[18];
            System.arraycopy(idCardData, 220, passnumber, 0, passnumber.length);
            return unicode2String(passnumber).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getIssueNum(byte[] idCardData){
        try{
            byte[] issuenum = new byte[4];
            System.arraycopy(idCardData, 238, issuenum, 0, issuenum.length);
            return unicode2String(issuenum).trim();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getAcceptMatter(byte[] idCardData){
        try {
            byte[] acceptmatter=new byte[8];
            System.arraycopy(idCardData,240,acceptmatter,0,acceptmatter.length);
            return unicode2String(acceptmatter);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getCardType(byte[] idCardData){
        try{
            byte[] cardtype = new byte[2];
            System.arraycopy(idCardData, 248, cardtype, 0, cardtype.length);
            String type=unicode2String(cardtype).trim();
            return type;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap  getFaceBit(byte[] idCardData){
        try {
            byte[] facebit=new byte[1024];
            System.arraycopy(idCardData, 256, facebit, 0, facebit.length);
            return getBitmap(facebit);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String unicode2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length / 2; i++) {
            int a = bytes[2 * i + 1];
            if (a < 0) {
                a = a + 256;
            }
            int b = bytes[2 * i];
            if (b < 0) {
                b = b + 256;
            }
            int c = (a << 8) | b;
            sb.append((char) c);
        }
        return sb.toString();
    }

    private static Bitmap getBitmap(byte[] wlt) {
        byte[] buffer = new byte[38556];
        int result = WLTService.wlt2Bmp(wlt, buffer);
        if (result == 1) {
            return Bgr2Bitmap(buffer);
        }
        return null;
    }

    private static Bitmap Bgr2Bitmap(byte[] bgrbuf) {
        int width = WLTService.imgWidth;
        int height = WLTService.imgHeight;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int row = 0, col = width - 1;
        for (int i = bgrbuf.length - 1; i >= 3; i -= 3) {
            int color = bgrbuf[i] & 0xFF;
            color += (bgrbuf[i - 1] << 8) & 0xFF00;
            color += ((bgrbuf[i - 2]) << 16) & 0xFF0000;
            bmp.setPixel(col--, row, color);
            if (col < 0) {
                col = width - 1;
                row++;
            }
        }
        return bmp;
    }
}
